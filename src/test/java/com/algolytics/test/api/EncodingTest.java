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
import org.springframework.test.context.junit4.SpringRunner;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class EncodingTest extends HTTPBaseTest{

    @LocalServerPort
    int serverPort;

    private RequestSpecification spec;

    @Before
    public void setUp(){
        spec = new RequestSpecBuilder().build();
    }

    private void simpleTestToCheckIfJavaHandlesGivenCharactersInGivenEncoding(Charset messageCharset){
        String polishCharacters = new MyResponse(PL_SMALL).getFieldPl();
        byte[] bytes = polishCharacters.getBytes(messageCharset);
        String result = new String(bytes, messageCharset);
        Assert.assertEquals(polishCharacters, result);
    }

    private void performEncodingTest(Charset messageCharset) throws Exception {
        simpleTestToCheckIfJavaHandlesGivenCharactersInGivenEncoding(messageCharset);

        byte[] request = json(new MyRequest(PL_SMALL)).getBytes(messageCharset);
        MyResponse expectedResponse = new MyResponse(PL_SMALL);

        String contentType = ContentType.JSON.withCharset(messageCharset);
        ResponseOptions response = RestAssured.given(spec)
                .port(serverPort)
                .contentType(ContentType.JSON.withCharset(messageCharset))
                .body(request)
                .post(MyRequestMapping.ENCODING_METHOD);
        Assert.assertEquals(HttpStatus.OK.value(), response.getStatusCode());
        Assert.assertTrue(ContentType.fromContentType(response.getContentType()).equals(ContentType.fromContentType(contentType)));
        MyResponse myResponse = object(new String(response.getBody().asByteArray(), messageCharset), MyResponse.class);
        Assert.assertEquals(expectedResponse.getFieldPl(), myResponse.getFieldPl());
        Assert.assertEquals(expectedResponse.getFieldEn(), myResponse.getFieldEn());
    }

    @Test
    public void testUTF_8() throws Exception {
        performEncodingTest(StandardCharsets.UTF_8);
    }

    @Test
    public void testUTF_16() throws Exception {
        performEncodingTest(StandardCharsets.UTF_16);
    }

    @Test
    public void testUTF_16BE() throws Exception {
        performEncodingTest(StandardCharsets.UTF_16BE);
    }

    @Test
    public void testUTF_16LE() throws Exception {
        performEncodingTest(StandardCharsets.UTF_16LE);
    }

    @Test
    public void testWindows_1250() throws Exception {
        performEncodingTest(Charset.forName("windows-1250"));
    }

    @Test
    public void testISO_8859_2() throws Exception {
        performEncodingTest(Charset.forName("ISO_8859-2"));
    }

    @Test
    public void testISO_8859_13() throws Exception {
        performEncodingTest(Charset.forName("ISO_8859-13"));
    }
}
