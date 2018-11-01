package com.algolytics.test.api;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
public class TestController {

    @RequestMapping(method = RequestMethod.POST, value = MyRequestMapping.ENCODING_METHOD)
    public MyResponse encoding(@RequestBody MyRequest request){
        String field = request.getField();
        System.out.println(field);
        return new MyResponse(request.getField());
    }

    @RequestMapping(method = RequestMethod.POST, value = MyRequestMapping.ERROR_METHOD)
    public MyResponse error(@RequestBody MyRequest request){
        throw new MyException();
    }

    @ResponseStatus(value = HttpStatus.BAD_REQUEST, reason="Bad Request")
    public class MyException extends RuntimeException{}
}
