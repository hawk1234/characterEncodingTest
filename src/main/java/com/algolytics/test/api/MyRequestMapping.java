package com.algolytics.test.api;

public class MyRequestMapping {

    private static final String BASE_PATH = "/api/";

    public static final String ENCODING_METHOD = BASE_PATH + "encoding";
    public static final String MY_ENCODING_HANDLING_METHOD = BASE_PATH + "encoding/myHandling";
    public static final String ERROR_METHOD = BASE_PATH + "error";

    private MyRequestMapping(){}
}
