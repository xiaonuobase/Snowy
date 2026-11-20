-- ----------------------------
-- Snowy 3.7.0 升级 3.7.1 增量脚本
-- 适用：MySQL 5.7 / 8.0；可重复执行（每段都有存在性判断）
-- 执行后：重启后端，用户重新登录以刷新字典与按钮权限
-- 注意：系统配置有 Redis 永久缓存，本脚本只改数据库。执行后须在 Redis（与后端同库）删除以下两个键，否则密码复杂度修正不生效：
--       Cache:dev-config:SNOWY_SYS_DEFAULT_PASSWORD_COMPLEXITY_FOR_B
--       Cache:dev-config:SNOWY_SYS_DEFAULT_PASSWORD_COMPLEXITY_FOR_C
--       （也可在「系统配置 → 密码策略」的 B 端、C 端两个页面各点一次保存，保存时会自动清除对应缓存）
-- ----------------------------
SET NAMES utf8mb4;

-- ----------------------------
-- SYS_USER 新增过期时间字段（为空表示永不过期）
-- ----------------------------
SET @ddl = (
    SELECT IF(COUNT(*) = 0,
        'ALTER TABLE `SYS_USER` ADD COLUMN `EXPIRE_TIME` datetime NULL DEFAULT NULL COMMENT ''过期时间'' AFTER `USER_STATUS`',
        'SELECT 1')
    FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE()
      AND UPPER(TABLE_NAME) = 'SYS_USER'
      AND UPPER(COLUMN_NAME) = 'EXPIRE_TIME'
);
PREPARE stmt FROM @ddl;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- ----------------------------
-- CLIENT_USER 新增过期时间字段（为空表示永不过期）
-- ----------------------------
SET @ddl = (
    SELECT IF(COUNT(*) = 0,
        'ALTER TABLE `CLIENT_USER` ADD COLUMN `EXPIRE_TIME` datetime NULL DEFAULT NULL COMMENT ''过期时间'' AFTER `USER_STATUS`',
        'SELECT 1')
    FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE()
      AND UPPER(TABLE_NAME) = 'CLIENT_USER'
      AND UPPER(COLUMN_NAME) = 'EXPIRE_TIME'
);
PREPARE stmt FROM @ddl;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- ----------------------------
-- 密码复杂策略字典：第 5 项字典值由 default 修正为 REG5（与后端枚举一致）
-- ----------------------------
UPDATE `DEV_DICT` SET `DICT_VALUE` = 'REG5'
WHERE `ID` = '1556319786349166608' AND `DICT_VALUE` = 'default';

-- 已选过该项的 B 端、C 端密码复杂度配置同步修正
UPDATE `DEV_CONFIG` SET `CONFIG_VALUE` = 'REG5'
WHERE `CONFIG_KEY` IN ('SNOWY_SYS_DEFAULT_PASSWORD_COMPLEXITY_FOR_B', 'SNOWY_SYS_DEFAULT_PASSWORD_COMPLEXITY_FOR_C')
  AND `CONFIG_VALUE` = 'default';

-- ----------------------------
-- 会话管理菜单补充「强制退出」按钮（前端 hasPerm('authForceQuit')）
-- ----------------------------
INSERT INTO `SYS_RESOURCE` (`ID`, `PARENT_ID`, `TITLE`, `NAME`, `CODE`, `CATEGORY`, `MODULE`, `MENU_TYPE`, `PATH`, `COMPONENT`, `ICON`, `COLOR`, `VISIBLE`, `DISPLAY_LAYOUT`, `KEEP_LIVE`, `SORT_CODE`, `EXT_JSON`, `DELETE_FLAG`, `CREATE_TIME`, `CREATE_USER`, `UPDATE_TIME`, `UPDATE_USER`)
SELECT '2003522569956777987', '1548901111999772626', '强制退出', NULL, 'authForceQuit', 'BUTTON', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, NULL, 'NOT_DELETE', NULL, NULL, NULL, NULL
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM `SYS_RESOURCE` WHERE `ID` = '2003522569956777987')
  AND NOT EXISTS (SELECT 1 FROM `SYS_RESOURCE` WHERE `PARENT_ID` = '1548901111999772626' AND `CODE` = 'authForceQuit' AND `DELETE_FLAG` = 'NOT_DELETE');

-- ----------------------------
-- 超管角色在「会话管理」菜单上授予「强制退出」按钮（按菜单+编码解析实际按钮ID，兼容存量库中手工建过的同编码按钮）
-- ----------------------------
UPDATE `SYS_RELATION` rel
INNER JOIN (
    SELECT `ID` AS `BUTTON_ID` FROM `SYS_RESOURCE`
    WHERE `PARENT_ID` = '1548901111999772626' AND `CODE` = 'authForceQuit' AND `DELETE_FLAG` = 'NOT_DELETE'
    ORDER BY `ID`
    LIMIT 1
) btn
SET rel.`EXT_JSON` = JSON_ARRAY_APPEND(rel.`EXT_JSON`, '$.buttonInfo', btn.`BUTTON_ID`)
WHERE rel.`CATEGORY` = 'SYS_ROLE_HAS_RESOURCE'
  AND rel.`TARGET_ID` = '1548901111999772626'
  AND rel.`OBJECT_ID` IN (SELECT r.`ID` FROM `SYS_ROLE` r WHERE r.`CODE` = 'superAdmin' AND r.`DELETE_FLAG` = 'NOT_DELETE')
  AND JSON_VALID(rel.`EXT_JSON`)
  AND JSON_CONTAINS_PATH(rel.`EXT_JSON`, 'one', '$.buttonInfo')
  AND JSON_CONTAINS(rel.`EXT_JSON`, JSON_QUOTE(btn.`BUTTON_ID`), '$.buttonInfo') = 0;

-- ----------------------------
-- 超管角色补授 C 端用户管理接口权限（3.7.0 将 /client/user/** 移出超管路由白名单、改用 @SaCheckPermission 时漏授）
-- ----------------------------
INSERT INTO `SYS_RELATION` (`ID`, `OBJECT_ID`, `TARGET_ID`, `CATEGORY`, `EXT_JSON`)
SELECT '2003522704178700301', r.`ID`, '/client/user/page', 'SYS_ROLE_HAS_PERMISSION', '{"apiUrl":"/client/user/page","scopeCategory":"SCOPE_ALL","scopeDefineOrgIdList":[]}'
FROM `SYS_ROLE` r
WHERE r.`CODE` = 'superAdmin' AND r.`DELETE_FLAG` = 'NOT_DELETE'
  AND NOT EXISTS (SELECT 1 FROM `SYS_RELATION` x WHERE x.`OBJECT_ID` = r.`ID` AND x.`TARGET_ID` = '/client/user/page' AND x.`CATEGORY` = 'SYS_ROLE_HAS_PERMISSION')
  AND NOT EXISTS (SELECT 1 FROM `SYS_RELATION` y WHERE y.`ID` = '2003522704178700301')
LIMIT 1;

INSERT INTO `SYS_RELATION` (`ID`, `OBJECT_ID`, `TARGET_ID`, `CATEGORY`, `EXT_JSON`)
SELECT '2003522704178700302', r.`ID`, '/client/user/add', 'SYS_ROLE_HAS_PERMISSION', '{"apiUrl":"/client/user/add","scopeCategory":"SCOPE_ALL","scopeDefineOrgIdList":[]}'
FROM `SYS_ROLE` r
WHERE r.`CODE` = 'superAdmin' AND r.`DELETE_FLAG` = 'NOT_DELETE'
  AND NOT EXISTS (SELECT 1 FROM `SYS_RELATION` x WHERE x.`OBJECT_ID` = r.`ID` AND x.`TARGET_ID` = '/client/user/add' AND x.`CATEGORY` = 'SYS_ROLE_HAS_PERMISSION')
  AND NOT EXISTS (SELECT 1 FROM `SYS_RELATION` y WHERE y.`ID` = '2003522704178700302')
LIMIT 1;

INSERT INTO `SYS_RELATION` (`ID`, `OBJECT_ID`, `TARGET_ID`, `CATEGORY`, `EXT_JSON`)
SELECT '2003522704178700303', r.`ID`, '/client/user/edit', 'SYS_ROLE_HAS_PERMISSION', '{"apiUrl":"/client/user/edit","scopeCategory":"SCOPE_ALL","scopeDefineOrgIdList":[]}'
FROM `SYS_ROLE` r
WHERE r.`CODE` = 'superAdmin' AND r.`DELETE_FLAG` = 'NOT_DELETE'
  AND NOT EXISTS (SELECT 1 FROM `SYS_RELATION` x WHERE x.`OBJECT_ID` = r.`ID` AND x.`TARGET_ID` = '/client/user/edit' AND x.`CATEGORY` = 'SYS_ROLE_HAS_PERMISSION')
  AND NOT EXISTS (SELECT 1 FROM `SYS_RELATION` y WHERE y.`ID` = '2003522704178700303')
LIMIT 1;

INSERT INTO `SYS_RELATION` (`ID`, `OBJECT_ID`, `TARGET_ID`, `CATEGORY`, `EXT_JSON`)
SELECT '2003522704178700304', r.`ID`, '/client/user/delete', 'SYS_ROLE_HAS_PERMISSION', '{"apiUrl":"/client/user/delete","scopeCategory":"SCOPE_ALL","scopeDefineOrgIdList":[]}'
FROM `SYS_ROLE` r
WHERE r.`CODE` = 'superAdmin' AND r.`DELETE_FLAG` = 'NOT_DELETE'
  AND NOT EXISTS (SELECT 1 FROM `SYS_RELATION` x WHERE x.`OBJECT_ID` = r.`ID` AND x.`TARGET_ID` = '/client/user/delete' AND x.`CATEGORY` = 'SYS_ROLE_HAS_PERMISSION')
  AND NOT EXISTS (SELECT 1 FROM `SYS_RELATION` y WHERE y.`ID` = '2003522704178700304')
LIMIT 1;

INSERT INTO `SYS_RELATION` (`ID`, `OBJECT_ID`, `TARGET_ID`, `CATEGORY`, `EXT_JSON`)
SELECT '2003522704178700305', r.`ID`, '/client/user/detail', 'SYS_ROLE_HAS_PERMISSION', '{"apiUrl":"/client/user/detail","scopeCategory":"SCOPE_ALL","scopeDefineOrgIdList":[]}'
FROM `SYS_ROLE` r
WHERE r.`CODE` = 'superAdmin' AND r.`DELETE_FLAG` = 'NOT_DELETE'
  AND NOT EXISTS (SELECT 1 FROM `SYS_RELATION` x WHERE x.`OBJECT_ID` = r.`ID` AND x.`TARGET_ID` = '/client/user/detail' AND x.`CATEGORY` = 'SYS_ROLE_HAS_PERMISSION')
  AND NOT EXISTS (SELECT 1 FROM `SYS_RELATION` y WHERE y.`ID` = '2003522704178700305')
LIMIT 1;

INSERT INTO `SYS_RELATION` (`ID`, `OBJECT_ID`, `TARGET_ID`, `CATEGORY`, `EXT_JSON`)
SELECT '2003522704178700306', r.`ID`, '/client/user/disableUser', 'SYS_ROLE_HAS_PERMISSION', '{"apiUrl":"/client/user/disableUser","scopeCategory":"SCOPE_ALL","scopeDefineOrgIdList":[]}'
FROM `SYS_ROLE` r
WHERE r.`CODE` = 'superAdmin' AND r.`DELETE_FLAG` = 'NOT_DELETE'
  AND NOT EXISTS (SELECT 1 FROM `SYS_RELATION` x WHERE x.`OBJECT_ID` = r.`ID` AND x.`TARGET_ID` = '/client/user/disableUser' AND x.`CATEGORY` = 'SYS_ROLE_HAS_PERMISSION')
  AND NOT EXISTS (SELECT 1 FROM `SYS_RELATION` y WHERE y.`ID` = '2003522704178700306')
LIMIT 1;

INSERT INTO `SYS_RELATION` (`ID`, `OBJECT_ID`, `TARGET_ID`, `CATEGORY`, `EXT_JSON`)
SELECT '2003522704178700307', r.`ID`, '/client/user/enableUser', 'SYS_ROLE_HAS_PERMISSION', '{"apiUrl":"/client/user/enableUser","scopeCategory":"SCOPE_ALL","scopeDefineOrgIdList":[]}'
FROM `SYS_ROLE` r
WHERE r.`CODE` = 'superAdmin' AND r.`DELETE_FLAG` = 'NOT_DELETE'
  AND NOT EXISTS (SELECT 1 FROM `SYS_RELATION` x WHERE x.`OBJECT_ID` = r.`ID` AND x.`TARGET_ID` = '/client/user/enableUser' AND x.`CATEGORY` = 'SYS_ROLE_HAS_PERMISSION')
  AND NOT EXISTS (SELECT 1 FROM `SYS_RELATION` y WHERE y.`ID` = '2003522704178700307')
LIMIT 1;

INSERT INTO `SYS_RELATION` (`ID`, `OBJECT_ID`, `TARGET_ID`, `CATEGORY`, `EXT_JSON`)
SELECT '2003522704178700308', r.`ID`, '/client/user/resetPassword', 'SYS_ROLE_HAS_PERMISSION', '{"apiUrl":"/client/user/resetPassword","scopeCategory":"SCOPE_ALL","scopeDefineOrgIdList":[]}'
FROM `SYS_ROLE` r
WHERE r.`CODE` = 'superAdmin' AND r.`DELETE_FLAG` = 'NOT_DELETE'
  AND NOT EXISTS (SELECT 1 FROM `SYS_RELATION` x WHERE x.`OBJECT_ID` = r.`ID` AND x.`TARGET_ID` = '/client/user/resetPassword' AND x.`CATEGORY` = 'SYS_ROLE_HAS_PERMISSION')
  AND NOT EXISTS (SELECT 1 FROM `SYS_RELATION` y WHERE y.`ID` = '2003522704178700308')
LIMIT 1;
