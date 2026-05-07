-- 中国海关单一窗口菜单增量脚本，适用于达梦 DM8。
-- 作用：
-- 1. 在 SYS_PERMISSION 中注册前端页面菜单。
-- 2. 将该菜单授权给 role_code = 'admin' 的管理员角色。
-- 3. 执行后需要重新登录或清理前端权限缓存，才能在左侧菜单中刷新显示。

-- 菜单 ID：固定 32 位，便于脚本重复执行时清理和重建。
DELETE FROM "SYS_ROLE_PERMISSION"
WHERE "PERMISSION_ID" = 'a40f8c0e6c4e4f65b53f1f5ed0c80101';

DELETE FROM "SYS_PERMISSION"
WHERE "ID" = 'a40f8c0e6c4e4f65b53f1f5ed0c80101';

INSERT INTO "SYS_PERMISSION" (
  "ID",
  "PARENT_ID",
  "NAME",
  "URL",
  "COMPONENT",
  "IS_ROUTE",
  "COMPONENT_NAME",
  "REDIRECT",
  "MENU_TYPE",
  "PERMS",
  "PERMS_TYPE",
  "SORT_NO",
  "ALWAYS_SHOW",
  "ICON",
  "IS_LEAF",
  "KEEP_ALIVE",
  "HIDDEN",
  "HIDE_TAB",
  "DESCRIPTION",
  "CREATE_BY",
  "CREATE_TIME",
  "UPDATE_BY",
  "UPDATE_TIME",
  "DEL_FLAG",
  "RULE_FLAG",
  "STATUS",
  "INTERNAL_OR_EXTERNAL"
) VALUES (
  'a40f8c0e6c4e4f65b53f1f5ed0c80101',
  '',
  '中国海关单一窗口',
  '/custom/cit/single-window',
  'custom/cit/singleWindow/index',
  1,
  'CustomCitSingleWindow',
  NULL,
  0,
  NULL,
  '0',
  13.00,
  0,
  'ant-design:global-outlined',
  1,
  0,
  0,
  0,
  '中国海关单一窗口业务页面',
  'admin',
  SYSDATE,
  NULL,
  NULL,
  0,
  0,
  '1',
  0
);

-- 授权给管理员角色。若当前库中没有 role_code = 'admin' 的角色，此语句不会插入任何记录。
INSERT INTO "SYS_ROLE_PERMISSION" (
  "ID",
  "ROLE_ID",
  "PERMISSION_ID",
  "DATA_RULE_IDS",
  "OPERATE_DATE",
  "OPERATE_IP"
)
SELECT
  'a40f8c0e6c4e4f65b53f1f5ed0c80102',
  "ID",
  'a40f8c0e6c4e4f65b53f1f5ed0c80101',
  NULL,
  SYSDATE,
  '127.0.0.1'
FROM "SYS_ROLE"
WHERE "ROLE_CODE" = 'admin';

COMMIT;
