package com.coder.routingapi.controller;

import com.coder.routingapi.dto.RegisterInstanceDTO;
import com.coder.routingapi.service.LoadBalanceService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;


import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = LoadBalanceController.class)
@AutoConfigureMockMvc
public class LoadBalanceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private final String LOAD_BALANCES_API_ENDPOINT = "/load-balances/register";

    @MockitoBean
    private LoadBalanceService loadBalanceService;

    @Test
    void sendBackJson_success() throws Exception {
        RegisterInstanceDTO req = new RegisterInstanceDTO("8081", "localhost", 8081);

        doNothing().when(loadBalanceService).registerInstance(any());

        mockMvc.perform(post(LOAD_BALANCES_API_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/plain;charset=UTF-8"))
                .andDo(print());

        verify(loadBalanceService, times(1)).registerInstance(any());
    }
}
