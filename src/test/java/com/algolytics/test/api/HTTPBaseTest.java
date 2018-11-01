package com.algolytics.test.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

abstract class HTTPBaseTest {

    final static String PL_SMALL = "ąćęłńóśźż";

    String json(Object o) throws JsonProcessingException {
        return new ObjectMapper().writeValueAsString(o);
    }

    <T> T object(String s, Class<T> clazz) throws IOException {
        return new ObjectMapper().readValue(s, clazz);
    }
}
