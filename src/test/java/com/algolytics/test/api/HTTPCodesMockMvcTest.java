package com.algolytics.test.api;

import com.algolytics.test.Application;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
@AutoConfigureMockMvc
public class HTTPCodesMockMvcTest extends HTTPBaseTest{

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testMockMvcOk() throws Exception {
        MyRequest request = new MyRequest(PL_SMALL);
        MyResponse response = new MyResponse(PL_SMALL);
        mockMvc.perform(MockMvcRequestBuilders.post(MyRequestMapping.ENCODING_METHOD)
                .content(json(request))
                .contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(json(response)))
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(MockMvcResultMatchers.jsonPath("$.fieldPl").value(response.getFieldPl()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.fieldEn").value(response.getFieldEn()));
    }

    @Test
    public void testMockMvcError() throws Exception {
        MyRequest request = new MyRequest(PL_SMALL);
        mockMvc.perform(MockMvcRequestBuilders.post(MyRequestMapping.ERROR_METHOD)
                .content(json(request))
                .contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(HttpStatus.BAD_REQUEST.value()))
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_UTF8));
    }

    @Test
    public void testMockMvcFailsOnUnsupportedMediaType() throws Exception {
        MyRequest request = new MyRequest(PL_SMALL);
        mockMvc.perform(MockMvcRequestBuilders.post(MyRequestMapping.ENCODING_METHOD)
                .content(json(request))
                .contentType(MediaType.TEXT_PLAIN))
                .andExpect(MockMvcResultMatchers.status().isUnsupportedMediaType())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(HttpStatus.UNSUPPORTED_MEDIA_TYPE.value()))
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_UTF8));
    }
}
