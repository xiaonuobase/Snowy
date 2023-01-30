SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Records of dev_dict
-- ----------------------------
INSERT INTO `dev_dict` VALUES ('1619343323218432002', '0', '移动菜单状态', 'MOBILE_STATUS', 'FRM', 93, NULL, 'NOT_DELETE', NULL, NULL, NULL, NULL);
INSERT INTO `dev_dict` VALUES ('1619343680636047362', '1619343323218432002', '可用', 'ENABLE', 'FRM', 94, NULL, 'NOT_DELETE', NULL, NULL, NULL, NULL);
INSERT INTO `dev_dict` VALUES ('1619343846382358529', '1619343323218432002', '不可用', 'DISABLED', 'FRM', 96, NULL, 'NOT_DELETE', NULL, NULL, NULL, NULL);
INSERT INTO `dev_dict` VALUES ('1619344256295882753', '0', '移动菜单规则', 'MOBILE_IS_REG_EXP', 'FRM', 97, NULL, 'NOT_DELETE', NULL, NULL, NULL, NULL);
INSERT INTO `dev_dict` VALUES ('1619344428111351809', '1619344256295882753', '正规则', 'YES', 'FRM', 98, NULL, 'NOT_DELETE', NULL, NULL, NULL, NULL);
INSERT INTO `dev_dict` VALUES ('1619344504456073218', '1619344256295882753', '反规则', 'NO', 'FRM', 98, NULL, 'NOT_DELETE', NULL, NULL, NULL, NULL);

-- ----------------------------
-- Records of sys_relation
-- ----------------------------
INSERT INTO `sys_relation` VALUES ('1619345262266142721', '1570687866138206208', '1619345262001901569', 'SYS_ROLE_HAS_RESOURCE', '{\"menuId\":\"1619345262001901569\",\"buttonInfo\":[\"1619345262085787650\",\"1619345262131924994\",\"1619345262131924995\",\"1619345262131924996\"]}');

-- ----------------------------
-- Records of sys_resource
-- ----------------------------
INSERT INTO `sys_resource` VALUES ('1619345262001901569', '0', '移动管理', NULL, 'gsnnmdy8yi', 'MENU', '1548901111999770525', 'CATALOG', '/a0l7fxfq3m', NULL, 'appstore-outlined', NULL, 99, NULL, 'NOT_DELETE', NULL, NULL, NULL, NULL);
INSERT INTO `sys_resource` VALUES ('1619345262001901570', '1619345262001901569', '菜单类别', 'category', 'psf3uj3q90', 'MENU', '1548901111999770525', 'MENU', '/mobile/category', 'mobile/category/index', 'appstore-outlined', NULL, 99, NULL, 'NOT_DELETE', NULL, NULL, NULL, NULL);
INSERT INTO `sys_resource` VALUES ('1619345262001901571', '1619345262001901569', '移动菜单', 'menu', '4a84jeju7l', 'MENU', '1548901111999770525', 'MENU', '/mobile/menu', 'mobile/menu/index', 'appstore-outlined', NULL, 99, NULL, 'NOT_DELETE', NULL, NULL, NULL, NULL);

SET FOREIGN_KEY_CHECKS = 1;