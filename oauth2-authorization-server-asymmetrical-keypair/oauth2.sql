/*
 Navicat Premium Data Transfer

 Source Server         : MySQL
 Source Server Type    : MySQL
 Source Server Version : 50720
 Source Host           : localhost:3306
 Source Schema         : oauth2

 Target Server Type    : MySQL
 Target Server Version : 50720
 File Encoding         : 65001

 Date: 13/12/2022 17:39:24
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for oauth_access_token
-- ----------------------------
DROP TABLE IF EXISTS `oauth_access_token`;
CREATE TABLE `oauth_access_token`  (
  `token_id` varchar(256) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `token` blob NULL,
  `authentication_id` varchar(256) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `user_name` varchar(256) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `client_id` varchar(256) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `authentication` blob NULL,
  `refresh_token` varchar(256) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL
) ENGINE = MyISAM CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of oauth_access_token
-- ----------------------------

-- ----------------------------
-- Table structure for oauth_client_details
-- ----------------------------
DROP TABLE IF EXISTS `oauth_client_details`;
CREATE TABLE `oauth_client_details`  (
  `client_id` varchar(256) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `resource_ids` varchar(256) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `client_secret` varchar(256) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `plain_client_secret` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `scope` varchar(256) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `authorized_grant_types` varchar(256) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `web_server_redirect_uri` varchar(256) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `authorities` varchar(256) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `access_token_validity` int(11) NULL DEFAULT NULL,
  `refresh_token_validity` int(11) NULL DEFAULT NULL,
  `additional_information` varchar(4096) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `autoapprove` varchar(256) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  PRIMARY KEY (`client_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of oauth_client_details
-- ----------------------------
INSERT INTO `oauth_client_details` VALUES ('client1', NULL, '$2a$10$/Ci6DDwsvM6/dk9XOkPivuCCX.5WCLDM2H4VchKLyee4NIZdIVapW', '123456', 'user_base,all', 'authorization_code,password,client_credentials,implicit,refresh_token,captcha,sms_code', 'http://localhost:8888/callback', NULL, 300, 1500, NULL, 'false');
INSERT INTO `oauth_client_details` VALUES ('client2', NULL, '$2a$10$/Ci6DDwsvM6/dk9XOkPivuCCX.5WCLDM2H4VchKLyee4NIZdIVapW', '123456', 'all', 'authorization_code,password,client_credentials,implicit,refresh_token', 'http://localhost:8001/auth/getCode', NULL, 300, 1500, NULL, 'true');
INSERT INTO `oauth_client_details` VALUES ('client3', NULL, '$2a$10$/Ci6DDwsvM6/dk9XOkPivuCCX.5WCLDM2H4VchKLyee4NIZdIVapW', '123456', 'profile,write,user', 'authorization_code,password,client_credentials,implicit,refresh_token', 'http://localhost:8080/callback', NULL, 300, 1500, NULL, 'false');
INSERT INTO `oauth_client_details` VALUES ('client4', NULL, '$2a$10$/Ci6DDwsvM6/dk9XOkPivuCCX.5WCLDM2H4VchKLyee4NIZdIVapW', '123456', 'profile,message.read,message.write', 'authorization_code,password,client_credentials,implicit,refresh_token', 'http://localhost:1112/login', NULL, 300, 1500, NULL, 'false');
INSERT INTO `oauth_client_details` VALUES ('client5', NULL, '$2a$10$/Ci6DDwsvM6/dk9XOkPivuCCX.5WCLDM2H4VchKLyee4NIZdIVapW', '123456', 'profile,message.read,message.write', 'authorization_code,password,client_credentials,implicit,refresh_token', 'http://localhost:1113/login', NULL, -1, -1, NULL, 'false');
INSERT INTO `oauth_client_details` VALUES ('e5a1b04f08837cec9060591e8dfbe45e', NULL, '$2a$10$Cy.cIlwguTJrvtXLy9JnE.mxzXripJFJeGnQURtHExysxIgdlV6eK', 'a24959b7c8abd3a8fe2f3ab84f91b216', 'all', 'authorization_code,password,client_credentials,implicit,refresh_token,captcha,sms_code', 'http://localhost:8888/callback', NULL, 3600, 21600, NULL, 'false');

-- ----------------------------
-- Table structure for oauth_refresh_token
-- ----------------------------
DROP TABLE IF EXISTS `oauth_refresh_token`;
CREATE TABLE `oauth_refresh_token`  (
  `token_id` varchar(256) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `token` blob NULL,
  `authentication` blob NULL
) ENGINE = MyISAM CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of oauth_refresh_token
-- ----------------------------

-- ----------------------------
-- Table structure for role_user
-- ----------------------------
DROP TABLE IF EXISTS `role_user`;
CREATE TABLE `role_user`  (
  `roleId` int(12) NOT NULL,
  `userId` int(12) NOT NULL,
  PRIMARY KEY (`userId`, `roleId`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of role_user
-- ----------------------------
INSERT INTO `role_user` VALUES (2, 1);
INSERT INTO `role_user` VALUES (1, 6);

-- ----------------------------
-- Table structure for sys_role
-- ----------------------------
DROP TABLE IF EXISTS `sys_role`;
CREATE TABLE `sys_role`  (
  `id` int(12) NOT NULL AUTO_INCREMENT,
  `roleName` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_role
-- ----------------------------
INSERT INTO `sys_role` VALUES (1, 'ROLE_admin');
INSERT INTO `sys_role` VALUES (2, 'ROLE_user');

-- ----------------------------
-- Table structure for sys_user
-- ----------------------------
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user`  (
  `id` int(12) NOT NULL AUTO_INCREMENT,
  `username` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '用户名',
  `password` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '密码',
  `phone` varchar(16) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '手机号',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_user
-- ----------------------------
INSERT INTO `sys_user` VALUES (1, 'user', '$2a$10$/Ci6DDwsvM6/dk9XOkPivuCCX.5WCLDM2H4VchKLyee4NIZdIVapW', '17321019208');

SET FOREIGN_KEY_CHECKS = 1;
