package org.jeecg.modules.custom.ai.controller;

import org.jeecg.modules.custom.ai.vo.ApiAppAgentGrantRequest;
import org.jeecg.modules.custom.ai.vo.UserAgentGrantRequest;
import org.junit.jupiter.api.Test;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.lang.reflect.Method;

import static org.assertj.core.api.Assertions.assertThat;

class CustomAgentControllerContractTest {

    @Test
    void exposesCanonicalAndFrontendCompatibleGrantRoutes() throws Exception {
        RequestMapping root = CustomAgentController.class.getAnnotation(RequestMapping.class);
        assertThat(root.value()).containsExactly("/custom/ai");

        assertGet("listUserGrants", new Class<?>[]{String.class, String.class, String.class},
                "/admin/user-grants", "/agents/grants/list");
        assertPut("saveUserGrants", new Class<?>[]{UserAgentGrantRequest.class},
                "/admin/user-grants", "/agents/grants/save");
        assertDelete("deleteUserGrants", new Class<?>[]{String.class, String.class},
                "/admin/user-grants", "/agents/grants/delete");
        assertGet("agents", new Class<?>[0], "/admin/agents", "/agents/list");
        assertGet("customers", new Class<?>[0], "/admin/customers", "/agents/customers");
    }

    @Test
    void exposesListSaveDeleteForEveryAdminGrantResource() throws Exception {
        assertPut("saveCustomer", new Class<?>[]{org.jeecg.modules.custom.ai.entity.CustomCustomer.class},
                "/admin/customers");
        assertDelete("deleteCustomer", new Class<?>[]{String.class}, "/admin/customers");
        assertPut("saveAgent", new Class<?>[]{org.jeecg.modules.custom.ai.entity.CustomAiAgent.class},
                "/admin/agents");
        assertDelete("deleteAgent", new Class<?>[]{String.class}, "/admin/agents");
        assertGet("listAppGrants", new Class<?>[]{Long.class}, "/admin/app-grants");
        assertPut("saveAppGrants", new Class<?>[]{ApiAppAgentGrantRequest.class}, "/admin/app-grants");
        assertDelete("deleteAppGrants", new Class<?>[]{Long.class}, "/admin/app-grants");
    }

    private void assertGet(String methodName, Class<?>[] parameterTypes, String... paths) throws Exception {
        Method method = CustomAgentController.class.getDeclaredMethod(methodName, parameterTypes);
        assertThat(method.getAnnotation(GetMapping.class).value()).containsExactlyInAnyOrder(paths);
    }

    private void assertPut(String methodName, Class<?>[] parameterTypes, String... paths) throws Exception {
        Method method = CustomAgentController.class.getDeclaredMethod(methodName, parameterTypes);
        assertThat(method.getAnnotation(PutMapping.class).value()).containsExactlyInAnyOrder(paths);
    }

    private void assertDelete(String methodName, Class<?>[] parameterTypes, String... paths) throws Exception {
        Method method = CustomAgentController.class.getDeclaredMethod(methodName, parameterTypes);
        assertThat(method.getAnnotation(DeleteMapping.class).value()).containsExactlyInAnyOrder(paths);
    }
}
