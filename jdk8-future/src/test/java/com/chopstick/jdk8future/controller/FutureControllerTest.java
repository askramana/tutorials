package com.chopstick.jdk8future.controller;

import com.chopstick.jdk8future.advise.GenericExceptionHandler;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.core.task.TaskDecorator;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.concurrent.CompletionException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@WebAppConfiguration
@RunWith(SpringRunner.class)
public class FutureControllerTest {

    private MockMvc mvc;

    @Autowired
    private WebApplicationContext webAppContext;

    @SpyBean
    private TaskDecorator taskDecorator;

    @SpyBean
    private GenericExceptionHandler exceptionHandler;

    @Before
    public void initialize() {
        mvc = MockMvcBuilders.webAppContextSetup(webAppContext).build();
    }

    @Test
    public void testGetCustomer() throws Exception {
        long customerId = 1L;
        mvc.perform(get("/customer/" + customerId))
                .andExpect(status().is(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.id").value(customerId))
                .andExpect(jsonPath("$.emailId").isNotEmpty())
                .andExpect(jsonPath("$.phoneNumber").isNotEmpty());

        verify(taskDecorator, times(2)).decorate(any(Runnable.class));
        verify(exceptionHandler, never()).handleUncaughtException(any(), any(), any());
    }

    @Test
    public void testGetCustomer_InvalidId() throws Exception {
        ArgumentCaptor<CompletionException> exceptionCaptor = ArgumentCaptor.forClass(CompletionException.class);

        long customerId = -1L;
        mvc.perform(get("/customer/" + customerId))
                .andExpect(status().is(HttpStatus.INTERNAL_SERVER_ERROR.value()))
                .andExpect(jsonPath("$.err").value("CompletionException"));

        verify(exceptionHandler, times(1)).handleCompletionExceptions(exceptionCaptor.capture());

        Exception exception = exceptionCaptor.getValue();
        assertEquals(IllegalArgumentException.class, exception.getCause().getClass());
        assertTrue(exception.getMessage().contains("Invalid customer id"));
    }
}
