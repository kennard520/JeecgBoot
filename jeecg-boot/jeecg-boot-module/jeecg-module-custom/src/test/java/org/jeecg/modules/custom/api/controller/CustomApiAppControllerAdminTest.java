package org.jeecg.modules.custom.api.controller;

import org.jeecg.modules.custom.ai.service.CustomAgentAccessService;
import org.jeecg.modules.custom.api.service.ICustomApiAppService;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CustomApiAppControllerAdminTest {

    @Test
    void managementEndpointsEnforceSuperAdminOnTheServer() throws Exception {
        CustomAgentAccessService accessService = mock(CustomAgentAccessService.class);
        ICustomApiAppService appService = mock(ICustomApiAppService.class);
        when(appService.appKeyExists("client-a", null)).thenReturn(false);
        CustomApiAppController controller = new CustomApiAppController();
        setField(controller, "accessService", accessService);
        setField(controller, "appService", appService);

        controller.checkAppKey("client-a", null);

        verify(accessService).requireSuperAdmin();
    }

    @Test
    void deleteDelegatesToTransactionalAppCleanup() throws Exception {
        CustomAgentAccessService accessService = mock(CustomAgentAccessService.class);
        ICustomApiAppService appService = mock(ICustomApiAppService.class);
        CustomApiAppController controller = new CustomApiAppController();
        setField(controller, "accessService", accessService);
        setField(controller, "appService", appService);

        controller.delete("7");
        controller.deleteBatch("8,9");

        verify(accessService, org.mockito.Mockito.times(2)).requireSuperAdmin();
        verify(appService).deleteApp(7L);
        verify(appService).deleteApps(java.util.List.of(8L, 9L));
    }

    private void setField(Object target, String name, Object value) throws Exception {
        Field field = target.getClass().getDeclaredField(name);
        field.setAccessible(true);
        field.set(target, value);
    }
}
