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

    private void performEncodingSupportedTest(Charset messageCharset, String endpoint) throws Exception {
        simpleTestToCheckIfJavaHandlesGivenCharactersInGivenEncoding(messageCharset);

        byte[] request = json(new MyRequest(PL_SMALL)).getBytes(messageCharset);
        MyResponse expectedResponse = new MyResponse(PL_SMALL);

        String contentType = ContentType.JSON.withCharset(messageCharset);
        ResponseOptions response = RestAssured.given(spec)
                .port(serverPort)
                .contentType(contentType)
                .body(request)
                .post(endpoint);
        Assert.assertEquals(HttpStatus.OK.value(), response.getStatusCode());
        Assert.assertTrue(ContentType.fromContentType(response.getContentType()).equals(ContentType.fromContentType(contentType)));
        MyResponse myResponse = object(new String(response.getBody().asByteArray(), messageCharset), MyResponse.class);
        Assert.assertEquals(expectedResponse.getFieldPl(), myResponse.getFieldPl());
        Assert.assertEquals(expectedResponse.getFieldEn(), myResponse.getFieldEn());
    }

    private void performEncodingNotSupportedTest(Charset messageCharset, String endpoint) throws Exception {
        byte[] request = json(new MyRequest(PL_SMALL)).getBytes(messageCharset);

        String contentType = ContentType.JSON.withCharset(messageCharset);
        ResponseOptions response = RestAssured.given(spec)
                .port(serverPort)
                .contentType(contentType)
                .body(request)
                .post(endpoint);
        Assert.assertEquals(HttpStatus.UNSUPPORTED_MEDIA_TYPE.value(), response.getStatusCode());
        Assert.assertTrue(ContentType.fromContentType(response.getContentType()).equals(
                ContentType.fromContentType(ContentType.JSON.withCharset(StandardCharsets.UTF_8))));
    }

    @Test
    public void testMyHandlingUTF_8() throws Exception {
        performEncodingSupportedTest(StandardCharsets.UTF_8, MyRequestMapping.MY_ENCODING_HANDLING_METHOD);
    }

    @Test
    public void testMyHandlingUTF_16() throws Exception {
        performEncodingSupportedTest(StandardCharsets.UTF_16, MyRequestMapping.MY_ENCODING_HANDLING_METHOD);
    }

    @Test
    public void testMyHandlingUTF_16BE() throws Exception {
        performEncodingSupportedTest(StandardCharsets.UTF_16BE, MyRequestMapping.MY_ENCODING_HANDLING_METHOD);
    }

    @Test
    public void testMyHandlingUTF_16LE() throws Exception {
        performEncodingSupportedTest(StandardCharsets.UTF_16LE, MyRequestMapping.MY_ENCODING_HANDLING_METHOD);
    }

    @Test
    public void testMyHandlingWindows_1250() throws Exception {
        performEncodingSupportedTest(Charset.forName("windows-1250"), MyRequestMapping.MY_ENCODING_HANDLING_METHOD);
    }

    @Test
    public void testMyHandlingISO_8859_2() throws Exception {
        performEncodingSupportedTest(Charset.forName("ISO_8859-2"), MyRequestMapping.MY_ENCODING_HANDLING_METHOD);
    }

    @Test
    public void testMyHandlingISO_8859_13() throws Exception {
        performEncodingSupportedTest(Charset.forName("ISO_8859-13"), MyRequestMapping.MY_ENCODING_HANDLING_METHOD);
    }

    @Test
    public void testDefaultHandlingUTF_8() throws Exception {
        performEncodingSupportedTest(StandardCharsets.UTF_8, MyRequestMapping.ENCODING_METHOD);
    }

    @Test
    public void testDefaultHandlingUTF_16() throws Exception {
        performEncodingNotSupportedTest(StandardCharsets.UTF_16, MyRequestMapping.ENCODING_METHOD);
    }

    @Test
    public void testDefaultHandlingUTF_16BE() throws Exception {
        performEncodingNotSupportedTest(StandardCharsets.UTF_16BE, MyRequestMapping.ENCODING_METHOD);
    }

    @Test
    public void testDefaultHandlingUTF_16LE() throws Exception {
        performEncodingNotSupportedTest(StandardCharsets.UTF_16LE, MyRequestMapping.ENCODING_METHOD);
    }

    @Test
    public void testDefaultHandlingWindows_1250() throws Exception {
        performEncodingNotSupportedTest(Charset.forName("windows-1250"), MyRequestMapping.ENCODING_METHOD);
    }

    @Test
    public void testDefaultHandlingISO_8859_2() throws Exception {
        performEncodingNotSupportedTest(Charset.forName("ISO_8859-2"), MyRequestMapping.ENCODING_METHOD);
    }

    @Test
    public void testDefaultHandlingISO_8859_13() throws Exception {
        performEncodingNotSupportedTest(Charset.forName("ISO_8859-13"), MyRequestMapping.ENCODING_METHOD);
    }
}
