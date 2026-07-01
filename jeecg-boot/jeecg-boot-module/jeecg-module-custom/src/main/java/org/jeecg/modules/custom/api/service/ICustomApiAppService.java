package org.jeecg.modules.custom.api.service;

import com.baomidou.mybatisplus.extension.service.IService;
import jakarta.servlet.http.HttpServletRequest;
import org.jeecg.modules.custom.api.entity.CustomApiApp;
import org.jeecg.modules.custom.api.vo.AuthTokenRequest;
import org.jeecg.modules.custom.api.vo.AuthTokenResponse;

public interface ICustomApiAppService extends IService<CustomApiApp> {
    AuthTokenResponse issueToken(AuthTokenRequest request);

    CustomApiApp requireApp(HttpServletRequest request);
}
