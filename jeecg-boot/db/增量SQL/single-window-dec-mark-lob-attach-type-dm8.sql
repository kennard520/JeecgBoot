-- 达梦数据库版本：标记唛码附件类型字段放宽，用于保存图片扩展名
ALTER TABLE "DEC_MARK_LOB" MODIFY "ATTACH_TYPE" VARCHAR2(20);

COMMENT ON COLUMN "DEC_MARK_LOB"."ATTACH_TYPE" IS '附件类型；字段代码: AttachType；数据类型: String；长度: 20；暂存必填: 否；申报必填: 否；说明: 附件类型，保存图片文件扩展名，如 png、jpg、jpeg、webp';
