package com.wwr.apipost.parse;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtilCore;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import com.wwr.apipost.config.ApiPostConfig;
import com.wwr.apipost.config.domain.Api;
import com.wwr.apipost.config.domain.EventData;
import com.wwr.apipost.parse.constant.SpringConstants;
import com.wwr.apipost.parse.model.*;
import com.wwr.apipost.parse.parser.ParseHelper;
import com.wwr.apipost.parse.parser.PathParser;
import com.wwr.apipost.parse.parser.RequestParser;
import com.wwr.apipost.parse.parser.ResponseParser;
import com.wwr.apipost.parse.util.InternalUtils;
import com.wwr.apipost.parse.util.PathUtils;
import com.wwr.apipost.util.psi.PsiAnnotationUtils;
import com.wwr.apipost.util.psi.PsiUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.util.Objects.isNull;

/**
 * Api接口解析器
 */
public class ApiParser {

    private final RequestParser requestParser;
    private final ResponseParser responseParser;
    private final ParseHelper parseHelper;
    private final Project project;
    private final Module module;
    private final ApiPostConfig settings;

    public ApiParser(Project project, Module module, ApiPostConfig settings) {
        checkNotNull(project);
        checkNotNull(module);
        checkNotNull(settings);
        this.project = project;
        this.module = module;
        this.settings = settings;
        this.requestParser = new RequestParser(project, module, settings);
        this.responseParser = new ResponseParser(project, module, settings);
        this.parseHelper = new ParseHelper(project, module);
    }

    /**
     * 解析接口
     */
    public ClassApiData parse(PsiClass psiClass) {
        ClassApiData data = new ClassApiData();
        if (!isParseTargetPsiClass(psiClass) || parseHelper.isClassIgnored(psiClass)) {
            data.setValid(false);
            return data;
        }
        ClassLevelApiInfo classLevelApiInfo = doParseClassLevelApiInfo(psiClass);

        List<PsiMethod> methods = filterMethodsToParse(psiClass);
        List<MethodApiData> methodApiDataList = methods.stream()
                .map(method -> doParseMethod(method, classLevelApiInfo))
                .collect(Collectors.toList());

        data.setDeclaredCategory(classLevelApiInfo.getDeclareCategory());
        data.setMethodDataList(methodApiDataList);
        return data;
    }

    /**
     * 解析接口
     */
    public ClassApiData parseV1(EventData data1) {
        PsiClass psiClass = data1.getSelectedClass();
        ClassApiData data = new ClassApiData();
        if (!isParseTargetPsiClass(psiClass) || parseHelper.isClassIgnored(psiClass)) {
            data.setValid(false);
            return data;
        }
        PsiFile psiFile = psiClass.getContainingFile();
        VirtualFile virtualFile = psiFile.getVirtualFile();
        Module module = ModuleUtilCore.findModuleForFile(virtualFile, data1.getProject());
        ClassLevelApiInfo classLevelApiInfo = doParseClassLevelApiInfo(psiClass);

        List<PsiMethod> methods = filterMethodsToParse(psiClass);
        List<MethodApiData> methodApiDataList = methods.stream()
                .map(method -> doParseMethod(method, classLevelApiInfo))
                .collect(Collectors.toList());

        data.setDeclaredCategory(classLevelApiInfo.getDeclareCategory());
        data.setMethodDataList(methodApiDataList);
        data.setModule(module);
        return data;
    }

    /**
     * 解析接口
     */
    public MethodApiData parse(EventData data) {
        PsiMethod method = data.getSelectedMethod();
        PsiClass psiClass = method.getContainingClass();
        PsiFile psiFile = psiClass.getContainingFile();
        VirtualFile virtualFile = psiFile.getVirtualFile();
        Module module = ModuleUtilCore.findModuleForFile(virtualFile, data.getProject());
        MethodApiData methodApiData = doParseMethod(method, doParseClassLevelApiInfo(psiClass));
        methodApiData.setModule(module);
        return methodApiData;
    }


    /**
     * 解析类级别信息，包括路径前缀、分类等
     */
    private ClassLevelApiInfo doParseClassLevelApiInfo(PsiClass psiClass) {
        ClassLevelApiInfo info = new ClassLevelApiInfo();
        PsiAnnotation annotation = PsiAnnotationUtils.getAnnotationIncludeExtends(psiClass, SpringConstants.RequestMapping);
        if (annotation != null) {
            PathInfo path = PathParser.parseRequestMappingAnnotation(annotation);
            info.setPath(PathUtils.path(path.getPath()));
        }
        info.setCategory(parseHelper.getDeclareApiCategory(psiClass));
        info.setDeclareCategory(info.getCategory());
        if (StringUtils.isEmpty(info.getCategory())) {
            info.setCategory(parseHelper.getDefaultApiCategory(psiClass));
        }
        return info;
    }

    /**
     * 解析某个方法的接口信息
     */
    private MethodApiData doParseMethod(PsiMethod method, ClassLevelApiInfo classLevelInfo) {
        MethodApiData data = new MethodApiData();

        // 1.该方法是否被跳过
        if (parseHelper.isMethodIgnored(method)) {
            data.setValid(false);
            return data;
        }

        // 2.解析方法级别@XxxMapping注解
        PathInfo pathInfo = PathParser.parse(method);
        if (pathInfo == null || pathInfo.getPaths() == null) {
            data.setValid(false);
            return data;
        }

        // 3.解析方法的参数和响应信息
        Api methodApi = getMethodApi(method, pathInfo);
        data.setDeclaredApiSummary(methodApi.getSummary());

        // 4.多路径处理
        List<Api> apis = pathInfo.getPaths().stream().map(path -> {
            Api api = methodApi;
            if (pathInfo.getPaths().size() > 1) {
                api = InternalUtils.clone(methodApi);
            }
            api.setMethod(pathInfo.getMethod());
            api.setPath(PathUtils.path(classLevelInfo.getPath(), path));
            if (this.settings.getPath() != null) {
                api.setPath(PathUtils.path(this.settings.getPath(), api.getPath()));
            }
            api.setCategory(classLevelInfo.getCategory());
            return api;
        }).collect(Collectors.toList());
        data.setApis(apis);

        return data;
    }

    /**
     * 解析方法的通用信息，除请求路径、请求方法外.
     */
    private Api getMethodApi(PsiMethod method, PathInfo path) {
        Api api = new Api();
        // 基本信息
        api.setMethod(path.getMethod());
        api.setSummary(parseHelper.getApiSummary(method));
        api.setDescription(parseHelper.getApiDescription(method));
        api.setDeprecated(parseHelper.getApiDeprecated(method));
        api.setTags(parseHelper.getApiTags(method));
        // 请求信息
        RequestInfo requestInfo = requestParser.parse(method, path.getMethod());
        api.setParameters(requestInfo.getParameters());
        api.setRequestBodyType(requestInfo.getRequestBodyType());
        api.setRequestBody(requestInfo.getRequestBody());
        api.setRequestBodyForm(requestInfo.getRequestBodyForm());
        // 响应信息
        api.setResponses(responseParser.parse(method));
        return api;
    }

    /**
     * 判断是否是控制类或接口
     */
    private boolean isParseTargetPsiClass(PsiClass psiClass) {
        // 是否有@RestController、@Controller注解
        boolean isController = psiClass.isInterface()
                || PsiAnnotationUtils.getAnnotation(psiClass, SpringConstants.RestController) != null
                || PsiAnnotationUtils.getAnnotation(psiClass, SpringConstants.Controller) != null;
        if (isController) {
            return true;
        }

        // 支持一级组合继承@RestController、@Controller的情况
        PsiAnnotation[] annotations = psiClass.getAnnotations();
        for (PsiAnnotation annotation : annotations) {
            PsiClass thePsiClass = PsiUtils.findPsiClass(project, module, annotation.getQualifiedName());
            if (isNull(thePsiClass)) {
                continue;
            }
            isController = PsiAnnotationUtils.getAnnotation(thePsiClass, SpringConstants.RestController) != null
                    || PsiAnnotationUtils.getAnnotation(thePsiClass, SpringConstants.Controller) != null;
            if (isController) {
                break;
            }
        }
        return isController;
    }

    /**
     * 获取待处理的方法列表
     */
    private List<PsiMethod> filterMethodsToParse(PsiClass psiClass) {
        return Arrays.stream(psiClass.getAllMethods())
                .filter(m -> {
                    PsiModifierList modifier = m.getModifierList();
                    return !modifier.hasModifierProperty(PsiModifier.PRIVATE)
                            && !modifier.hasModifierProperty(PsiModifier.STATIC);
                })
                .collect(Collectors.toList());
    }


}
