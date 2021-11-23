---------------------初始化示例数据---------------------------------------

--系统菜单数据

INSERT INTO "public"."tb_menu" VALUES ('570d8ce0-4906-11ec-b2c3-7f574d105067', 'XTCD1637305212069', '边缘实例', 1, 1, NULL, '9e1b46a0-481a-11ec-931b-a78b7aaa97d6', 'router', NULL, 'Global', 1637305212099, '5a797660-4612-11e7-a919-92ebcb67fe33', '1637305212099', NULL, 'PC', '/edgeInstances', 'f', 'edge.edge-instances');
INSERT INTO "public"."tb_menu" VALUES ('376a56b0-3ea3-11ec-80ce-5546796a1b1d', 'XTCD1636163127194', '设备字典 - 删除', 1, 1, NULL, 'c3b93c80-3e08-11ec-866a-059202e55853', NULL, NULL, 'Global', 1636163127196, 'fc4b0e50-0c5a-11ec-bdcf-83a250730c01', '1636163127196', NULL, 'PC', NULL, 't', 'action.delete');
INSERT INTO "public"."tb_menu" VALUES ('67e660c0-3ea5-11ec-9090-a3316146e5ad', 'XTCD1636164067530', '数据字典 - 编辑', 1, 1, NULL, '2df216f0-3d49-11ec-9809-df813b08b61b', NULL, NULL, 'Global', 1636164067592, 'fc4b0e50-0c5a-11ec-bdcf-83a250730c01', '1636164067592', NULL, 'PC', NULL, 't', 'action.edit');
INSERT INTO "public"."tb_menu" VALUES ('d171c040-411e-11ec-8ed3-dda66ae3bde5', 'XTCD1636436116013', '角色管理 - 关联用户', 1, 1, NULL, 'c84605b0-3e0a-11ec-866a-059202e55853', NULL, NULL, 'Global', 1636436116048, 'fc4b0e50-0c5a-11ec-bdcf-83a250730c01', '1636436116048', NULL, 'PC', NULL, 't', 'auth-mng.bind-users');
INSERT INTO "public"."tb_menu" VALUES ('eccb2a40-3d48-11ec-9809-df813b08b61b', 'XTCD1636014396130', '设备管理', 0, 1, NULL, NULL, 'devices', NULL, 'Global', 1636014396134, 'fc4b0e50-0c5a-11ec-bdcf-83a250730c01', '1636014396134', NULL, 'PC', '/deviceManagement', 'f', 'device-mng.device-mng');
INSERT INTO "public"."tb_menu" VALUES ('2df216f0-3d49-11ec-9809-df813b08b61b', 'XTCD1636014505438', '数据字典', 1, 1, NULL, 'eccb2a40-3d48-11ec-9809-df813b08b61b', 'book', NULL, 'Global', 1636014505441, 'fc4b0e50-0c5a-11ec-bdcf-83a250730c01', '1636014505441', NULL, 'PC', '/deviceManagement/dataDictionary', 'f', 'device-mng.data-dic');
INSERT INTO "public"."tb_menu" VALUES ('c3b93c80-3e08-11ec-866a-059202e55853', 'XTCD1636096790591', '设备字典', 1, 1, NULL, 'eccb2a40-3d48-11ec-9809-df813b08b61b', 'book', NULL, 'Global', 1636096790986, 'fc4b0e50-0c5a-11ec-bdcf-83a250730c01', '1636096790986', NULL, 'PC', '/deviceManagement/deviceDictionary', 'f', 'device-mng.device-dic');
INSERT INTO "public"."tb_menu" VALUES ('976c8770-3e0a-11ec-866a-059202e55853', 'XTCD1636097575270', '权限管理', 0, 1, NULL, NULL, 'verified_user', NULL, 'Global', 1636097575272, 'fc4b0e50-0c5a-11ec-bdcf-83a250730c01', '1636097575272', NULL, 'PC', '/authManagement', 'f', 'auth-mng.auth-mng');
INSERT INTO "public"."tb_menu" VALUES ('aa2af5e0-3e0a-11ec-866a-059202e55853', 'XTCD1636097606717', '用户管理', 1, 1, NULL, '976c8770-3e0a-11ec-866a-059202e55853', 'people', NULL, 'Global', 1636097606719, 'fc4b0e50-0c5a-11ec-bdcf-83a250730c01', '1636097606719', NULL, 'PC', '/authManagement/userManagement', 'f', 'auth-mng.user-mng');
INSERT INTO "public"."tb_menu" VALUES ('c84605b0-3e0a-11ec-866a-059202e55853', 'XTCD1636097657227', '角色管理', 1, 1, NULL, '976c8770-3e0a-11ec-866a-059202e55853', 'mdi:shield-account', NULL, 'Global', 1636097657228, 'fc4b0e50-0c5a-11ec-bdcf-83a250730c01', '1636097657228', NULL, 'PC', '/authManagement/roleManagemnet', 'f', 'auth-mng.role-mng');
INSERT INTO "public"."tb_menu" VALUES ('f74d38e0-3ea2-11ec-80ce-5546796a1b1d', 'XTCD1636163019626', '数据字典 - 添加数据字典', 1, 1, NULL, '2df216f0-3d49-11ec-9809-df813b08b61b', NULL, NULL, 'Global', 1636163019637, 'fc4b0e50-0c5a-11ec-bdcf-83a250730c01', '1636163019637', NULL, 'PC', NULL, 't', 'device-mng.add-data-dic');
INSERT INTO "public"."tb_menu" VALUES ('07a2e4b0-3ea3-11ec-80ce-5546796a1b1d', 'XTCD1636163047034', '数据字典 - 删除', 1, 1, NULL, '2df216f0-3d49-11ec-9809-df813b08b61b', NULL, NULL, 'Global', 1636163047037, 'fc4b0e50-0c5a-11ec-bdcf-83a250730c01', '1636163047037', NULL, 'PC', NULL, 't', 'action.delete');
INSERT INTO "public"."tb_menu" VALUES ('27e0fb90-3ea3-11ec-80ce-5546796a1b1d', 'XTCD1636163101127', '设备字典 - 添加设备字典', 1, 1, NULL, 'c3b93c80-3e08-11ec-866a-059202e55853', NULL, NULL, 'Global', 1636163101130, 'fc4b0e50-0c5a-11ec-bdcf-83a250730c01', '1636163101130', NULL, 'PC', NULL, 't', 'device-mng.add-device-dic');
INSERT INTO "public"."tb_menu" VALUES ('07025df0-411f-11ec-8ed3-dda66ae3bde5', 'XTCD1636436205885', '角色管理 - 配置权限', 1, 1, NULL, 'c84605b0-3e0a-11ec-866a-059202e55853', NULL, NULL, 'Global', 1636436205920, 'fc4b0e50-0c5a-11ec-bdcf-83a250730c01', '1636436205920', NULL, 'PC', NULL, 't', 'auth-mng.set-permissions');
INSERT INTO "public"."tb_menu" VALUES ('0486cc10-411e-11ec-8ed3-dda66ae3bde5', 'XTCD1636435772225', '用户管理 - 删除', 1, 1, NULL, 'aa2af5e0-3e0a-11ec-866a-059202e55853', NULL, NULL, 'Global', 1636435772247, 'fc4b0e50-0c5a-11ec-bdcf-83a250730c01', '1636435772247', NULL, 'PC', NULL, 't', 'action.delete');
INSERT INTO "public"."tb_menu" VALUES ('eed8ded0-411d-11ec-8ed3-dda66ae3bde5', 'XTCD1636435735851', '用户管理 - 添加用户', 1, 1, NULL, 'aa2af5e0-3e0a-11ec-866a-059202e55853', NULL, NULL, 'Global', 1636435790262, 'fc4b0e50-0c5a-11ec-bdcf-83a250730c01', '1636435790262', 'fc4b0e50-0c5a-11ec-bdcf-83a250730c01', 'PC', NULL, 't', 'user.add');
INSERT INTO "public"."tb_menu" VALUES ('279d0930-411e-11ec-8ed3-dda66ae3bde5', 'XTCD1636435831090', '用户管理 - 编辑', 1, 1, NULL, 'aa2af5e0-3e0a-11ec-866a-059202e55853', NULL, NULL, 'Global', 1636435831116, 'fc4b0e50-0c5a-11ec-bdcf-83a250730c01', '1636435831116', NULL, 'PC', NULL, 't', 'action.edit');
INSERT INTO "public"."tb_menu" VALUES ('3a985990-411e-11ec-8ed3-dda66ae3bde5', 'XTCD1636435862929', '用户管理 - 修改密码', 1, 1, NULL, 'aa2af5e0-3e0a-11ec-866a-059202e55853', NULL, NULL, 'Global', 1636435862964, 'fc4b0e50-0c5a-11ec-bdcf-83a250730c01', '1636435862964', NULL, 'PC', NULL, 't', 'auth-mng.change-pwd');
INSERT INTO "public"."tb_menu" VALUES ('52b523f0-411e-11ec-8ed3-dda66ae3bde5', 'XTCD1636435903390', '角色管理 - 添加角色', 1, 1, NULL, 'c84605b0-3e0a-11ec-866a-059202e55853', NULL, NULL, 'Global', 1636435903417, 'fc4b0e50-0c5a-11ec-bdcf-83a250730c01', '1636435903417', NULL, 'PC', NULL, 't', 'auth-mng.add-role');
INSERT INTO "public"."tb_menu" VALUES ('630885d0-411e-11ec-8ed3-dda66ae3bde5', 'XTCD1636435930784', '角色管理 - 删除', 1, 1, NULL, 'c84605b0-3e0a-11ec-866a-059202e55853', NULL, NULL, 'Global', 1636435930807, 'fc4b0e50-0c5a-11ec-bdcf-83a250730c01', '1636435930807', NULL, 'PC', NULL, 't', 'action.delete');
INSERT INTO "public"."tb_menu" VALUES ('9e599ca0-411e-11ec-8ed3-dda66ae3bde5', 'XTCD1636436030296', '角色管理 - 编辑', 1, 1, NULL, 'c84605b0-3e0a-11ec-866a-059202e55853', NULL, NULL, 'Global', 1636436030323, 'fc4b0e50-0c5a-11ec-bdcf-83a250730c01', '1636436030323', NULL, 'PC', NULL, 't', 'action.edit');
INSERT INTO "public"."tb_menu" VALUES ('4ecf6b00-412e-11ec-8ed3-dda66ae3bde5', 'XTCD1636442768781', '工厂管理', 1, 1, NULL, 'eccb2a40-3d48-11ec-9809-df813b08b61b', 'mdi:factory-mng', NULL, 'Global', 1636442768841, 'fc4b0e50-0c5a-11ec-bdcf-83a250730c01', '1636442768841', NULL, 'PC', '/deviceManagement/factoryManagement', 'f', 'device-mng.factory-mng');
INSERT INTO "public"."tb_menu" VALUES ('ffae2640-435f-11ec-956d-c10484bf3370', 'XTCD1636684013211', '实时监控', 0, 1, NULL, NULL, NULL, NULL, 'Global', 1636684013303, 'fc4b0e50-0c5a-11ec-bdcf-83a250730c01', '1636684013303', NULL, 'APP', NULL, 'f', 'auth-mng.set-permissions');
INSERT INTO "public"."tb_menu" VALUES ('08b713a0-4360-11ec-956d-c10484bf3370', 'XTCD1636684028377', '报警记录', 0, 1, NULL, NULL, NULL, NULL, 'Global', 1636684028379, 'fc4b0e50-0c5a-11ec-bdcf-83a250730c01', '1636684028379', NULL, 'APP', NULL, 'f', 'auth-mng.set-permissions');
INSERT INTO "public"."tb_menu" VALUES ('11692e70-4360-11ec-956d-c10484bf3370', 'XTCD1636684042965', '产能分析', 0, 1, NULL, NULL, NULL, NULL, 'Global', 1636684042968, 'fc4b0e50-0c5a-11ec-bdcf-83a250730c01', '1636684042968', NULL, 'APP', NULL, 'f', 'auth-mng.set-permissions');
INSERT INTO "public"."tb_menu" VALUES ('1b5c2db0-4360-11ec-956d-c10484bf3370', 'XTCD1636684059658', '能耗分析', 0, 1, NULL, NULL, NULL, NULL, 'Global', 1636684059660, 'fc4b0e50-0c5a-11ec-bdcf-83a250730c01', '1636684059660', NULL, 'APP', NULL, 'f', 'auth-mng.set-permissions');
INSERT INTO "public"."tb_menu" VALUES ('23cc0f60-4360-11ec-956d-c10484bf3370', 'XTCD1636684073813', '运行状态', 0, 1, NULL, NULL, NULL, NULL, 'Global', 1636684073815, 'fc4b0e50-0c5a-11ec-bdcf-83a250730c01', '1636684073815', NULL, 'APP', NULL, 'f', 'auth-mng.set-permissions');
INSERT INTO "public"."tb_menu" VALUES ('5374c300-46c7-11ec-9d4c-d5cce199ce69', 'XTCD1637058245420', '工厂管理 - 添加', 1, 1, NULL, '4ecf6b00-412e-11ec-8ed3-dda66ae3bde5', NULL, NULL, 'Global', 1637058245447, 'fc4b0e50-0c5a-11ec-bdcf-83a250730c01', '1637058245447', NULL, 'PC', NULL, 't', 'action.add');
INSERT INTO "public"."tb_menu" VALUES ('6d9dff30-46c7-11ec-9d4c-d5cce199ce69', 'XTCD1637058289313', '工厂管理 - 删除', 1, 1, NULL, '4ecf6b00-412e-11ec-8ed3-dda66ae3bde5', NULL, NULL, 'Global', 1637058289317, 'fc4b0e50-0c5a-11ec-bdcf-83a250730c01', '1637058289317', NULL, 'PC', NULL, 't', 'action.delete');
INSERT INTO "public"."tb_menu" VALUES ('80cda600-46c7-11ec-9d4c-d5cce199ce69', 'XTCD1637058321502', '工厂管理 - 编辑', 1, 1, NULL, '4ecf6b00-412e-11ec-8ed3-dda66ae3bde5', NULL, NULL, 'Global', 1637058321505, 'fc4b0e50-0c5a-11ec-bdcf-83a250730c01', '1637058321505', NULL, 'PC', NULL, 't', 'action.edit');
INSERT INTO "public"."tb_menu" VALUES ('a0bf71a0-46c7-11ec-9d4c-d5cce199ce69', 'XTCD1637058375095', '工厂管理 - 分配设备', 1, 1, NULL, '4ecf6b00-412e-11ec-8ed3-dda66ae3bde5', NULL, NULL, 'Global', 1637058375101, 'fc4b0e50-0c5a-11ec-bdcf-83a250730c01', '1637058375101', NULL, 'PC', NULL, 't', 'device-mng.distribute-device');
INSERT INTO "public"."tb_menu" VALUES ('26607400-46d0-11ec-9d4c-d5cce199ce69', 'XTCD1637062035259', '报警记录 - 确认', 1, 1, NULL, '32db2320-3e0a-11ec-866a-059202e55853', NULL, NULL, 'Global', 1637062035282, 'fc4b0e50-0c5a-11ec-bdcf-83a250730c01', '1637062035282', NULL, 'PC', NULL, 't', 'action.confirm');
INSERT INTO "public"."tb_menu" VALUES ('50f506e0-46d0-11ec-9d4c-d5cce199ce69', 'XTCD1637062106681', '报警记录 - 清除', 1, 1, NULL, '32db2320-3e0a-11ec-866a-059202e55853', NULL, NULL, 'Global', 1637062106744, 'fc4b0e50-0c5a-11ec-bdcf-83a250730c01', '1637062106744', NULL, 'PC', NULL, 't', 'alarm.clear');
INSERT INTO "public"."tb_menu" VALUES ('77c83c20-3ea5-11ec-9090-a3316146e5ad', 'XTCD1636164094175', '设备字典 - 编辑', 1, 1, NULL, 'c3b93c80-3e08-11ec-866a-059202e55853', NULL, NULL, 'Global', 1637065134887, 'fc4b0e50-0c5a-11ec-bdcf-83a250730c01', '1637065134887', 'fc4b0e50-0c5a-11ec-bdcf-83a250730c01', 'PC', NULL, 't', 'action.edit');
INSERT INTO "public"."tb_menu" VALUES ('763c6cb0-4750-11ec-bee8-51118663de80', 'XTCD1637117144794', '报警规则', 1, 1, NULL, 'f7e301c0-3e09-11ec-866a-059202e55853', 'mdi:alarm-rules', NULL, 'Global', 1637117546745, 'fc4b0e50-0c5a-11ec-bdcf-83a250730c01', '1637117546745', 'fc4b0e50-0c5a-11ec-bdcf-83a250730c01', 'PC', '/deviceMonitor/alarmRules', 'f', 'device-monitor.alarm-rules');
INSERT INTO "public"."tb_menu" VALUES ('32db2320-3e0a-11ec-866a-059202e55853', 'XTCD1636097406545', '报警记录', 1, 1, NULL, 'f7e301c0-3e09-11ec-866a-059202e55853', 'mdi:alarm-records', NULL, 'Global', 1637118082797, 'fc4b0e50-0c5a-11ec-bdcf-83a250730c01', '1637118082797', 'fc4b0e50-0c5a-11ec-bdcf-83a250730c01', 'PC', '/deviceMonitor/alarmRecord', 'f', 'device-monitor.alarm-record');
INSERT INTO "public"."tb_menu" VALUES ('10fc7ba0-3e0a-11ec-866a-059202e55853', 'XTCD1636097349722', '实时监控', 1, 1, NULL, 'f7e301c0-3e09-11ec-866a-059202e55853', 'mdi:real-time-monitor', NULL, 'Global', 1637118568386, 'fc4b0e50-0c5a-11ec-bdcf-83a250730c01', '1637118568386', 'fc4b0e50-0c5a-11ec-bdcf-83a250730c01', 'PC', '/deviceMonitor/realTimeMonitor', 'f', 'device-monitor.real-time-monitor');
INSERT INTO "public"."tb_menu" VALUES ('f7e301c0-3e09-11ec-866a-059202e55853', 'XTCD1636097307611', '设备监控', 0, 1, NULL, NULL, 'mdi:device-monitor', NULL, 'Global', 1637118819789, 'fc4b0e50-0c5a-11ec-bdcf-83a250730c01', '1637118819789', 'fc4b0e50-0c5a-11ec-bdcf-83a250730c01', 'PC', '/deviceMonitor', 'f', 'device-monitor.device-monitor');
INSERT INTO "public"."tb_menu" VALUES ('4495a7e0-4907-11ec-b2c3-7f574d105067', 'XTCD1637305610578', '边缘管理', 1, 1, NULL, '9e1b46a0-481a-11ec-931b-a78b7aaa97d6', 'settings_input_antenna', NULL, 'Global', 1637305610603, '5a797660-4612-11e7-a919-92ebcb67fe33', '1637305610603', NULL, 'PC', '/edgeManagement', 'f', 'edge.management');
INSERT INTO "public"."tb_menu" VALUES ('5c082880-4907-11ec-b2c3-7f574d105067', 'XTCD1637305649919', '规则链模板', 1, 1, NULL, '4495a7e0-4907-11ec-b2c3-7f574d105067', 'settings_ethernet', NULL, 'Global', 1637305649937, '5a797660-4612-11e7-a919-92ebcb67fe33', '1637305649937', NULL, 'PC', '/edgeManagement/ruleChains', 'f', 'edge.rulechain-templates');
INSERT INTO "public"."tb_menu" VALUES ('f28628c0-4907-11ec-b2c3-7f574d105067', 'XTCD1637305902402', '部件库', 1, 1, NULL, '9e1b46a0-481a-11ec-931b-a78b7aaa97d6', 'now_widgets', NULL, 'Global', 1637305902429, '5a797660-4612-11e7-a919-92ebcb67fe33', '1637305902429', NULL, 'PC', '/widgets-bundles', 'f', 'widget.widget-library');
INSERT INTO "public"."tb_menu" VALUES ('3e433280-4908-11ec-b2c3-7f574d105067', 'XTCD1637306029473', '仪表板库', 1, 1, NULL, '9e1b46a0-481a-11ec-931b-a78b7aaa97d6', 'dashboards', NULL, 'Global', 1637306029495, '5a797660-4612-11e7-a919-92ebcb67fe33', '1637306029495', NULL, 'PC', '/dashboards', 'f', 'dashboard.dashboards');
INSERT INTO "public"."tb_menu" VALUES ('a67f2840-4908-11ec-a773-27e2f8b15961', 'XTCD1637306204338', '审计日志', 1, 1, NULL, '9e1b46a0-481a-11ec-931b-a78b7aaa97d6', 'track_changes', NULL, 'Global', 1637306204483, '5a797660-4612-11e7-a919-92ebcb67fe33', '1637306204483', NULL, 'PC', '/auditLogs', 'f', 'audit-log.audit-logs');
INSERT INTO "public"."tb_menu" VALUES ('3abb75e0-4909-11ec-a773-27e2f8b15961', 'XTCD1637306453045', 'Api使用统计', 1, 1, NULL, '9e1b46a0-481a-11ec-931b-a78b7aaa97d6', 'insert_chart', NULL, 'Global', 1637306453066, '5a797660-4612-11e7-a919-92ebcb67fe33', '1637306453066', NULL, 'PC', '/usage', 'f', 'api-usage.api-usage');
INSERT INTO "public"."tb_menu" VALUES ('9e1b46a0-481a-11ec-931b-a78b7aaa97d6', 'XTCD1637203970039', '平台管理', 0, 1, NULL, NULL, 'settings', NULL, 'Global', 1637204066585, '5a797660-4612-11e7-a919-92ebcb67fe33', '1637204066585', '5a797660-4612-11e7-a919-92ebcb67fe33', 'PC', NULL, 'f', 'platform.platform-mng');
INSERT INTO "public"."tb_menu" VALUES ('f3fe94a0-481a-11ec-931b-a78b7aaa97d6', 'XTCD1637204114129', '设备', 1, 1, NULL, '9e1b46a0-481a-11ec-931b-a78b7aaa97d6', 'devices_other', NULL, 'Global', 1637204114165, '5a797660-4612-11e7-a919-92ebcb67fe33', '1637204114165', NULL, 'PC', '/devices', 'f', 'device.devices');
INSERT INTO "public"."tb_menu" VALUES ('e23f9d60-4831-11ec-931b-a78b7aaa97d6', 'XTCD1637213962800', '设备配置', 1, 1, NULL, '9e1b46a0-481a-11ec-931b-a78b7aaa97d6', 'mdi:alpha-d-box', NULL, 'Global', 1637213962817, '5a797660-4612-11e7-a919-92ebcb67fe33', '1637213962817', NULL, 'PC', '/deviceProfiles', 'f', 'device-profile.device-profiles');
INSERT INTO "public"."tb_menu" VALUES ('39c6c030-490a-11ec-a773-27e2f8b15961', 'XTCD1637306880936', '资源库', 1, 1, NULL, '9e1b46a0-481a-11ec-931b-a78b7aaa97d6', 'folder', NULL, 'Global', 1637315730975, '5a797660-4612-11e7-a919-92ebcb67fe33', '1637315730975', '5a797660-4612-11e7-a919-92ebcb67fe33', 'PC', '/settings/resources-library', 'f', 'resource.resources-library');
INSERT INTO "public"."tb_menu" VALUES ('0d3114e0-4904-11ec-b2c3-7f574d105067', 'XTCD1637304229158', '规则链', 1, 1, NULL, '9e1b46a0-481a-11ec-931b-a78b7aaa97d6', 'settings_ethernet', NULL, 'Global', 1637304407484, '5a797660-4612-11e7-a919-92ebcb67fe33', '1637304407484', '5a797660-4612-11e7-a919-92ebcb67fe33', 'PC', '/ruleChains', 'f', 'rulechain.rulechains');
INSERT INTO "public"."tb_menu" VALUES ('61d700e0-4904-11ec-b2c3-7f574d105067', 'XTCD1637304371176', '资产', 1, 1, NULL, '9e1b46a0-481a-11ec-931b-a78b7aaa97d6', 'domain', NULL, 'Global', 1637304416874, '5a797660-4612-11e7-a919-92ebcb67fe33', '1637304416874', '5a797660-4612-11e7-a919-92ebcb67fe33', 'PC', '/assets', 'f', 'asset.assets');
INSERT INTO "public"."tb_menu" VALUES ('4f6af830-4904-11ec-b2c3-7f574d105067', 'XTCD1637304340266', '客户', 1, 1, NULL, '9e1b46a0-481a-11ec-931b-a78b7aaa97d6', 'supervisor_account', NULL, 'Global', 1637304432742, '5a797660-4612-11e7-a919-92ebcb67fe33', '1637304432742', '5a797660-4612-11e7-a919-92ebcb67fe33', 'PC', '/customers', 'f', 'customer.customers');
INSERT INTO "public"."tb_menu" VALUES ('db9bb1f0-4909-11ec-a773-27e2f8b15961', 'XTCD1637306722953', '首页设置', 1, 1, NULL, '9e1b46a0-481a-11ec-931b-a78b7aaa97d6', 'settings_applications', NULL, 'Global', 1637315752750, '5a797660-4612-11e7-a919-92ebcb67fe33', '1637315752750', '5a797660-4612-11e7-a919-92ebcb67fe33', 'PC', '/settings/home', 'f', 'admin.home-settings');
INSERT INTO "public"."tb_menu" VALUES ('94eb8590-4905-11ec-b2c3-7f574d105067', 'XTCD1637304886368', '客户实体视图', 1, 1, NULL, '9e1b46a0-481a-11ec-931b-a78b7aaa97d6', 'view_quilt', NULL, 'Global', 1637304886386, '5a797660-4612-11e7-a919-92ebcb67fe33', '1637304886386', NULL, 'PC', '/entityViews', 'f', 'entity-view.entity-views');
INSERT INTO "public"."tb_menu" VALUES ('49039b40-4905-11ec-b2c3-7f574d105067', 'XTCD1637304759019', 'OAT更新', 1, 1, NULL, '9e1b46a0-481a-11ec-931b-a78b7aaa97d6', 'memory', NULL, 'Global', 1637304904392, '5a797660-4612-11e7-a919-92ebcb67fe33', '1637304904392', '5a797660-4612-11e7-a919-92ebcb67fe33', 'PC', '/otaUpdates', 'f', 'ota-update.ota-updates');
INSERT INTO "public"."tb_menu" VALUES ('173b0fd0-490a-11ec-a773-27e2f8b15961', 'XTCD1637306822980', '主题设置', 1, 1, NULL, '9e1b46a0-481a-11ec-931b-a78b7aaa97d6', 'color_lens', NULL, 'Global', 1637315819609, '5a797660-4612-11e7-a919-92ebcb67fe33', '1637315819609', '5a797660-4612-11e7-a919-92ebcb67fe33', 'PC', '/settings/custom-ui', 'f', 'system-settings.custom-ui');
COMMIT;













