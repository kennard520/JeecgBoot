package org.jeecg.modules.custom.ai.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.jeecg.common.exception.JeecgBootException;
import org.jeecg.modules.custom.ai.vo.CurrentCustomer;
import org.jeecg.modules.custom.cit.entity.DecHead;
import org.jeecg.modules.custom.cit.entity.DecList;
import org.jeecg.modules.custom.cit.mapper.DecHeadMapper;
import org.jeecg.modules.custom.cit.mapper.DecListMapper;
import org.jeecg.modules.custom.task.entity.Document;
import org.jeecg.modules.custom.task.mapper.DocumentMapper;
import org.springframework.stereotype.Service;

@Service
public class CustomCustomerDataScopeService {

    private final CustomAgentAccessService accessService;
    private final DocumentMapper documentMapper;
    private final DecHeadMapper decHeadMapper;
    private final DecListMapper decListMapper;

    public CustomCustomerDataScopeService(CustomAgentAccessService accessService,
                                          DocumentMapper documentMapper,
                                          DecHeadMapper decHeadMapper,
                                          DecListMapper decListMapper) {
        this.accessService = accessService;
        this.documentMapper = documentMapper;
        this.decHeadMapper = decHeadMapper;
        this.decListMapper = decListMapper;
    }

    public CurrentCustomer currentCustomer() {
        return accessService.requireCurrentCustomer();
    }

    public void applyDocumentScope(QueryWrapper<Document> wrapper) {
        CurrentCustomer current = currentCustomer();
        if (!current.superAdmin()) {
            wrapper.eq("CUSTOMER_CODE", current.customerCode());
        }
    }

    public void applyDecHeadScope(QueryWrapper<DecHead> wrapper) {
        CurrentCustomer current = currentCustomer();
        if (!current.superAdmin()) {
            wrapper.eq("CUSTOMER_CODE", current.customerCode());
        }
    }

    public void applyDecListScope(QueryWrapper<DecList> wrapper) {
        CurrentCustomer current = currentCustomer();
        if (!current.superAdmin()) {
            wrapper.exists(
                    "SELECT 1 FROM \"DEC_HEAD\" H "
                            + "WHERE H.\"ID\" = \"DEC_LIST\".\"DEC_HEAD_ID\" "
                            + "AND H.\"CUSTOMER_CODE\" = {0}",
                    current.customerCode()
            );
        }
    }

    public Document requireDocument(Long id) {
        Document document = id == null ? null : documentMapper.selectById(id);
        requireOwned(document == null ? null : document.getCustomerCode());
        return document;
    }

    public DecHead requireDecHead(Long id) {
        DecHead head = id == null ? null : decHeadMapper.selectById(id);
        requireOwned(head == null ? null : head.getCustomerCode());
        return head;
    }

    public DecList requireDecList(Long id) {
        DecList row = id == null ? null : decListMapper.selectById(id);
        if (row == null) {
            throw inaccessible();
        }
        requireDecHead(row.getDecHeadId());
        return row;
    }

    public void requireDecHeadAccess(Long decHeadId) {
        requireDecHead(decHeadId);
    }

    public void stampNewDocument(Document document, String agentCode) {
        CurrentCustomer current = currentCustomer();
        document.setCustomerCode(resolveCustomerCode(current, document.getCustomerCode()))
                .setUploaderUserId(resolveUserId(current, document.getUploaderUserId()))
                .setAgentCode(agentCode);
    }

    public void stampNewDecHead(DecHead head) {
        CurrentCustomer current = currentCustomer();
        head.setCustomerCode(resolveCustomerCode(current, head.getCustomerCode()))
                .setUploaderUserId(resolveUserId(current, head.getUploaderUserId()));
    }

    public void preserveDecHeadOwnership(DecHead target, DecHead existing) {
        target.setCustomerCode(existing.getCustomerCode())
                .setUploaderUserId(existing.getUploaderUserId())
                .setSourceTaskId(existing.getSourceTaskId())
                .setSourceType(existing.getSourceType());
    }

    public void requireSuperAdmin() {
        accessService.requireSuperAdmin();
    }

    private void requireOwned(String customerCode) {
        CurrentCustomer current = currentCustomer();
        if (customerCode == null || (!current.superAdmin() && !customerCode.equals(current.customerCode()))) {
            throw inaccessible();
        }
    }

    private String resolveCustomerCode(CurrentCustomer current, String requested) {
        if (!current.superAdmin()) {
            return current.customerCode();
        }
        return requested == null || requested.isBlank() ? "INTERNAL" : requested.trim();
    }

    private String resolveUserId(CurrentCustomer current, String requested) {
        if (!current.superAdmin()) {
            return current.userId();
        }
        return requested == null || requested.isBlank() ? current.userId() : requested.trim();
    }

    private JeecgBootException inaccessible() {
        return new JeecgBootException("数据不存在或无权访问");
    }
}
