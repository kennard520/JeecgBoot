-- CIT.sql
-- 根据 saved_resource.html 生成，适用于达梦数据库。
-- 字段注释包含字段中文名、原字段代码、原数据类型、长度、暂存/申报必填状态和原文说明。

CREATE TABLE "CIT_ATTRIBUTES" (
    "ID" BIGINT IDENTITY(1,1) NOT NULL,
    "VERSION" VARCHAR2(5) NOT NULL,
    CONSTRAINT "PK_CIT_ATTRIBUTES" PRIMARY KEY ("ID")
);

COMMENT ON TABLE "CIT_ATTRIBUTES" IS '报文属性attributes';
COMMENT ON COLUMN "CIT_ATTRIBUTES"."ID" IS '主键ID，达梦自增标识列';
COMMENT ON COLUMN "CIT_ATTRIBUTES"."VERSION" IS '接口版本号；字段代码: Version；数据类型: String；长度: 5；暂存必填: 是；申报必填: 是';

CREATE TABLE "DEC_HEAD" (
    "ID" BIGINT IDENTITY(1,1) NOT NULL,
    "AGENT_CODE" VARCHAR2(10) NOT NULL,
    "AGENT_NAME" VARCHAR2(100) NOT NULL,
    "APPR_NO" VARCHAR2(32),
    "BILL_NO" VARCHAR2(32),
    "CONTR_NO" VARCHAR2(32),
    "COP_CODE" VARCHAR2(9) NOT NULL,
    "COP_NAME" VARCHAR2(70) NOT NULL,
    "CUSTOM_MASTER" VARCHAR2(4) NOT NULL,
    "CUT_MODE" VARCHAR2(3),
    "DATA_SOURCE" VARCHAR2(5),
    "DECL_TRN_REL" VARCHAR2(1) NOT NULL,
    "DISTINATE_PORT" VARCHAR2(6) NOT NULL,
    "EDI_ID" VARCHAR2(1) NOT NULL,
    "ENTRY_ID" VARCHAR2(18),
    "ENTRY_TYPE" VARCHAR2(1) NOT NULL,
    "FEE_CURR" VARCHAR2(3),
    "FEE_MARK" VARCHAR2(1),
    "FEE_RATE" DECIMAL(19,5),
    "GROSS_WET" DECIMAL(19,5) NOT NULL,
    "IE_DATE" DATE,
    "IE_FLAG" VARCHAR2(1) NOT NULL,
    "IE_PORT" VARCHAR2(4) NOT NULL,
    "INPUTER_NAME" VARCHAR2(32) NOT NULL,
    "INSUR_CURR" VARCHAR2(3),
    "INSUR_MARK" VARCHAR2(1),
    "INSUR_RATE" DECIMAL(19,5),
    "LICENSE_NO" VARCHAR2(20),
    "MANUAL_NO" VARCHAR2(12),
    "NET_WT" DECIMAL(19,5) NOT NULL,
    "NOTE_S" VARCHAR2(255),
    "OTHER_CURR" VARCHAR2(3),
    "OTHER_MARK" VARCHAR2(1),
    "OTHER_RATE" DECIMAL(19,5),
    "OWNER_CODE" VARCHAR2(18),
    "OWNER_NAME" VARCHAR2(100) NOT NULL,
    "PACK_NO" INTEGER NOT NULL,
    "PARTENER_ID" VARCHAR2(20),
    "P_DATE" DATE,
    "PRE_ENTRY_ID" VARCHAR2(9),
    "RISK" VARCHAR2(10),
    "SEQ_NO" VARCHAR2(18),
    "TGD_NO" VARCHAR2(18),
    "TRADE_COUNTRY" VARCHAR2(3) NOT NULL,
    "TRADE_MODE" VARCHAR2(4) NOT NULL,
    "TRADE_CODE" VARCHAR2(18) NOT NULL,
    "TRAF_MODE" VARCHAR2(1) NOT NULL,
    "TRAF_NAME" VARCHAR2(50),
    "TRADE_NAME" VARCHAR2(100) NOT NULL,
    "TRANS_MODE" VARCHAR2(1) NOT NULL,
    "TYPE" VARCHAR2(6) NOT NULL,
    "TYPIST_NO" VARCHAR2(30) NOT NULL,
    "WRAP_TYPE" VARCHAR2(2) NOT NULL,
    "CHK_SURETY" VARCHAR2(1),
    "BILL_TYPE" VARCHAR2(1),
    "COP_CODE_SCC" VARCHAR2(18),
    "TRADE_CO_SCC" VARCHAR2(18) NOT NULL,
    "AGENT_CODE_SCC" VARCHAR2(18) NOT NULL,
    "OWNER_CODE_SCC" VARCHAR2(18) NOT NULL,
    "PROMISE_ITMES" VARCHAR2(32) NOT NULL,
    "BUSINESS_ITEMS" VARCHAR2(5) DEFAULT '00000',
    "TRADE_AREA_CODE" VARCHAR2(3) NOT NULL,
    "CHECK_FLOW" VARCHAR2(1),
    "TAX_AAMIN_MARK" VARCHAR2(1),
    "MARK_NO" VARCHAR2(400),
    "DESP_PORT_CODE" VARCHAR2(8) NOT NULL,
    "ENTY_PORT_CODE" VARCHAR2(8) NOT NULL,
    "GOODS_PLACE" VARCHAR2(100) NOT NULL,
    "BL_NO" VARCHAR2(50),
    "INSP_ORG_CODE" VARCHAR2(10),
    "SPEC_DECL_FLAG" VARCHAR2(10),
    "PURP_ORG_CODE" VARCHAR2(4),
    "DESP_DATE" DATE,
    "CMPL_DSCHRG_DT" DATE,
    "CORRELATION_REASON_FLAG" VARCHAR2(2),
    "VSA_ORG_CODE" VARCHAR2(10),
    "ORIG_BOX_FLAG" VARCHAR2(1),
    "DECLARE_NAME" VARCHAR2(50) NOT NULL,
    "NO_OTHER_PACK" VARCHAR2(1),
    "ORG_CODE" VARCHAR2(10),
    "OVERSEAS_CONSIGNOR_CODE" VARCHAR2(50),
    "OVERSEAS_CONSIGNOR_CNAME" VARCHAR2(150),
    "OVERSEAS_CONSIGNOR_ENAME" VARCHAR2(100),
    "OVERSEAS_CONSIGNOR_ADDR" VARCHAR2(100),
    "OVERSEAS_CONSIGNEE_CODE" VARCHAR2(50),
    "OVERSEAS_CONSIGNEE_ENAME" VARCHAR2(400),
    "DOMESTIC_CONSIGNEE_ENAME" VARCHAR2(400),
    "CORRELATION_NO" VARCHAR2(500),
    "EDI_REMARK" VARCHAR2(128),
    "EDI_REMARK2" VARCHAR2(128),
    "TRADE_CIQ_CODE" VARCHAR2(10),
    "OWNER_CIQ_CODE" VARCHAR2(10),
    "DECL_CIQ_CODE" VARCHAR2(10),
    CONSTRAINT "PK_DEC_HEAD" PRIMARY KEY ("ID")
);

COMMENT ON TABLE "DEC_HEAD" IS '进口/出口报关单表头 DecHead';
COMMENT ON COLUMN "DEC_HEAD"."ID" IS '主键ID，达梦自增标识列';
COMMENT ON COLUMN "DEC_HEAD"."AGENT_CODE" IS '申报单位代码；字段代码: AgentCode；数据类型: String；长度: 10；暂存必填: 是；申报必填: 是';
COMMENT ON COLUMN "DEC_HEAD"."AGENT_NAME" IS '申报单位名称；字段代码: AgentName；数据类型: String；长度: 100；暂存必填: 是；申报必填: 是';
COMMENT ON COLUMN "DEC_HEAD"."APPR_NO" IS '批准文号；字段代码: ApprNo；数据类型: String；长度: 32；暂存必填: 否；申报必填: 否；说明: 实填“外汇核销单号”';
COMMENT ON COLUMN "DEC_HEAD"."BILL_NO" IS '提单号；字段代码: BillNo；数据类型: String；长度: 32；暂存必填: 否；申报必填: 否';
COMMENT ON COLUMN "DEC_HEAD"."CONTR_NO" IS '合同号；字段代码: ContrNo；数据类型: String；长度: 32；暂存必填: 否；申报必填: 否';
COMMENT ON COLUMN "DEC_HEAD"."COP_CODE" IS '录入单位代码；字段代码: CopCode；数据类型: String；长度: 9；暂存必填: 是；申报必填: 是；说明: 预录入企业组织机构编码，导入暂存时，必填';
COMMENT ON COLUMN "DEC_HEAD"."COP_NAME" IS '录入单位名称；字段代码: CopName；数据类型: String；长度: 70；暂存必填: 是；申报必填: 是；说明: 预录入企业名称，导入暂存时，必填';
COMMENT ON COLUMN "DEC_HEAD"."CUSTOM_MASTER" IS '主管海关（申报地海关）；字段代码: CustomMaster；数据类型: String；长度: 4；暂存必填: 是；申报必填: 是';
COMMENT ON COLUMN "DEC_HEAD"."CUT_MODE" IS '征免性质；字段代码: CutMode；数据类型: String；长度: 3；暂存必填: 否；申报必填: 否';
COMMENT ON COLUMN "DEC_HEAD"."DATA_SOURCE" IS '扩展字段；字段代码: DataSource；数据类型: String；长度: 5；暂存必填: 否；申报必填: 否；说明: 扩展字段。 第一位：仅出口非医用口罩填写，申报必填； 1：国外标准 2：国内标准 第二位：无实际意义，空字符占位 第三位：表示商品拆分 空/0：不拆分 1：拆分 第四位：无实际意义，空字符占位。 第五位：表示智能申报标记： 空/0：非智能申报 1：增加智能申报 2：取消智能申报';
COMMENT ON COLUMN "DEC_HEAD"."DECL_TRN_REL" IS '报关/转关关系标志；字段代码: DeclTrnRel；数据类型: String；长度: 1；暂存必填: 是；申报必填: 是；说明: 报关/转关关系标志。 0：一般报关单 1：转关提前报关单';
COMMENT ON COLUMN "DEC_HEAD"."DISTINATE_PORT" IS '经停港/指运港；字段代码: DistinatePort；数据类型: String；长度: 6；暂存必填: 否；申报必填: 是';
COMMENT ON COLUMN "DEC_HEAD"."EDI_ID" IS '报关标志；字段代码: EdiId；数据类型: String；长度: 1；暂存必填: 否；申报必填: 是；说明: 1：普通报关 3：北方转关提前 5：南方转关提前 6：普通报关，运输工具名称以‘◎’开头，南方H2000直转';
COMMENT ON COLUMN "DEC_HEAD"."ENTRY_ID" IS '海关编号；字段代码: EntryId；数据类型: String；长度: 18；暂存必填: 否；申报必填: 否';
COMMENT ON COLUMN "DEC_HEAD"."ENTRY_TYPE" IS '报关单类型；字段代码: EntryType；数据类型: String；长度: 1；暂存必填: 否；申报必填: 是；说明: 0普通报关单，L为带报关单清单的报关单，W无纸报关类型，D既是清单又是无纸报关的情况，M：无纸化通关';
COMMENT ON COLUMN "DEC_HEAD"."FEE_CURR" IS '运费币制；字段代码: FeeCurr；数据类型: String；长度: 3；暂存必填: 否；申报必填: 否';
COMMENT ON COLUMN "DEC_HEAD"."FEE_MARK" IS '运费标记；字段代码: FeeMark；数据类型: String；长度: 1；暂存必填: 否；申报必填: 否';
COMMENT ON COLUMN "DEC_HEAD"."FEE_RATE" IS '运费／率；字段代码: FeeRate；数据类型: Decimal；长度: 19,5；暂存必填: 否；申报必填: 否';
COMMENT ON COLUMN "DEC_HEAD"."GROSS_WET" IS '毛重；字段代码: GrossWet；数据类型: Decimal；长度: 19,5；暂存必填: 否；申报必填: 是';
COMMENT ON COLUMN "DEC_HEAD"."IE_DATE" IS '进出口日期；字段代码: IEDate；数据类型: Date；长度: 8；暂存必填: 否；申报必填: 否；说明: 格式为：yyyyMMdd';
COMMENT ON COLUMN "DEC_HEAD"."IE_FLAG" IS '进出口标志；字段代码: IEFlag；数据类型: String；长度: 1；暂存必填: 是；申报必填: 是；说明: 进出口标志。 “I”：进口。“E”：出口';
COMMENT ON COLUMN "DEC_HEAD"."IE_PORT" IS '进出境关别；字段代码: IEPort；数据类型: String；长度: 4；暂存必填: 否；申报必填: 是；说明: 需要代码转换';
COMMENT ON COLUMN "DEC_HEAD"."INPUTER_NAME" IS '录入员名称；字段代码: InputerName；数据类型: String；长度: 32；暂存必填: 是；申报必填: 是；说明: 导入暂存时，必填';
COMMENT ON COLUMN "DEC_HEAD"."INSUR_CURR" IS '保险费币制；字段代码: InsurCurr；数据类型: String；长度: 3；暂存必填: 否；申报必填: 否';
COMMENT ON COLUMN "DEC_HEAD"."INSUR_MARK" IS '保险费标记；字段代码: InsurMark；数据类型: String；长度: 1；暂存必填: 否；申报必填: 否';
COMMENT ON COLUMN "DEC_HEAD"."INSUR_RATE" IS '保险费／率；字段代码: InsurRate；数据类型: Decimal；长度: 19,5；暂存必填: 否；申报必填: 否';
COMMENT ON COLUMN "DEC_HEAD"."LICENSE_NO" IS '许可证编号；字段代码: LicenseNo；数据类型: String；长度: 20；暂存必填: 否；申报必填: 否';
COMMENT ON COLUMN "DEC_HEAD"."MANUAL_NO" IS '备案号；字段代码: ManualNo；数据类型: String；长度: 12；暂存必填: 否；申报必填: 否';
COMMENT ON COLUMN "DEC_HEAD"."NET_WT" IS '净重；字段代码: NetWt；数据类型: Decimal；长度: 19,5；暂存必填: 否；申报必填: 是';
COMMENT ON COLUMN "DEC_HEAD"."NOTE_S" IS '备注；字段代码: NoteS；数据类型: String；长度: 255；暂存必填: 否；申报必填: 否';
COMMENT ON COLUMN "DEC_HEAD"."OTHER_CURR" IS '杂费币制；字段代码: OtherCurr；数据类型: String；长度: 3；暂存必填: 否；申报必填: 否';
COMMENT ON COLUMN "DEC_HEAD"."OTHER_MARK" IS '杂费标志；字段代码: OtherMark；数据类型: String；长度: 1；暂存必填: 否；申报必填: 否';
COMMENT ON COLUMN "DEC_HEAD"."OTHER_RATE" IS '杂费／率；字段代码: OtherRate；数据类型: Decimal；长度: 19,5；暂存必填: 否；申报必填: 否';
COMMENT ON COLUMN "DEC_HEAD"."OWNER_CODE" IS '消费使用/生产销售单位代码；字段代码: OwnerCode；数据类型: String；长度: 18；暂存必填: 否；申报必填: 否；说明: 10位或9位，或NO';
COMMENT ON COLUMN "DEC_HEAD"."OWNER_NAME" IS '消费使用/生产销售单位名称；字段代码: OwnerName；数据类型: String；长度: 100；暂存必填: 否；申报必填: 是';
COMMENT ON COLUMN "DEC_HEAD"."PACK_NO" IS '件数；字段代码: PackNo；数据类型: Integer；长度: 9；暂存必填: 否；申报必填: 是';
COMMENT ON COLUMN "DEC_HEAD"."PARTENER_ID" IS '申报人标识；字段代码: PartenerID；数据类型: String；长度: 20；暂存必填: 否；申报必填: 否；说明: 申报人姓名';
COMMENT ON COLUMN "DEC_HEAD"."P_DATE" IS '打印日期；字段代码: PDate；数据类型: Date；长度: 8；暂存必填: 否；申报必填: 否；说明: 预录入日期，格式为：yyyyMMdd';
COMMENT ON COLUMN "DEC_HEAD"."PRE_ENTRY_ID" IS '预录入编号；字段代码: PreEntryId；数据类型: String；长度: 9；暂存必填: 否；申报必填: 否';
COMMENT ON COLUMN "DEC_HEAD"."RISK" IS '风险评估参数；字段代码: Risk；数据类型: String；长度: 10；暂存必填: 否；申报必填: 否；说明: 空值（上海专用）';
COMMENT ON COLUMN "DEC_HEAD"."SEQ_NO" IS '数据中心统一编号；字段代码: SeqNo；数据类型: String；长度: 18；暂存必填: 否；申报必填: 否；说明: 唯一标识一票报关单。首次导入传空值，由系统自动生成并返回客户端。';
COMMENT ON COLUMN "DEC_HEAD"."TGD_NO" IS '关联单据号；字段代码: TgdNo；数据类型: String；长度: 18；暂存必填: 否；申报必填: 否；说明: 空值，预留字段';
COMMENT ON COLUMN "DEC_HEAD"."TRADE_COUNTRY" IS '启运国/运抵国；字段代码: TradeCountry；数据类型: String；长度: 3；暂存必填: 否；申报必填: 是；说明: （进口：起运国；出口：运抵国）';
COMMENT ON COLUMN "DEC_HEAD"."TRADE_MODE" IS '监管方式；字段代码: TradeMode；数据类型: String；长度: 4；暂存必填: 否；申报必填: 是';
COMMENT ON COLUMN "DEC_HEAD"."TRADE_CODE" IS '境内收发货人编号；字段代码: TradeCode；数据类型: String；长度: 18；暂存必填: 否；申报必填: 是';
COMMENT ON COLUMN "DEC_HEAD"."TRAF_MODE" IS '运输方式代码；字段代码: TrafMode；数据类型: String；长度: 1；暂存必填: 否；申报必填: 是';
COMMENT ON COLUMN "DEC_HEAD"."TRAF_NAME" IS '运输工具代码及名称；字段代码: TrafName；数据类型: String；长度: 50；暂存必填: 否；申报必填: 否';
COMMENT ON COLUMN "DEC_HEAD"."TRADE_NAME" IS '境内收发货人名称；字段代码: TradeName；数据类型: String；长度: 100；暂存必填: 否；申报必填: 是';
COMMENT ON COLUMN "DEC_HEAD"."TRANS_MODE" IS '成交方式；字段代码: TransMode；数据类型: String；长度: 1；暂存必填: 否；申报必填: 是';
COMMENT ON COLUMN "DEC_HEAD"."TYPE" IS '单据类型；字段代码: Type；数据类型: String；长度: 6；暂存必填: 否；申报必填: 是；说明: 前两位：一般报关单填空值； ML：保税区进出境备案清单 SD: “属地申报，口岸验放”报关单 LY:两单一审备案清单； CL:汇总征税报关单； SS:”属地申报，属地验放”报关单 SW：税单无纸化 SZ：水运中转普通报关单 SM：水运中转保税区进出境备案清单 SL：水运中转两单一审备案清单 MF:公路舱单跨境快速通关报关单 MK: 公路舱单跨境快速通关备案清单 MT:TIR运输。 TT:纳税期限报关单； IT:分期纳税报关单； EX：低值快速货物报关单； EC：低值快速货物汇总征税报关单； MU:多式联运报关单 AL：极简备案清单； BL：次简备案清单； H1：径予放行简化申报（海南）； H2：二线出岛简化申报（海南）； 第三位： 0/空：整合申报一次录入； 5:新模式概要申报; 6:新模式完整申报。 第四位：无实际意义，空字符占位。 第五位：无实际意义，空字符占位。 第六位：无实际意义，空字符占位。 第七位： 填写Z表示组合港。';
COMMENT ON COLUMN "DEC_HEAD"."TYPIST_NO" IS '录入员IC卡号；字段代码: TypistNo；数据类型: String；长度: 30；暂存必填: 是；申报必填: 是；说明: 导入暂存时，必填';
COMMENT ON COLUMN "DEC_HEAD"."WRAP_TYPE" IS '包装种类；字段代码: WrapType；数据类型: String；长度: 2；暂存必填: 否；申报必填: 是';
COMMENT ON COLUMN "DEC_HEAD"."CHK_SURETY" IS '担保验放标志；字段代码: ChkSurety；数据类型: String；长度: 1；暂存必填: 否；申报必填: 否；说明: 担保验放：1：是、0：否';
COMMENT ON COLUMN "DEC_HEAD"."BILL_TYPE" IS '备案清单类型；字段代码: BillType；数据类型: String；长度: 1；暂存必填: 否；申报必填: 否；说明: 自贸区特有的类型： 1：普通备案清单 2：先进区后报关 3：分送集报备案清单 4；分送集报报关单 注：当选择自贸区类型时，以下字段不可填写 表头：合同号、批准文件、内销比率 表体：用途、工缴费、成品货号 自由文本：监管仓号、报关员联系方式';
COMMENT ON COLUMN "DEC_HEAD"."COP_CODE_SCC" IS '录入单位统一编码；字段代码: CopCodeScc；数据类型: String；长度: 18；暂存必填: 否；申报必填: 否';
COMMENT ON COLUMN "DEC_HEAD"."TRADE_CO_SCC" IS '收发货人统一编码；字段代码: TradeCoScc；数据类型: String；长度: 18；暂存必填: 否；申报必填: 是';
COMMENT ON COLUMN "DEC_HEAD"."AGENT_CODE_SCC" IS '申报代码统一编码；字段代码: AgentCodeScc；数据类型: String；长度: 18；暂存必填: 否；申报必填: 是';
COMMENT ON COLUMN "DEC_HEAD"."OWNER_CODE_SCC" IS '消费使用/生产销售单位单位统一编码；字段代码: OwnerCodeScc；数据类型: String；长度: 18；暂存必填: 否；申报必填: 是';
COMMENT ON COLUMN "DEC_HEAD"."PROMISE_ITMES" IS '价格说明；字段代码: PromiseItmes；数据类型: String；长度: 32；暂存必填: 是；申报必填: 是；说明: 1-勾选、0-未选、9-空 第一位特殊关系确认 第二位价格影响确认 第三位支付特许权使用费确认 第四位公式定价确认 第五位暂定价格确认';
COMMENT ON COLUMN "DEC_HEAD"."BUSINESS_ITEMS" IS '业务事项：说明：1-勾选、0-未勾选 第一位税单无纸化 第二位担保验放 第三位跨境电商海外仓 第四位组合港 第五位自报自享';
COMMENT ON COLUMN "DEC_HEAD"."TRADE_AREA_CODE" IS '贸易国别；字段代码: TradeAreaCode；数据类型: String；长度: 3；暂存必填: 是；申报必填: 是';
COMMENT ON COLUMN "DEC_HEAD"."CHECK_FLOW" IS '查验分流；字段代码: CheckFlow；数据类型: String；长度: 1；暂存必填: 否；申报必填: 否；说明: 查验分流： 1：是查验分流 0：不是查验分流';
COMMENT ON COLUMN "DEC_HEAD"."TAX_AAMIN_MARK" IS '税收征管标记；字段代码: TaxAaminMark；数据类型: String；长度: 1；暂存必填: 否；申报必填: 否；说明: 税收征管标记：0：无 1：有';
COMMENT ON COLUMN "DEC_HEAD"."MARK_NO" IS '标记唛码；字段代码: MarkNo；数据类型: String；长度: 400；暂存必填: 否；申报必填: 否；说明: 标记及号码【本批货物的标记和号码】';
COMMENT ON COLUMN "DEC_HEAD"."DESP_PORT_CODE" IS '启运港代码；字段代码: DespPortCode；数据类型: String；长度: 8；暂存必填: 否；申报必填: 是；说明: 启运港代码【本批货物的启运口岸】';
COMMENT ON COLUMN "DEC_HEAD"."ENTY_PORT_CODE" IS '入境口岸代码；字段代码: EntyPortCode；数据类型: String；长度: 8；暂存必填: 否；申报必填: 是；说明: 入境口岸代码【货物从运输工具卸离的第一个境内口岸】';
COMMENT ON COLUMN "DEC_HEAD"."GOODS_PLACE" IS '存放地点；字段代码: GoodsPlace；数据类型: String；长度: 100；暂存必填: 否；申报必填: 是；说明: 货物存放地点【报检时货物的存放地点】';
COMMENT ON COLUMN "DEC_HEAD"."BL_NO" IS 'B/L号；字段代码: BLNo；数据类型: String；长度: 50；暂存必填: 否；申报必填: 否；说明: 提货单号【本批货物的提货单或出库单号码】';
COMMENT ON COLUMN "DEC_HEAD"."INSP_ORG_CODE" IS '口岸检验检疫机关；字段代码: InspOrgCode；数据类型: String；长度: 10；暂存必填: 否；申报必填: 否；说明: 字段已废弃，不再使用';
COMMENT ON COLUMN "DEC_HEAD"."SPEC_DECL_FLAG" IS '特种业务标识；字段代码: SpecDeclFlag；数据类型: String；长度: 10；暂存必填: 否；申报必填: 否；说明: 特种业务标识： 0-未勾选，1-选中 第一位：“国际赛事” 第二位：“特殊进出军工物资” 第三位：“国际援助物资” 第四位：“国际会议” 第五位：“直通放行” 第六位：“外交礼遇” 第七位：“转关';
COMMENT ON COLUMN "DEC_HEAD"."PURP_ORG_CODE" IS '目的地海关；字段代码: PurpOrgCode；数据类型: String；长度: 4；暂存必填: 否；申报必填: 否；说明: 目的地海关【入境货物流向的目的地检验检疫机构】，填写4位关区代码。';
COMMENT ON COLUMN "DEC_HEAD"."DESP_DATE" IS '启运日期；字段代码: DespDate；数据类型: Date；长度: 8；暂存必填: 否；申报必填: 否；说明: 发货日期【本批拟发货的日期】 格式为：yyyyMMdd';
COMMENT ON COLUMN "DEC_HEAD"."CMPL_DSCHRG_DT" IS '卸毕日期；字段代码: CmplDschrgDt；数据类型: Date；长度: 8；暂存必填: 否；申报必填: 否；说明: 卸毕日期【本批货物全部卸离运输工具的日期】 格式为：yyyyMMdd';
COMMENT ON COLUMN "DEC_HEAD"."CORRELATION_REASON_FLAG" IS '关联理由；字段代码: CorrelationReasonFlag；数据类型: String；长度: 2；暂存必填: 否；申报必填: 否；说明: 关联理由【关联报检号的关联理由】';
COMMENT ON COLUMN "DEC_HEAD"."VSA_ORG_CODE" IS '领证机关；字段代码: VsaOrgCode；数据类型: String；长度: 10；暂存必填: 否；申报必填: 否；说明: 字段已废弃，不再使用';
COMMENT ON COLUMN "DEC_HEAD"."ORIG_BOX_FLAG" IS '原集装箱标识；字段代码: OrigBoxFlag；数据类型: String；长度: 1；暂存必填: 否；申报必填: 否；说明: 入境原集装箱装载直接到目的机构，【1：是；0：否】';
COMMENT ON COLUMN "DEC_HEAD"."DECLARE_NAME" IS '申报人员姓名；字段代码: DeclareName；数据类型: String；长度: 50；暂存必填: 是；申报必填: 是';
COMMENT ON COLUMN "DEC_HEAD"."NO_OTHER_PACK" IS '无其他包装；字段代码: NoOtherPack；数据类型: String；长度: 1；暂存必填: 否；申报必填: 否；说明: 勾选 0-未选，有其他包装；1：选中，无其他包装。';
COMMENT ON COLUMN "DEC_HEAD"."ORG_CODE" IS '检验检疫受理机关；字段代码: OrgCode；数据类型: String；长度: 10；暂存必填: 否；申报必填: 否；说明: 字段已废弃，不再使用';
COMMENT ON COLUMN "DEC_HEAD"."OVERSEAS_CONSIGNOR_CODE" IS '境外发货人代码；字段代码: OverseasConsignorCode；数据类型: String；长度: 50；暂存必填: 否；申报必填: 否；说明: 境外发货人代码';
COMMENT ON COLUMN "DEC_HEAD"."OVERSEAS_CONSIGNOR_CNAME" IS '境外收发货人名称；字段代码: OverseasConsignorCname；数据类型: String；长度: 150；暂存必填: 否；申报必填: 否；说明: 境外收发货人中文名称';
COMMENT ON COLUMN "DEC_HEAD"."OVERSEAS_CONSIGNOR_ENAME" IS '境外发货人名称（外文）；字段代码: OverseasConsignorEname；数据类型: String；长度: 100；暂存必填: 否；申报必填: 否；说明: 境外发货人名称（外文）';
COMMENT ON COLUMN "DEC_HEAD"."OVERSEAS_CONSIGNOR_ADDR" IS '境外收发货人地址；字段代码: OverseasConsignorAddr；数据类型: String；长度: 100；暂存必填: 否；申报必填: 否；说明: 境外收发货人地址';
COMMENT ON COLUMN "DEC_HEAD"."OVERSEAS_CONSIGNEE_CODE" IS '境外收货人编码；字段代码: OverseasConsigneeCode；数据类型: String；长度: 50；暂存必填: 否；申报必填: 否；说明: 境外收货人编码';
COMMENT ON COLUMN "DEC_HEAD"."OVERSEAS_CONSIGNEE_ENAME" IS '境外收货人名称(外文)；字段代码: OverseasConsigneeEname；数据类型: String；长度: 400；暂存必填: 否；申报必填: 否；说明: 境外收货人名称（外文）';
COMMENT ON COLUMN "DEC_HEAD"."DOMESTIC_CONSIGNEE_ENAME" IS '境内收发货人名称（外文）；字段代码: DomesticConsigneeEname；数据类型: String；长度: 400；暂存必填: 否；申报必填: 否；说明: 境内收发货人名称（外文）';
COMMENT ON COLUMN "DEC_HEAD"."CORRELATION_NO" IS '关联号码；字段代码: CorrelationNo；数据类型: String；长度: 500；暂存必填: 否；申报必填: 否';
COMMENT ON COLUMN "DEC_HEAD"."EDI_REMARK" IS 'EDI申报备注；字段代码: EdiRemark；数据类型: String；长度: 128；暂存必填: 否；申报必填: 否';
COMMENT ON COLUMN "DEC_HEAD"."EDI_REMARK2" IS 'EDI申报备注2；字段代码: EdiRemark2；数据类型: String；长度: 128；暂存必填: 否；申报必填: 否';
COMMENT ON COLUMN "DEC_HEAD"."TRADE_CIQ_CODE" IS '境内收发货人检验检疫编码；字段代码: TradeCiqCode；数据类型: String；长度: 10；暂存必填: 否；申报必填: 否；说明: 境内收发货人检验检疫编码';
COMMENT ON COLUMN "DEC_HEAD"."OWNER_CIQ_CODE" IS '消费使用/生产销售单位检验检疫编码；字段代码: OwnerCiqCode；数据类型: String；长度: 10；暂存必填: 否；申报必填: 否；说明: 消费使用/生产销售单位检验检疫编码';
COMMENT ON COLUMN "DEC_HEAD"."DECL_CIQ_CODE" IS '申报单位检验检疫编码；字段代码: DeclCiqCode；数据类型: String；长度: 10；暂存必填: 否；申报必填: 否；说明: 申报单位检验检疫编码';

CREATE TABLE "DEC_LIST" (
    "ID" BIGINT IDENTITY(1,1) NOT NULL,
    "DEC_HEAD_ID" BIGINT,
    "CLASS_MARK" VARCHAR2(1),
    "CODE_TS" VARCHAR2(10) NOT NULL,
    "CONTR_ITEM" INTEGER,
    "DECL_PRICE" DECIMAL(19,4) NOT NULL,
    "DECL_TOTAL" DECIMAL(19,4) NOT NULL,
    "DUTY_MODE" VARCHAR2(1) NOT NULL,
    "EXG_NO" VARCHAR2(30),
    "EXG_VERSION" INTEGER,
    "FACTOR" DECIMAL(11,3),
    "FIRST_UNIT" VARCHAR2(3) NOT NULL,
    "FIRST_QTY" DECIMAL(19,5) NOT NULL,
    "G_UNIT" VARCHAR2(3) NOT NULL,
    "G_MODEL" VARCHAR2(255),
    "G_NAME" VARCHAR2(255) NOT NULL,
    "G_NO" INTEGER NOT NULL,
    "G_QTY" DECIMAL(19,5) NOT NULL,
    "ORIGIN_COUNTRY" VARCHAR2(3) NOT NULL,
    "SECOND_UNIT" VARCHAR2(3),
    "SECOND_QTY" DECIMAL(19,5) NOT NULL,
    "TRADE_CURR" VARCHAR2(3) NOT NULL,
    "USE_TO" VARCHAR2(2),
    "WORK_USD" DECIMAL(19,4),
    "DESTINATION_COUNTRY" VARCHAR2(3) NOT NULL,
    "CIQ_CODE" VARCHAR2(20),
    "DECL_GOODS_ENAME" VARCHAR2(100),
    "ORIG_PLACE_CODE" VARCHAR2(50),
    "PURPOSE" VARCHAR2(4),
    "PROD_VALID_DT" VARCHAR2(20),
    "PROD_QGP" VARCHAR2(20),
    "GOODS_ATTR" VARCHAR2(20),
    "STUFF" VARCHAR2(400),
    "UNCODE" VARCHAR2(20),
    "DANG_NAME" VARCHAR2(80),
    "DANG_PACK_TYPE" VARCHAR2(4),
    "DANG_PACK_SPEC" VARCHAR2(24),
    "ENG_MAN_ENT_CNM" VARCHAR2(100),
    "NO_DANG_FLAG" VARCHAR2(1),
    "DEST_CODE" VARCHAR2(8),
    "GOODS_SPEC" VARCHAR2(2000),
    "GOODS_MODEL" VARCHAR2(2000),
    "GOODS_BRAND" VARCHAR2(2000),
    "PRODUCE_DATE" VARCHAR2(2000),
    "PROD_BATCH_NO" VARCHAR2(2000),
    "DISTRICT_CODE" VARCHAR2(5) NOT NULL,
    "CIQ_NAME" VARCHAR2(50),
    "MNUFCTR_REG_NO" VARCHAR2(20),
    "MNUFCTR_REG_NAME" VARCHAR2(150),
    "RCEP_ORIG_PLACE_CODE" VARCHAR2(3),
    "CONTROL_EXPLAIN_CODE" VARCHAR2(4),
    CONSTRAINT "PK_DEC_LIST" PRIMARY KEY ("ID")
);

COMMENT ON TABLE "DEC_LIST" IS '进口/出口报关单表体 DecList';
COMMENT ON COLUMN "DEC_LIST"."ID" IS '主键ID，达梦自增标识列';
COMMENT ON COLUMN "DEC_LIST"."DEC_HEAD_ID" IS '报关单表头ID，父表关联ID';
COMMENT ON COLUMN "DEC_LIST"."CLASS_MARK" IS '归类标志；字段代码: ClassMark；数据类型: String；长度: 1；暂存必填: 否；申报必填: 否；说明: 空值';
COMMENT ON COLUMN "DEC_LIST"."CODE_TS" IS '商品编号；字段代码: CodeTS；数据类型: String；长度: 10；暂存必填: 是；申报必填: 是';
COMMENT ON COLUMN "DEC_LIST"."CONTR_ITEM" IS '备案序号；字段代码: ContrItem；数据类型: Integer；长度: 19；暂存必填: 否；申报必填: 否；说明: 程序控制9位';
COMMENT ON COLUMN "DEC_LIST"."DECL_PRICE" IS '申报单价；字段代码: DeclPrice；数据类型: Decimal；长度: 19,4；暂存必填: 否；申报必填: 是';
COMMENT ON COLUMN "DEC_LIST"."DECL_TOTAL" IS '申报总价；字段代码: DeclTotal；数据类型: Decimal；长度: 19,4；暂存必填: 否；申报必填: 是';
COMMENT ON COLUMN "DEC_LIST"."DUTY_MODE" IS '征免方式；字段代码: DutyMode；数据类型: String；长度: 1；暂存必填: 否；申报必填: 是';
COMMENT ON COLUMN "DEC_LIST"."EXG_NO" IS '货号；字段代码: ExgNo；数据类型: String；长度: 30；暂存必填: 否；申报必填: 否';
COMMENT ON COLUMN "DEC_LIST"."EXG_VERSION" IS '版本号；字段代码: ExgVersion；数据类型: Integer；长度: 8；暂存必填: 否；申报必填: 否';
COMMENT ON COLUMN "DEC_LIST"."FACTOR" IS '申报计量单位与法定单位比例因子；字段代码: Factor；数据类型: Decimal；长度: 11,3；暂存必填: 否；申报必填: 否';
COMMENT ON COLUMN "DEC_LIST"."FIRST_UNIT" IS '第一计量单位（法定单位）；字段代码: FirstUnit；数据类型: String；长度: 3；暂存必填: 否；申报必填: 是';
COMMENT ON COLUMN "DEC_LIST"."FIRST_QTY" IS '第一法定数量；字段代码: FirstQty；数据类型: Decimal；长度: 19,5；暂存必填: 否；申报必填: 是';
COMMENT ON COLUMN "DEC_LIST"."G_UNIT" IS '成交计量单位；字段代码: GUnit；数据类型: String；长度: 3；暂存必填: 否；申报必填: 是';
COMMENT ON COLUMN "DEC_LIST"."G_MODEL" IS '商品规格、型号；字段代码: GModel；数据类型: String；长度: 255；暂存必填: 否；申报必填: 否';
COMMENT ON COLUMN "DEC_LIST"."G_NAME" IS '商品名称；字段代码: GName；数据类型: String；长度: 255；暂存必填: 否；申报必填: 是';
COMMENT ON COLUMN "DEC_LIST"."G_NO" IS '商品序号；字段代码: GNo；数据类型: Integer；长度: 19；暂存必填: 是；申报必填: 是';
COMMENT ON COLUMN "DEC_LIST"."G_QTY" IS '成交数量；字段代码: GQty；数据类型: Decimal；长度: 19,5；暂存必填: 否；申报必填: 是';
COMMENT ON COLUMN "DEC_LIST"."ORIGIN_COUNTRY" IS '原产国；字段代码: OriginCountry；数据类型: String；长度: 3；暂存必填: 否；申报必填: 是；说明: 对于出口报关单，表体的originalCountry需要填目的国，destinationCountry填的是原产国。 对于进口报关单，表体的originalCountry需要填原产国，destinationCountry填的是目的国。';
COMMENT ON COLUMN "DEC_LIST"."SECOND_UNIT" IS '第二计量单位；字段代码: SecondUnit；数据类型: String；长度: 3；暂存必填: 否；申报必填: 否';
COMMENT ON COLUMN "DEC_LIST"."SECOND_QTY" IS '第二法定数量；字段代码: SecondQty；数据类型: Decimal；长度: 19,5；暂存必填: 否；申报必填: 是';
COMMENT ON COLUMN "DEC_LIST"."TRADE_CURR" IS '成交币制；字段代码: TradeCurr；数据类型: String；长度: 3；暂存必填: 否；申报必填: 是';
COMMENT ON COLUMN "DEC_LIST"."USE_TO" IS '用途/生产厂家；字段代码: UseTo；数据类型: String；长度: 2；暂存必填: 否；申报必填: 否';
COMMENT ON COLUMN "DEC_LIST"."WORK_USD" IS '工缴费；字段代码: WorkUsd；数据类型: Decimal；长度: 19,4；暂存必填: 否；申报必填: 否';
COMMENT ON COLUMN "DEC_LIST"."DESTINATION_COUNTRY" IS '最终目的国（地区）；字段代码: DestinationCountry；数据类型: String；长度: 3；暂存必填: 是；申报必填: 是；说明: 对于出口报关单，表体的originalCountry需要填目的国，destinationCountry填的是原产国。 对于进口报关单，表体的originalCountry需要填原产国，destinationCountry填的是目的国。';
COMMENT ON COLUMN "DEC_LIST"."CIQ_CODE" IS '监管类别名称；字段代码: CiqCode；数据类型: String；长度: 20；暂存必填: 否；申报必填: 否；说明: 填写3位监管类别';
COMMENT ON COLUMN "DEC_LIST"."DECL_GOODS_ENAME" IS '商品英文名称；字段代码: DeclGoodsEname；数据类型: String；长度: 100；暂存必填: 否；申报必填: 否；说明: 申报货物名称(外文)【货物的外文名称】';
COMMENT ON COLUMN "DEC_LIST"."ORIG_PLACE_CODE" IS '原产地区代码；字段代码: OrigPlaceCode；数据类型: String；长度: 50；暂存必填: 否；申报必填: 否；说明: 原产地区代码【原产国内的生产区域，如州、省等】';
COMMENT ON COLUMN "DEC_LIST"."PURPOSE" IS '用途代码；字段代码: Purpose；数据类型: String；长度: 4；暂存必填: 否；申报必填: 否；说明: 用途【本项货物的用途】';
COMMENT ON COLUMN "DEC_LIST"."PROD_VALID_DT" IS '产品有效期；字段代码: ProdValidDt；数据类型: String；长度: 20；暂存必填: 否；申报必填: 否；说明: 产品有效期【质量保障的截止日期】 格式：yyyyMMdd';
COMMENT ON COLUMN "DEC_LIST"."PROD_QGP" IS '产品保质期；字段代码: ProdQgp；数据类型: String；长度: 20；暂存必填: 否；申报必填: 否；说明: 产品保质期【质量保障的月数或天数】';
COMMENT ON COLUMN "DEC_LIST"."GOODS_ATTR" IS '货物属性代码；字段代码: GoodsAttr；数据类型: String；长度: 20；暂存必填: 否；申报必填: 否；说明: 货物属性【声明本项货物的相关属性】';
COMMENT ON COLUMN "DEC_LIST"."STUFF" IS '成份/原料/组份；字段代码: Stuff；数据类型: String；长度: 400；暂存必填: 否；申报必填: 否；说明: 成份/原料/组份【本项货物含有的成份或者货物原料,或化学品组份】';
COMMENT ON COLUMN "DEC_LIST"."UNCODE" IS 'UN编码；字段代码: Uncode；数据类型: String；长度: 20；暂存必填: 否；申报必填: 否；说明: UN编码【危险品货物对应的《危险化学品目录》中的UN编码】';
COMMENT ON COLUMN "DEC_LIST"."DANG_NAME" IS '危险货物名称；字段代码: DangName；数据类型: String；长度: 80；暂存必填: 否；申报必填: 否；说明: 危险品名称【危险品货物对应的《危险化学品目录》中的名称】';
COMMENT ON COLUMN "DEC_LIST"."DANG_PACK_TYPE" IS '危包类别；字段代码: DangPackType；数据类型: String；长度: 4；暂存必填: 否；申报必填: 否；说明: 危包类别【危险化学品包装类别】';
COMMENT ON COLUMN "DEC_LIST"."DANG_PACK_SPEC" IS '危包规格；字段代码: DangPackSpec；数据类型: String；长度: 24；暂存必填: 否；申报必填: 否；说明: 危包规格【危险化学品包装规格】';
COMMENT ON COLUMN "DEC_LIST"."ENG_MAN_ENT_CNM" IS '境外生产企业名称；字段代码: EngManEntCnm；数据类型: String；长度: 100；暂存必填: 否；申报必填: 否；说明: 境外生产企业名称';
COMMENT ON COLUMN "DEC_LIST"."NO_DANG_FLAG" IS '非危险化学品；字段代码: NoDangFlag；数据类型: String；长度: 1；暂存必填: 否；申报必填: 否；说明: 危险品';
COMMENT ON COLUMN "DEC_LIST"."DEST_CODE" IS '目的地代码；字段代码: DestCode；数据类型: String；长度: 8；暂存必填: 否；申报必填: 否；说明: 目的地代码【货物在境内预定最终抵达的交货地】';
COMMENT ON COLUMN "DEC_LIST"."GOODS_SPEC" IS '检验检疫货物规格；字段代码: GoodsSpec；数据类型: String；长度: 2000；暂存必填: 否；申报必填: 否；说明: 原检验检疫货物规格';
COMMENT ON COLUMN "DEC_LIST"."GOODS_MODEL" IS '货物型号；字段代码: GoodsModel；数据类型: String；长度: 2000；暂存必填: 否；申报必填: 否；说明: 货物型号【本项货物的所有型号】';
COMMENT ON COLUMN "DEC_LIST"."GOODS_BRAND" IS '货物品牌；字段代码: GoodsBrand；数据类型: String；长度: 2000；暂存必填: 否；申报必填: 否；说明: 货物品牌【本项货物的品牌】';
COMMENT ON COLUMN "DEC_LIST"."PRODUCE_DATE" IS '生产日期；字段代码: ProduceDate；数据类型: String；长度: 2000；暂存必填: 否；申报必填: 否；说明: 生产日期【货物的生产日期或者生产批号】格式：yyyyMMdd,多个日期用英文半角分号分隔';
COMMENT ON COLUMN "DEC_LIST"."PROD_BATCH_NO" IS '生产批号；字段代码: ProdBatchNo；数据类型: String；长度: 2000；暂存必填: 否；申报必填: 否；说明: 生产批号【货物的生产批号】';
COMMENT ON COLUMN "DEC_LIST"."DISTRICT_CODE" IS '境内目的地/境内货源地；字段代码: DistrictCode；数据类型: String；长度: 5；暂存必填: 否；申报必填: 是；说明: 进口指境内目的地，出口指境内货源地';
COMMENT ON COLUMN "DEC_LIST"."CIQ_NAME" IS '检验检疫名称；字段代码: CiqName；数据类型: String；长度: 50；暂存必填: 否；申报必填: 否；说明: CIQ代码对应的商品描述';
COMMENT ON COLUMN "DEC_LIST"."MNUFCTR_REG_NO" IS '生产单位注册号；字段代码: MnufctrRegNo；数据类型: String；长度: 20；暂存必填: 否；申报必填: 否；说明: 出口独有';
COMMENT ON COLUMN "DEC_LIST"."MNUFCTR_REG_NAME" IS '生产单位名称；字段代码: MnufctrRegName；数据类型: String；长度: 150；暂存必填: 否；申报必填: 否；说明: 出口独有';
COMMENT ON COLUMN "DEC_LIST"."RCEP_ORIG_PLACE_CODE" IS '优惠贸易协定项下原产地；字段代码: RcepOrigPlaceCode；数据类型: String；长度: 3；暂存必填: 否；申报必填: 否；说明: 优惠贸易协定项下原产地';
COMMENT ON COLUMN "DEC_LIST"."CONTROL_EXPLAIN_CODE" IS '禁限管制识别码；字段代码: ControlExplainCode；数据类型: String；长度: 4；暂存必填: 否；申报必填: 否；说明: 填写4位禁限管制识别码';

CREATE TABLE "DEC_CONTAINER" (
    "ID" BIGINT IDENTITY(1,1) NOT NULL,
    "DEC_HEAD_ID" BIGINT,
    "DEC_LIST_ID" BIGINT,
    "CONTAINER_ID" VARCHAR2(32) NOT NULL,
    "CONTAINER_MD" VARCHAR2(32) NOT NULL,
    "GOODS_NO" VARCHAR2(255) NOT NULL,
    "LCL_FLAG" VARCHAR2(1),
    "GOODS_CONTA_WT" DECIMAL(19,5),
    "CONTAINER_WT" DECIMAL(18,5),
    CONSTRAINT "PK_DEC_CONTAINER" PRIMARY KEY ("ID")
);

COMMENT ON TABLE "DEC_CONTAINER" IS '报关单集装箱 DecContainer';
COMMENT ON COLUMN "DEC_CONTAINER"."ID" IS '主键ID，达梦自增标识列';
COMMENT ON COLUMN "DEC_CONTAINER"."DEC_HEAD_ID" IS '报关单表头ID，父表关联ID';
COMMENT ON COLUMN "DEC_CONTAINER"."DEC_LIST_ID" IS '报关单商品表体ID，父表关联ID';
COMMENT ON COLUMN "DEC_CONTAINER"."CONTAINER_ID" IS '集装箱号；字段代码: ContainerId；数据类型: String；长度: 32；暂存必填: 是；申报必填: 否；说明: 集装箱号';
COMMENT ON COLUMN "DEC_CONTAINER"."CONTAINER_MD" IS '集装箱规格；字段代码: ContainerMd；数据类型: String；长度: 32；暂存必填: 是；申报必填: 否；说明: 集装箱规格';
COMMENT ON COLUMN "DEC_CONTAINER"."GOODS_NO" IS '商品项号；字段代码: GoodsNo；数据类型: String；长度: 255；暂存必填: 是；申报必填: 否；说明: 商品项号用半角逗号分隔，如“1,3”';
COMMENT ON COLUMN "DEC_CONTAINER"."LCL_FLAG" IS '拼箱标识；字段代码: LclFlag；数据类型: String；长度: 1；暂存必填: 否；申报必填: 否';
COMMENT ON COLUMN "DEC_CONTAINER"."GOODS_CONTA_WT" IS '箱货重量；字段代码: GoodsContaWt；数据类型: Decimal；长度: 19,5；暂存必填: 否；申报必填: 否';
COMMENT ON COLUMN "DEC_CONTAINER"."CONTAINER_WT" IS '自重；字段代码: ContainerWt；数据类型: Decimal；长度: 18,5；暂存必填: 否；申报必填: 否';

CREATE TABLE "DEC_LICENSE_DOCUS" (
    "ID" BIGINT IDENTITY(1,1) NOT NULL,
    "DEC_HEAD_ID" BIGINT,
    "DOCU_CODE" VARCHAR2(1) NOT NULL,
    "CERT_CODE" VARCHAR2(32) NOT NULL,
    CONSTRAINT "PK_DEC_LICENSE_DOCUS" PRIMARY KEY ("ID")
);

COMMENT ON TABLE "DEC_LICENSE_DOCUS" IS '随附单证 DecLicenseDocus';
COMMENT ON COLUMN "DEC_LICENSE_DOCUS"."ID" IS '主键ID，达梦自增标识列';
COMMENT ON COLUMN "DEC_LICENSE_DOCUS"."DEC_HEAD_ID" IS '报关单表头ID，父表关联ID';
COMMENT ON COLUMN "DEC_LICENSE_DOCUS"."DOCU_CODE" IS '单证代码；字段代码: DocuCode；数据类型: String；长度: 1；暂存必填: 是；申报必填: 否';
COMMENT ON COLUMN "DEC_LICENSE_DOCUS"."CERT_CODE" IS '单证编号；字段代码: CertCode；数据类型: String；长度: 32；暂存必填: 是；申报必填: 否';

CREATE TABLE "DEC_REQUEST_CERT" (
    "ID" BIGINT IDENTITY(1,1) NOT NULL,
    "DEC_HEAD_ID" BIGINT,
    "APP_CERT_CODE" VARCHAR2(50) NOT NULL,
    "APPL_ORI" VARCHAR2(50),
    "APPL_COPY_QUAN" VARCHAR2(50),
    CONSTRAINT "PK_DEC_REQUEST_CERT" PRIMARY KEY ("ID")
);

COMMENT ON TABLE "DEC_REQUEST_CERT" IS '申请单证信息表 DecRequestCert';
COMMENT ON COLUMN "DEC_REQUEST_CERT"."ID" IS '主键ID，达梦自增标识列';
COMMENT ON COLUMN "DEC_REQUEST_CERT"."DEC_HEAD_ID" IS '报关单表头ID，父表关联ID';
COMMENT ON COLUMN "DEC_REQUEST_CERT"."APP_CERT_CODE" IS '申请单证代码；字段代码: AppCertCode；数据类型: String；长度: 50；暂存必填: 是；申报必填: 否';
COMMENT ON COLUMN "DEC_REQUEST_CERT"."APPL_ORI" IS '申请单证正本数；字段代码: ApplOri；数据类型: String；长度: 50；暂存必填: 否；申报必填: 否';
COMMENT ON COLUMN "DEC_REQUEST_CERT"."APPL_COPY_QUAN" IS '申请单证副本数；字段代码: ApplCopyQuan；数据类型: String；长度: 50；暂存必填: 否；申报必填: 否';

CREATE TABLE "DEC_GOODS_LIMIT" (
    "ID" BIGINT IDENTITY(1,1) NOT NULL,
    "DEC_HEAD_ID" BIGINT,
    "DEC_LIST_ID" BIGINT,
    "GOODS_NO" INTEGER NOT NULL,
    "LIC_TYPE_CODE" VARCHAR2(5) NOT NULL,
    "LICENCE_NO" VARCHAR2(40) NOT NULL,
    "LIC_WRTOF_DETAIL_NO" VARCHAR2(4),
    "LIC_WRTOF_QTY" VARCHAR2(20),
    "LIC_WRTOF_QTY_UNIT" VARCHAR2(3),
    CONSTRAINT "PK_DEC_GOODS_LIMIT" PRIMARY KEY ("ID")
);

COMMENT ON TABLE "DEC_GOODS_LIMIT" IS '许可证信息表 DecGoodsLimit';
COMMENT ON COLUMN "DEC_GOODS_LIMIT"."ID" IS '主键ID，达梦自增标识列';
COMMENT ON COLUMN "DEC_GOODS_LIMIT"."DEC_HEAD_ID" IS '报关单表头ID，父表关联ID';
COMMENT ON COLUMN "DEC_GOODS_LIMIT"."DEC_LIST_ID" IS '报关单商品表体ID，父表关联ID';
COMMENT ON COLUMN "DEC_GOODS_LIMIT"."GOODS_NO" IS '商品序号；字段代码: GoodsNo；数据类型: Integer；长度: 9；暂存必填: 是；申报必填: 是';
COMMENT ON COLUMN "DEC_GOODS_LIMIT"."LIC_TYPE_CODE" IS '许可证类别代码；字段代码: LicTypeCode；数据类型: String；长度: 5；暂存必填: 否；申报必填: 是';
COMMENT ON COLUMN "DEC_GOODS_LIMIT"."LICENCE_NO" IS '许可证编号；字段代码: LicenceNo；数据类型: String；长度: 40；暂存必填: 否；申报必填: 是';
COMMENT ON COLUMN "DEC_GOODS_LIMIT"."LIC_WRTOF_DETAIL_NO" IS '许可证核销明细序号；字段代码: LicWrtofDetailNo；数据类型: String；长度: 4；暂存必填: 否；申报必填: 否';
COMMENT ON COLUMN "DEC_GOODS_LIMIT"."LIC_WRTOF_QTY" IS '许可证核销数量；字段代码: LicWrtofQty；数据类型: String；长度: 20；暂存必填: 否；申报必填: 否';
COMMENT ON COLUMN "DEC_GOODS_LIMIT"."LIC_WRTOF_QTY_UNIT" IS '许可证核销数量单位；字段代码: LicWrtofQtyUnit；数据类型: String；长度: 3；暂存必填: 否；申报必填: 否';

CREATE TABLE "DEC_GOODS_LIMIT_VIN" (
    "ID" BIGINT IDENTITY(1,1) NOT NULL,
    "DEC_HEAD_ID" BIGINT,
    "DEC_GOODS_LIMIT_ID" BIGINT,
    "LICENCE_NO" VARCHAR2(40) NOT NULL,
    "LIC_TYPE_CODE" VARCHAR2(5) NOT NULL,
    "VIN_NO" VARCHAR2(100),
    "BILL_LAD_DATE" DATE,
    "QUALITY_QGP" VARCHAR2(100),
    "MOTOR_NO" VARCHAR2(100),
    "VIN_CODE" VARCHAR2(20),
    "CHASSIS_NO" VARCHAR2(10),
    "INVOICE_NUM" DECIMAL(19,5),
    "PROD_CNNM" VARCHAR2(500),
    "PROD_ENNM" VARCHAR2(500),
    "MODEL_EN" VARCHAR2(500),
    "PRICE_PER_UNIT" VARCHAR2(20),
    "INVOICE_NO" VARCHAR2(30),
    CONSTRAINT "PK_DEC_GOODS_LIMIT_VIN" PRIMARY KEY ("ID")
);

COMMENT ON TABLE "DEC_GOODS_LIMIT_VIN" IS '许可证VIN信息 DecGoodsLimitVin';
COMMENT ON COLUMN "DEC_GOODS_LIMIT_VIN"."ID" IS '主键ID，达梦自增标识列';
COMMENT ON COLUMN "DEC_GOODS_LIMIT_VIN"."DEC_HEAD_ID" IS '报关单表头ID，父表关联ID';
COMMENT ON COLUMN "DEC_GOODS_LIMIT_VIN"."DEC_GOODS_LIMIT_ID" IS '许可证信息ID，父表关联ID';
COMMENT ON COLUMN "DEC_GOODS_LIMIT_VIN"."LICENCE_NO" IS '许可证编号；字段代码: LicenceNo；数据类型: String；长度: 40；暂存必填: 是；申报必填: 是；说明: 许可证编号';
COMMENT ON COLUMN "DEC_GOODS_LIMIT_VIN"."LIC_TYPE_CODE" IS '许可证类别代码；字段代码: LicTypeCode；数据类型: String；长度: 5；暂存必填: 是；申报必填: 是；说明: 许可证类别代码';
COMMENT ON COLUMN "DEC_GOODS_LIMIT_VIN"."VIN_NO" IS 'VIN序号；字段代码: VinNo；数据类型: String；长度: 100；暂存必填: 否；申报必填: 否；说明: VIN序号';
COMMENT ON COLUMN "DEC_GOODS_LIMIT_VIN"."BILL_LAD_DATE" IS '提/运单日期；字段代码: BillLadDate；数据类型: DATE；长度: 8；暂存必填: 否；申报必填: 否；说明: 提/运单日期 yyyyMMdd';
COMMENT ON COLUMN "DEC_GOODS_LIMIT_VIN"."QUALITY_QGP" IS '质量保质期；字段代码: QualityQgp；数据类型: String；长度: 100；暂存必填: 否；申报必填: 否；说明: 质量保质期';
COMMENT ON COLUMN "DEC_GOODS_LIMIT_VIN"."MOTOR_NO" IS '发动机号或电机号；字段代码: MotorNo；数据类型: String；长度: 100；暂存必填: 否；申报必填: 否；说明: 发动机号或电机号';
COMMENT ON COLUMN "DEC_GOODS_LIMIT_VIN"."VIN_CODE" IS '车辆识别代码（VIN）；字段代码: VinCode；数据类型: String；长度: 20；暂存必填: 否；申报必填: 否；说明: 车辆识别代码（VIN）';
COMMENT ON COLUMN "DEC_GOODS_LIMIT_VIN"."CHASSIS_NO" IS '底盘(车架)号；字段代码: ChassisNo；数据类型: String；长度: 10；暂存必填: 否；申报必填: 否；说明: 底盘(车架)号';
COMMENT ON COLUMN "DEC_GOODS_LIMIT_VIN"."INVOICE_NUM" IS '发票所列数量；字段代码: InvoiceNum；数据类型: Decimal；长度: 19,5；暂存必填: 否；申报必填: 否；说明: 发票所列数量';
COMMENT ON COLUMN "DEC_GOODS_LIMIT_VIN"."PROD_CNNM" IS '品名（中文名称）；字段代码: ProdCnnm；数据类型: String；长度: 500；暂存必填: 否；申报必填: 否；说明: 品名（中文名称）';
COMMENT ON COLUMN "DEC_GOODS_LIMIT_VIN"."PROD_ENNM" IS '品名（英文名称）；字段代码: ProdEnnm；数据类型: String；长度: 500；暂存必填: 否；申报必填: 否；说明: 品名（英文名称）';
COMMENT ON COLUMN "DEC_GOODS_LIMIT_VIN"."MODEL_EN" IS '型号（英文）；字段代码: ModelEn；数据类型: String；长度: 500；暂存必填: 否；申报必填: 否；说明: 型号（英文）';
COMMENT ON COLUMN "DEC_GOODS_LIMIT_VIN"."PRICE_PER_UNIT" IS '单价；字段代码: PricePerUnit；数据类型: String；长度: 20；暂存必填: 否；申报必填: 否；说明: 单价';
COMMENT ON COLUMN "DEC_GOODS_LIMIT_VIN"."INVOICE_NO" IS '发票号；字段代码: InvoiceNo；数据类型: String；长度: 30；暂存必填: 否；申报必填: 否';

CREATE TABLE "DEC_OTHER_PACK" (
    "ID" BIGINT IDENTITY(1,1) NOT NULL,
    "DEC_HEAD_ID" BIGINT,
    "PACK_QTY" DECIMAL(19,5) NOT NULL,
    "PACK_TYPE" VARCHAR2(2) NOT NULL,
    CONSTRAINT "PK_DEC_OTHER_PACK" PRIMARY KEY ("ID")
);

COMMENT ON TABLE "DEC_OTHER_PACK" IS '其他包装信息表 DecOtherPack';
COMMENT ON COLUMN "DEC_OTHER_PACK"."ID" IS '主键ID，达梦自增标识列';
COMMENT ON COLUMN "DEC_OTHER_PACK"."DEC_HEAD_ID" IS '报关单表头ID，父表关联ID';
COMMENT ON COLUMN "DEC_OTHER_PACK"."PACK_QTY" IS '包装件数；字段代码: PackQty；数据类型: Decimal；长度: 19,5；暂存必填: 否；申报必填: 是；说明: 根据包装种类确定，挂装、散装、裸装时可不输入';
COMMENT ON COLUMN "DEC_OTHER_PACK"."PACK_TYPE" IS '包装材料种类；字段代码: PackType；数据类型: String；长度: 2；暂存必填: 否；申报必填: 是；说明: 包装材料种类';

CREATE TABLE "DEC_COP_LIMIT" (
    "ID" BIGINT IDENTITY(1,1) NOT NULL,
    "DEC_HEAD_ID" BIGINT,
    "ENT_QUALIF_NO" VARCHAR2(40),
    "ENT_QUALIF_TYPE_CODE" VARCHAR2(5) NOT NULL,
    CONSTRAINT "PK_DEC_COP_LIMIT" PRIMARY KEY ("ID")
);

COMMENT ON TABLE "DEC_COP_LIMIT" IS '企业资质信息表 DecCopLimit';
COMMENT ON COLUMN "DEC_COP_LIMIT"."ID" IS '主键ID，达梦自增标识列';
COMMENT ON COLUMN "DEC_COP_LIMIT"."DEC_HEAD_ID" IS '报关单表头ID，父表关联ID';
COMMENT ON COLUMN "DEC_COP_LIMIT"."ENT_QUALIF_NO" IS '企业资质编号；字段代码: EntQualifNo；数据类型: String；长度: 40；暂存必填: 否；申报必填: 否；说明: 根据HS编码设限情况确定';
COMMENT ON COLUMN "DEC_COP_LIMIT"."ENT_QUALIF_TYPE_CODE" IS '企业资质类别代码；字段代码: EntQualifTypeCode；数据类型: String；长度: 5；暂存必填: 否；申报必填: 是；说明: 根据HS编码设限情况确定';

CREATE TABLE "DEC_USER" (
    "ID" BIGINT IDENTITY(1,1) NOT NULL,
    "DEC_HEAD_ID" BIGINT,
    "USE_ORG_PERSON_CODE" VARCHAR2(20),
    "USE_ORG_PERSON_TEL" VARCHAR2(20),
    CONSTRAINT "PK_DEC_USER" PRIMARY KEY ("ID")
);

COMMENT ON TABLE "DEC_USER" IS '使用人信息表 DecUser';
COMMENT ON COLUMN "DEC_USER"."ID" IS '主键ID，达梦自增标识列';
COMMENT ON COLUMN "DEC_USER"."DEC_HEAD_ID" IS '报关单表头ID，父表关联ID';
COMMENT ON COLUMN "DEC_USER"."USE_ORG_PERSON_CODE" IS '使用单位联系人；字段代码: UseOrgPersonCode；数据类型: String；长度: 20；暂存必填: 否；申报必填: 否；说明: 使用单位联系人';
COMMENT ON COLUMN "DEC_USER"."USE_ORG_PERSON_TEL" IS '使用单位联系电话；字段代码: UseOrgPersonTel；数据类型: String；长度: 20；暂存必填: 否；申报必填: 否；说明: 使用单位联系电话';

CREATE TABLE "DEC_MARK_LOB" (
    "ID" BIGINT IDENTITY(1,1) NOT NULL,
    "DEC_HEAD_ID" BIGINT,
    "ATTACH_NAME" VARCHAR2(80) NOT NULL,
    "ATTACH_TYPE" VARCHAR2(20),
    "ATTACHMENT" BLOB NOT NULL,
    CONSTRAINT "PK_DEC_MARK_LOB" PRIMARY KEY ("ID")
);

COMMENT ON TABLE "DEC_MARK_LOB" IS '标记号码附件表DecMarkLob';
COMMENT ON COLUMN "DEC_MARK_LOB"."ID" IS '主键ID，达梦自增标识列';
COMMENT ON COLUMN "DEC_MARK_LOB"."DEC_HEAD_ID" IS '报关单表头ID，父表关联ID';
COMMENT ON COLUMN "DEC_MARK_LOB"."ATTACH_NAME" IS '附件名称；字段代码: AttachName；数据类型: String；长度: 80；暂存必填: 是；申报必填: 是；说明: 附件名称';
COMMENT ON COLUMN "DEC_MARK_LOB"."ATTACH_TYPE" IS '附件类型；字段代码: AttachType；数据类型: String；长度: 20；暂存必填: 否；申报必填: 否；说明: 附件类型，保存图片文件扩展名，如 png、jpg、jpeg、webp';
COMMENT ON COLUMN "DEC_MARK_LOB"."ATTACHMENT" IS '附件；字段代码: Attachment；数据类型: BLOB；暂存必填: 是；申报必填: 是；说明: 附件（计算机无法录入的标记及号码的图案或内容，不超过1MB）。 需对附件进行BASE64编码。';

CREATE TABLE "DEC_FREE_TXT" (
    "ID" BIGINT IDENTITY(1,1) NOT NULL,
    "DEC_HEAD_ID" BIGINT,
    "BON_NO" VARCHAR2(32),
    "CUS_FIE" VARCHAR2(8),
    "DEC_BP_NO" VARCHAR2(32),
    "DEC_NO" VARCHAR2(13),
    "REL_ID" VARCHAR2(18),
    "REL_MAN_NO" VARCHAR2(12),
    "VOY_NO" VARCHAR2(32),
    CONSTRAINT "PK_DEC_FREE_TXT" PRIMARY KEY ("ID")
);

COMMENT ON TABLE "DEC_FREE_TXT" IS '报关单自由文本信息DecFreeTxt';
COMMENT ON COLUMN "DEC_FREE_TXT"."ID" IS '主键ID，达梦自增标识列';
COMMENT ON COLUMN "DEC_FREE_TXT"."DEC_HEAD_ID" IS '报关单表头ID，父表关联ID';
COMMENT ON COLUMN "DEC_FREE_TXT"."BON_NO" IS '监管仓号；字段代码: BonNo；数据类型: String；长度: 32；暂存必填: 否；申报必填: 否；说明: 空值';
COMMENT ON COLUMN "DEC_FREE_TXT"."CUS_FIE" IS '货场代码；字段代码: CusFie；数据类型: String；长度: 8；暂存必填: 否；申报必填: 否；说明: 空值';
COMMENT ON COLUMN "DEC_FREE_TXT"."DEC_BP_NO" IS '报关员联系方式；字段代码: DecBpNo；数据类型: String；长度: 32；暂存必填: 否；申报必填: 否';
COMMENT ON COLUMN "DEC_FREE_TXT"."DEC_NO" IS '申报人员证号；字段代码: DecNo；数据类型: String；长度: 13；暂存必填: 否；申报必填: 否；说明: 报关员海关注册编码';
COMMENT ON COLUMN "DEC_FREE_TXT"."REL_ID" IS '关联报关单号；字段代码: RelId；数据类型: String；长度: 18；暂存必填: 否；申报必填: 否；说明: 空值';
COMMENT ON COLUMN "DEC_FREE_TXT"."REL_MAN_NO" IS '关联备案号；字段代码: RelManNo；数据类型: String；长度: 12；暂存必填: 否；申报必填: 否；说明: 空值';
COMMENT ON COLUMN "DEC_FREE_TXT"."VOY_NO" IS '航次号；字段代码: VoyNo；数据类型: String；长度: 32；暂存必填: 否；申报必填: 否';

CREATE TABLE "DEC_SIGN" (
    "ID" BIGINT IDENTITY(1,1) NOT NULL,
    "DEC_HEAD_ID" BIGINT,
    "OPER_TYPE" VARCHAR2(1) NOT NULL,
    "IC_CODE" VARCHAR2(13),
    "OPER_NAME" VARCHAR2(30),
    "COP_CODE" VARCHAR2(18),
    "CLIENT_SEQ_NO" VARCHAR2(18) NOT NULL,
    "SIGN" VARCHAR2(200) NOT NULL,
    "SIGN_DATE" TIMESTAMP NOT NULL,
    "HOST_ID" VARCHAR2(10),
    "CERTIFICATE" VARCHAR2(20),
    "DOMAIN_ID" VARCHAR2(1),
    "NOTE" VARCHAR2(100),
    "BILL_SEQ_NO" VARCHAR2(18),
    CONSTRAINT "PK_DEC_SIGN" PRIMARY KEY ("ID")
);

COMMENT ON TABLE "DEC_SIGN" IS '报关单签名DecSign';
COMMENT ON COLUMN "DEC_SIGN"."ID" IS '主键ID，达梦自增标识列';
COMMENT ON COLUMN "DEC_SIGN"."DEC_HEAD_ID" IS '报关单表头ID，父表关联ID';
COMMENT ON COLUMN "DEC_SIGN"."OPER_TYPE" IS '操作类型；字段代码: OperType；数据类型: String；长度: 1；暂存必填: 是；申报必填: 是；说明: 操作类型 A：报关单上载 B：报关单、转关单上载 C：报关单申报 D：报关单、转关单申报 E：电子手册报关单上载（此种操作类型的报关单上载时不作非空和逻辑校验） G：报关单暂存（转关提前报关单暂存）';
COMMENT ON COLUMN "DEC_SIGN"."IC_CODE" IS '操作员IC卡号；字段代码: ICCode；数据类型: String；长度: 13；暂存必填: 否；申报必填: 否；说明: 当前操作的操作员IC卡号。 对应于2.0版接口： <ICCARD_ID>签名人IC卡ID</ICCARD_ID>；对应表头中的TypistNo';
COMMENT ON COLUMN "DEC_SIGN"."OPER_NAME" IS '操作员姓名；字段代码: OperName；数据类型: String；长度: 30；暂存必填: 否；申报必填: 否；说明: 当前操作的操作员姓名';
COMMENT ON COLUMN "DEC_SIGN"."COP_CODE" IS '操作企业组织机构代码；字段代码: CopCode；数据类型: String；长度: 18；暂存必填: 否；申报必填: 否；说明: 操作企业组织机构代码，对应表头中的CopCode';
COMMENT ON COLUMN "DEC_SIGN"."CLIENT_SEQ_NO" IS '客户端报关单编号；字段代码: ClientSeqNo；数据类型: String；长度: 18；暂存必填: 是；申报必填: 是；说明: 客户端自行编制的编号，唯一识别一票报关单。';
COMMENT ON COLUMN "DEC_SIGN"."SIGN" IS '数字签名信息；字段代码: Sign；数据类型: String；长度: 200；暂存必填: 否；申报必填: 是';
COMMENT ON COLUMN "DEC_SIGN"."SIGN_DATE" IS '签名日期；字段代码: SignDate；数据类型: DateTime；长度: 16；暂存必填: 否；申报必填: 是';
COMMENT ON COLUMN "DEC_SIGN"."HOST_ID" IS '客户端邮箱的HostId；字段代码: HostId；数据类型: String；长度: 10；暂存必填: 否；申报必填: 否；说明: 字段已废弃，不再使用。';
COMMENT ON COLUMN "DEC_SIGN"."CERTIFICATE" IS '操作员卡的证书号；字段代码: Certificate；数据类型: String；长度: 20；暂存必填: 否；申报必填: 否；说明: 所有操作都需求取卡信息进行权限校验，所以所有导入都必须填写该字段。';
COMMENT ON COLUMN "DEC_SIGN"."DOMAIN_ID" IS '签名人分类；字段代码: DomainId；数据类型: String；长度: 1；暂存必填: 否；申报必填: 否；说明: 1：录入人 2：申报人';
COMMENT ON COLUMN "DEC_SIGN"."NOTE" IS '备注；字段代码: Note；数据类型: String；长度: 100；暂存必填: 否；申报必填: 否';
COMMENT ON COLUMN "DEC_SIGN"."BILL_SEQ_NO" IS '对应清单统一编号；字段代码: BillSeqNo；数据类型: String；长度: 18；暂存必填: 否；申报必填: 否；说明: 如果报关单有对应的清单填报关单对应的清单统一编号；如果没有清单填企业自编统一编号（对每个邮箱要唯一）';

CREATE TABLE "TRN_HEAD" (
    "ID" BIGINT IDENTITY(1,1) NOT NULL,
    "DEC_HEAD_ID" BIGINT,
    "TRN_PRE_ID" VARCHAR2(18),
    "TRANS_NO" VARCHAR2(16),
    "TURN_NO" VARCHAR2(16),
    "NATIVE_TRAF_MODE" VARCHAR2(1),
    "TRAF_CUSTOMS_NO" VARCHAR2(13),
    "NATIVE_SHIP_NAME" VARCHAR2(30),
    "NATIVE_VOYAGE_NO" VARCHAR2(32),
    "CONTRACTOR_NAME" VARCHAR2(70),
    "CONTRACTOR_CODE" VARCHAR2(10),
    "TRANS_FLAG" VARCHAR2(2),
    "VALID_TIME" DATE,
    "E_SEAL_FLAG" VARCHAR2(1),
    "NOTES" VARCHAR2(200),
    "TRN_TYPE" VARCHAR2(1),
    "APPL_CODE_SCC" VARCHAR2(18),
    CONSTRAINT "PK_TRN_HEAD" PRIMARY KEY ("ID")
);

COMMENT ON TABLE "TRN_HEAD" IS '进口/出口转关单表头 TrnHead';
COMMENT ON COLUMN "TRN_HEAD"."ID" IS '主键ID，达梦自增标识列';
COMMENT ON COLUMN "TRN_HEAD"."DEC_HEAD_ID" IS '关联报关单表头ID，父表关联ID';
COMMENT ON COLUMN "TRN_HEAD"."TRN_PRE_ID" IS '转关单统一编号；字段代码: TrnPreId；数据类型: String；长度: 18；暂存必填: 否；申报必填: 否；说明: 唯一标识一票报关单中转关提前报关单。首次导入传空值，由系统自动生成并返回客户端。';
COMMENT ON COLUMN "TRN_HEAD"."TRANS_NO" IS '载货清单号；字段代码: TransNo；数据类型: String；长度: 16；暂存必填: 否；申报必填: 否';
COMMENT ON COLUMN "TRN_HEAD"."TURN_NO" IS '转关申报单号；字段代码: TurnNo；数据类型: String；长度: 16；暂存必填: 否；申报必填: 否';
COMMENT ON COLUMN "TRN_HEAD"."NATIVE_TRAF_MODE" IS '境内运输方式；字段代码: NativeTrafMode；数据类型: String；长度: 1；暂存必填: 否；申报必填: 否';
COMMENT ON COLUMN "TRN_HEAD"."TRAF_CUSTOMS_NO" IS '境内运输工具编号；字段代码: TrafCustomsNo；数据类型: String；长度: 13；暂存必填: 否；申报必填: 否';
COMMENT ON COLUMN "TRN_HEAD"."NATIVE_SHIP_NAME" IS '境内运输工具名称；字段代码: NativeShipName；数据类型: String；长度: 30；暂存必填: 否；申报必填: 否';
COMMENT ON COLUMN "TRN_HEAD"."NATIVE_VOYAGE_NO" IS '境内运输工具航次；字段代码: NativeVoyageNo；数据类型: String；长度: 32；暂存必填: 否；申报必填: 否';
COMMENT ON COLUMN "TRN_HEAD"."CONTRACTOR_NAME" IS '承运单位名称；字段代码: ContractorName；数据类型: String；长度: 70；暂存必填: 否；申报必填: 否';
COMMENT ON COLUMN "TRN_HEAD"."CONTRACTOR_CODE" IS '承运单位组织机构代码；字段代码: ContractorCode；数据类型: String；长度: 10；暂存必填: 否；申报必填: 否';
COMMENT ON COLUMN "TRN_HEAD"."TRANS_FLAG" IS '转关类型；字段代码: TransFlag；数据类型: String；长度: 2；暂存必填: 否；申报必填: 否；说明: 转关类型：1—转关提前 AA：出口二次转关 1G：提前/工厂验放 1T：提前/暂时进出口 1E：提前/中欧班列 1P：提前/市场采购出口 1K：提前/出口空运联程';
COMMENT ON COLUMN "TRN_HEAD"."VALID_TIME" IS '预计运抵指运地时间；字段代码: ValidTime；数据类型: Date；长度: 8；暂存必填: 否；申报必填: 否；说明: 格式为：yyyyMMdd';
COMMENT ON COLUMN "TRN_HEAD"."E_SEAL_FLAG" IS '是否启用电子关锁标志；字段代码: ESealFlag；数据类型: String；长度: 1；暂存必填: 否；申报必填: 否；说明: Y：是 N：否';
COMMENT ON COLUMN "TRN_HEAD"."NOTES" IS '备注；字段代码: Notes；数据类型: String；长度: 200；暂存必填: 否；申报必填: 否';
COMMENT ON COLUMN "TRN_HEAD"."TRN_TYPE" IS '转关单类型；字段代码: TrnType；数据类型: String；长度: 1；暂存必填: 否；申报必填: 否；说明: 1：无纸申报 0或空：普通有纸申报';
COMMENT ON COLUMN "TRN_HEAD"."APPL_CODE_SCC" IS '转关申报单位统一代码；字段代码: ApplCodeScc；数据类型: String；长度: 18；暂存必填: 否；申报必填: 否';

CREATE TABLE "TRN_LIST" (
    "ID" BIGINT IDENTITY(1,1) NOT NULL,
    "TRN_HEAD_ID" BIGINT,
    "TRAF_MODE" VARCHAR2(1),
    "SHIP_ID" VARCHAR2(32),
    "SHIP_NAME_EN" VARCHAR2(255),
    "VOYAGE_NO" VARCHAR2(32),
    "BILL_NO" VARCHAR2(32),
    "IE_DATE" DATE,
    CONSTRAINT "PK_TRN_LIST" PRIMARY KEY ("ID")
);

COMMENT ON TABLE "TRN_LIST" IS '进口/出口转关单表体(提单) TrnList';
COMMENT ON COLUMN "TRN_LIST"."ID" IS '主键ID，达梦自增标识列';
COMMENT ON COLUMN "TRN_LIST"."TRN_HEAD_ID" IS '转关单表头ID，父表关联ID';
COMMENT ON COLUMN "TRN_LIST"."TRAF_MODE" IS '进出境运输方式；字段代码: TrafMode；数据类型: String；长度: 1；暂存必填: 否；申报必填: 否';
COMMENT ON COLUMN "TRN_LIST"."SHIP_ID" IS '进出境运输工具编号；字段代码: ShipId；数据类型: String；长度: 32；暂存必填: 否；申报必填: 否';
COMMENT ON COLUMN "TRN_LIST"."SHIP_NAME_EN" IS '进出境运输工具名称；字段代码: ShipNameEn；数据类型: String；长度: 255；暂存必填: 否；申报必填: 否';
COMMENT ON COLUMN "TRN_LIST"."VOYAGE_NO" IS '进出境运输工具航次；字段代码: VoyageNo；数据类型: String；长度: 32；暂存必填: 否；申报必填: 否';
COMMENT ON COLUMN "TRN_LIST"."BILL_NO" IS '提单号；字段代码: BillNo；数据类型: String；长度: 32；暂存必填: 否；申报必填: 否';
COMMENT ON COLUMN "TRN_LIST"."IE_DATE" IS '实际进出境日期；字段代码: IEDate；数据类型: Date；长度: 8；暂存必填: 否；申报必填: 否；说明: 格式为：yyyyMMdd';

CREATE TABLE "TRN_CONTAINER" (
    "ID" BIGINT IDENTITY(1,1) NOT NULL,
    "TRN_HEAD_ID" BIGINT,
    "TRN_LIST_ID" BIGINT,
    "CONTA_NO" VARCHAR2(11) NOT NULL,
    "CONTA_SN" INTEGER,
    "CONTA_MODEL" VARCHAR2(4),
    "SEAL_NO" VARCHAR2(128),
    "TRANS_NAME" VARCHAR2(32),
    "TRANS_WEIGHT" DECIMAL(19,5),
    "SEAL_QTY" VARCHAR2(4),
    CONSTRAINT "PK_TRN_CONTAINER" PRIMARY KEY ("ID")
);

COMMENT ON TABLE "TRN_CONTAINER" IS '进口/出口提运单集装箱表 TrnContainer';
COMMENT ON COLUMN "TRN_CONTAINER"."ID" IS '主键ID，达梦自增标识列';
COMMENT ON COLUMN "TRN_CONTAINER"."TRN_HEAD_ID" IS '转关单表头ID，父表关联ID';
COMMENT ON COLUMN "TRN_CONTAINER"."TRN_LIST_ID" IS '转关单提单ID，父表关联ID';
COMMENT ON COLUMN "TRN_CONTAINER"."CONTA_NO" IS '集装箱号；字段代码: ContaNo；数据类型: String；长度: 11；暂存必填: 是；申报必填: 否';
COMMENT ON COLUMN "TRN_CONTAINER"."CONTA_SN" IS '集装箱序号；字段代码: ContaSn；数据类型: Integer；长度: 3；暂存必填: 否；申报必填: 否';
COMMENT ON COLUMN "TRN_CONTAINER"."CONTA_MODEL" IS '集装箱规格；字段代码: ContaModel；数据类型: String；长度: 4；暂存必填: 否；申报必填: 否';
COMMENT ON COLUMN "TRN_CONTAINER"."SEAL_NO" IS '电子关锁号；字段代码: SealNo；数据类型: String；长度: 128；暂存必填: 否；申报必填: 否';
COMMENT ON COLUMN "TRN_CONTAINER"."TRANS_NAME" IS '境内运输工具名称；字段代码: TransName；数据类型: String；长度: 32；暂存必填: 否；申报必填: 否';
COMMENT ON COLUMN "TRN_CONTAINER"."TRANS_WEIGHT" IS '工具实际重量；字段代码: TransWeight；数据类型: Decimal；长度: 19,5；暂存必填: 否；申报必填: 否';
COMMENT ON COLUMN "TRN_CONTAINER"."SEAL_QTY" IS '关锁个数；字段代码: SealQty；数据类型: String；长度: 4；暂存必填: 否；申报必填: 否';

CREATE TABLE "TRN_CONTA_GOODS_LIST" (
    "ID" BIGINT IDENTITY(1,1) NOT NULL,
    "TRN_CONTAINER_ID" BIGINT,
    "TRN_LIST_ID" BIGINT,
    "G_NO" INTEGER NOT NULL,
    "CONTA_NO" VARCHAR2(11) NOT NULL,
    "CONTA_GOODS_COUNT" INTEGER NOT NULL,
    "CONTA_GOODS_WEIGHT" DECIMAL(19,5) NOT NULL,
    CONSTRAINT "PK_TRN_CONTA_GOODS_LIST" PRIMARY KEY ("ID")
);

COMMENT ON TABLE "TRN_CONTA_GOODS_LIST" IS '进口/出口提运单集装箱商品装配表 TrnContaGoodsList';
COMMENT ON COLUMN "TRN_CONTA_GOODS_LIST"."ID" IS '主键ID，达梦自增标识列';
COMMENT ON COLUMN "TRN_CONTA_GOODS_LIST"."TRN_CONTAINER_ID" IS '转关单集装箱ID，父表关联ID';
COMMENT ON COLUMN "TRN_CONTA_GOODS_LIST"."TRN_LIST_ID" IS '转关单提单ID，父表关联ID';
COMMENT ON COLUMN "TRN_CONTA_GOODS_LIST"."G_NO" IS '商品序号；字段代码: GNo；数据类型: Integer；长度: 9；暂存必填: 是；申报必填: 是';
COMMENT ON COLUMN "TRN_CONTA_GOODS_LIST"."CONTA_NO" IS '集装箱号；字段代码: ContaNo；数据类型: String；长度: 11；暂存必填: 是；申报必填: 是';
COMMENT ON COLUMN "TRN_CONTA_GOODS_LIST"."CONTA_GOODS_COUNT" IS '商品件数；字段代码: ContaGoodsCount；数据类型: Integer；长度: 9；暂存必填: 否；申报必填: 是';
COMMENT ON COLUMN "TRN_CONTA_GOODS_LIST"."CONTA_GOODS_WEIGHT" IS '商品重量；字段代码: ContaGoodsWeight；数据类型: Decimal；长度: 19,5；暂存必填: 否；申报必填: 是';

CREATE TABLE "DEC_SUPPLEMENT" (
    "ID" BIGINT IDENTITY(1,1) NOT NULL,
    "DEC_HEAD_ID" BIGINT,
    "DEC_LIST_ID" BIGINT,
    "G_NO" INTEGER,
    "SUP_TYPE" VARCHAR2(1),
    "BRAND_CN" VARCHAR2(75),
    "BRAND_EN" VARCHAR2(150),
    "BUYER" VARCHAR2(225),
    "BUYER_CONTACT" VARCHAR2(75),
    "BUYER_ADDR" VARCHAR2(400),
    "BUYER_TEL" VARCHAR2(30),
    "SELLER" VARCHAR2(225),
    "SELLER_CONTACT" VARCHAR2(75),
    "SELLER_ADDR" VARCHAR2(400),
    "SELLER_TEL" VARCHAR2(30),
    "FACTORY" VARCHAR2(225),
    "FACTORY_CONTACT" VARCHAR2(75),
    "FACTORY_ADDR" VARCHAR2(400),
    "FACTORY_TEL" VARCHAR2(30),
    "CONTR_NO" VARCHAR2(75),
    "CONTR_DATE" DATE,
    "INVOICE_NO" VARCHAR2(75),
    "INVOICE_DATE" DATE,
    "I_BAB_REL" VARCHAR2(30),
    "I_PRICE_EFFECT" VARCHAR2(1),
    "I_PRICE_CLOSE" VARCHAR2(30),
    "I_OTHER_LIMITED" VARCHAR2(1),
    "I_OTHER_EFFECT" VARCHAR2(1),
    "I_NOTE1" VARCHAR2(400),
    "I_IS_USEFEE" VARCHAR2(1),
    "I_IS_PROFIT" VARCHAR2(1),
    "I_NOTE2" VARCHAR2(600),
    "CURR" VARCHAR2(3),
    "INVOICE_PRICE" DECIMAL(19,4),
    "INVOICE_AMOUNT" DECIMAL(19,2),
    "INVOICE_NOTE" VARCHAR2(400),
    "GOODS_PRICE" DECIMAL(19,4),
    "GOODS_AMOUNT" DECIMAL(19,2),
    "GOODS_NOTE" VARCHAR2(400),
    "I_COMMISSION_PRICE" DECIMAL(19,4),
    "I_COMMISSION_AMOUNT" DECIMAL(19,2),
    "I_COMMISSION_NOTE" VARCHAR2(400),
    "I_CONTAINER_PRICE" DECIMAL(19,4),
    "I_CONTAINER_AMOUNT" DECIMAL(19,2),
    "I_CONTAINER_NOTE" VARCHAR2(400),
    "I_PACK_PRICE" DECIMAL(19,4),
    "I_PACK_AMOUNT" DECIMAL(19,2),
    "I_PACK_NOTE" VARCHAR2(400),
    "I_PART_PRICE" DECIMAL(19,4),
    "I_PART_AMOUNT" DECIMAL(19,2),
    "I_PART_NOTE" VARCHAR2(400),
    "I_TOOL_PRICE" DECIMAL(19,4),
    "I_TOOL_AMOUNT" DECIMAL(19,2),
    "I_TOOL_NOTE" VARCHAR2(400),
    "I_LOSS_PRICE" DECIMAL(19,4),
    "I_LOSS_AMOUNT" DECIMAL(19,2),
    "I_LOSS_NOTE" VARCHAR2(400),
    "I_DESIGN_PRICE" DECIMAL(19,4),
    "I_DESIGN_AMOUNT" DECIMAL(19,2),
    "I_DESIGN_NOTE" VARCHAR2(400),
    "I_USEFEE_PRICE" DECIMAL(19,4),
    "I_USEFEE_AMOUNT" DECIMAL(19,2),
    "I_USEFEE_NOTE" VARCHAR2(400),
    "I_PROFIT_PRICE" DECIMAL(19,4),
    "I_PROFIT_AMOUNT" DECIMAL(19,2),
    "I_PROFIT_NOTE" VARCHAR2(400),
    "I_FEE_PRICE" DECIMAL(19,4),
    "I_FEE_AMOUNT" DECIMAL(19,2),
    "I_FEE_NOTE" VARCHAR2(400),
    "I_OTHER_PRICE" DECIMAL(19,4),
    "I_OTHER_AMOUNT" DECIMAL(19,2),
    "I_OTHER_NOTE" VARCHAR2(400),
    "I_INSUR_PRICE" DECIMAL(19,4),
    "I_INSUR_AMOUNT" DECIMAL(19,2),
    "I_INSUR_NOTE" VARCHAR2(400),
    "E_IS_DUTY_DEL" VARCHAR2(1),
    "G_NAME_OTHER" VARCHAR2(400),
    "CODE_TS_OTHER" VARCHAR2(15),
    "IS_CLASS_DECISION" VARCHAR2(1),
    "DECISION_NO" VARCHAR2(45),
    "CODE_TS_DECISION" VARCHAR2(15),
    "DECISION_CUS" VARCHAR2(4),
    "IS_SAMPLE_TEST" VARCHAR2(1),
    "G_OPTIONS" VARCHAR2(32),
    "TRAF_MODE" VARCHAR2(1),
    "IS_DIRECT_TRAF" VARCHAR2(1),
    "TRANSIT_COUNTRY" VARCHAR2(20),
    "DEST_PORT" VARCHAR2(4),
    "DECL_PORT" VARCHAR2(4),
    "BILL_NO" VARCHAR2(48),
    "ORIGIN_COUNTRY" VARCHAR2(3),
    "ORIGIN_MARK" VARCHAR2(5),
    "CERT_COUNTRY" VARCHAR2(30),
    "CERT_NO" VARCHAR2(45),
    "CERT_STANDARD" VARCHAR2(1),
    "OTHER_NOTE" VARCHAR2(1500),
    "IS_SECRET" VARCHAR2(1),
    "AGENT_TYPE" VARCHAR2(1),
    "DECL_ADDR" VARCHAR2(255),
    "DECL_POST" VARCHAR2(10),
    "DECL_TEL" VARCHAR2(30),
    CONSTRAINT "PK_DEC_SUPPLEMENT" PRIMARY KEY ("ID")
);

COMMENT ON TABLE "DEC_SUPPLEMENT" IS '补充申报单信息DecSupplement';
COMMENT ON COLUMN "DEC_SUPPLEMENT"."ID" IS '主键ID，达梦自增标识列';
COMMENT ON COLUMN "DEC_SUPPLEMENT"."DEC_HEAD_ID" IS '报关单表头ID，父表关联ID';
COMMENT ON COLUMN "DEC_SUPPLEMENT"."DEC_LIST_ID" IS '报关单商品表体ID，父表关联ID';
COMMENT ON COLUMN "DEC_SUPPLEMENT"."G_NO" IS '补充申报单商品序号；字段代码: GNo；数据类型: Integer；长度: 2；暂存必填: 否；申报必填: 否';
COMMENT ON COLUMN "DEC_SUPPLEMENT"."SUP_TYPE" IS '补充申报单类型；字段代码: SupType；数据类型: String；长度: 1；暂存必填: 否；申报必填: 否';
COMMENT ON COLUMN "DEC_SUPPLEMENT"."BRAND_CN" IS '品牌中文；字段代码: BrandCN；数据类型: String；长度: 75；暂存必填: 否；申报必填: 否';
COMMENT ON COLUMN "DEC_SUPPLEMENT"."BRAND_EN" IS '品牌英文；字段代码: BrandEN；数据类型: String；长度: 150；暂存必填: 否；申报必填: 否';
COMMENT ON COLUMN "DEC_SUPPLEMENT"."BUYER" IS '买方名称；字段代码: Buyer；数据类型: String；长度: 225；暂存必填: 否；申报必填: 否';
COMMENT ON COLUMN "DEC_SUPPLEMENT"."BUYER_CONTACT" IS '买方联系人；字段代码: BuyerContact；数据类型: String；长度: 75；暂存必填: 否；申报必填: 否';
COMMENT ON COLUMN "DEC_SUPPLEMENT"."BUYER_ADDR" IS '买方地址；字段代码: BuyerAddr；数据类型: String；长度: 400；暂存必填: 否；申报必填: 否';
COMMENT ON COLUMN "DEC_SUPPLEMENT"."BUYER_TEL" IS '买方电话；字段代码: BuyerTel；数据类型: String；长度: 30；暂存必填: 否；申报必填: 否';
COMMENT ON COLUMN "DEC_SUPPLEMENT"."SELLER" IS '卖方名称；字段代码: Seller；数据类型: String；长度: 225；暂存必填: 否；申报必填: 否';
COMMENT ON COLUMN "DEC_SUPPLEMENT"."SELLER_CONTACT" IS '卖方联系人；字段代码: SellerContact；数据类型: String；长度: 75；暂存必填: 否；申报必填: 否';
COMMENT ON COLUMN "DEC_SUPPLEMENT"."SELLER_ADDR" IS '卖方地址；字段代码: SellerAddr；数据类型: String；长度: 400；暂存必填: 否；申报必填: 否';
COMMENT ON COLUMN "DEC_SUPPLEMENT"."SELLER_TEL" IS '卖方电话；字段代码: SellerTel；数据类型: String；长度: 30；暂存必填: 否；申报必填: 否';
COMMENT ON COLUMN "DEC_SUPPLEMENT"."FACTORY" IS '生产厂商名称；字段代码: Factory；数据类型: String；长度: 225；暂存必填: 否；申报必填: 否';
COMMENT ON COLUMN "DEC_SUPPLEMENT"."FACTORY_CONTACT" IS '生产厂商联系人；字段代码: FactoryContact；数据类型: String；长度: 75；暂存必填: 否；申报必填: 否';
COMMENT ON COLUMN "DEC_SUPPLEMENT"."FACTORY_ADDR" IS '生产厂商地址；字段代码: FactoryAddr；数据类型: String；长度: 400；暂存必填: 否；申报必填: 否';
COMMENT ON COLUMN "DEC_SUPPLEMENT"."FACTORY_TEL" IS '生产厂商电话；字段代码: FactoryTel；数据类型: String；长度: 30；暂存必填: 否；申报必填: 否';
COMMENT ON COLUMN "DEC_SUPPLEMENT"."CONTR_NO" IS '合同协议号；字段代码: ContrNo；数据类型: String；长度: 75；暂存必填: 否；申报必填: 否';
COMMENT ON COLUMN "DEC_SUPPLEMENT"."CONTR_DATE" IS '签约日期；字段代码: ContrDate；数据类型: Date；暂存必填: 否；申报必填: 否；说明: 签约日期格式：yyyyMMdd';
COMMENT ON COLUMN "DEC_SUPPLEMENT"."INVOICE_NO" IS '发票编号；字段代码: InvoiceNo；数据类型: String；长度: 75；暂存必填: 否；申报必填: 否';
COMMENT ON COLUMN "DEC_SUPPLEMENT"."INVOICE_DATE" IS '发票日期；字段代码: InvoiceDate；数据类型: Date；暂存必填: 否；申报必填: 否；说明: 发票日期格式：yyyyMMdd';
COMMENT ON COLUMN "DEC_SUPPLEMENT"."I_BAB_REL" IS '价格申报项，进口货物申报项；字段代码: I_BabRel；数据类型: String；长度: 30；暂存必填: 否；申报必填: 否';
COMMENT ON COLUMN "DEC_SUPPLEMENT"."I_PRICE_EFFECT" IS '价格申报项，进口货物申报项；字段代码: I_PriceEffect；数据类型: String；长度: 1；暂存必填: 否；申报必填: 否';
COMMENT ON COLUMN "DEC_SUPPLEMENT"."I_PRICE_CLOSE" IS '价格申报项，进口货物申报项；字段代码: I_PriceClose；数据类型: String；长度: 30；暂存必填: 否；申报必填: 否';
COMMENT ON COLUMN "DEC_SUPPLEMENT"."I_OTHER_LIMITED" IS '价格申报项，进口货物申报项；字段代码: I_OtherLimited；数据类型: String；长度: 1；暂存必填: 否；申报必填: 否';
COMMENT ON COLUMN "DEC_SUPPLEMENT"."I_OTHER_EFFECT" IS '价格申报项，进口货物申报项；字段代码: I_OtherEffect；数据类型: String；长度: 1；暂存必填: 否；申报必填: 否';
COMMENT ON COLUMN "DEC_SUPPLEMENT"."I_NOTE1" IS '价格申报项，进口货物申报项；字段代码: I_Note1；数据类型: String；长度: 400；暂存必填: 否；申报必填: 否';
COMMENT ON COLUMN "DEC_SUPPLEMENT"."I_IS_USEFEE" IS '价格申报项，进口货物申报项。；字段代码: I_IsUsefee；数据类型: String；长度: 1；暂存必填: 否；申报必填: 否';
COMMENT ON COLUMN "DEC_SUPPLEMENT"."I_IS_PROFIT" IS '价格申报项，进口货物申报项；字段代码: I_IsProfit；数据类型: String；长度: 1；暂存必填: 否；申报必填: 否';
COMMENT ON COLUMN "DEC_SUPPLEMENT"."I_NOTE2" IS '价格申报项，进口货物申报项；字段代码: I_Note2；数据类型: String；长度: 600；暂存必填: 否；申报必填: 否';
COMMENT ON COLUMN "DEC_SUPPLEMENT"."CURR" IS '价格申报项，币制；字段代码: Curr；数据类型: String；长度: 3；暂存必填: 否；申报必填: 否';
COMMENT ON COLUMN "DEC_SUPPLEMENT"."INVOICE_PRICE" IS '价格申报项，发票价格单位金额；字段代码: InvoicePrice；数据类型: Decimal；长度: 19,4；暂存必填: 否；申报必填: 否';
COMMENT ON COLUMN "DEC_SUPPLEMENT"."INVOICE_AMOUNT" IS '价格申报项，发票价格总金额；字段代码: InvoiceAmount；数据类型: Decimal；长度: 19,2；暂存必填: 否；申报必填: 否';
COMMENT ON COLUMN "DEC_SUPPLEMENT"."INVOICE_NOTE" IS '价格申报项，发票价格备注；字段代码: InvoiceNote；数据类型: String；长度: 400；暂存必填: 否；申报必填: 否';
COMMENT ON COLUMN "DEC_SUPPLEMENT"."GOODS_PRICE" IS '价格申报项，间接支付/收取的货款单位金额，进口是间接支付，出口是间接收取；字段代码: GoodsPrice；数据类型: Decimal；长度: 19,4；暂存必填: 否；申报必填: 否';
COMMENT ON COLUMN "DEC_SUPPLEMENT"."GOODS_AMOUNT" IS '价格申报项，间接支付/收取的货款总金额，进口是间接支付，出口是间接收取；字段代码: GoodsAmount；数据类型: Decimal；长度: 19,2；暂存必填: 否；申报必填: 否';
COMMENT ON COLUMN "DEC_SUPPLEMENT"."GOODS_NOTE" IS '价格申报项，间接支付/收取的货款备注，进口是间接支付，出口是间接收取；字段代码: GoodsNote；数据类型: String；长度: 400；暂存必填: 否；申报必填: 否';
COMMENT ON COLUMN "DEC_SUPPLEMENT"."I_COMMISSION_PRICE" IS '价格申报项，进口货物除购货佣金以外的佣金和经纪费单位金额；字段代码: I_CommissionPrice；数据类型: Decimal；长度: 19,4；暂存必填: 否；申报必填: 否';
COMMENT ON COLUMN "DEC_SUPPLEMENT"."I_COMMISSION_AMOUNT" IS '价格申报项，进口货物除购货佣金以外的佣金和经纪费总金额；字段代码: I_CommissionAmount；数据类型: Decimal；长度: 19,2；暂存必填: 否；申报必填: 否';
COMMENT ON COLUMN "DEC_SUPPLEMENT"."I_COMMISSION_NOTE" IS '价格申报项，进口货物除购货佣金以外的佣金和经纪费备注；字段代码: I_CommissionNote；数据类型: String；长度: 400；暂存必填: 否；申报必填: 否';
COMMENT ON COLUMN "DEC_SUPPLEMENT"."I_CONTAINER_PRICE" IS '价格申报项，与该进口货物视为一体的容器费用单位金额；字段代码: I_ContainerPrice；数据类型: Decimal；长度: 19,4；暂存必填: 否；申报必填: 否';
COMMENT ON COLUMN "DEC_SUPPLEMENT"."I_CONTAINER_AMOUNT" IS '价格申报项，与该进口货物视为一体的容器费用总金额；字段代码: I_ContainerAmount；数据类型: Decimal；长度: 19,2；暂存必填: 否；申报必填: 否';
COMMENT ON COLUMN "DEC_SUPPLEMENT"."I_CONTAINER_NOTE" IS '价格申报项，与该进口货物视为一体的容器费用备注；字段代码: I_ContainerNote；数据类型: String；长度: 400；暂存必填: 否；申报必填: 否';
COMMENT ON COLUMN "DEC_SUPPLEMENT"."I_PACK_PRICE" IS '价格申报项，进口货物包装材料和包装劳务费用单位金额；字段代码: I_PackPrice；数据类型: Decimal；长度: 19,4；暂存必填: 否；申报必填: 否';
COMMENT ON COLUMN "DEC_SUPPLEMENT"."I_PACK_AMOUNT" IS '价格申报项，进口货物包装材料和包装劳务费用总金额；字段代码: I_PackAmount；数据类型: Decimal；长度: 19,2；暂存必填: 否；申报必填: 否';
COMMENT ON COLUMN "DEC_SUPPLEMENT"."I_PACK_NOTE" IS '价格申报项，进口货物包装材料和包装劳务费用备注；字段代码: I_PackNote；数据类型: String；长度: 400；暂存必填: 否；申报必填: 否';
COMMENT ON COLUMN "DEC_SUPPLEMENT"."I_PART_PRICE" IS '价格申报项，进口货物包含的材料、部件、零件和类似货物单位金额；字段代码: I_PartPrice；数据类型: Decimal；长度: 19,4；暂存必填: 否；申报必填: 否';
COMMENT ON COLUMN "DEC_SUPPLEMENT"."I_PART_AMOUNT" IS '价格申报项，进口货物包含的材料、部件、零件和类似货物总金额；字段代码: I_PartAmount；数据类型: Decimal；长度: 19,2；暂存必填: 否；申报必填: 否';
COMMENT ON COLUMN "DEC_SUPPLEMENT"."I_PART_NOTE" IS '价格申报项，进口货物包含的材料、部件、零件和类似货物备注；字段代码: I_PartNote；数据类型: String；长度: 400；暂存必填: 否；申报必填: 否';
COMMENT ON COLUMN "DEC_SUPPLEMENT"."I_TOOL_PRICE" IS '价格申报项，在生产进口货物过程中使用的工具、模具和类似货物单位金额；字段代码: I_ToolPrice；数据类型: Decimal；长度: 19,4；暂存必填: 否；申报必填: 否';
COMMENT ON COLUMN "DEC_SUPPLEMENT"."I_TOOL_AMOUNT" IS '价格申报项，在生产进口货物过程中使用的工具、模具和类似货物总金额；字段代码: I_ToolAmount；数据类型: Decimal；长度: 19,2；暂存必填: 否；申报必填: 否';
COMMENT ON COLUMN "DEC_SUPPLEMENT"."I_TOOL_NOTE" IS '价格申报项，在生产进口货物过程中使用的工具、模具和类似货物备注；字段代码: I_ToolNote；数据类型: String；长度: 400；暂存必填: 否；申报必填: 否';
COMMENT ON COLUMN "DEC_SUPPLEMENT"."I_LOSS_PRICE" IS '价格申报项，在生产进口货物过程中消耗的材料单位金额；字段代码: I_LossPrice；数据类型: Decimal；长度: 19,4；暂存必填: 否；申报必填: 否';
COMMENT ON COLUMN "DEC_SUPPLEMENT"."I_LOSS_AMOUNT" IS '价格申报项，在生产进口货物过程中消耗的材料总金额；字段代码: I_LossAmount；数据类型: Decimal；长度: 19,2；暂存必填: 否；申报必填: 否';
COMMENT ON COLUMN "DEC_SUPPLEMENT"."I_LOSS_NOTE" IS '价格申报项，在生产进口货物过程中消耗的材料备注；字段代码: I_LossNote；数据类型: String；长度: 400；暂存必填: 否；申报必填: 否';
COMMENT ON COLUMN "DEC_SUPPLEMENT"."I_DESIGN_PRICE" IS '价格申报项，进口货物在境外进行的为生产进口货物所需的工程设计、技术研发、工艺及制图等相关服务单位金额；字段代码: I_DesignPrice；数据类型: Decimal；长度: 19,4；暂存必填: 否；申报必填: 否';
COMMENT ON COLUMN "DEC_SUPPLEMENT"."I_DESIGN_AMOUNT" IS '价格申报项，进口货物在境外进行的为生产进口货物所需的工程设计、技术研发、工艺及制图等相关服务总金额；字段代码: I_DesignAmount；数据类型: Decimal；长度: 19,2；暂存必填: 否；申报必填: 否';
COMMENT ON COLUMN "DEC_SUPPLEMENT"."I_DESIGN_NOTE" IS '价格申报项，进口货物在境外进行的为生产进口货物所需的工程设计、技术研发、工艺及制图等相关服务备注；字段代码: I_DesignNote；数据类型: String；长度: 400；暂存必填: 否；申报必填: 否';
COMMENT ON COLUMN "DEC_SUPPLEMENT"."I_USEFEE_PRICE" IS '价格申报项，特许权使用费单位金额；字段代码: I_UsefeePrice；数据类型: Decimal；长度: 19,4；暂存必填: 否；申报必填: 否';
COMMENT ON COLUMN "DEC_SUPPLEMENT"."I_USEFEE_AMOUNT" IS '价格申报项，特许权使用费总金额；字段代码: I_UsefeeAmount；数据类型: Decimal；长度: 19,2；暂存必填: 否；申报必填: 否';
COMMENT ON COLUMN "DEC_SUPPLEMENT"."I_USEFEE_NOTE" IS '价格申报项，特许权使用费备注；字段代码: I_UsefeeNote；数据类型: String；长度: 400；暂存必填: 否；申报必填: 否';
COMMENT ON COLUMN "DEC_SUPPLEMENT"."I_PROFIT_PRICE" IS '价格申报项，卖方直接或间接从买方对货物进口后转售、处置或使用所得中获得的收益单位金额；字段代码: I_ProfitPrice；数据类型: Decimal；长度: 19,4；暂存必填: 否；申报必填: 否';
COMMENT ON COLUMN "DEC_SUPPLEMENT"."I_PROFIT_AMOUNT" IS '价格申报项，卖方直接或间接从买方对货物进口后转售、处置或使用所得中获得的收益总金额；字段代码: I_ProfitAmount；数据类型: Decimal；长度: 19,2；暂存必填: 否；申报必填: 否';
COMMENT ON COLUMN "DEC_SUPPLEMENT"."I_PROFIT_NOTE" IS '价格申报项，卖方直接或间接从买方对货物进口后转售、处置或使用所得中获得的收益备注；字段代码: I_ProfitNote；数据类型: String；长度: 400；暂存必填: 否；申报必填: 否';
COMMENT ON COLUMN "DEC_SUPPLEMENT"."I_FEE_PRICE" IS '价格申报项，进口货物运输费用单位金额；字段代码: I_FeePrice；数据类型: Decimal；长度: 19,4；暂存必填: 否；申报必填: 否';
COMMENT ON COLUMN "DEC_SUPPLEMENT"."I_FEE_AMOUNT" IS '价格申报项，进口货物运输费用总金额；字段代码: I_FeeAmount；数据类型: Decimal；长度: 19,2；暂存必填: 否；申报必填: 否';
COMMENT ON COLUMN "DEC_SUPPLEMENT"."I_FEE_NOTE" IS '价格申报项，进口货物运输费用备注；字段代码: I_FeeNote；数据类型: String；长度: 400；暂存必填: 否；申报必填: 否';
COMMENT ON COLUMN "DEC_SUPPLEMENT"."I_OTHER_PRICE" IS '价格申报项，进口货物运输相关费用单位金额；字段代码: I_OtherPrice；数据类型: Decimal；长度: 19,4；暂存必填: 否；申报必填: 否';
COMMENT ON COLUMN "DEC_SUPPLEMENT"."I_OTHER_AMOUNT" IS '价格申报项，进口货物运输相关费用总金额；字段代码: I_OtherAmount；数据类型: Decimal；长度: 19,2；暂存必填: 否；申报必填: 否';
COMMENT ON COLUMN "DEC_SUPPLEMENT"."I_OTHER_NOTE" IS '价格申报项，进口货物运输相关费用备注；字段代码: I_OtherNote；数据类型: String；长度: 400；暂存必填: 否；申报必填: 否';
COMMENT ON COLUMN "DEC_SUPPLEMENT"."I_INSUR_PRICE" IS '价格申报项，进口货物保险费单位金额；字段代码: I_InsurPrice；数据类型: Decimal；长度: 19,4；暂存必填: 否；申报必填: 否';
COMMENT ON COLUMN "DEC_SUPPLEMENT"."I_INSUR_AMOUNT" IS '价格申报项，进口货物保险费总金额；字段代码: I_InsurAmount；数据类型: Decimal；长度: 19,2；暂存必填: 否；申报必填: 否';
COMMENT ON COLUMN "DEC_SUPPLEMENT"."I_INSUR_NOTE" IS '价格申报项，进口货物保险费备注；字段代码: I_InsurNote；数据类型: String；长度: 400；暂存必填: 否；申报必填: 否';
COMMENT ON COLUMN "DEC_SUPPLEMENT"."E_IS_DUTY_DEL" IS '价格申报项，出口关税是否已经从申报价格中扣除；字段代码: E_IsDutyDel；数据类型: String；长度: 1；暂存必填: 否；申报必填: 否';
COMMENT ON COLUMN "DEC_SUPPLEMENT"."G_NAME_OTHER" IS '归类申报项，商品其他名称；字段代码: GNameOther；数据类型: String；长度: 400；暂存必填: 否；申报必填: 否';
COMMENT ON COLUMN "DEC_SUPPLEMENT"."CODE_TS_OTHER" IS '归类申报项，进/出口国地区海关商品编码；字段代码: CodeTsOther；数据类型: String；长度: 15；暂存必填: 否；申报必填: 否';
COMMENT ON COLUMN "DEC_SUPPLEMENT"."IS_CLASS_DECISION" IS '归类申报项；字段代码: IsClassDecision；数据类型: String；长度: 1；暂存必填: 否；申报必填: 否；说明: 该商品是否取得过海关预归类决定书： 0：否、1：是 如选择是，则后面三项必填。';
COMMENT ON COLUMN "DEC_SUPPLEMENT"."DECISION_NO" IS '归类申报项，预归类决定书编号；字段代码: DecisionNO；数据类型: String；长度: 45；暂存必填: 否；申报必填: 否';
COMMENT ON COLUMN "DEC_SUPPLEMENT"."CODE_TS_DECISION" IS '归类申报项，预归类决定书商品编码；字段代码: CodeTsDecision；数据类型: String；长度: 15；暂存必填: 否；申报必填: 否';
COMMENT ON COLUMN "DEC_SUPPLEMENT"."DECISION_CUS" IS '归类申报项，作出预归类决定的直属海关；字段代码: DecisionCus；数据类型: String；长度: 4；暂存必填: 否；申报必填: 否';
COMMENT ON COLUMN "DEC_SUPPLEMENT"."IS_SAMPLE_TEST" IS '归类申报项，该商品是否曾被海关取样化验；字段代码: IsSampleTest；数据类型: String；长度: 1；暂存必填: 否；申报必填: 否；说明: 归类申报项，该商品是否曾被海关取样化验： 0：否、1：是';
COMMENT ON COLUMN "DEC_SUPPLEMENT"."G_OPTIONS" IS '字段代码: GOptions；数据类型: String；长度: 32；暂存必填: 否；申报必填: 否；说明: 归类申报项，商品信息选项： A成分及比例 B原料及组成 C生产加工工艺 D构成 E技术参数 F具体规格 G工作原理 H车型、排量 I功能 J用途 K加工程度 L性能指标 M其他信息 以上可多选，至少选择一项。 存储和上传方式：13位有效位，选择了A－M项的任何一个，则对应位为1，否则为0。';
COMMENT ON COLUMN "DEC_SUPPLEMENT"."TRAF_MODE" IS '运输方式；字段代码: TrafMode；数据类型: String；长度: 1；暂存必填: 否；申报必填: 否';
COMMENT ON COLUMN "DEC_SUPPLEMENT"."IS_DIRECT_TRAF" IS '原产地申报项，是否直接运输；字段代码: IsDirectTraf；数据类型: String；长度: 1；暂存必填: 否；申报必填: 否；说明: 原产地申报项，是否直接运输： 0：否、1：是';
COMMENT ON COLUMN "DEC_SUPPLEMENT"."TRANSIT_COUNTRY" IS '原产地申报项，中转国地区，如果选择非直接运输，必填；字段代码: TransitCountry；数据类型: String；长度: 20；暂存必填: 否；申报必填: 否';
COMMENT ON COLUMN "DEC_SUPPLEMENT"."DEST_PORT" IS '原产地申报项，到货口岸；字段代码: DestPort；数据类型: String；长度: 4；暂存必填: 否；申报必填: 否；说明: CUSTOMS参数';
COMMENT ON COLUMN "DEC_SUPPLEMENT"."DECL_PORT" IS '原产地申报项，申报口岸；字段代码: DeclPort；数据类型: String；长度: 4；暂存必填: 否；申报必填: 否；说明: CUSTOMS参数';
COMMENT ON COLUMN "DEC_SUPPLEMENT"."BILL_NO" IS '原产地申报项，提单编号；字段代码: BillNo；数据类型: String；长度: 48；暂存必填: 否；申报必填: 否';
COMMENT ON COLUMN "DEC_SUPPLEMENT"."ORIGIN_COUNTRY" IS '原产地申报项；字段代码: OriginCountry；数据类型: String；长度: 3；暂存必填: 否；申报必填: 否；说明: COUNTRY参数';
COMMENT ON COLUMN "DEC_SUPPLEMENT"."ORIGIN_MARK" IS '原产地申报项，原产国地区标记的位置；字段代码: OriginMark；数据类型: String；长度: 5；暂存必填: 否；申报必填: 否；说明: 原产地申报项，原产国地区标记的位置： 1：外包装、2：内包装、3：产品本体、4：无';
COMMENT ON COLUMN "DEC_SUPPLEMENT"."CERT_COUNTRY" IS '原产地申报项，原产地证书签发机构及所在国家地区，非参数选项，可录入汉字或字母；字段代码: CertCountry；数据类型: String；长度: 30；暂存必填: 否；申报必填: 否';
COMMENT ON COLUMN "DEC_SUPPLEMENT"."CERT_NO" IS '原产地申报项，原产地证书编号；字段代码: CertNO；数据类型: String；长度: 45；暂存必填: 否；申报必填: 否';
COMMENT ON COLUMN "DEC_SUPPLEMENT"."CERT_STANDARD" IS '原产地申报项，适用的原产地标准；字段代码: CertStandard；数据类型: String；长度: 1；暂存必填: 否；申报必填: 否；说明: 原产地申报项，适用的原产地标准： 1：完全获得、2：税号改变、 3：制造或加工工序、4：从价百分比、5：混合标准、6：其他标准';
COMMENT ON COLUMN "DEC_SUPPLEMENT"."OTHER_NOTE" IS '公共申报项，其他需要说明的情况；字段代码: OtherNote；数据类型: String；长度: 1500；暂存必填: 否；申报必填: 否';
COMMENT ON COLUMN "DEC_SUPPLEMENT"."IS_SECRET" IS '对以上申报内容是否需要海关予以保密；字段代码: IsSecret；数据类型: String；长度: 1；暂存必填: 否；申报必填: 否；说明: 对以上申报内容是否需要海关予以保密： 0：否 1：是';
COMMENT ON COLUMN "DEC_SUPPLEMENT"."AGENT_TYPE" IS '申报单位类型；字段代码: AgentType；数据类型: String；长度: 1；暂存必填: 否；申报必填: 否；说明: 申报单位类型： 1：进出口货物收发货人 2：委托申报的报关企业';
COMMENT ON COLUMN "DEC_SUPPLEMENT"."DECL_ADDR" IS '申报人单位地址；字段代码: DeclAddr；数据类型: String；长度: 255；暂存必填: 否；申报必填: 否';
COMMENT ON COLUMN "DEC_SUPPLEMENT"."DECL_POST" IS '申报人邮编；字段代码: DeclPost；数据类型: String；长度: 10；暂存必填: 否；申报必填: 否';
COMMENT ON COLUMN "DEC_SUPPLEMENT"."DECL_TEL" IS '申报人电话；字段代码: DeclTel；数据类型: String；长度: 30；暂存必填: 否；申报必填: 否';

CREATE TABLE "EDOC_RELATION" (
    "ID" BIGINT IDENTITY(1,1) NOT NULL,
    "DEC_HEAD_ID" BIGINT,
    "EDOC_ID" VARCHAR2(64) NOT NULL,
    "EDOC_CODE" VARCHAR2(8) NOT NULL,
    "EDOC_FOMAT_TYPE" VARCHAR2(2) NOT NULL,
    "OP_NOTE" VARCHAR2(255),
    "EDOC_COP_ID" VARCHAR2(64),
    "EDOC_OWNER_CODE" VARCHAR2(10),
    "SIGN_UNIT" VARCHAR2(10),
    "SIGN_TIME" VARCHAR2(17),
    "EDOC_OWNER_NAME" VARCHAR2(100),
    "EDOC_SIZE" VARCHAR2(12),
    "G_NO_STR" VARCHAR2(150),
    CONSTRAINT "PK_EDOC_RELATION" PRIMARY KEY ("ID")
);

COMMENT ON TABLE "EDOC_RELATION" IS '电子随附单据关联关系信息EdocRealation';
COMMENT ON COLUMN "EDOC_RELATION"."ID" IS '主键ID，达梦自增标识列';
COMMENT ON COLUMN "EDOC_RELATION"."DEC_HEAD_ID" IS '报关单表头ID，父表关联ID';
COMMENT ON COLUMN "EDOC_RELATION"."EDOC_ID" IS '文件名、随附单据编号（文件名命名规则是：申报口岸+随附单据类别代码+IM+18位流水号+.pdf）；字段代码: EdocID；数据类型: String；长度: 64；暂存必填: 是/是；申报必填: 否';
COMMENT ON COLUMN "EDOC_RELATION"."EDOC_CODE" IS '随附单证类别；字段代码: EdocCode；数据类型: String；长度: 8；暂存必填: 是/是；申报必填: 否；说明: 如： 00000001:发票 00000002:装箱单 00000003:提/运单 00000004:合同 10000001：代理报关委托协议（电子） 10000002：减免税货物税款担保证明 10000003：减免税货物税款担保延期证明 具体详见海关总署网站《随附单据代码参数表》';
COMMENT ON COLUMN "EDOC_RELATION"."EDOC_FOMAT_TYPE" IS '随附单据格式类型；字段代码: EdocFomatType；数据类型: String；长度: 2；暂存必填: 是/是；申报必填: 否；说明: S:结构化 US:非结构化（pdf文件填写US）';
COMMENT ON COLUMN "EDOC_RELATION"."OP_NOTE" IS '操作说明（重传原因）；字段代码: OpNote；数据类型: String；长度: 255；暂存必填: 否；申报必填: 否';
COMMENT ON COLUMN "EDOC_RELATION"."EDOC_COP_ID" IS '随附单据文件企业名；字段代码: EdocCopId；数据类型: String；长度: 64；暂存必填: 否；申报必填: 否';
COMMENT ON COLUMN "EDOC_RELATION"."EDOC_OWNER_CODE" IS '所属单位海关编号；字段代码: EdocOwnerCode；数据类型: String；长度: 10；暂存必填: 否；申报必填: 否';
COMMENT ON COLUMN "EDOC_RELATION"."SIGN_UNIT" IS '签名单位代码；字段代码: SignUnit；数据类型: String；长度: 10；暂存必填: 否；申报必填: 否';
COMMENT ON COLUMN "EDOC_RELATION"."SIGN_TIME" IS '签名时间；字段代码: SignTime；数据类型: String；长度: 17；暂存必填: 否；申报必填: 否；说明: 格式为：yyyyMMdd hh:mm:ss';
COMMENT ON COLUMN "EDOC_RELATION"."EDOC_OWNER_NAME" IS '所属单位名称；字段代码: EdocOwnerName；数据类型: String；长度: 100；暂存必填: 否；申报必填: 否';
COMMENT ON COLUMN "EDOC_RELATION"."EDOC_SIZE" IS '随附单据文件大小；字段代码: EdocSize；数据类型: String；长度: 12；暂存必填: 否；申报必填: 否';
COMMENT ON COLUMN "EDOC_RELATION"."G_NO_STR" IS '商品项号关系；字段代码: GNoStr；数据类型: String；长度: 150；暂存必填: 否；申报必填: 否；说明: 商品项号关系，多个商品项号用英文半角逗号分隔，如“1,3”。';

CREATE TABLE "ECO_RELATION" (
    "ID" BIGINT IDENTITY(1,1) NOT NULL,
    "DEC_HEAD_ID" BIGINT,
    "DEC_LIST_ID" BIGINT,
    "CERT_TYPE" VARCHAR2(3) NOT NULL,
    "DEC_G_NO" VARCHAR2(9) NOT NULL,
    "ECO_CERT_NO" VARCHAR2(64) NOT NULL,
    "ECO_G_NO" VARCHAR2(9) NOT NULL,
    "EXTEND_FILED" VARCHAR2(255),
    CONSTRAINT "PK_ECO_RELATION" PRIMARY KEY ("ID")
);

COMMENT ON TABLE "ECO_RELATION" IS '随附单证对应关系EcoRelation';
COMMENT ON COLUMN "ECO_RELATION"."ID" IS '主键ID，达梦自增标识列';
COMMENT ON COLUMN "ECO_RELATION"."DEC_HEAD_ID" IS '报关单表头ID，父表关联ID';
COMMENT ON COLUMN "ECO_RELATION"."DEC_LIST_ID" IS '报关单商品表体ID，父表关联ID';
COMMENT ON COLUMN "ECO_RELATION"."CERT_TYPE" IS '单证代码；字段代码: CertType；数据类型: String；长度: 3；暂存必填: 是/是；申报必填: 否；说明: 1、当导入优惠贸易协定享惠信息中的原产地证明商品项号关联关系时，单证代码填写格式为：Y+优惠贸易协定代码。样例:Y03。2、当导入其他随附单证商品项号关联关系时，单证代码填写随附单证代码。';
COMMENT ON COLUMN "ECO_RELATION"."DEC_G_NO" IS '报关单商品项号；字段代码: DecGNo；数据类型: String；长度: 9；暂存必填: 是/是；申报必填: 否';
COMMENT ON COLUMN "ECO_RELATION"."ECO_CERT_NO" IS '单证编号；字段代码: EcoCertNo；数据类型: String；长度: 64；暂存必填: 是/是；申报必填: 否；说明: 当导入优惠贸易协定享惠信息中的原产地证明商品项号关联关系时，单证编号填写原产地证明类型（C/D）+原产地证明编号；如原产地证明类型为空，则仅填写原产地证明编号。样例：C1234567890。';
COMMENT ON COLUMN "ECO_RELATION"."ECO_G_NO" IS '对应随附单证商品项号；字段代码: EcoGNo；数据类型: String；长度: 9；暂存必填: 是/是；申报必填: 否';
COMMENT ON COLUMN "ECO_RELATION"."EXTEND_FILED" IS '扩展字段；字段代码: ExtendFiled；数据类型: String；长度: 255；暂存必填: 否/否；申报必填: 否';

CREATE TABLE "SDD_TAX" (
    "ID" BIGINT IDENTITY(1,1) NOT NULL,
    "DEC_HEAD_ID" BIGINT,
    "FILE_NAME" VARCHAR2(18),
    "SEQ_NO" VARCHAR2(18),
    "IE_FLAG" VARCHAR2(1),
    "DECL_PORT" VARCHAR2(4),
    "D_DATE" VARCHAR2(10),
    "TRADE_CO" VARCHAR2(10),
    "TRADE_NAME" VARCHAR2(70),
    "IN_RATIO" DECIMAL(21,7),
    "TRADE_MODE" VARCHAR2(4),
    "CUT_MODE" VARCHAR2(3),
    "TRANS_MODE" VARCHAR2(1),
    "FEE_MARK" VARCHAR2(1),
    "FEE_CURR" VARCHAR2(3),
    "FEE_RATE" DECIMAL(21,7),
    "INSUR_MARK" VARCHAR2(1),
    "INSUR_CURR" VARCHAR2(1),
    "INSUR_RATE" DECIMAL(21,7),
    "OTHER_MARK" VARCHAR2(1),
    "OTHER_CURR" VARCHAR2(3),
    "OTHER_RATE" DECIMAL(21,7),
    "MANUAL_NO" VARCHAR2(12),
    "TRADE_CO_SCC" VARCHAR2(18),
    "GROSS_WT" DECIMAL(21,7),
    "NOTE_S" VARCHAR2(255),
    "LEGAL_LIABILITY" VARCHAR2(255) NOT NULL,
    "SIGN" VARCHAR2(255) NOT NULL,
    "MESS_ID" VARCHAR2(20),
    "CERT_SEQ_NO" VARCHAR2(64) NOT NULL,
    "STATUS" VARCHAR2(1),
    CONSTRAINT "PK_SDD_TAX" PRIMARY KEY ("ID")
);

COMMENT ON TABLE "SDD_TAX" IS '报税单SddTax';
COMMENT ON COLUMN "SDD_TAX"."ID" IS '主键ID，达梦自增标识列';
COMMENT ON COLUMN "SDD_TAX"."DEC_HEAD_ID" IS '关联报关单表头ID，父表关联ID';
COMMENT ON COLUMN "SDD_TAX"."FILE_NAME" IS '文件名称；字段代码: FileName；数据类型: String；长度: 18；暂存必填: 否/否；申报必填: 否';
COMMENT ON COLUMN "SDD_TAX"."SEQ_NO" IS '数据中心统一编号；字段代码: SeqNo；数据类型: String；长度: 18；暂存必填: 否/否；申报必填: 否';
COMMENT ON COLUMN "SDD_TAX"."IE_FLAG" IS '进出口标志；字段代码: IEFlag；数据类型: String；长度: 1；暂存必填: 否/否；申报必填: 否';
COMMENT ON COLUMN "SDD_TAX"."DECL_PORT" IS '申报口岸；字段代码: DeclPort；数据类型: String；长度: 4；暂存必填: 否/否；申报必填: 否';
COMMENT ON COLUMN "SDD_TAX"."D_DATE" IS '预计申报日期；字段代码: DDate；数据类型: String；长度: 10；暂存必填: 否/否；申报必填: 否';
COMMENT ON COLUMN "SDD_TAX"."TRADE_CO" IS '境内收发货人编号；字段代码: TradeCo；数据类型: String；长度: 10；暂存必填: 否/否；申报必填: 否';
COMMENT ON COLUMN "SDD_TAX"."TRADE_NAME" IS '境内收发货人名称；字段代码: TradeName；数据类型: String；长度: 70；暂存必填: 否/否；申报必填: 否';
COMMENT ON COLUMN "SDD_TAX"."IN_RATIO" IS '内销比率；字段代码: InRatio；数据类型: Decimal；长度: 21,7；暂存必填: 否/否；申报必填: 否';
COMMENT ON COLUMN "SDD_TAX"."TRADE_MODE" IS '监管方式；字段代码: TradeMode；数据类型: String；长度: 4；暂存必填: 否/否；申报必填: 否';
COMMENT ON COLUMN "SDD_TAX"."CUT_MODE" IS '征免性质分类；字段代码: CutMode；数据类型: String；长度: 3；暂存必填: 否/否；申报必填: 否';
COMMENT ON COLUMN "SDD_TAX"."TRANS_MODE" IS '成交方式；字段代码: TransMode；数据类型: String；长度: 1；暂存必填: 否/否；申报必填: 否';
COMMENT ON COLUMN "SDD_TAX"."FEE_MARK" IS '运费标记；字段代码: FeeMark；数据类型: String；长度: 1；暂存必填: 否/否；申报必填: 否';
COMMENT ON COLUMN "SDD_TAX"."FEE_CURR" IS '运费币制；字段代码: FeeCurr；数据类型: String；长度: 3；暂存必填: 否/否；申报必填: 否';
COMMENT ON COLUMN "SDD_TAX"."FEE_RATE" IS '运费／率；字段代码: FeeRate；数据类型: Decimal；长度: 21,7；暂存必填: 否/否；申报必填: 否';
COMMENT ON COLUMN "SDD_TAX"."INSUR_MARK" IS '保险费标记；字段代码: InsurMark；数据类型: String；长度: 1；暂存必填: 否/否；申报必填: 否';
COMMENT ON COLUMN "SDD_TAX"."INSUR_CURR" IS '保险费币制；字段代码: InsurCurr；数据类型: String；长度: 1；暂存必填: 否/否；申报必填: 否';
COMMENT ON COLUMN "SDD_TAX"."INSUR_RATE" IS '保险费／率；字段代码: InsurRate；数据类型: Decimal；长度: 21,7；暂存必填: 否/否；申报必填: 否';
COMMENT ON COLUMN "SDD_TAX"."OTHER_MARK" IS '杂费标记；字段代码: OtherMark；数据类型: String；长度: 1；暂存必填: 否/否；申报必填: 否';
COMMENT ON COLUMN "SDD_TAX"."OTHER_CURR" IS '杂费币制；字段代码: OtherCurr；数据类型: String；长度: 3；暂存必填: 否/否；申报必填: 否';
COMMENT ON COLUMN "SDD_TAX"."OTHER_RATE" IS '杂费／率；字段代码: OtherRate；数据类型: Decimal；长度: 21,7；暂存必填: 否/否；申报必填: 否';
COMMENT ON COLUMN "SDD_TAX"."MANUAL_NO" IS '备案号；字段代码: ManualNo；数据类型: String；长度: 12；暂存必填: 否/否；申报必填: 否';
COMMENT ON COLUMN "SDD_TAX"."TRADE_CO_SCC" IS '境内收发货人统一代码；字段代码: TradeCoScc；数据类型: String；长度: 18；暂存必填: 否/否；申报必填: 否';
COMMENT ON COLUMN "SDD_TAX"."GROSS_WT" IS '毛重；字段代码: GrossWt；数据类型: Decimal；长度: 21,7；暂存必填: 否/否；申报必填: 否';
COMMENT ON COLUMN "SDD_TAX"."NOTE_S" IS '备注；字段代码: NoteS；数据类型: String；长度: 255；暂存必填: 否/否；申报必填: 否';
COMMENT ON COLUMN "SDD_TAX"."LEGAL_LIABILITY" IS '法律责任；字段代码: LegalLiability；数据类型: String；长度: 255；暂存必填: 否/是；申报必填: 否';
COMMENT ON COLUMN "SDD_TAX"."SIGN" IS '数字签名信息；字段代码: Sign；数据类型: String；长度: 255；暂存必填: 否/是；申报必填: 否';
COMMENT ON COLUMN "SDD_TAX"."MESS_ID" IS '签名消息号；字段代码: MessId；数据类型: String；长度: 20；暂存必填: 否/否；申报必填: 否';
COMMENT ON COLUMN "SDD_TAX"."CERT_SEQ_NO" IS '签名证书号；字段代码: CertSeqNo；数据类型: String；长度: 64；暂存必填: 否/是；申报必填: 否';
COMMENT ON COLUMN "SDD_TAX"."STATUS" IS '状态；字段代码: Status；数据类型: String；长度: 1；暂存必填: 否/否；申报必填: 否';

CREATE TABLE "DEC_RISK" (
    "ID" BIGINT IDENTITY(1,1) NOT NULL,
    "DEC_HEAD_ID" BIGINT,
    "RISK" VARCHAR2(10),
    "SIGN" VARCHAR2(255),
    "SIGN_DATE" TIMESTAMP,
    "NOTE" VARCHAR2(100),
    CONSTRAINT "PK_DEC_RISK" PRIMARY KEY ("ID")
);

COMMENT ON TABLE "DEC_RISK" IS '风险评估信息DecRisk';
COMMENT ON COLUMN "DEC_RISK"."ID" IS '主键ID，达梦自增标识列';
COMMENT ON COLUMN "DEC_RISK"."DEC_HEAD_ID" IS '报关单表头ID，父表关联ID';
COMMENT ON COLUMN "DEC_RISK"."RISK" IS '风险评估结果；字段代码: Risk；数据类型: String；长度: 10；暂存必填: 否；申报必填: 否';
COMMENT ON COLUMN "DEC_RISK"."SIGN" IS '数字签名信息；字段代码: Sign；数据类型: String；长度: 255；暂存必填: 否；申报必填: 否';
COMMENT ON COLUMN "DEC_RISK"."SIGN_DATE" IS '处理日期；字段代码: SignDate；数据类型: DateTime；长度: 16；暂存必填: 否；申报必填: 否';
COMMENT ON COLUMN "DEC_RISK"."NOTE" IS '备注；字段代码: Note；数据类型: String；长度: 100；暂存必填: 否；申报必填: 否';

CREATE TABLE "DEC_COP_PROMISE" (
    "ID" BIGINT IDENTITY(1,1) NOT NULL,
    "DEC_HEAD_ID" BIGINT,
    "DECLARATIO_MATERIAL_CODE" VARCHAR2(10) NOT NULL,
    CONSTRAINT "PK_DEC_COP_PROMISE" PRIMARY KEY ("ID")
);

COMMENT ON TABLE "DEC_COP_PROMISE" IS '企业承诺信息DecCopPromise';
COMMENT ON COLUMN "DEC_COP_PROMISE"."ID" IS '主键ID，达梦自增标识列';
COMMENT ON COLUMN "DEC_COP_PROMISE"."DEC_HEAD_ID" IS '报关单表头ID，父表关联ID';
COMMENT ON COLUMN "DEC_COP_PROMISE"."DECLARATIO_MATERIAL_CODE" IS '证明/声明材料代码；字段代码: DeclaratioMaterialCode；数据类型: String；长度: 10；暂存必填: 否；申报必填: 是；说明: 进口填写：“101040” 出口填写：“102053”';

CREATE TABLE "DEC_ROYALTY_FEE" (
    "ID" BIGINT IDENTITY(1,1) NOT NULL,
    "DEC_HEAD_ID" BIGINT,
    "PRICE_PRE_DETERMIN_NO" VARCHAR2(20),
    "TAX_ROYALTY_DECL_TYPE" VARCHAR2(1),
    "CONTRACT_NO" VARCHAR2(500) NOT NULL,
    "AUTHORIZER" VARCHAR2(500) NOT NULL,
    "AUTHORIZED_PERSON" VARCHAR2(500) NOT NULL,
    "PAY_TYPE" VARCHAR2(20) NOT NULL,
    "PAY_TIME" VARCHAR2(10) NOT NULL,
    "PAY_PERIOD" INTEGER,
    "EFFECTIVE_DATE_TIME" VARCHAR2(10) NOT NULL,
    "EXPIRATION_DATE_TIME" DATE NOT NULL,
    "ROYALTY_AMOUNT" DECIMAL(19,2) NOT NULL,
    "CURR" VARCHAR2(3) NOT NULL,
    "ROYALTY_FEE_TYPE" VARCHAR2(5) NOT NULL,
    "EDOC_TYPE" VARCHAR2(8) NOT NULL,
    "STATMENT" VARCHAR2(2500) NOT NULL,
    "IS_SECRET" VARCHAR2(1) NOT NULL,
    "IS_CUS_AUDIT" VARCHAR2(1),
    "IS_CUS_PRICE_PRE_DETERMIN" VARCHAR2(1),
    "ISSUE_DATE_TIME" VARCHAR2(10) NOT NULL,
    "PERIOD_START_DATE" VARCHAR2(10),
    "PERIOD_END_DATE" VARCHAR2(10),
    CONSTRAINT "PK_DEC_ROYALTY_FEE" PRIMARY KEY ("ID")
);

COMMENT ON TABLE "DEC_ROYALTY_FEE" IS '特许权使用费申请表DecRoyaltyFee';
COMMENT ON COLUMN "DEC_ROYALTY_FEE"."ID" IS '主键ID，达梦自增标识列';
COMMENT ON COLUMN "DEC_ROYALTY_FEE"."DEC_HEAD_ID" IS '报关单表头ID，父表关联ID';
COMMENT ON COLUMN "DEC_ROYALTY_FEE"."PRICE_PRE_DETERMIN_NO" IS '价格预裁定编号；字段代码: PricePreDeterminNo；数据类型: String；长度: 20；暂存必填: 否；申报必填: 否；说明: 是否经过海关价格预裁定选择是时必须填写，需要进行长度审核，同预裁定系统的编号要求。';
COMMENT ON COLUMN "DEC_ROYALTY_FEE"."TAX_ROYALTY_DECL_TYPE" IS '应税特许权使用费申报情形；字段代码: TaxRoyaltyDeclType；数据类型: String；长度: 1；暂存必填: 否；申报必填: 否；说明: 0：首次申报 1：延续性申报';
COMMENT ON COLUMN "DEC_ROYALTY_FEE"."CONTRACT_NO" IS '合同/协议号；字段代码: ContractNo；数据类型: String；长度: 500；暂存必填: 是；申报必填: 是';
COMMENT ON COLUMN "DEC_ROYALTY_FEE"."AUTHORIZER" IS '授权方；字段代码: Authorizer；数据类型: String；长度: 500；暂存必填: 是；申报必填: 是';
COMMENT ON COLUMN "DEC_ROYALTY_FEE"."AUTHORIZED_PERSON" IS '被授权方；字段代码: AuthorizedPerson；数据类型: String；长度: 500；暂存必填: 是；申报必填: 是';
COMMENT ON COLUMN "DEC_ROYALTY_FEE"."PAY_TYPE" IS '支付方式；字段代码: PayType；数据类型: String；长度: 20；暂存必填: 是；申报必填: 是；说明: 0：一次性支付 1：定期支付 2：混合';
COMMENT ON COLUMN "DEC_ROYALTY_FEE"."PAY_TIME" IS '支付时间；字段代码: PayTime；数据类型: String；长度: 10；暂存必填: 是；申报必填: 是；说明: 年月日 yyyy-MM-dd';
COMMENT ON COLUMN "DEC_ROYALTY_FEE"."PAY_PERIOD" IS '支付计提周期；字段代码: PayPeriod；数据类型: Integer；长度: 2；暂存必填: 否；申报必填: 否；说明: 按月来计算';
COMMENT ON COLUMN "DEC_ROYALTY_FEE"."EFFECTIVE_DATE_TIME" IS '合同/协议起始执行时间；字段代码: EffectiveDateTime；数据类型: String；长度: 10；暂存必填: 是；申报必填: 是；说明: 年月日 yyyy-MM-dd';
COMMENT ON COLUMN "DEC_ROYALTY_FEE"."EXPIRATION_DATE_TIME" IS '合同协议终止时间；字段代码: ExpirationDateTime；数据类型: DATE；长度: 10；暂存必填: 是；申报必填: 是；说明: 如果合同未明确规定有效时间或有效时间为长期，录入终止时间为10年，年月日.格式：yyyy-MM-dd';
COMMENT ON COLUMN "DEC_ROYALTY_FEE"."ROYALTY_AMOUNT" IS '特许权使用费金额；字段代码: RoyaltyAmount；数据类型: Decimal；长度: 19,2；暂存必填: 是；申报必填: 是；说明: 小数点后2位';
COMMENT ON COLUMN "DEC_ROYALTY_FEE"."CURR" IS '币制；字段代码: Curr；数据类型: String；长度: 3；暂存必填: 是；申报必填: 是';
COMMENT ON COLUMN "DEC_ROYALTY_FEE"."ROYALTY_FEE_TYPE" IS '特许权使用费类型；字段代码: RoyaltyFeeType；数据类型: String；长度: 5；暂存必填: 是；申报必填: 是；说明: 按位存放 第一位:专利权或者专有技术使用权 第二位:商标权 第三位:著作权 第四位:分销权、销售权或者其他类似权利 (0代表没有勾选,1代表勾选)';
COMMENT ON COLUMN "DEC_ROYALTY_FEE"."EDOC_TYPE" IS '随附材料清单类型；字段代码: EdocType；数据类型: String；长度: 8；暂存必填: 是；申报必填: 是；说明: 按位存放 第一位:特许权使用费涉及的原进口货物报关单号 第二位:特许权使用费合同/协议 第三位:特许权使用费发票 第四位:特许权使用费支付凭证 第五位:代扣代缴税纳税凭证 第六位:特许权使用费其他 (0代表没有勾选,1代表勾选)';
COMMENT ON COLUMN "DEC_ROYALTY_FEE"."STATMENT" IS '说明；字段代码: Statment；数据类型: String；长度: 2500；暂存必填: 是；申报必填: 是';
COMMENT ON COLUMN "DEC_ROYALTY_FEE"."IS_SECRET" IS '是否保密；字段代码: IsSecret；数据类型: String；长度: 1；暂存必填: 是；申报必填: 是；说明: 是否保密：1：是、0：否';
COMMENT ON COLUMN "DEC_ROYALTY_FEE"."IS_CUS_AUDIT" IS '是否经过海关审核认定；字段代码: IsCusAudit；数据类型: String；长度: 1；暂存必填: 否；申报必填: 否；说明: 是否经过海关审核认定： 1：是、0：否';
COMMENT ON COLUMN "DEC_ROYALTY_FEE"."IS_CUS_PRICE_PRE_DETERMIN" IS '是否经过海关价格预裁定；字段代码: IsCusPricePreDetermin；数据类型: String；长度: 1；暂存必填: 否；申报必填: 否；说明: 是否经过海关价格预裁定： 1：是、0：否';
COMMENT ON COLUMN "DEC_ROYALTY_FEE"."ISSUE_DATE_TIME" IS '合同/协议签约时间；字段代码: IssueDateTime；数据类型: String；长度: 10；暂存必填: 是；申报必填: 是；说明: 年月日 yyyy-MM-dd';
COMMENT ON COLUMN "DEC_ROYALTY_FEE"."PERIOD_START_DATE" IS '本次支付对应的计提周期起始时间；字段代码: PeriodStartDate；数据类型: String；长度: 10；暂存必填: 否；申报必填: 否；说明: 年月日 yyyy-MM-dd';
COMMENT ON COLUMN "DEC_ROYALTY_FEE"."PERIOD_END_DATE" IS '本次支付对应的计提周期终止时间；字段代码: PeriodEndDate；数据类型: String；长度: 10；暂存必填: 否；申报必填: 否；说明: 年月日 yyyy-MM-dd';

CREATE TABLE "DEC_TP_ACCESS" (
    "ID" BIGINT IDENTITY(1,1) NOT NULL,
    "DEC_HEAD_ID" BIGINT,
    "TRANSITION_APPLY" VARCHAR2(1),
    "TRANSITION_SITE" VARCHAR2(11),
    "CONDITIONAL_LIFTOFF_APPLY" VARCHAR2(1),
    "PORT_DEST_MERGE_CHECK_APPLY" VARCHAR2(1),
    CONSTRAINT "PK_DEC_TP_ACCESS" PRIMARY KEY ("ID")
);

COMMENT ON TABLE "DEC_TP_ACCESS" IS '两段准入申请DecTpAccess';
COMMENT ON COLUMN "DEC_TP_ACCESS"."ID" IS '主键ID，达梦自增标识列';
COMMENT ON COLUMN "DEC_TP_ACCESS"."DEC_HEAD_ID" IS '报关单表头ID，父表关联ID';
COMMENT ON COLUMN "DEC_TP_ACCESS"."TRANSITION_APPLY" IS '转场申请；字段代码: TransitionApply；数据类型: String；长度: 1；暂存必填: 否；申报必填: 否；说明: 空/0：未勾选 1：勾选';
COMMENT ON COLUMN "DEC_TP_ACCESS"."TRANSITION_SITE" IS '转入场所场地；字段代码: TransitionSite；数据类型: String；长度: 11；暂存必填: 否；申报必填: 否；说明: 填写监管场地海关编码； 仅在勾选了“转场申请”时必填。';
COMMENT ON COLUMN "DEC_TP_ACCESS"."CONDITIONAL_LIFTOFF_APPLY" IS '附条件提离申请；字段代码: ConditionalLiftoffApply；数据类型: String；长度: 1；暂存必填: 否；申报必填: 否；说明: 空/0：未勾选 1：勾选';
COMMENT ON COLUMN "DEC_TP_ACCESS"."PORT_DEST_MERGE_CHECK_APPLY" IS '口岸与目的地合并检查申请；字段代码: PortDestMergeCheckApply；数据类型: String；长度: 1；暂存必填: 否；申报必填: 否；说明: 空/0：未勾选 1：勾选';

CREATE TABLE "DEC_CONTROL_MODEL" (
    "ID" BIGINT IDENTITY(1,1) NOT NULL,
    "DEC_HEAD_ID" BIGINT,
    "DEC_LIST_ID" BIGINT,
    "ELEMENT_NO" VARCHAR2(19) NOT NULL,
    "ELEMENT_VALUE" VARCHAR2(512),
    "DECLARATION_CATEGORY_CODE" VARCHAR2(4) NOT NULL,
    CONSTRAINT "PK_DEC_CONTROL_MODEL" PRIMARY KEY ("ID")
);

COMMENT ON TABLE "DEC_CONTROL_MODEL" IS '禁限管制申报要素DecControlModel';
COMMENT ON COLUMN "DEC_CONTROL_MODEL"."ID" IS '主键ID，达梦自增标识列';
COMMENT ON COLUMN "DEC_CONTROL_MODEL"."DEC_HEAD_ID" IS '报关单表头ID，父表关联ID';
COMMENT ON COLUMN "DEC_CONTROL_MODEL"."DEC_LIST_ID" IS '报关单商品表体ID，父表关联ID';
COMMENT ON COLUMN "DEC_CONTROL_MODEL"."ELEMENT_NO" IS '要素序号；字段代码: ElementNo；数据类型: String；长度: 19；暂存必填: 是；申报必填: 是；说明: 禁限管制申报要素序号';
COMMENT ON COLUMN "DEC_CONTROL_MODEL"."ELEMENT_VALUE" IS '要素值；字段代码: ElementValue；数据类型: String；长度: 512；暂存必填: 否；申报必填: 否；说明: 禁限管制申报要素值';
COMMENT ON COLUMN "DEC_CONTROL_MODEL"."DECLARATION_CATEGORY_CODE" IS '禁限管制规范申报类别；字段代码: DeclarationCategoryCode；数据类型: String；长度: 4；暂存必填: 是；申报必填: 是；说明: 填写4位禁限管制规范申报类别代码';

-- 索引
CREATE INDEX "IDX_DEC_LIST_DEC_HEAD_ID" ON "DEC_LIST" ("DEC_HEAD_ID");
CREATE INDEX "IDX_DEC_CONTAINER_DEC_HEAD_ID" ON "DEC_CONTAINER" ("DEC_HEAD_ID");
CREATE INDEX "IDX_DEC_CONTAINER_DEC_LIST_ID" ON "DEC_CONTAINER" ("DEC_LIST_ID");
CREATE INDEX "IDX_DEC_LICENSE_DOCUS_DEC_HEAD_ID" ON "DEC_LICENSE_DOCUS" ("DEC_HEAD_ID");
CREATE INDEX "IDX_DEC_REQUEST_CERT_DEC_HEAD_ID" ON "DEC_REQUEST_CERT" ("DEC_HEAD_ID");
CREATE INDEX "IDX_DEC_GOODS_LIMIT_DEC_HEAD_ID" ON "DEC_GOODS_LIMIT" ("DEC_HEAD_ID");
CREATE INDEX "IDX_DEC_GOODS_LIMIT_DEC_LIST_ID" ON "DEC_GOODS_LIMIT" ("DEC_LIST_ID");
CREATE INDEX "IDX_DEC_GOODS_LIMIT_VIN_DEC_HEAD_ID" ON "DEC_GOODS_LIMIT_VIN" ("DEC_HEAD_ID");
CREATE INDEX "IDX_DEC_GOODS_LIMIT_VIN_DEC_GOODS_LIMIT_ID" ON "DEC_GOODS_LIMIT_VIN" ("DEC_GOODS_LIMIT_ID");
CREATE INDEX "IDX_DEC_OTHER_PACK_DEC_HEAD_ID" ON "DEC_OTHER_PACK" ("DEC_HEAD_ID");
CREATE INDEX "IDX_DEC_COP_LIMIT_DEC_HEAD_ID" ON "DEC_COP_LIMIT" ("DEC_HEAD_ID");
CREATE INDEX "IDX_DEC_USER_DEC_HEAD_ID" ON "DEC_USER" ("DEC_HEAD_ID");
CREATE INDEX "IDX_DEC_MARK_LOB_DEC_HEAD_ID" ON "DEC_MARK_LOB" ("DEC_HEAD_ID");
CREATE INDEX "IDX_DEC_FREE_TXT_DEC_HEAD_ID" ON "DEC_FREE_TXT" ("DEC_HEAD_ID");
CREATE INDEX "IDX_DEC_SIGN_DEC_HEAD_ID" ON "DEC_SIGN" ("DEC_HEAD_ID");
CREATE INDEX "IDX_TRN_HEAD_DEC_HEAD_ID" ON "TRN_HEAD" ("DEC_HEAD_ID");
CREATE INDEX "IDX_TRN_LIST_TRN_HEAD_ID" ON "TRN_LIST" ("TRN_HEAD_ID");
CREATE INDEX "IDX_TRN_CONTAINER_TRN_HEAD_ID" ON "TRN_CONTAINER" ("TRN_HEAD_ID");
CREATE INDEX "IDX_TRN_CONTAINER_TRN_LIST_ID" ON "TRN_CONTAINER" ("TRN_LIST_ID");
CREATE INDEX "IDX_TRN_CONTA_GOODS_LIST_TRN_CONTAINER_ID" ON "TRN_CONTA_GOODS_LIST" ("TRN_CONTAINER_ID");
CREATE INDEX "IDX_TRN_CONTA_GOODS_LIST_TRN_LIST_ID" ON "TRN_CONTA_GOODS_LIST" ("TRN_LIST_ID");
CREATE INDEX "IDX_DEC_SUPPLEMENT_DEC_HEAD_ID" ON "DEC_SUPPLEMENT" ("DEC_HEAD_ID");
CREATE INDEX "IDX_DEC_SUPPLEMENT_DEC_LIST_ID" ON "DEC_SUPPLEMENT" ("DEC_LIST_ID");
CREATE INDEX "IDX_EDOC_RELATION_DEC_HEAD_ID" ON "EDOC_RELATION" ("DEC_HEAD_ID");
CREATE INDEX "IDX_ECO_RELATION_DEC_HEAD_ID" ON "ECO_RELATION" ("DEC_HEAD_ID");
CREATE INDEX "IDX_ECO_RELATION_DEC_LIST_ID" ON "ECO_RELATION" ("DEC_LIST_ID");
CREATE INDEX "IDX_SDD_TAX_DEC_HEAD_ID" ON "SDD_TAX" ("DEC_HEAD_ID");
CREATE INDEX "IDX_DEC_RISK_DEC_HEAD_ID" ON "DEC_RISK" ("DEC_HEAD_ID");
CREATE INDEX "IDX_DEC_COP_PROMISE_DEC_HEAD_ID" ON "DEC_COP_PROMISE" ("DEC_HEAD_ID");
CREATE INDEX "IDX_DEC_ROYALTY_FEE_DEC_HEAD_ID" ON "DEC_ROYALTY_FEE" ("DEC_HEAD_ID");
CREATE INDEX "IDX_DEC_TP_ACCESS_DEC_HEAD_ID" ON "DEC_TP_ACCESS" ("DEC_HEAD_ID");
CREATE INDEX "IDX_DEC_CONTROL_MODEL_DEC_HEAD_ID" ON "DEC_CONTROL_MODEL" ("DEC_HEAD_ID");
CREATE INDEX "IDX_DEC_CONTROL_MODEL_DEC_LIST_ID" ON "DEC_CONTROL_MODEL" ("DEC_LIST_ID");

-- 外键
ALTER TABLE "DEC_LIST" ADD CONSTRAINT "FK_DEC_LIST_DEC_HEAD_ID" FOREIGN KEY ("DEC_HEAD_ID") REFERENCES "DEC_HEAD" ("ID") ON DELETE SET NULL;
ALTER TABLE "DEC_CONTAINER" ADD CONSTRAINT "FK_DEC_CONTAINER_DEC_HEAD_ID" FOREIGN KEY ("DEC_HEAD_ID") REFERENCES "DEC_HEAD" ("ID") ON DELETE SET NULL;
ALTER TABLE "DEC_CONTAINER" ADD CONSTRAINT "FK_DEC_CONTAINER_DEC_LIST_ID" FOREIGN KEY ("DEC_LIST_ID") REFERENCES "DEC_LIST" ("ID") ON DELETE SET NULL;
ALTER TABLE "DEC_LICENSE_DOCUS" ADD CONSTRAINT "FK_DEC_LICENSE_DOCUS_DEC_HEAD_ID" FOREIGN KEY ("DEC_HEAD_ID") REFERENCES "DEC_HEAD" ("ID") ON DELETE SET NULL;
ALTER TABLE "DEC_REQUEST_CERT" ADD CONSTRAINT "FK_DEC_REQUEST_CERT_DEC_HEAD_ID" FOREIGN KEY ("DEC_HEAD_ID") REFERENCES "DEC_HEAD" ("ID") ON DELETE SET NULL;
ALTER TABLE "DEC_GOODS_LIMIT" ADD CONSTRAINT "FK_DEC_GOODS_LIMIT_DEC_HEAD_ID" FOREIGN KEY ("DEC_HEAD_ID") REFERENCES "DEC_HEAD" ("ID") ON DELETE SET NULL;
ALTER TABLE "DEC_GOODS_LIMIT" ADD CONSTRAINT "FK_DEC_GOODS_LIMIT_DEC_LIST_ID" FOREIGN KEY ("DEC_LIST_ID") REFERENCES "DEC_LIST" ("ID") ON DELETE SET NULL;
ALTER TABLE "DEC_GOODS_LIMIT_VIN" ADD CONSTRAINT "FK_DEC_GOODS_LIMIT_VIN_DEC_HEAD_ID" FOREIGN KEY ("DEC_HEAD_ID") REFERENCES "DEC_HEAD" ("ID") ON DELETE SET NULL;
ALTER TABLE "DEC_GOODS_LIMIT_VIN" ADD CONSTRAINT "FK_DEC_GOODS_LIMIT_VIN_DEC_GOODS_LIMIT_ID" FOREIGN KEY ("DEC_GOODS_LIMIT_ID") REFERENCES "DEC_GOODS_LIMIT" ("ID") ON DELETE SET NULL;
ALTER TABLE "DEC_OTHER_PACK" ADD CONSTRAINT "FK_DEC_OTHER_PACK_DEC_HEAD_ID" FOREIGN KEY ("DEC_HEAD_ID") REFERENCES "DEC_HEAD" ("ID") ON DELETE SET NULL;
ALTER TABLE "DEC_COP_LIMIT" ADD CONSTRAINT "FK_DEC_COP_LIMIT_DEC_HEAD_ID" FOREIGN KEY ("DEC_HEAD_ID") REFERENCES "DEC_HEAD" ("ID") ON DELETE SET NULL;
ALTER TABLE "DEC_USER" ADD CONSTRAINT "FK_DEC_USER_DEC_HEAD_ID" FOREIGN KEY ("DEC_HEAD_ID") REFERENCES "DEC_HEAD" ("ID") ON DELETE SET NULL;
ALTER TABLE "DEC_MARK_LOB" ADD CONSTRAINT "FK_DEC_MARK_LOB_DEC_HEAD_ID" FOREIGN KEY ("DEC_HEAD_ID") REFERENCES "DEC_HEAD" ("ID") ON DELETE SET NULL;
ALTER TABLE "DEC_FREE_TXT" ADD CONSTRAINT "FK_DEC_FREE_TXT_DEC_HEAD_ID" FOREIGN KEY ("DEC_HEAD_ID") REFERENCES "DEC_HEAD" ("ID") ON DELETE SET NULL;
ALTER TABLE "DEC_SIGN" ADD CONSTRAINT "FK_DEC_SIGN_DEC_HEAD_ID" FOREIGN KEY ("DEC_HEAD_ID") REFERENCES "DEC_HEAD" ("ID") ON DELETE SET NULL;
ALTER TABLE "TRN_HEAD" ADD CONSTRAINT "FK_TRN_HEAD_DEC_HEAD_ID" FOREIGN KEY ("DEC_HEAD_ID") REFERENCES "DEC_HEAD" ("ID") ON DELETE SET NULL;
ALTER TABLE "TRN_LIST" ADD CONSTRAINT "FK_TRN_LIST_TRN_HEAD_ID" FOREIGN KEY ("TRN_HEAD_ID") REFERENCES "TRN_HEAD" ("ID") ON DELETE SET NULL;
ALTER TABLE "TRN_CONTAINER" ADD CONSTRAINT "FK_TRN_CONTAINER_TRN_HEAD_ID" FOREIGN KEY ("TRN_HEAD_ID") REFERENCES "TRN_HEAD" ("ID") ON DELETE SET NULL;
ALTER TABLE "TRN_CONTAINER" ADD CONSTRAINT "FK_TRN_CONTAINER_TRN_LIST_ID" FOREIGN KEY ("TRN_LIST_ID") REFERENCES "TRN_LIST" ("ID") ON DELETE SET NULL;
ALTER TABLE "TRN_CONTA_GOODS_LIST" ADD CONSTRAINT "FK_TRN_CONTA_GOODS_LIST_TRN_CONTAINER_ID" FOREIGN KEY ("TRN_CONTAINER_ID") REFERENCES "TRN_CONTAINER" ("ID") ON DELETE SET NULL;
ALTER TABLE "TRN_CONTA_GOODS_LIST" ADD CONSTRAINT "FK_TRN_CONTA_GOODS_LIST_TRN_LIST_ID" FOREIGN KEY ("TRN_LIST_ID") REFERENCES "TRN_LIST" ("ID") ON DELETE SET NULL;
ALTER TABLE "DEC_SUPPLEMENT" ADD CONSTRAINT "FK_DEC_SUPPLEMENT_DEC_HEAD_ID" FOREIGN KEY ("DEC_HEAD_ID") REFERENCES "DEC_HEAD" ("ID") ON DELETE SET NULL;
ALTER TABLE "DEC_SUPPLEMENT" ADD CONSTRAINT "FK_DEC_SUPPLEMENT_DEC_LIST_ID" FOREIGN KEY ("DEC_LIST_ID") REFERENCES "DEC_LIST" ("ID") ON DELETE SET NULL;
ALTER TABLE "EDOC_RELATION" ADD CONSTRAINT "FK_EDOC_RELATION_DEC_HEAD_ID" FOREIGN KEY ("DEC_HEAD_ID") REFERENCES "DEC_HEAD" ("ID") ON DELETE SET NULL;
ALTER TABLE "ECO_RELATION" ADD CONSTRAINT "FK_ECO_RELATION_DEC_HEAD_ID" FOREIGN KEY ("DEC_HEAD_ID") REFERENCES "DEC_HEAD" ("ID") ON DELETE SET NULL;
ALTER TABLE "ECO_RELATION" ADD CONSTRAINT "FK_ECO_RELATION_DEC_LIST_ID" FOREIGN KEY ("DEC_LIST_ID") REFERENCES "DEC_LIST" ("ID") ON DELETE SET NULL;
ALTER TABLE "SDD_TAX" ADD CONSTRAINT "FK_SDD_TAX_DEC_HEAD_ID" FOREIGN KEY ("DEC_HEAD_ID") REFERENCES "DEC_HEAD" ("ID") ON DELETE SET NULL;
ALTER TABLE "DEC_RISK" ADD CONSTRAINT "FK_DEC_RISK_DEC_HEAD_ID" FOREIGN KEY ("DEC_HEAD_ID") REFERENCES "DEC_HEAD" ("ID") ON DELETE SET NULL;
ALTER TABLE "DEC_COP_PROMISE" ADD CONSTRAINT "FK_DEC_COP_PROMISE_DEC_HEAD_ID" FOREIGN KEY ("DEC_HEAD_ID") REFERENCES "DEC_HEAD" ("ID") ON DELETE SET NULL;
ALTER TABLE "DEC_ROYALTY_FEE" ADD CONSTRAINT "FK_DEC_ROYALTY_FEE_DEC_HEAD_ID" FOREIGN KEY ("DEC_HEAD_ID") REFERENCES "DEC_HEAD" ("ID") ON DELETE SET NULL;
ALTER TABLE "DEC_TP_ACCESS" ADD CONSTRAINT "FK_DEC_TP_ACCESS_DEC_HEAD_ID" FOREIGN KEY ("DEC_HEAD_ID") REFERENCES "DEC_HEAD" ("ID") ON DELETE SET NULL;
ALTER TABLE "DEC_CONTROL_MODEL" ADD CONSTRAINT "FK_DEC_CONTROL_MODEL_DEC_HEAD_ID" FOREIGN KEY ("DEC_HEAD_ID") REFERENCES "DEC_HEAD" ("ID") ON DELETE SET NULL;
ALTER TABLE "DEC_CONTROL_MODEL" ADD CONSTRAINT "FK_DEC_CONTROL_MODEL_DEC_LIST_ID" FOREIGN KEY ("DEC_LIST_ID") REFERENCES "DEC_LIST" ("ID") ON DELETE SET NULL;
