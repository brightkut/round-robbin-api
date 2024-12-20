package com.coder.simpleapi.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = SimpleController.class)
@AutoConfigureMockMvc
public class SimpleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private final String SIMPLE_API_ENDPOINT = "/simples";

    @Test
    void sendBackJson_success() throws Exception {

        Map<String , Object> req = new HashMap<>();
        req.put("game", "Mobile Legends");
        req.put("gamerID", "GYUTDTE");
        req.put("points", 20);

        mockMvc.perform(post(SIMPLE_API_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.game").value("Mobile Legends"))
                .andExpect(jsonPath("$.gamerID").value("GYUTDTE"))
                .andExpect(jsonPath("$.points").value(20))
                .andDo(print());
    }
}
