package com.algolytics.test.api;

public class MyResponse {

    private String fieldPl;
    private String fieldEn;

    private MyResponse(){}

    public MyResponse(String addToResponse){
        fieldPl = "ĄĆĘŁŃÓŚŹŻ"+addToResponse;
        fieldEn = "ACELNOSZZ"+addToResponse;
    }

    public String getFieldPl() {
        return fieldPl;
    }

    public String getFieldEn() {
        return fieldEn;
    }
}
