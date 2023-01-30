SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for mobile_resource
-- ----------------------------
DROP TABLE IF EXISTS `mobile_resource`;
CREATE TABLE `mobile_resource`  (
    `ID` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '主键',
    `PARENT_ID` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '父ID',
    `TITLE` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '名称',
    `CODE` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '编码',
    `PAGES` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '界面路径',
    `CATEGORY` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '分类',
    `MODULE` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '模块',
    `ICON` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '图标',
    `COLOR` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '颜色',
    `REG_TYPE` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '规则类型',
    `STATUS` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '可用状态',
    `SORT_CODE` int(0) NULL DEFAULT NULL COMMENT '排序码',
    `EXT_JSON` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '扩展信息',
    `DELETE_FLAG` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '删除标志',
    `CREATE_TIME` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
    `CREATE_USER` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '创建用户',
    `UPDATE_TIME` datetime(0) NULL DEFAULT NULL COMMENT '修改时间',
    `UPDATE_USER` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '修改用户',
    PRIMARY KEY (`ID`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '移动资源' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of dev_dict
-- ----------------------------
INSERT INTO `dev_dict` VALUES ('1619343323218432002', '0', '移动菜单状态', 'MOBILE_STATUS', 'FRM', 93, NULL, 'NOT_DELETE', NULL, NULL, NULL, NULL);
INSERT INTO `dev_dict` VALUES ('1619343680636047362', '1619343323218432002', '可用', 'ENABLE', 'FRM', 94, NULL, 'NOT_DELETE', NULL, NULL, NULL, NULL);
INSERT INTO `dev_dict` VALUES ('1619343846382358529', '1619343323218432002', '不可用', 'DISABLED', 'FRM', 96, NULL, 'NOT_DELETE', NULL, NULL, NULL, NULL);
INSERT INTO `dev_dict` VALUES ('1619344256295882753', '0', '移动菜单规则', 'MOBILE_REG_TYPE', 'FRM', 97, NULL, 'NOT_DELETE', NULL, NULL, NULL, NULL);
INSERT INTO `dev_dict` VALUES ('1619344428111351809', '1619344256295882753', '正规则', 'YES', 'FRM', 98, NULL, 'NOT_DELETE', NULL, NULL, NULL, NULL);
INSERT INTO `dev_dict` VALUES ('1619344504456073218', '1619344256295882753', '反规则', 'NO', 'FRM', 99, NULL, 'NOT_DELETE', NULL, NULL, NULL, NULL);

-- ----------------------------
-- Records of sys_relation
-- ----------------------------
INSERT INTO `sys_relation` VALUES ('1619345262266142721', '1570687866138206208', '1619345262001901569', 'SYS_ROLE_HAS_RESOURCE', '{\"menuId\":\"1619345262001901569\",\"buttonInfo\":[\"1619345262085787650\",\"1619345262131924994\",\"1619345262131924995\",\"1619345262131924996\"]}');

-- ----------------------------
-- Records of sys_resource
-- ----------------------------
INSERT INTO `sys_resource` VALUES ('1619345262001901569', '0', '移动管理', NULL, 'gsnnmdy8yj', 'MENU', '1548901111999770525', 'CATALOG', '/a0l7fxfq3n', NULL, 'appstore-outlined', NULL, 99, NULL, 'NOT_DELETE', NULL, NULL, NULL, NULL);
INSERT INTO `sys_resource` VALUES ('1619345262001901570', '1619345262001901569', '菜单类别', 'module', 'psf3uj3q90', 'MENU', '1548901111999770525', 'MENU', '/mobile/module', 'mobile/resource/module/index', 'appstore-outlined', NULL, 99, NULL, 'NOT_DELETE', NULL, NULL, NULL, NULL);
INSERT INTO `sys_resource` VALUES ('1619345262001901571', '1619345262001901569', '移动菜单', 'menu', '4a84jeju7l', 'MENU', '1548901111999770525', 'MENU', '/mobile/menu', 'mobile/resource/menu/index', 'appstore-outlined', NULL, 99, NULL, 'NOT_DELETE', NULL, NULL, NULL, NULL);

SET FOREIGN_KEY_CHECKS = 1;
