package com.hotel.management.application.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.HashMap;
import java.util.Map;

/**
 * Author: Mohammed Kharma
 */
@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class BadRequestException extends RuntimeException {

    private String resourceName;
    private String fieldName;

    public BadRequestException(String message) {
        super(message);
    }

    public BadRequestException(String resourceName, String fieldName) {
        super(String.format("%s has wrong %s value'", resourceName, fieldName));
        this.resourceName = resourceName;
        this.fieldName = fieldName;
    }


    public String getResourceName() {
        return resourceName;
    }

    public String getFieldName() {
        return fieldName;
    }

    private static Map<String, Object> getParameters(String resourceName, String fieldName) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("message", "error." + fieldName);
        parameters.put("params", resourceName);
        return parameters;
    }
}
