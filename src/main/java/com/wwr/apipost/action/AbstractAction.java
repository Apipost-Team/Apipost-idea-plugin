package com.wwr.apipost.action;

import com.google.common.collect.Lists;
import com.google.common.util.concurrent.AtomicDouble;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiClass;
import com.wwr.apipost.config.ApiPostConfig;
import com.wwr.apipost.config.ApiPostConfigUtils;
import com.wwr.apipost.config.DefaultConstants;
import com.wwr.apipost.config.domain.Api;
import com.wwr.apipost.config.domain.EventData;
import com.wwr.apipost.handle.apipost.config.ApiPostSettings;
import com.wwr.apipost.parse.ApiParser;
import com.wwr.apipost.parse.model.ClassApiData;
import com.wwr.apipost.parse.model.MethodApiData;
import com.wwr.apipost.parse.util.NotificationUtils;
import com.wwr.apipost.util.ConcurrentUtils;
import com.wwr.apipost.util.psi.PsiFileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static com.wwr.apipost.config.DefaultConstants.DEFAULT_PROPERTY_FILE_CACHE;
import static com.wwr.apipost.parse.util.NotificationUtils.notifyError;
import static com.wwr.apipost.parse.util.NotificationUtils.notifyInfo;
import static com.wwr.apipost.util.CommonUtil.getServerPerUrl;
import static java.lang.String.format;

/**
 * API文档解析处理的动作模板类
 */
public abstract class AbstractAction extends AnAction {

    /**
     * 配置文件是否必须
     */
    private final boolean requiredConfigFile;


    public AbstractAction(@Nullable Icon icon, boolean requiredConfigFile) {
        super(icon);
        this.requiredConfigFile = requiredConfigFile;
    }

    protected AbstractAction() {
         this.requiredConfigFile = true;
//        this.requiredConfigFile = false;
    }

    protected AbstractAction(boolean requiredConfigFile) {
        this.requiredConfigFile = requiredConfigFile;
    }

    @Override
    public void actionPerformed(AnActionEvent event) {
        // make sure documents all saved before refresh v-files in sync/recursive.
        FileDocumentManager.getInstance().saveAllDocuments();
        EventData data = EventData.of(event);
        if (!data.shouldHandle()) {
            return;
        }
        // 1.解析配置
        StepResult<ApiPostConfig> configResult = resolveConfig(data);
        ApiPostConfig config = configResult.getData();
        if (!configResult.isContinue()) {
            return;
        }
        // 2.前置处理
        if (!before(event, config)) {
            return;
        }
        if (!after(event, config, data)) {
            return;
        }
        // 3.解析文档
        StepResult<List<Api>> apisResult = parse(data, config);
        if (!apisResult.isContinue()) {
            return;
        }
        // 4.文档处理
        List<Api> apis = apisResult.getData();
        handle(event, config, apis);
    }

    /**
     * 检查前操作
     */
    public boolean before(AnActionEvent event, ApiPostConfig config) {
        return true;
    }

    /**
     * 后置操作
     */
    public boolean after(AnActionEvent event, ApiPostConfig config, EventData data) {
        return true;
    }

    /**
     * 文档处理
     */
    public abstract void handle(AnActionEvent event, ApiPostConfig config, List<Api> apis);

    /**
     * 解析文档模型数据
     */
    private StepResult<List<Api>> parse(EventData data, ApiPostConfig config) {
        ApiParser parser = new ApiParser(data.getProject(), data.getModule(), config);
        // 选中方法
        if (data.getSelectedMethod() != null) {
            MethodApiData methodData = parser.parse(data.getSelectedMethod());
            if (!methodData.isValid()) {
                NotificationUtils.notifyWarning(DefaultConstants.NAME,
                        "The current method is not a valid api or ignored");
                return StepResult.stop();
            }
            if (config.isStrict() && StringUtils.isEmpty(methodData.getDeclaredApiSummary())) {
                NotificationUtils.notifyWarning(DefaultConstants.NAME, "The current method must declare summary");
                return StepResult.stop();
            }
            return StepResult.ok(methodData.getApis());
        }

        // 选中类
        if (data.getSelectedClass() != null) {
            ClassApiData controllerData = parser.parse(data.getSelectedClass());
            if (!controllerData.isValid()) {
                NotificationUtils.notifyWarning(DefaultConstants.NAME,
                        "The current class is not a valid controller or ignored");
                return StepResult.stop();
            }
            if (config.isStrict() && StringUtils.isEmpty(controllerData.getDeclaredCategory())) {
                NotificationUtils.notifyWarning(DefaultConstants.NAME, "The current class must declare category");
                return StepResult.stop();
            }
            return StepResult.ok(controllerData.getApis());
        }

        // 批量
        List<PsiClass> controllers = PsiFileUtils.getPsiClassByFile(data.getSelectedJavaFiles());
        if (controllers.isEmpty()) {
            NotificationUtils.notifyWarning(DefaultConstants.NAME, "Not found valid controller class");
            return StepResult.stop();
        }
        List<Api> apis = Lists.newLinkedList();
        for (PsiClass controller : controllers) {
            ClassApiData controllerData = parser.parse(controller);
            if (!controllerData.isValid()) {
                continue;
            }
            if (config.isStrict() && StringUtils.isEmpty(controllerData.getDeclaredCategory())) {
                continue;
            }
            List<Api> controllerApis = controllerData.getApis();
            if (config.isStrict()) {
                controllerApis = controllerApis.stream().filter(o -> StringUtils.isNotEmpty(o.getSummary()))
                        .collect(Collectors.toList());
            }
            apis.addAll(controllerApis);
        }
        return StepResult.ok(apis);
    }

    /**
     * 获取配置
     */
    private StepResult<ApiPostConfig> resolveConfig(EventData data) {
        // 配置文件解析
        VirtualFile file = ApiPostConfigUtils.findConfigFile(data.getProject(), data.getModule());
        if (requiredConfigFile && (file == null || !file.exists())) {
//            ServiceManager.getService(ApiPostSettings.class).setProjectId(null);
            NotificationUtils.notify(NotificationType.WARNING, "",
                    "Not found config file " + DEFAULT_PROPERTY_FILE_CACHE,
                    new CreateConfigFileAction(data.getProject(), data.getModule(), "Create Config File"));
            return StepResult.stop();
        }
        ApiPostConfig config = null;
        if (file != null && file.exists()) {
            try {
                config = ApiPostConfigUtils.readConfig(file);
            } catch (Exception e) {
                notifyError(String.format("Config file error: %s", e.getMessage()));
                return StepResult.stop();
            }
        }
        if (config == null) {
            config = new ApiPostConfig();
        }
        ApiPostSettings settings = ApiPostSettings.getInstance();
        String currentUrl = getServerPerUrl(data.getModule(), settings);
        if (StringUtils.isNotBlank(currentUrl)) {
            String currentPath = config.getPath();
            String updatedPath = StringUtils.isNotBlank(currentPath) ? currentUrl + currentPath : currentUrl;
            config.setPath(updatedPath);
        }
        config = ApiPostConfig.getMergedInternalConfig(config, data.getLocalDefaultFileCache());
        return StepResult.ok(config);
    }


    /**
     * 异步上传模板方法
     *
     * @param project       项目
     * @param apis          待处理接口列表
     * @param apiHandle     单个接口数据消费者
     * @param afterCallback 所有接口列表处理完毕后的回调执行，用于关闭资源
     */
    protected void handleUploadAsync(Project project, List<Api> apis, Function<Api, ApiUploadResult> apiHandle,
                                     Supplier<?> afterCallback) {
        // 异步处理
        ProgressManager.getInstance().run(new Task.Backgroundable(project, DefaultConstants.NAME) {
            @Override
            public void run(@NotNull ProgressIndicator indicator) {
                indicator.setIndeterminate(false);
                int poolSize = apis.size() == 1 ? 1 : 4;
                // 进度和并发
                Semaphore semaphore = new Semaphore(poolSize);
                ExecutorService threadPool = Executors.newFixedThreadPool(poolSize);
                double step = 1.0 / apis.size();
                AtomicInteger count = new AtomicInteger();
                AtomicDouble fraction = new AtomicDouble();

                List<ApiUploadResult> urls = null;
                try {
                    List<Future<ApiUploadResult>> futures = Lists.newArrayListWithExpectedSize(apis.size());
                    for (int i = 0; i < apis.size() && !indicator.isCanceled(); i++) {
                        Api api = apis.get(i);
                        semaphore.acquire();
                        Future<ApiUploadResult> future = threadPool.submit(() -> {
                            try {
                                // 上传
                                String text = format("[%d/%d] %s %s", count.incrementAndGet(), apis.size(),
                                        api.getMethod(), api.getPath());
                                indicator.setText(text);
                                return apiHandle.apply(api);
                            } catch (Exception e) {
                                notifyError(
                                        String.format("Upload failed: [%s %s]", api.getMethod(), api.getPath()),
                                        ExceptionUtils.getStackTrace(e));
                            } finally {
                                indicator.setFraction(fraction.addAndGet(step));
                                semaphore.release();
                            }
                            return null;
                        });
                        futures.add(future);
                    }
                    urls = ConcurrentUtils.waitFuturesSilence(futures).stream()
                            .filter(Objects::nonNull).collect(Collectors.toList());
                } catch (InterruptedException e) {
                    // ignore
                } finally {
                    if (urls != null && !urls.isEmpty()) {
                        ApiUploadResult uploadResult = urls.get(0);
                        String url = urls.size() == 1 ? uploadResult.getApiUrl() : uploadResult.getCategoryUrl();
                        if (url != null && url.length() > 0) {
                            notifyInfo("Upload successful", format("<a href=\"%s\">%s</a>", url, url));
                        } else {
                            notifyInfo("Upload successful");
                        }
                    }
                    threadPool.shutdown();
                    if (afterCallback != null) {
                        afterCallback.get();
                    }
                }
            }
        });
    }


    public static class ApiUploadResult {

        private String categoryUrl;
        private String apiUrl;

        //------------------ generated ------------------------//

        public String getCategoryUrl() {
            return categoryUrl;
        }

        public void setCategoryUrl(String categoryUrl) {
            this.categoryUrl = categoryUrl;
        }

        public String getApiUrl() {
            return apiUrl;
        }

        public void setApiUrl(String apiUrl) {
            this.apiUrl = apiUrl;
        }
    }

    /**
     * 某个步骤的执行结果
     */
    public static class StepResult<T> {

        private StepType type;
        private T data;

        public enum StepType {
            CONTINUE, STOP
        }

        public boolean isContinue() {
            return type == StepType.CONTINUE;
        }

        public StepResult(StepType type, T data) {
            this.type = type;
            this.data = data;
        }

        public static <T> StepResult<T> ok(T data) {
            return new StepResult<>(StepType.CONTINUE, data);
        }

        public static <T> StepResult<T> stop() {
            return new StepResult<>(StepType.STOP, null);
        }

        public T getData() {
            return data;
        }
    }
}
