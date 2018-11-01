package com.algolytics.test.api;

import com.algolytics.test.Application;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class HTTPCodesWebTestClientTest extends HTTPBaseTest{

    @Autowired
    private TestController testController;

    private WebTestClient webTestClient;

    @Before
    public void setUp() {
        webTestClient = WebTestClient.bindToController(testController).build();
    }

        @Test
    public void testWebTestClientOk() throws Exception {
        final MyResponse expectedResponse = new MyResponse(PL_SMALL);
        webTestClient.post()
                    .uri(MyRequestMapping.ENCODING_METHOD)
                    .contentType(MediaType.APPLICATION_JSON_UTF8)
                    .syncBody(new MyRequest(PL_SMALL))
                    .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON_UTF8)
                .expectBody(MyResponse.class).consumeWith(response -> {
            Assert.assertEquals(expectedResponse.getFieldEn(), response.getResponseBody().getFieldEn());
            Assert.assertEquals(expectedResponse.getFieldPl(), response.getResponseBody().getFieldPl());
        });
    }

    @Test
    public void testWebTestClientError() throws Exception {
        webTestClient.post()
                .uri(MyRequestMapping.ERROR_METHOD)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .syncBody(new MyRequest(PL_SMALL))
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.BAD_REQUEST)
                .expectHeader().contentType(MediaType.APPLICATION_JSON_UTF8)
                .expectBody().jsonPath("$.status").isEqualTo(HttpStatus.UNSUPPORTED_MEDIA_TYPE.value());
    }

    @Test
    public void testWebTestClientFailsOnUnsupportedMediaType() throws Exception {
        webTestClient.post()
                    .uri(MyRequestMapping.ENCODING_METHOD)
                    .contentType(MediaType.TEXT_PLAIN)
                    .syncBody(new MyRequest(PL_SMALL))
                    .exchange()
                .expectStatus().isEqualTo(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
                .expectHeader().contentType(MediaType.APPLICATION_JSON_UTF8)
                .expectBody().jsonPath("$.status").isEqualTo(HttpStatus.UNSUPPORTED_MEDIA_TYPE.value());
    }
}
