package com.coder.routingapi.controller;

import com.coder.routingapi.dto.RegisterInstanceDTO;
import com.coder.routingapi.service.LoadBalanceService;
import com.coder.routingapi.service.RoutingService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = RoutingController.class)
@AutoConfigureMockMvc
public class RoutingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private final String ROUTING_API_ENDPOINT = "/routings";

    @MockitoBean
    private RoutingService routingService;

    @Test
    void sendBackJson_success() throws Exception {
        Map<String, Object> req = new HashMap<>();
        req.put("game", "Mobile Legends");
        req.put("gamerID", "GYUTDTE");
        req.put("points", 20);

        doReturn(req).when(routingService).sendReqJson(any());

        mockMvc.perform(post(ROUTING_API_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.game").value("Mobile Legends"))
                .andExpect(jsonPath("$.gamerID").value("GYUTDTE"))
                .andExpect(jsonPath("$.points").value(20))
                .andDo(print());

        verify(routingService, times(1)).sendReqJson(any());
    }
}
