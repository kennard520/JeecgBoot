package org.jeecg.modules.custom.api.exception;

import org.jeecg.common.api.vo.Result;
import org.jeecg.modules.custom.api.controller.CustomApiAuthController;
import org.jeecg.modules.custom.api.service.ICustomApiAppService;
import org.junit.jupiter.api.Test;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class CustomApiExceptionHandlerTest {

    @Test
    void moduleHandlerRunsBeforeTheGlobalCatchAllHandler() {
        Order order = CustomApiExceptionHandler.class.getAnnotation(Order.class);

        assertThat(order).isNotNull();
        assertThat(order.value()).isEqualTo(Ordered.HIGHEST_PRECEDENCE);
    }

    @Test
    void hiddenCrossCustomerResourceReturnsHttp404() {
        CustomApiExceptionHandler handler = new CustomApiExceptionHandler();

        ResponseEntity<Result<?>> response = handler.notFound(
                new CustomApiNotFoundException("task not found"));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
        assertThat(response.getBody().getMessage()).isEqualTo("task not found");
    }

    @Test
    void missingApiCredentialReturnsHttp401() {
        CustomApiExceptionHandler handler = new CustomApiExceptionHandler();

        ResponseEntity<Result<?>> response = handler.unauthorized(
                new CustomApiUnauthorizedException("missing X-Custom-Api-Token"));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getCode()).isEqualTo(HttpStatus.UNAUTHORIZED.value());
        assertThat(response.getBody().getMessage()).isEqualTo("missing X-Custom-Api-Token");
    }

    @Test
    void malformedJsonReturnsCleanHttp400() throws Exception {
        CustomApiAuthController controller = new CustomApiAuthController();
        ReflectionTestUtils.setField(controller, "appService", mock(ICustomApiAppService.class));
        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new CustomApiExceptionHandler())
                .build();

        mockMvc.perform(post("/custom/api/auth/token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{appKey:"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(HttpStatus.BAD_REQUEST.value()))
                .andExpect(jsonPath("$.message").value("malformed JSON request"))
                .andExpect(jsonPath("$.trace").doesNotExist())
                .andExpect(jsonPath("$.exception").doesNotExist());
    }
}
