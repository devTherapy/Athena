package co.retaila.athena.common.utils;

import co.retaila.athena.common.exceptions.ServerErrorException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.io.Resource;

import java.io.IOException;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Slf4j
public class ObjectMapperUtils {

    public static String serialize(ObjectMapper objectMapper, Object object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException ex) {
            log.error("Could not serialize object of class: " + object.getClass().getSimpleName());
            throw new ServerErrorException();
        }
    }

    public static <TResult> TResult deserialize(ObjectMapper objectMapper, Resource resource, Class<TResult> resultClass) {
        if (resource == null)
            return null;

        try {
            return objectMapper.readValue(resource.getFile(), resultClass);
        } catch (IOException ex) {
            log.error("Could not deserialize json to class: " + resultClass.getSimpleName());
            throw new ServerErrorException();
        }
    }

    public static <TResult> TResult deserialize(ObjectMapper objectMapper, String resultJson, Class<TResult> resultClass) {
        if (StringUtils.isBlank(resultJson))
            return null;

        try {
            return objectMapper.readValue(resultJson, resultClass);
        } catch (JsonProcessingException ex) {
            log.error("Could not deserialize json to class: " + resultClass.getSimpleName());
            throw new ServerErrorException();
        }
    }

    public static <TResult> TResult deserialize(ObjectMapper objectMapper, String resultJson, TypeReference<TResult> typeReference) {
        if (StringUtils.isBlank(resultJson))
            return null;

        try {
            return objectMapper.readValue(resultJson, typeReference);
        } catch (JsonProcessingException ex) {
            log.error("Could not deserialize json to class: " + typeReference.getType().getTypeName());
            throw new ServerErrorException();
        }
    }

}