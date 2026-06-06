package com.microfinance.los.exception;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = GlobalExceptionHandlerTest.DummyController.class)
@AutoConfigureMockMvc(addFilters = false) // Bypass spring security for exception testing
@Import(GlobalExceptionHandler.class)
public class GlobalExceptionHandlerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testHandleIllegalArgumentException() throws Exception {
        mockMvc.perform(get("/test/bad-request")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Invalid argument provided"));
    }

    @Test
    public void testHandleGlobalException() throws Exception {
        mockMvc.perform(get("/test/internal-error")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status").value(500))
                .andExpect(jsonPath("$.message").value("Unexpected failure"));
    }

    @RestController
    public static class DummyController {
        @GetMapping("/test/bad-request")
        public void throwIllegalArgument() {
            throw new IllegalArgumentException("Invalid argument provided");
        }

        @GetMapping("/test/internal-error")
        public void throwException() throws Exception {
            throw new Exception("Unexpected failure");
        }
    }
}
