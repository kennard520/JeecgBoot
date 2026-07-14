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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CustomCustomerDataScopeServiceTest {

    private CustomAgentAccessService accessService;
    private DocumentMapper documentMapper;
    private DecHeadMapper decHeadMapper;
    private DecListMapper decListMapper;
    private CustomCustomerDataScopeService service;

    @BeforeEach
    void setUp() {
        accessService = mock(CustomAgentAccessService.class);
        documentMapper = mock(DocumentMapper.class);
        decHeadMapper = mock(DecHeadMapper.class);
        decListMapper = mock(DecListMapper.class);
        service = new CustomCustomerDataScopeService(accessService, documentMapper, decHeadMapper, decListMapper);
    }

    @Test
    void ordinaryUserQueriesAreBoundToTheCurrentCustomer() {
        asCustomer("CUSTOMER-A", "user-a");
        QueryWrapper<Document> documents = new QueryWrapper<>();
        QueryWrapper<DecHead> heads = new QueryWrapper<>();
        QueryWrapper<DecList> lists = new QueryWrapper<>();

        service.applyDocumentScope(documents);
        service.applyDecHeadScope(heads);
        service.applyDecListScope(lists);

        assertThat(documents.getSqlSegment()).containsIgnoringCase("CUSTOMER_CODE");
        assertThat(documents.getParamNameValuePairs()).containsValue("CUSTOMER-A");
        assertThat(heads.getSqlSegment()).containsIgnoringCase("CUSTOMER_CODE");
        assertThat(heads.getParamNameValuePairs()).containsValue("CUSTOMER-A");
        assertThat(lists.getSqlSegment()).containsIgnoringCase("EXISTS").containsIgnoringCase("DEC_HEAD");
        assertThat(lists.getParamNameValuePairs()).containsValue("CUSTOMER-A");
    }

    @Test
    void superAdminQueriesRemainGlobal() {
        when(accessService.requireCurrentCustomer())
                .thenReturn(new CurrentCustomer(null, "admin-id", "admin", true));
        QueryWrapper<Document> documents = new QueryWrapper<>();

        service.applyDocumentScope(documents);

        assertThat(documents.getSqlSegment()).isEmpty();
    }

    @Test
    void crossCustomerDocumentLookupIsRejected() {
        asCustomer("CUSTOMER-A", "user-a");
        when(documentMapper.selectById(7L)).thenReturn(
                new Document().setId(7L).setCustomerCode("CUSTOMER-B")
        );

        assertThatThrownBy(() -> service.requireDocument(7L))
                .isInstanceOf(JeecgBootException.class)
                .hasMessageContaining("无权访问");
    }

    @Test
    void decListOwnershipIsInheritedFromItsHead() {
        asCustomer("CUSTOMER-A", "user-a");
        when(decListMapper.selectById(9L)).thenReturn(new DecList().setId(9L).setDecHeadId(8L));
        when(decHeadMapper.selectById(8L)).thenReturn(new DecHead().setId(8L).setCustomerCode("CUSTOMER-B"));

        assertThatThrownBy(() -> service.requireDecList(9L))
                .isInstanceOf(JeecgBootException.class)
                .hasMessageContaining("无权访问");
    }

    @Test
    void newOwnedRowsAreStampedFromTheAuthenticatedCustomer() {
        asCustomer("CUSTOMER-A", "user-a");
        Document document = new Document();
        DecHead head = new DecHead();

        service.stampNewDocument(document, "CUSTOMS");
        service.stampNewDecHead(head);

        assertThat(document.getCustomerCode()).isEqualTo("CUSTOMER-A");
        assertThat(document.getUploaderUserId()).isEqualTo("user-a");
        assertThat(document.getAgentCode()).isEqualTo("CUSTOMS");
        assertThat(head.getCustomerCode()).isEqualTo("CUSTOMER-A");
        assertThat(head.getUploaderUserId()).isEqualTo("user-a");
    }

    private void asCustomer(String customerCode, String userId) {
        when(accessService.requireCurrentCustomer())
                .thenReturn(new CurrentCustomer(customerCode, userId, "alice", false));
    }
}
