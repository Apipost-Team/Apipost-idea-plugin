package com.wwr.apipost.openapi;

import com.google.common.collect.Sets;
import com.google.gson.*;
import io.swagger.v3.oas.models.OpenAPI;
import org.yaml.snakeyaml.introspector.BeanAccess;
import org.yaml.snakeyaml.introspector.Property;
import org.yaml.snakeyaml.introspector.PropertyUtils;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

public class OpenApiGenerator {

    public JsonObject generate(OpenAPI openApi) {
        Gson gson = new GsonBuilder()
                .setExclusionStrategies(new NamedExclusionStrategy(Sets.newHashSet("exampleSetFlag", "specVersion")))
                .setPrettyPrinting()
                .create();
        JsonElement jsonElement = gson.toJsonTree(openApi);
        return jsonElement.getAsJsonObject();
    }

//    @NotNull
//    private static Yaml buildYaml() {
//        DumperOptions dumperOptions = new DumperOptions();
//        dumperOptions.setCanonical(false);
//        dumperOptions.setDefaultScalarStyle(ScalarStyle.PLAIN);
//        dumperOptions.setDefaultFlowStyle(FlowStyle.BLOCK);
//        dumperOptions.setPrettyFlow(true);
//
//        CustomRepresent represent = new CustomRepresent();
//        represent.setPropertyUtils(new CustomPropertyUtils());
//        represent.getPropertyUtils().setBeanAccess(BeanAccess.FIELD);
//        return new Yaml(represent, dumperOptions);
//    }

//    private static class CustomRepresent extends Representer {
//
//        @Override
//        protected NodeTuple representJavaBeanProperty(Object javaBean, Property property, Object propertyValue, Tag customTag) {
//            // if value of property is null, ignore it.
//            if (propertyValue == null) {
//                return null;
//            }
//
//            // ignore specified property
//            if (property.getName().equals("exampleSetFlag") || property.getName().equals("specVersion")) {
//                return null;
//            }
//
//            return super.representJavaBeanProperty(javaBean, property, propertyValue, customTag);
//        }
//
//        @Override
//        protected MappingNode representJavaBean(Set<Property> properties, Object javaBean) {
//            if (!classTags.containsKey(javaBean.getClass())) {
//                addClassTag(javaBean.getClass(), Tag.MAP);
//            }
//
//            return super.representJavaBean(properties, javaBean);
//        }
//    }

    private static class CustomPropertyUtils extends PropertyUtils {

        @Override
        protected Set<Property> createPropertySet(Class<? extends Object> type, BeanAccess bAccess) {
            // Note: 保证属性是有序的
            Set<Property> properties = new LinkedHashSet<>();
            Collection<Property> props = getPropertiesMap(type, bAccess).values();
            for (Property property : props) {
                if (property.isReadable() && (isAllowReadOnlyProperties() || property.isWritable())) {
                    properties.add(property);
                }
            }
            return properties;
        }

    }

    /**
     * 按照字段名称过滤的策略
     */
    private static class NamedExclusionStrategy implements ExclusionStrategy {

        private final Set<String> skipFields;

        public NamedExclusionStrategy(Set<String> skipFields) {
            this.skipFields = skipFields;
        }

        @Override
        public boolean shouldSkipField(FieldAttributes fieldAttributes) {
            return skipFields.contains(fieldAttributes.getName());
        }

        @Override
        public boolean shouldSkipClass(Class<?> aClass) {
            return false;
        }
    }
}
