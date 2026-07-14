package org.jeecg.modules.custom.ai.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.exception.JeecgBootException;
import org.jeecg.modules.custom.ai.entity.CustomAiAgent;
import org.jeecg.modules.custom.ai.entity.CustomApiAppAgent;
import org.jeecg.modules.custom.ai.entity.CustomCustomer;
import org.jeecg.modules.custom.ai.entity.CustomCustomerUser;
import org.jeecg.modules.custom.ai.entity.CustomUserAgent;
import org.jeecg.modules.custom.ai.service.CustomAgentAccessService;
import org.jeecg.modules.custom.ai.service.ICustomAiAgentService;
import org.jeecg.modules.custom.ai.service.ICustomApiAppAgentService;
import org.jeecg.modules.custom.ai.service.ICustomCustomerService;
import org.jeecg.modules.custom.ai.service.ICustomCustomerUserService;
import org.jeecg.modules.custom.ai.service.ICustomUserAgentService;
import org.jeecg.modules.custom.ai.vo.AgentMineResponse;
import org.jeecg.modules.custom.ai.vo.ApiAppAgentGrantRequest;
import org.jeecg.modules.custom.ai.vo.UserAgentGrantRequest;
import org.jeecg.modules.custom.ai.vo.UserAgentGrantListItem;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

@Tag(name = "Customs AI customer and agent grants")
@RestController
@RequestMapping("/custom/ai")
public class CustomAgentController {
    private final CustomAgentAccessService accessService;
    private final ICustomAiAgentService agentService;
    private final ICustomCustomerService customerService;
    private final ICustomCustomerUserService customerUserService;
    private final ICustomUserAgentService userAgentService;
    private final ICustomApiAppAgentService appAgentService;

    public CustomAgentController(CustomAgentAccessService accessService,
                                 ICustomAiAgentService agentService,
                                 ICustomCustomerService customerService,
                                 ICustomCustomerUserService customerUserService,
                                 ICustomUserAgentService userAgentService,
                                 ICustomApiAppAgentService appAgentService) {
        this.accessService = accessService;
        this.agentService = agentService;
        this.customerService = customerService;
        this.customerUserService = customerUserService;
        this.userAgentService = userAgentService;
        this.appAgentService = appAgentService;
    }

    @Operation(summary = "List agents granted to current user")
    @GetMapping("/agents/mine")
    public Result<AgentMineResponse> mine() {
        return Result.OK(new AgentMineResponse()
                .setCustomer(accessService.requireCurrentCustomer())
                .setAgents(accessService.listCurrentUserAgents()));
    }

    @GetMapping({"/admin/customers", "/agents/customers"})
    public Result<List<CustomCustomer>> customers() {
        accessService.requireSuperAdmin();
        return Result.OK(customerService.list());
    }

    @PutMapping("/admin/customers")
    public Result<CustomCustomer> saveCustomer(@RequestBody CustomCustomer customer) {
        accessService.requireSuperAdmin();
        if (customer == null || blank(customer.getCustomerCode()) || blank(customer.getCustomerName())) {
            throw new JeecgBootException("customerCode 和 customerName 不能为空");
        }
        LocalDateTime now = LocalDateTime.now();
        CustomCustomer existing = customerService.getById(customer.getCustomerCode());
        customer.setEnabled(customer.getEnabled() == null ? 1 : customer.getEnabled()).setUpdatedAt(now);
        if (existing == null) {
            customer.setCreatedAt(now);
            customerService.save(customer);
        } else {
            customerService.updateById(customer);
        }
        return Result.OK(customer);
    }

    @DeleteMapping("/admin/customers")
    public Result<?> deleteCustomer(@RequestParam String customerCode) {
        accessService.requireSuperAdmin();
        if (blank(customerCode)) {
            throw new JeecgBootException("customerCode 不能为空");
        }
        CustomCustomer customer = customerService.getById(customerCode.trim());
        if (customer != null) {
            customer.setEnabled(0).setUpdatedAt(LocalDateTime.now());
            customerService.updateById(customer);
        }
        return Result.OK("删除成功");
    }

    @GetMapping({"/admin/agents", "/agents/list"})
    public Result<List<CustomAiAgent>> agents() {
        accessService.requireSuperAdmin();
        return Result.OK(agentService.list());
    }

    @PutMapping("/admin/agents")
    public Result<CustomAiAgent> saveAgent(@RequestBody CustomAiAgent agent) {
        accessService.requireSuperAdmin();
        if (agent == null || blank(agent.getAgentCode()) || blank(agent.getAgentName())) {
            throw new JeecgBootException("agentCode 和 agentName 不能为空");
        }
        LocalDateTime now = LocalDateTime.now();
        CustomAiAgent existing = agentService.getById(agent.getAgentCode());
        agent.setEnabled(agent.getEnabled() == null ? 1 : agent.getEnabled()).setUpdatedAt(now);
        if (existing == null) {
            agent.setCreatedAt(now);
            agentService.save(agent);
        } else {
            agentService.updateById(agent);
        }
        return Result.OK(agent);
    }

    @DeleteMapping("/admin/agents")
    public Result<?> deleteAgent(@RequestParam String agentCode) {
        accessService.requireSuperAdmin();
        if (blank(agentCode)) {
            throw new JeecgBootException("agentCode 不能为空");
        }
        CustomAiAgent agent = agentService.getById(agentCode.trim());
        if (agent != null) {
            agent.setEnabled(0).setUpdatedAt(LocalDateTime.now());
            agentService.updateById(agent);
        }
        return Result.OK("删除成功");
    }

    @PutMapping("/admin/customer-users")
    public Result<CustomCustomerUser> saveCustomerUser(@RequestBody CustomCustomerUser relation) {
        return Result.OK(accessService.replaceCustomerUser(relation));
    }

    @DeleteMapping("/admin/customer-users")
    public Result<?> deleteCustomerUser(@RequestParam String userId) {
        accessService.requireSuperAdmin();
        customerUserService.remove(new LambdaQueryWrapper<CustomCustomerUser>().eq(CustomCustomerUser::getUserId, userId));
        userAgentService.remove(new LambdaQueryWrapper<CustomUserAgent>().eq(CustomUserAgent::getUserId, userId));
        return Result.OK("删除成功");
    }

    @GetMapping({"/admin/user-grants", "/agents/grants/list"})
    public Result<List<UserAgentGrantListItem>> listUserGrants(
            @RequestParam(required = false) String customerCode,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String agentCode) {
        return Result.OK(accessService.listUserGrants(customerCode, username, agentCode));
    }

    @GetMapping("/admin/user-grants/{userId}")
    public Result<List<CustomUserAgent>> userGrantsByUser(@PathVariable String userId) {
        accessService.requireSuperAdmin();
        return Result.OK(userAgentService.list(new LambdaQueryWrapper<CustomUserAgent>().eq(CustomUserAgent::getUserId, userId)));
    }

    @PutMapping({"/admin/user-grants", "/agents/grants/save"})
    public Result<List<CustomUserAgent>> saveUserGrants(@RequestBody UserAgentGrantRequest request) {
        return Result.OK(accessService.replaceUserAgents(request));
    }

    @DeleteMapping({"/admin/user-grants", "/agents/grants/delete"})
    public Result<?> deleteUserGrants(@RequestParam String userId,
                                      @RequestParam String customerCode) {
        accessService.deleteUserAgents(customerCode, userId);
        return Result.OK("删除成功");
    }

    @GetMapping("/admin/app-grants")
    public Result<List<CustomApiAppAgent>> listAppGrants(@RequestParam Long appId) {
        accessService.requireSuperAdmin();
        return Result.OK(appAgentService.list(new LambdaQueryWrapper<CustomApiAppAgent>().eq(CustomApiAppAgent::getAppId, appId)));
    }

    @GetMapping("/admin/app-grants/{appId}")
    public Result<List<CustomApiAppAgent>> appGrantsById(@PathVariable Long appId) {
        return listAppGrants(appId);
    }

    @PutMapping("/admin/app-grants")
    public Result<List<CustomApiAppAgent>> saveAppGrants(@RequestBody ApiAppAgentGrantRequest request) {
        return Result.OK(accessService.replaceApiAppAgents(request));
    }

    @DeleteMapping("/admin/app-grants")
    public Result<?> deleteAppGrants(@RequestParam Long appId) {
        accessService.deleteApiAppAgents(appId);
        return Result.OK("删除成功");
    }

    private boolean blank(String value) {
        return value == null || value.isBlank();
    }
}
