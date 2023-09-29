package com.fashion.utils;

import com.fashion.annotation.RequestPart;
import com.fashion.dto.base.FileItem;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.CharUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.modelmapper.ModelMapper;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;
import java.io.IOException;
import java.lang.reflect.*;
import java.math.BigDecimal;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@UtilityClass
public class ObjectUtil {

    private final ModelMapper mapper = new ModelMapper();
    private ObjectMapper objectMapper;

    @NonNull
    @SneakyThrows
    public <T> T getRequestBody(HttpServletRequest request, Parameter x) {
        Class<T> type = (Class<T>) x.getType();
        if (List.class.isAssignableFrom(type)) {
            Class<?> aClass = getGenericType(x);
            return (T) getBodyAsListCollection(request, aClass);
        }
        return mappingValueToField(type, field -> {
            if (List.class.equals(field.getType())) {
                String[] parameterValues = request.getParameterValues(field.getName());
                Class<?> genericType = getGenericOfField(field);
                if (Objects.isNull(parameterValues)) return new ArrayList<>();
                return Arrays.stream(parameterValues)
                        .map(v -> convertValue(v, genericType))
                        .collect(Collectors.toList());
            }

            if (FileItem.class.equals(field.getType())) {
                return getPart(request, field.getName());
            }
            return request.getParameter(field.getName());
        });
    }

    @SneakyThrows
    public <T> List<T> getBodyAsListCollection(HttpServletRequest request, Class<T> type) {
        int maxLength = 0;
        Map<String, String[]> valueBodyMap = new HashMap<>();
        for (Field declaredField : type.getDeclaredFields()) {
            String[] parameterValues = request.getParameterValues(declaredField.getName());
            valueBodyMap.put(declaredField.getName(), parameterValues);
            if (parameterValues.length > maxLength) {
                maxLength = parameterValues.length;
            }
        }
        List<T> list = new ArrayList<>();
        for (int i = 0; i < maxLength; i++) {
            final int _index = i;
            T instance = mappingValueToField(type, field -> {
                String[] values = valueBodyMap.get(field.getName());
                if (Objects.nonNull(values)) {
                    return values[_index];
                }
                return null;
            });
            list.add(instance);
        }
        return list;
    }

    @SneakyThrows
    private <T> T mappingValueToField(Class<T> type, Function<Field, Object> valueSelector) {
        Constructor<T> constructor = type.getConstructor();
        T instance = constructor.newInstance();
        for (Field field : type.getDeclaredFields()) {
            field.setAccessible(true);
            Object value;

            Object valueInBody = valueSelector.apply(field);
            if (valueInBody instanceof Collection) {
                value = valueInBody;
            } else if (valueInBody instanceof Part) {
                value = mappingRequestPart((Part) valueInBody, false);
            } else {
                value = convertValue(valueInBody, field.getType());
            }
            field.set(instance, value);
        }
        return instance;
    }

    public <T> T map(Object source, Class<T> type) {
        return mapper.map(source, type);
    }

    public <T> List<T> mapList(List<?> source, Class<T> type) {
        if (CollectionUtils.isEmpty(source)) return new ArrayList<>();
        return source.stream()
                .map(x -> map(x, type))
                .collect(Collectors.toList());
    }

    public Object convertValue(Object value, Class<?> type) {
        if (Objects.isNull(value) || value.toString().isBlank()) return null;
        String v = value.toString();
        if (String.class.equals(type)) return v;
        if (Short.class.equals(type)) NumberUtils.toShort(v);
        if (Character.class.equals(type)) return CharUtils.toChar(v);
        if (Integer.class.equals(type)) return NumberUtils.toInt(v);
        if (Long.class.equals(type)) return NumberUtils.toLong(v);
        if (Double.class.equals(type)) return NumberUtils.toDouble(v);
        if (Boolean.class.equals(type)) return BooleanUtils.toBoolean(v);
        if (BigDecimal.class.equals(type)) return new BigDecimal(v);
        if (type.isEnum()) return Enum.valueOf((Class<? extends Enum>) type, v);
        return v;
    }

    public ObjectMapper getObjectMapper() {
        if (Objects.isNull(objectMapper)) {
            objectMapper = new ObjectMapper();
            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        }
        return objectMapper;
    }

    @SneakyThrows
    public String toJSON(Object obj) {
        return getObjectMapper().writeValueAsString(obj);
    }

    @SneakyThrows
    public <T> T jsonToObj(String json, Class<T> type) {
        return getObjectMapper().readValue(json, type);
    }

    public FileItem mappingRequestPart(HttpServletRequest req, String name, boolean multiple) {
        Part part = getPart(req, name);
        return mappingRequestPart(part, multiple);
    }

    @SneakyThrows(IOException.class)
    public FileItem mappingRequestPart(Part part, boolean multiple) {
        return FileItem.builder()
                .contentType(part.getContentType())
                .bytes(part.getInputStream().readAllBytes())
                .name(part.getSubmittedFileName())
                .build();
    }

    public FileItem mappingRequestPart(HttpServletRequest req, RequestPart annotation) {
        return mappingRequestPart(req, annotation.value(), annotation.multiple());
    }

    @SneakyThrows(value = {IOException.class, ServletException.class})
    private Part getPart(HttpServletRequest request, String partName) {
        return request.getPart(partName);
    }

    public Class<?> getGenericType(Parameter parameter) {
        ParameterizedType parameterizedType = (ParameterizedType) parameter.getParameterizedType();
        return getGenericType(parameterizedType);
    }

    @SneakyThrows
    public Class<?> getGenericType(ParameterizedType parameterizedType) {
        Type actualTypeArgument = parameterizedType.getActualTypeArguments()[0];
        return Class.forName(actualTypeArgument.getTypeName());
    }

    public Class<?> getGenericOfField(Field field) {
        return getGenericType((ParameterizedType) field.getGenericType());
    }
}
