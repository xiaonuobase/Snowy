/*
 Navicat Premium Data Transfer

 Source Server         : 本地Mysql
 Source Server Type    : MySQL
 Source Server Version : 80026
 Source Host           : localhost:3306
 Source Schema         : snowy

 Target Server Type    : MySQL
 Target Server Version : 80026
 File Encoding         : 65001

 Date: 30/01/2023 16:50:05
*/

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
-- Records of mobile_resource
-- ----------------------------

SET FOREIGN_KEY_CHECKS = 1;
