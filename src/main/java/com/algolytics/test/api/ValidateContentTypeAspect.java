package com.algolytics.test.api;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.stream.Stream;

@Aspect
@Component
public class ValidateContentTypeAspect {

    @Pointcut("@annotation(com.algolytics.test.api.ValidateContentTypeAspect.ValidateContentType)")
    public void checkedMethods() {}

    @Before("checkedMethods()")
    public void checkBefore(JoinPoint joinPoint) {
        HttpHeaders httpHeaders = validateArguments(joinPoint);
        Charset charset = validateNotNullAndGet(validateNotNullAndGet(httpHeaders.getContentType()).getCharset());
        if(!charset.equals(StandardCharsets.UTF_8)){
            throw new ContentTypeValidationException();
        }
    }

    private HttpHeaders validateArguments(JoinPoint joinPoint){
        Optional<HttpHeaders> httpHeaders = Stream.of(joinPoint.getArgs())
                .filter(arg -> arg != null && HttpHeaders.class.isAssignableFrom(arg.getClass()))
                .map(HttpHeaders.class::cast)
                .findFirst();
        if(!httpHeaders.isPresent()){
            throw new IllegalStateException("Methods annotated with "+ValidateContentType.class+" must declare "+HttpHeaders.class+" argument.");
        }
        return httpHeaders.get();
    }

    private <T> T validateNotNullAndGet(T input){
        if(input == null){
            throw new ContentTypeValidationException();
        }
        return input;
    }

    @Target(value = ElementType.METHOD)
    public @interface ValidateContentType{}

    @ResponseStatus(value = HttpStatus.UNSUPPORTED_MEDIA_TYPE, reason="Application only supports " + MediaType.APPLICATION_JSON_UTF8_VALUE)
    private static class ContentTypeValidationException extends RuntimeException{}
}
