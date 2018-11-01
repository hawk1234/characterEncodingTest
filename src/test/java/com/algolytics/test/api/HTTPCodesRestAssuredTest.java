package com.algolytics.test.api;

import com.algolytics.test.Application;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.response.ResponseOptions;
import io.restassured.specification.RequestSpecification;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;

import java.nio.charset.StandardCharsets;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class HTTPCodesRestAssuredTest extends HTTPBaseTest{

    @LocalServerPort
    int serverPort;

    private RequestSpecification spec;

    @Before
    public void setUp(){
        spec = new RequestSpecBuilder().build();
    }

    @Test
    public void testRestAssuredOk() throws Exception {
        MyResponse myResponse = new MyResponse(PL_SMALL);
        ResponseOptions response = RestAssured.given(spec)
                .port(serverPort)
                .contentType(ContentType.JSON.withCharset(StandardCharsets.UTF_8))
                .body(new MyRequest(PL_SMALL))
                .post(MyRequestMapping.ENCODING_METHOD);
        Assert.assertEquals(HttpStatus.OK.value(), response.getStatusCode());
        Assert.assertTrue(response.getContentType().equals(MediaType.APPLICATION_JSON_UTF8_VALUE));
        Assert.assertEquals(myResponse.getFieldEn(), response.getBody().jsonPath().get("fieldEn"));
        Assert.assertEquals(myResponse.getFieldPl(), response.getBody().jsonPath().get("fieldPl"));
    }

    @Test
    public void testRestAssuredError() throws Exception {
        ResponseOptions response = RestAssured.given(spec)
                .port(serverPort)
                .contentType(ContentType.JSON.withCharset(StandardCharsets.UTF_8))
                .body(json(new MyRequest(PL_SMALL)))
                .post(MyRequestMapping.ERROR_METHOD);
        Assert.assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatusCode());
        Assert.assertTrue(response.getContentType().equals(MediaType.APPLICATION_JSON_UTF8_VALUE));
        Assert.assertEquals(Integer.valueOf(HttpStatus.BAD_REQUEST.value()),
                response.getBody().jsonPath().get("status"));
    }

    @Test
    public void testRestAssuredFailsOnUnsupportedMediaType() throws Exception {
        ResponseOptions response = RestAssured.given(spec)
                .port(serverPort)
                .contentType(ContentType.TEXT.withCharset(StandardCharsets.UTF_8))
                .body(json(new MyRequest(PL_SMALL)))
                .post(MyRequestMapping.ENCODING_METHOD);
        Assert.assertEquals(HttpStatus.UNSUPPORTED_MEDIA_TYPE.value(), response.getStatusCode());
        Assert.assertTrue(response.getContentType().equals(MediaType.APPLICATION_JSON_UTF8_VALUE));
        Assert.assertEquals(Integer.valueOf(HttpStatus.UNSUPPORTED_MEDIA_TYPE.value()),
                response.getBody().jsonPath().get("status"));
    }
}
