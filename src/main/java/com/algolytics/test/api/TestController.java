package com.algolytics.test.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.CharStreams;
import io.restassured.http.ContentType;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

@RestController
public class TestController {

    @RequestMapping(method = RequestMethod.POST, value = MyRequestMapping.ENCODING_METHOD)
    public MyResponse encoding(@RequestBody MyRequest request){
        String field = request.getField();
        System.out.println(field);
        return new MyResponse(request.getField());
    }

    @RequestMapping(method = RequestMethod.POST, value = MyRequestMapping.MY_ENCODING_HANDLING_METHOD)
    public void myEncodingHandling(HttpServletRequest request, HttpServletResponse response, @RequestHeader HttpHeaders headers){
        try {
            //extract encoding for header
            MediaType mediaType = headers.getContentType();
            Charset contentTypeCharset = mediaType == null ? StandardCharsets.UTF_8 : mediaType.getCharset();
            if(contentTypeCharset == null){
                contentTypeCharset = StandardCharsets.UTF_8;
            }

            //check request body encoding
            String bodyEncoding = request.getCharacterEncoding();
            if(bodyEncoding == null){
                //assume utf-8 if not provided
                bodyEncoding = "UTF-8";
            }
            System.out.println("Encoding used to read request body "+bodyEncoding);
            System.out.println("Encoding passed in content type header "+contentTypeCharset.name());

            //set proper encoding to read message body
            request.setCharacterEncoding(contentTypeCharset.name());
            String message = CharStreams.toString(request.getReader());

            //do intended business logic
            MyRequest myRequest = new ObjectMapper().readValue(message, MyRequest.class);
            MyResponse myResponse = encoding(myRequest);

            //write response
            String responseMessage = new ObjectMapper().writeValueAsString(myResponse);
            response.setContentType(ContentType.JSON.withCharset(contentTypeCharset));
            response.getWriter().append(responseMessage);
            response.getWriter().flush();
            response.getWriter().close();
            response.setStatus(HttpStatus.OK.value());
        } catch (IOException e) {
            e.printStackTrace();
            throw new UnsupportedOperationException("TODO: Error handling", e);
        }
    }

    @RequestMapping(method = RequestMethod.POST, value = MyRequestMapping.ERROR_METHOD)
    public MyResponse error(@RequestBody MyRequest request){
        throw new MyException();
    }

    @ResponseStatus(value = HttpStatus.BAD_REQUEST, reason="Bad Request")
    public class MyException extends RuntimeException{}
}
