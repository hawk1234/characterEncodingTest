package com.algolytics.test.api;

public class MyRequest {

    private String field;

    private MyRequest(){}

    public MyRequest(String request){
        field = request;
    }

    public String getField() {
        return field;
    }
}
