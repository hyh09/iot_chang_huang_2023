

--设备字典属性初始数据
INSERT INTO "public"."hs_init"("id", "init_data", "scope", "created_time", "created_user", "updated_time", "updated_user") VALUES ('a6bcd176-7538-402c-9035-5b966888faa0', '[{"id": null, "name": "能耗", "groupPropertyList": [{"id": null, "name": "water", "unit": "T", "title": "耗水量", "content": "0", "createdTime": null}, {"id": null, "name": "electric", "unit": "KWH", "title": "耗电量", "content": "0", "createdTime": null}, {"id": null, "name": "gas", "unit": "T", "title": "耗气量", "content": "0", "createdTime": null}]}, {"id": null, "name": "产能", "groupPropertyList": [{"id": null, "name": "capacities", "unit": "", "title": "总产能", "content": "0", "createdTime": null}]}]', 'DICT_DEVICE_GROUP', 1636522070426, '07b770d0-3bb3-11ec-ad5a-9bec5deb66b9', 1636522070426, '07b770d0-3bb3-11ec-ad5a-9bec5deb66b9');


-- July 22, 2022 17:50:48 GMT+8
UPDATE "public"."hs_production_line" SET "sort" = '1' WHERE "name" = '前处理段';
UPDATE "public"."hs_production_line" SET "sort" = '2' WHERE "name" = '转移印染段';
UPDATE "public"."hs_production_line" SET "sort" = '3' WHERE "name" = '后处理段';



---初始化系统菜单
INSERT INTO "public"."tb_menu" ("id", "code", "name", "level", "sort", "url", "parent_id", "menu_icon", "menu_images", "region", "created_time", "created_user", "updated_time", "updated_user", "menu_type", "path", "is_button", "lang_key") VALUES
('570d8ce0-4906-11ec-b2c3-7f574d105067', 'XTCD1637305212069', '边缘实例', 1, 1, NULL, '9e1b46a0-481a-11ec-931b-a78b7aaa97d6', 'router', NULL, 'Global', 1637305212099, '5a797660-4612-11e7-a919-92ebcb67fe33', '1637305212099', NULL, 'PC', '/edgeInstances', 'f', 'edge.edge-instances'),
('376a56b0-3ea3-11ec-80ce-5546796a1b1d', 'XTCD1636163127194', '设备字典 - 删除', 1, 1, NULL, 'c3b93c80-3e08-11ec-866a-059202e55853', NULL, NULL, 'Global', 1636163127196, 'fc4b0e50-0c5a-11ec-bdcf-83a250730c01', '1636163127196', NULL, 'PC', NULL, 't', 'action.delete'),
('67e660c0-3ea5-11ec-9090-a3316146e5ad', 'XTCD1636164067530', '数据字典 - 编辑', 1, 1, NULL, '2df216f0-3d49-11ec-9809-df813b08b61b', NULL, NULL, 'Global', 1636164067592, 'fc4b0e50-0c5a-11ec-bdcf-83a250730c01', '1636164067592', NULL, 'PC', NULL, 't', 'action.edit'),
('d171c040-411e-11ec-8ed3-dda66ae3bde5', 'XTCD1636436116013', '角色管理 - 关联用户', 1, 1, NULL, 'c84605b0-3e0a-11ec-866a-059202e55853', NULL, NULL, 'Global', 1636436116048, 'fc4b0e50-0c5a-11ec-bdcf-83a250730c01', '1636436116048', NULL, 'PC', NULL, 't', 'auth-mng.bind-users'),
('eccb2a40-3d48-11ec-9809-df813b08b61b', 'XTCD1636014396130', '设备管理', 0, 1, NULL, NULL, 'devices', NULL, 'Global', 1636014396134, 'fc4b0e50-0c5a-11ec-bdcf-83a250730c01', '1636014396134', NULL, 'PC', '/deviceManagement', 'f', 'device-mng.device-mng'),
('976c8770-3e0a-11ec-866a-059202e55853', 'XTCD1636097575270', '权限管理', 0, 1, NULL, NULL, 'verified_user', NULL, 'Global', 1636097575272, 'fc4b0e50-0c5a-11ec-bdcf-83a250730c01', '1636097575272', NULL, 'PC', '/authManagement', 'f', 'auth-mng.auth-mng'),
('aa2af5e0-3e0a-11ec-866a-059202e55853', 'XTCD1636097606717', '用户管理', 1, 1, NULL, '976c8770-3e0a-11ec-866a-059202e55853', 'people', NULL, 'Global', 1636097606719, 'fc4b0e50-0c5a-11ec-bdcf-83a250730c01', '1636097606719', NULL, 'PC', '/authManagement/userManagement', 'f', 'auth-mng.user-mng'),
('c84605b0-3e0a-11ec-866a-059202e55853', 'XTCD1636097657227', '角色管理', 1, 1, NULL, '976c8770-3e0a-11ec-866a-059202e55853', 'mdi:shield-account', NULL, 'Global', 1636097657228, 'fc4b0e50-0c5a-11ec-bdcf-83a250730c01', '1636097657228', NULL, 'PC', '/authManagement/roleManagemnet', 'f', 'auth-mng.role-mng'),
('f74d38e0-3ea2-11ec-80ce-5546796a1b1d', 'XTCD1636163019626', '数据字典 - 添加数据字典', 1, 1, NULL, '2df216f0-3d49-11ec-9809-df813b08b61b', NULL, NULL, 'Global', 1636163019637, 'fc4b0e50-0c5a-11ec-bdcf-83a250730c01', '1636163019637', NULL, 'PC', NULL, 't', 'device-mng.add-data-dic'),
('07a2e4b0-3ea3-11ec-80ce-5546796a1b1d', 'XTCD1636163047034', '数据字典 - 删除', 1, 1, NULL, '2df216f0-3d49-11ec-9809-df813b08b61b', NULL, NULL, 'Global', 1636163047037, 'fc4b0e50-0c5a-11ec-bdcf-83a250730c01', '1636163047037', NULL, 'PC', NULL, 't', 'action.delete'),
('27e0fb90-3ea3-11ec-80ce-5546796a1b1d', 'XTCD1636163101127', '设备字典 - 添加设备字典', 1, 1, NULL, 'c3b93c80-3e08-11ec-866a-059202e55853', NULL, NULL, 'Global', 1636163101130, 'fc4b0e50-0c5a-11ec-bdcf-83a250730c01', '1636163101130', NULL, 'PC', NULL, 't', 'device-mng.add-device-dic'),
('07025df0-411f-11ec-8ed3-dda66ae3bde5', 'XTCD1636436205885', '角色管理 - 配置权限', 1, 1, NULL, 'c84605b0-3e0a-11ec-866a-059202e55853', NULL, NULL, 'Global', 1636436205920, 'fc4b0e50-0c5a-11ec-bdcf-83a250730c01', '1636436205920', NULL, 'PC', NULL, 't', 'auth-mng.set-permissions'),
('0486cc10-411e-11ec-8ed3-dda66ae3bde5', 'XTCD1636435772225', '用户管理 - 删除', 1, 1, NULL, 'aa2af5e0-3e0a-11ec-866a-059202e55853', NULL, NULL, 'Global', 1636435772247, 'fc4b0e50-0c5a-11ec-bdcf-83a250730c01', '1636435772247', NULL, 'PC', NULL, 't', 'action.delete'),
('eed8ded0-411d-11ec-8ed3-dda66ae3bde5', 'XTCD1636435735851', '用户管理 - 添加用户', 1, 1, NULL, 'aa2af5e0-3e0a-11ec-866a-059202e55853', NULL, NULL, 'Global', 1636435790262, 'fc4b0e50-0c5a-11ec-bdcf-83a250730c01', '1636435790262', 'fc4b0e50-0c5a-11ec-bdcf-83a250730c01', 'PC', NULL, 't', 'user.add'),
('279d0930-411e-11ec-8ed3-dda66ae3bde5', 'XTCD1636435831090', '用户管理 - 编辑', 1, 1, NULL, 'aa2af5e0-3e0a-11ec-866a-059202e55853', NULL, NULL, 'Global', 1636435831116, 'fc4b0e50-0c5a-11ec-bdcf-83a250730c01', '1636435831116', NULL, 'PC', NULL, 't', 'action.edit'),
('3a985990-411e-11ec-8ed3-dda66ae3bde5', 'XTCD1636435862929', '用户管理 - 修改密码', 1, 1, NULL, 'aa2af5e0-3e0a-11ec-866a-059202e55853', NULL, NULL, 'Global', 1636435862964, 'fc4b0e50-0c5a-11ec-bdcf-83a250730c01', '1636435862964', NULL, 'PC', NULL, 't', 'auth-mng.change-pwd'),
('52b523f0-411e-11ec-8ed3-dda66ae3bde5', 'XTCD1636435903390', '角色管理 - 添加角色', 1, 1, NULL, 'c84605b0-3e0a-11ec-866a-059202e55853', NULL, NULL, 'Global', 1636435903417, 'fc4b0e50-0c5a-11ec-bdcf-83a250730c01', '1636435903417', NULL, 'PC', NULL, 't', 'auth-mng.add-role'),
('630885d0-411e-11ec-8ed3-dda66ae3bde5', 'XTCD1636435930784', '角色管理 - 删除', 1, 1, NULL, 'c84605b0-3e0a-11ec-866a-059202e55853', NULL, NULL, 'Global', 1636435930807, 'fc4b0e50-0c5a-11ec-bdcf-83a250730c01', '1636435930807', NULL, 'PC', NULL, 't', 'action.delete'),
('9e599ca0-411e-11ec-8ed3-dda66ae3bde5', 'XTCD1636436030296', '角色管理 - 编辑', 1, 1, NULL, 'c84605b0-3e0a-11ec-866a-059202e55853', NULL, NULL, 'Global', 1636436030323, 'fc4b0e50-0c5a-11ec-bdcf-83a250730c01', '1636436030323', NULL, 'PC', NULL, 't', 'action.edit'),
('4ecf6b00-412e-11ec-8ed3-dda66ae3bde5', 'XTCD1636442768781', '工厂管理', 1, 1, NULL, 'eccb2a40-3d48-11ec-9809-df813b08b61b', 'mdi:factory-mng', NULL, 'Global', 1636442768841, 'fc4b0e50-0c5a-11ec-bdcf-83a250730c01', '1636442768841', NULL, 'PC', '/deviceManagement/factoryManagement', 'f', 'device-mng.factory-mng'),
('ffae2640-435f-11ec-956d-c10484bf3370', 'XTCD1636684013211', '实时监控', 0, 1, NULL, NULL, NULL, NULL, 'Global', 1636684013303, 'fc4b0e50-0c5a-11ec-bdcf-83a250730c01', '1636684013303', NULL, 'APP', NULL, 'f', 'auth-mng.set-permissions'),
('08b713a0-4360-11ec-956d-c10484bf3370', 'XTCD1636684028377', '报警记录', 0, 1, NULL, NULL, NULL, NULL, 'Global', 1636684028379, 'fc4b0e50-0c5a-11ec-bdcf-83a250730c01', '1636684028379', NULL, 'APP', NULL, 'f', 'auth-mng.set-permissions'),
('11692e70-4360-11ec-956d-c10484bf3370', 'XTCD1636684042965', '产能分析', 0, 1, NULL, NULL, NULL, NULL, 'Global', 1636684042968, 'fc4b0e50-0c5a-11ec-bdcf-83a250730c01', '1636684042968', NULL, 'APP', NULL, 'f', 'auth-mng.set-permissions'),
('1b5c2db0-4360-11ec-956d-c10484bf3370', 'XTCD1636684059658', '能耗分析', 0, 1, NULL, NULL, NULL, NULL, 'Global', 1636684059660, 'fc4b0e50-0c5a-11ec-bdcf-83a250730c01', '1636684059660', NULL, 'APP', NULL, 'f', 'auth-mng.set-permissions'),
('23cc0f60-4360-11ec-956d-c10484bf3370', 'XTCD1636684073813', '运行状态', 0, 1, NULL, NULL, NULL, NULL, 'Global', 1636684073815, 'fc4b0e50-0c5a-11ec-bdcf-83a250730c01', '1636684073815', NULL, 'APP', NULL, 'f', 'auth-mng.set-permissions'),
('80cda600-46c7-11ec-9d4c-d5cce199ce69', 'XTCD1637058321502', '工厂管理 - 编辑', 1, 1, NULL, '4ecf6b00-412e-11ec-8ed3-dda66ae3bde5', NULL, NULL, 'Global', 1637058321505, 'fc4b0e50-0c5a-11ec-bdcf-83a250730c01', '1637058321505', NULL, 'PC', NULL, 't', 'action.edit'),
('a0bf71a0-46c7-11ec-9d4c-d5cce199ce69', 'XTCD1637058375095', '工厂管理 - 分配设备', 1, 1, NULL, '4ecf6b00-412e-11ec-8ed3-dda66ae3bde5', NULL, NULL, 'Global', 1637058375101, 'fc4b0e50-0c5a-11ec-bdcf-83a250730c01', '1637058375101', NULL, 'PC', NULL, 't', 'device-mng.distribute-device'),
('26607400-46d0-11ec-9d4c-d5cce199ce69', 'XTCD1637062035259', '报警记录 - 确认', 1, 1, NULL, '32db2320-3e0a-11ec-866a-059202e55853', NULL, NULL, 'Global', 1637062035282, 'fc4b0e50-0c5a-11ec-bdcf-83a250730c01', '1637062035282', NULL, 'PC', NULL, 't', 'action.confirm'),
('50f506e0-46d0-11ec-9d4c-d5cce199ce69', 'XTCD1637062106681', '报警记录 - 清除', 1, 1, NULL, '32db2320-3e0a-11ec-866a-059202e55853', NULL, NULL, 'Global', 1637062106744, 'fc4b0e50-0c5a-11ec-bdcf-83a250730c01', '1637062106744', NULL, 'PC', NULL, 't', 'alarm.clear'),
('77c83c20-3ea5-11ec-9090-a3316146e5ad', 'XTCD1636164094175', '设备字典 - 编辑', 1, 1, NULL, 'c3b93c80-3e08-11ec-866a-059202e55853', NULL, NULL, 'Global', 1637065134887, 'fc4b0e50-0c5a-11ec-bdcf-83a250730c01', '1637065134887', 'fc4b0e50-0c5a-11ec-bdcf-83a250730c01', 'PC', NULL, 't', 'action.edit'),
('763c6cb0-4750-11ec-bee8-51118663de80', 'XTCD1637117144794', '报警规则', 1, 1, NULL, 'f7e301c0-3e09-11ec-866a-059202e55853', 'mdi:alarm-rules', NULL, 'Global', 1637117546745, 'fc4b0e50-0c5a-11ec-bdcf-83a250730c01', '1637117546745', 'fc4b0e50-0c5a-11ec-bdcf-83a250730c01', 'PC', '/deviceMonitor/alarmRules', 'f', 'device-monitor.alarm-rules'),
('32db2320-3e0a-11ec-866a-059202e55853', 'XTCD1636097406545', '报警记录', 1, 1, NULL, 'f7e301c0-3e09-11ec-866a-059202e55853', 'mdi:alarm-records', NULL, 'Global', 1637118082797, 'fc4b0e50-0c5a-11ec-bdcf-83a250730c01', '1637118082797', 'fc4b0e50-0c5a-11ec-bdcf-83a250730c01', 'PC', '/deviceMonitor/alarmRecord', 'f', 'device-monitor.alarm-record'),
('10fc7ba0-3e0a-11ec-866a-059202e55853', 'XTCD1636097349722', '实时监控', 1, 1, NULL, 'f7e301c0-3e09-11ec-866a-059202e55853', 'mdi:real-time-monitor', NULL, 'Global', 1637118568386, 'fc4b0e50-0c5a-11ec-bdcf-83a250730c01', '1637118568386', 'fc4b0e50-0c5a-11ec-bdcf-83a250730c01', 'PC', '/deviceMonitor/realTimeMonitor', 'f', 'device-monitor.real-time-monitor'),
('f7e301c0-3e09-11ec-866a-059202e55853', 'XTCD1636097307611', '设备监控', 0, 1, NULL, NULL, 'mdi:device-monitor', NULL, 'Global', 1637118819789, 'fc4b0e50-0c5a-11ec-bdcf-83a250730c01', '1637118819789', 'fc4b0e50-0c5a-11ec-bdcf-83a250730c01', 'PC', '/deviceMonitor', 'f', 'device-monitor.device-monitor'),
('f28628c0-4907-11ec-b2c3-7f574d105067', 'XTCD1637305902402', '部件库', 1, 1, NULL, '9e1b46a0-481a-11ec-931b-a78b7aaa97d6', 'now_widgets', NULL, 'Global', 1637305902429, '5a797660-4612-11e7-a919-92ebcb67fe33', '1637305902429', NULL, 'PC', '/widgets-bundles', 'f', 'widget.widget-library'),
('3e433280-4908-11ec-b2c3-7f574d105067', 'XTCD1637306029473', '仪表板库', 1, 1, NULL, '9e1b46a0-481a-11ec-931b-a78b7aaa97d6', 'dashboards', NULL, 'Global', 1637306029495, '5a797660-4612-11e7-a919-92ebcb67fe33', '1637306029495', NULL, 'PC', '/dashboards', 'f', 'dashboard.dashboards'),
('a67f2840-4908-11ec-a773-27e2f8b15961', 'XTCD1637306204338', '审计日志', 1, 1, NULL, '9e1b46a0-481a-11ec-931b-a78b7aaa97d6', 'track_changes', NULL, 'Global', 1637306204483, '5a797660-4612-11e7-a919-92ebcb67fe33', '1637306204483', NULL, 'PC', '/auditLogs', 'f', 'audit-log.audit-logs'),
('3abb75e0-4909-11ec-a773-27e2f8b15961', 'XTCD1637306453045', 'Api使用统计', 1, 1, NULL, '9e1b46a0-481a-11ec-931b-a78b7aaa97d6', 'insert_chart', NULL, 'Global', 1637306453066, '5a797660-4612-11e7-a919-92ebcb67fe33', '1637306453066', NULL, 'PC', '/usage', 'f', 'api-usage.api-usage'),
('f3fe94a0-481a-11ec-931b-a78b7aaa97d6', 'XTCD1637204114129', '设备', 1, 1, NULL, '9e1b46a0-481a-11ec-931b-a78b7aaa97d6', 'devices_other', NULL, 'Global', 1637204114165, '5a797660-4612-11e7-a919-92ebcb67fe33', '1637204114165', NULL, 'PC', '/devices', 'f', 'device.devices'),
('e23f9d60-4831-11ec-931b-a78b7aaa97d6', 'XTCD1637213962800', '设备配置', 1, 1, NULL, '9e1b46a0-481a-11ec-931b-a78b7aaa97d6', 'mdi:alpha-d-box', NULL, 'Global', 1637213962817, '5a797660-4612-11e7-a919-92ebcb67fe33', '1637213962817', NULL, 'PC', '/deviceProfiles', 'f', 'device-profile.device-profiles'),
('39c6c030-490a-11ec-a773-27e2f8b15961', 'XTCD1637306880936', '资源库', 1, 1, NULL, '9e1b46a0-481a-11ec-931b-a78b7aaa97d6', 'folder', NULL, 'Global', 1637315730975, '5a797660-4612-11e7-a919-92ebcb67fe33', '1637315730975', '5a797660-4612-11e7-a919-92ebcb67fe33', 'PC', '/settings/resources-library', 'f', 'resource.resources-library'),
('0d3114e0-4904-11ec-b2c3-7f574d105067', 'XTCD1637304229158', '规则链', 1, 1, NULL, '9e1b46a0-481a-11ec-931b-a78b7aaa97d6', 'settings_ethernet', NULL, 'Global', 1637304407484, '5a797660-4612-11e7-a919-92ebcb67fe33', '1637304407484', '5a797660-4612-11e7-a919-92ebcb67fe33', 'PC', '/ruleChains', 'f', 'rulechain.rulechains'),
('61d700e0-4904-11ec-b2c3-7f574d105067', 'XTCD1637304371176', '资产', 1, 1, NULL, '9e1b46a0-481a-11ec-931b-a78b7aaa97d6', 'domain', NULL, 'Global', 1637304416874, '5a797660-4612-11e7-a919-92ebcb67fe33', '1637304416874', '5a797660-4612-11e7-a919-92ebcb67fe33', 'PC', '/assets', 'f', 'asset.assets'),
('4f6af830-4904-11ec-b2c3-7f574d105067', 'XTCD1637304340266', '客户', 1, 1, NULL, '9e1b46a0-481a-11ec-931b-a78b7aaa97d6', 'supervisor_account', NULL, 'Global', 1637304432742, '5a797660-4612-11e7-a919-92ebcb67fe33', '1637304432742', '5a797660-4612-11e7-a919-92ebcb67fe33', 'PC', '/customers', 'f', 'customer.customers'),
('db9bb1f0-4909-11ec-a773-27e2f8b15961', 'XTCD1637306722953', '首页设置', 1, 1, NULL, '9e1b46a0-481a-11ec-931b-a78b7aaa97d6', 'settings_applications', NULL, 'Global', 1637315752750, '5a797660-4612-11e7-a919-92ebcb67fe33', '1637315752750', '5a797660-4612-11e7-a919-92ebcb67fe33', 'PC', '/settings/home', 'f', 'admin.home-settings'),
('94eb8590-4905-11ec-b2c3-7f574d105067', 'XTCD1637304886368', '客户实体视图', 1, 1, NULL, '9e1b46a0-481a-11ec-931b-a78b7aaa97d6', 'view_quilt', NULL, 'Global', 1637304886386, '5a797660-4612-11e7-a919-92ebcb67fe33', '1637304886386', NULL, 'PC', '/entityViews', 'f', 'entity-view.entity-views'),
('49039b40-4905-11ec-b2c3-7f574d105067', 'XTCD1637304759019', 'OAT更新', 1, 1, NULL, '9e1b46a0-481a-11ec-931b-a78b7aaa97d6', 'memory', NULL, 'Global', 1637304904392, '5a797660-4612-11e7-a919-92ebcb67fe33', '1637304904392', '5a797660-4612-11e7-a919-92ebcb67fe33', 'PC', '/otaUpdates', 'f', 'ota-update.ota-updates'),
('173b0fd0-490a-11ec-a773-27e2f8b15961', 'XTCD1637306822980', '主题设置', 1, 1, NULL, '9e1b46a0-481a-11ec-931b-a78b7aaa97d6', 'color_lens', NULL, 'Global', 1637315819609, '5a797660-4612-11e7-a919-92ebcb67fe33', '1637315819609', '5a797660-4612-11e7-a919-92ebcb67fe33', 'PC', '/settings/custom-ui', 'f', 'system-settings.custom-ui'),
('1ee3f690-4c00-11ec-ac73-c546a4193fa0', 'XTCD1637632394354', '产能分析', 1, 1, NULL, '42462640-4bff-11ec-ac73-c546a4193fa0', 'mdi:capacity', NULL, 'Global', 1637632394367, '5a797660-4612-11e7-a919-92ebcb67fe33', '1637632394367', NULL, 'PC', '/potency/deviceCapacity', 'f', 'potency.device-capacity'),
('42462640-4bff-11ec-ac73-c546a4193fa0', 'XTCD1637632024202', '效能分析', 0, 1, NULL, NULL, 'mdi:potency', NULL, 'Global', 1637632332123, '5a797660-4612-11e7-a919-92ebcb67fe33', '1637632332123', '5a797660-4612-11e7-a919-92ebcb67fe33', 'PC', 'potency', 'f', 'potency.potency'),
('be0754b0-4c32-11ec-a459-159d9ed885bc', 'XTCD1637654136176', '运行状态', 1, 1, NULL, '42462640-4bff-11ec-ac73-c546a4193fa0', 'mdi:running-state', NULL, 'Global', 1637654136624, '5a797660-4612-11e7-a919-92ebcb67fe33', '1637654136624', NULL, 'PC', '/potency/runningState', 'f', 'potency.running-state'),
('2c1c9600-4c23-11ec-b550-4f9d96b0fc51', 'XTCD1637647448914', '能耗分析', 1, 1, NULL, '42462640-4bff-11ec-ac73-c546a4193fa0', 'mdi:energy', NULL, 'Global', 1637647452108, '5a797660-4612-11e7-a919-92ebcb67fe33', '1637647452108', NULL, 'PC', '/potency/energyConsumption', 'f', 'potency.energy-consumption'),
('4cea95a0-531a-11ec-8f0d-e7a9f5cb74f8', 'XTCD1638413296623', '工厂管理 - 管理工厂管理员', 1, 1, NULL, '4ecf6b00-412e-11ec-8ed3-dda66ae3bde5', NULL, NULL, 'Global', 1638413297057, '265dd610-40a8-11ec-afb6-7fee9c405457', '1638413297057', NULL, 'PC', NULL, 't', 'device-mng.manage-factory-manager'),
('74357bc0-531a-11ec-8f0d-e7a9f5cb74f8', 'XTCD1638413362546', '工厂管理 - 配置管理员权限', 1, 1, NULL, '4ecf6b00-412e-11ec-8ed3-dda66ae3bde5', NULL, NULL, 'Global', 1638413362567, '265dd610-40a8-11ec-afb6-7fee9c405457', '1638413362567', NULL, 'PC', NULL, 't', 'device-mng.set-factory-manager-permissions'),
('5c082880-4907-11ec-b2c3-7f574d105067', 'XTCD1637305649919', '规则链模板', 1, 1, NULL, '9e1b46a0-481a-11ec-931b-a78b7aaa97d6', 'settings_ethernet', NULL, 'Global', 1638772682272, '5a797660-4612-11e7-a919-92ebcb67fe33', '1638772682272', '265dd610-40a8-11ec-afb6-7fee9c405457', 'PC', '/edgeManagement/ruleChains', 'f', 'edge.rulechain-templates'),
('9e1b46a0-481a-11ec-931b-a78b7aaa97d6', 'XTCD1637203970039', '平台管理', 0, 1, NULL, NULL, 'mdi:platform-mng', NULL, 'Global', 1638777266756, '5a797660-4612-11e7-a919-92ebcb67fe33', '1638777266756', '265dd610-40a8-11ec-afb6-7fee9c405457', 'PC', NULL, 'f', 'platform.platform-mng'),
('d9a70800-5669-11ec-bdfd-b18997b56dbf', 'XTCD1638777316463', '系统管理', 0, 1, NULL, NULL, 'settings', NULL, 'Global', 1638777316553, '265dd610-40a8-11ec-afb6-7fee9c405457', '1638777316553', NULL, 'PC', NULL, 'f', 'system-mng.system-mng'),
('18245880-566a-11ec-bdfd-b18997b56dbf', 'XTCD1638777421305', '工厂软件版本', 1, 1, NULL, 'd9a70800-5669-11ec-bdfd-b18997b56dbf', 'mdi:software', NULL, 'Global', 1638777904214, '265dd610-40a8-11ec-afb6-7fee9c405457', '1638777904214', '265dd610-40a8-11ec-afb6-7fee9c405457', 'PC', '/systemManagement/factoryVersion', 'f', 'system-mng.factory-version'),
('ca940330-570a-11ec-bc52-37d90f432ffa', 'XTCD1638846440141', '产能运算配置', 1, 1, NULL, 'eccb2a40-3d48-11ec-9809-df813b08b61b', 'mdi:switch-config', NULL, 'Global', 1638846800395, '265dd610-40a8-11ec-afb6-7fee9c405457', '1638846800395', '265dd610-40a8-11ec-afb6-7fee9c405457', 'PC', '/deviceManagement/productionCapacitySettings', 'f', 'device-mng.prod-capactity-settings'),
('2df216f0-3d49-11ec-9809-df813b08b61b', 'XTCD1636014505438', '数据字典', 1, 1, NULL, 'eccb2a40-3d48-11ec-9809-df813b08b61b', 'mdi:data-dictionary', NULL, 'Global', 1638847276351, 'fc4b0e50-0c5a-11ec-bdcf-83a250730c01', '1638847276351', '265dd610-40a8-11ec-afb6-7fee9c405457', 'PC', '/deviceManagement/dataDictionary', 'f', 'device-mng.data-dic'),
('c3b93c80-3e08-11ec-866a-059202e55853', 'XTCD1636096790591', '设备字典', 1, 1, NULL, 'eccb2a40-3d48-11ec-9809-df813b08b61b', 'mdi:device-dictionary', NULL, 'Global', 1638847308338, 'fc4b0e50-0c5a-11ec-bdcf-83a250730c01', '1638847308338', '265dd610-40a8-11ec-afb6-7fee9c405457', 'PC', '/deviceManagement/deviceDictionary', 'f', 'device-mng.device-dic'),
('5374c300-46c7-11ec-9d4c-d5cce199ce69', 'XTCD1637058245420', '工厂管理 - 添加工厂', 1, 1, NULL, '4ecf6b00-412e-11ec-8ed3-dda66ae3bde5', NULL, NULL, 'Global', 1639041038760, 'fc4b0e50-0c5a-11ec-bdcf-83a250730c01', '1639041038760', '265dd610-40a8-11ec-afb6-7fee9c405457', 'PC', NULL, 't', 'device-mng.add-factory'),
('f2e2ff00-58cf-11ec-b7c6-c9d29ed15db6', 'XTCD1639041069806', '工厂管理 - 添加车间', 1, 1, NULL, '4ecf6b00-412e-11ec-8ed3-dda66ae3bde5', NULL, NULL, 'Global', 1639041069809, '265dd610-40a8-11ec-afb6-7fee9c405457', '1639041069809', NULL, 'PC', NULL, 't', 'device-mng.add-work-shop'),
('04d284b0-58d0-11ec-b7c6-c9d29ed15db6', 'XTCD1639041099899', '工厂管理 - 添加产线', 1, 1, NULL, '4ecf6b00-412e-11ec-8ed3-dda66ae3bde5', NULL, NULL, 'Global', 1639041099900, '265dd610-40a8-11ec-afb6-7fee9c405457', '1639041099900', NULL, 'PC', NULL, 't', 'device-mng.add-prod-line'),
('26e163f0-58d0-11ec-b7c6-c9d29ed15db6', 'XTCD1639041157039', '工厂管理 - 添加设备', 1, 1, NULL, '4ecf6b00-412e-11ec-8ed3-dda66ae3bde5', NULL, NULL, 'Global', 1639041157040, '265dd610-40a8-11ec-afb6-7fee9c405457', '1639041157040', NULL, 'PC', NULL, 't', 'device-mng.add-device'),
('e4452c30-595a-11ec-b7c6-c9d29ed15db6', 'XTCD1639100745330', '系统版本', 1, 1, NULL, 'd9a70800-5669-11ec-bdfd-b18997b56dbf', 'mdi:version', NULL, 'Global', 1639123254701, '265dd610-40a8-11ec-afb6-7fee9c405457', '1639123254701', '265dd610-40a8-11ec-afb6-7fee9c405457', 'PC', '/systemManagement/systemVersion', 'f', 'system-mng.system-version'),
('23d4f2c0-5bfb-11ec-b3d1-453d9782c4b2', 'XTCD1639389473769', '设备字典 - 配置下发', 1, 1, NULL, 'c3b93c80-3e08-11ec-866a-059202e55853', NULL, NULL, 'Global', 1639389473787, '265dd610-40a8-11ec-afb6-7fee9c405457', '1639389473787', NULL, 'PC', NULL, 't', 'device-mng.distribut-config'),
('6d9dff30-46c7-11ec-9d4c-d5cce199ce69', 'XTCD1637058289313', '工厂管理 - 删除工厂', 1, 1, NULL, '4ecf6b00-412e-11ec-8ed3-dda66ae3bde5', NULL, NULL, 'Global', 1640050431742, 'fc4b0e50-0c5a-11ec-bdcf-83a250730c01', '1640050431742', '265dd610-40a8-11ec-afb6-7fee9c405457', 'PC', NULL, 't', 'device-mng.delete-factory'),
('1d8055a0-61fe-11ec-8332-7b8df1e64f2d', 'XTCD1640050458615', '工厂管理 - 删除车间', 1, 1, NULL, '4ecf6b00-412e-11ec-8ed3-dda66ae3bde5', NULL, NULL, 'Global', 1640050458619, '265dd610-40a8-11ec-afb6-7fee9c405457', '1640050458619', NULL, 'PC', NULL, 't', 'device-mng.delete-work-shop'),
('2f16e5e0-61fe-11ec-8332-7b8df1e64f2d', 'XTCD1640050488125', '工厂管理 - 删除产线', 1, 1, NULL, '4ecf6b00-412e-11ec-8ed3-dda66ae3bde5', NULL, NULL, 'Global', 1640050488127, '265dd610-40a8-11ec-afb6-7fee9c405457', '1640050488127', NULL, 'PC', NULL, 't', 'device-mng.delete-prod-line'),
('43a07760-61fe-11ec-8332-7b8df1e64f2d', 'XTCD1640050522581', '工厂管理 - 删除设备', 1, 1, NULL, '4ecf6b00-412e-11ec-8ed3-dda66ae3bde5', NULL, NULL, 'Global', 1640050522582, '265dd610-40a8-11ec-afb6-7fee9c405457', '1640050522582', NULL, 'PC', NULL, 't', 'device-mng.delete-device'),
('4bb5f1a0-9dd6-11ec-a79b-07ce2c030081', 'XTCD1646630426041', '生产管理 - 管理日历', 0, 1, NULL, NULL, NULL, NULL, 'Global', 1647833476427, '265dd610-40a8-11ec-afb6-7fee9c405457', '1647833476427', '265dd610-40a8-11ec-afb6-7fee9c405457', 'PC', NULL, 't', 'device-mng.mng-calendars'),
('2e0c8f60-9dd6-11ec-9386-9d079fb5289f', 'XTCD1646630376277', '生产管理', 0, 1, NULL, NULL, 'mdi:prod-mng', NULL, 'Global', 1647833556149, '265dd610-40a8-11ec-afb6-7fee9c405457', '1647833556149', '265dd610-40a8-11ec-afb6-7fee9c405457', 'PC', '/deviceManagement/prodManagement', 'f', 'device-mng.prod-mng'),
('1346b330-6eb1-11ec-9772-d79aa7bd0c88', 'XTCD1641446735331', '角色管理 - 查看', 1, 1, NULL, 'c84605b0-3e0a-11ec-866a-059202e55853', NULL, NULL, 'Global', 1641448524178, '825fafb0-694d-11ec-b4a8-134013dcacfd', '1641448524178', '50570d90-6eae-11ec-9772-d79aa7bd0c88', 'PC', NULL, 't', NULL),
('df6720e0-6eb0-11ec-9772-d79aa7bd0c88', 'XTCD1641446648301', '用户管理 - 查看', 1, 1, NULL, 'aa2af5e0-3e0a-11ec-866a-059202e55853', NULL, NULL, 'Global', 1641448539990, '825fafb0-694d-11ec-b4a8-134013dcacfd', '1641448539990', '50570d90-6eae-11ec-9772-d79aa7bd0c88', 'PC', NULL, 't', NULL),
('b680da40-6eb0-11ec-9772-d79aa7bd0c88', 'XTCD1641446579683', '报警规则 - 查看', 1, 1, NULL, '763c6cb0-4750-11ec-bee8-51118663de80', NULL, NULL, 'Global', 1641448560803, '825fafb0-694d-11ec-b4a8-134013dcacfd', '1641448560803', '50570d90-6eae-11ec-9772-d79aa7bd0c88', 'PC', NULL, 't', NULL),
('ab0b80c0-6eb0-11ec-9772-d79aa7bd0c88', 'XTCD1641446560460', '报警记录 - 查看', 1, 1, NULL, '32db2320-3e0a-11ec-866a-059202e55853', NULL, NULL, 'Global', 1641448578095, '825fafb0-694d-11ec-b4a8-134013dcacfd', '1641448578095', '50570d90-6eae-11ec-9772-d79aa7bd0c88', 'PC', NULL, 't', NULL),
('719ce590-6eb0-11ec-9772-d79aa7bd0c88', 'XTCD1641446464104', '工厂管理 - 查看', 1, 1, NULL, '4ecf6b00-412e-11ec-8ed3-dda66ae3bde5', NULL, NULL, 'Global', 1641448590223, '265dd610-40a8-11ec-afb6-7fee9c405457', '1641448590223', '50570d90-6eae-11ec-9772-d79aa7bd0c88', 'PC', NULL, 't', NULL),
('459561c0-6eb0-11ec-9772-d79aa7bd0c88', 'XTCD1641446390236', '设备字典 - 查看', 1, 1, NULL, 'c3b93c80-3e08-11ec-866a-059202e55853', NULL, NULL, 'Global', 1641448603623, '265dd610-40a8-11ec-afb6-7fee9c405457', '1641448603623', '50570d90-6eae-11ec-9772-d79aa7bd0c88', 'PC', NULL, 't', NULL),
('e95bc340-6eaf-11ec-9772-d79aa7bd0c88', 'XTCD1641446235507', '数据字典 - 查看', 1, 1, NULL, '2df216f0-3d49-11ec-9809-df813b08b61b', NULL, NULL, 'Global', 1641448615681, '265dd610-40a8-11ec-afb6-7fee9c405457', '1641448615681', '50570d90-6eae-11ec-9772-d79aa7bd0c88', 'PC', NULL, 't', NULL),
('92c431b0-6eb5-11ec-9772-d79aa7bd0c88', 'XTCD1641448667211', '报警规则 - 添加', 1, 1, NULL, '763c6cb0-4750-11ec-bee8-51118663de80', NULL, NULL, 'Global', 1641449905099, '50570d90-6eae-11ec-9772-d79aa7bd0c88', '1641449905099', '50570d90-6eae-11ec-9772-d79aa7bd0c88', 'PC', NULL, 't', 'device-monitor.add-alarm-rule'),
('dfd5f601-6eb5-11ec-9772-d79aa7bd0c88', 'XTCD1641448796512', '报警规则 - 删除', 1, 1, NULL, '763c6cb0-4750-11ec-bee8-51118663de80', NULL, NULL, 'Global', 1641449935238, '50570d90-6eae-11ec-9772-d79aa7bd0c88', '1641449935238', '50570d90-6eae-11ec-9772-d79aa7bd0c88', 'PC', NULL, 't', 'action.delete'),
('b0c9e4c0-6eb5-11ec-9772-d79aa7bd0c88', 'XTCD1641448717579', '报警规则 - 编辑', 1, 1, NULL, '763c6cb0-4750-11ec-bee8-51118663de80', NULL, NULL, 'Global', 1641449956341, '50570d90-6eae-11ec-9772-d79aa7bd0c88', '1641449956341', '50570d90-6eae-11ec-9772-d79aa7bd0c88', 'PC', NULL, 't', 'action.edit'),
('34ca7f50-71b8-11ec-b62f-bd99beea2ad2', 'XTCD1641779651509', '订单列表 - 删除', 1, 1, NULL, '2e5837d0-71b7-11ec-b62f-bd99beea2ad2', NULL, NULL, 'Global', 1641779651544, '265dd610-40a8-11ec-afb6-7fee9c405457', '1641779651544', NULL, 'PC', NULL, 't', 'action.delete'),
('f0b7bd50-71b7-11ec-b62f-bd99beea2ad2', 'XTCD1641779537308', '订单列表 - 编辑', 1, 1, NULL, '2e5837d0-71b7-11ec-b62f-bd99beea2ad2', NULL, NULL, 'Global', 1641779537328, '265dd610-40a8-11ec-afb6-7fee9c405457', '1641779537328', NULL, 'PC', NULL, 't', 'action.edit'),
('de594930-71b7-11ec-b62f-bd99beea2ad2', 'XTCD1641779506484', '订单列表 - 添加订单', 1, 1, NULL, '2e5837d0-71b7-11ec-b62f-bd99beea2ad2', NULL, NULL, 'Global', 1641779506516, '265dd610-40a8-11ec-afb6-7fee9c405457', '1641779506516', NULL, 'PC', NULL, 't', 'order.add-order'),
('badcd030-71b7-11ec-b62f-bd99beea2ad2', 'XTCD1641779446942', '订单管理 - 订单产能监控', 1, 1, NULL, 'd296d690-71b6-11ec-b62f-bd99beea2ad2', 'mdi:order-capacity', NULL, 'Global', 1641779446985, '265dd610-40a8-11ec-afb6-7fee9c405457', '1641779446985', NULL, 'PC', '/orderFormManagement/orderCapacity', 'f', 'order.order-capacity'),
('2e5837d0-71b7-11ec-b62f-bd99beea2ad2', 'XTCD1641779211199', '订单管理 - 订单列表', 1, 1, NULL, 'd296d690-71b6-11ec-b62f-bd99beea2ad2', 'mdi:order', NULL, 'Global', 1641779211233, '265dd610-40a8-11ec-afb6-7fee9c405457', '1641779211233', NULL, 'PC', '/orderFormManagement/orders', 'f', 'order.orders'),
('d296d690-71b6-11ec-b62f-bd99beea2ad2', 'XTCD1641779057244', '订单管理', 0, 1, NULL, NULL, 'mdi:order-mng', NULL, 'Global', 1641779091264, '265dd610-40a8-11ec-afb6-7fee9c405457', '1641779091264', '265dd610-40a8-11ec-afb6-7fee9c405457', 'PC', NULL, 'f', 'order.order-form-mng'),
('13796970-7437-11ec-b80f-7b335e68957e', 'XTCD1642054044037', '订单管理', 0, 1, NULL, NULL, NULL, NULL, 'Global', 1642154780041, '265dd610-40a8-11ec-afb6-7fee9c405457', '1642154780041', '265dd610-40a8-11ec-afb6-7fee9c405457', 'APP', NULL, 'f', NULL),
('9aa35850-9ba8-11ec-96ee-613d9bcc5d10', 'XTCD1646390899284', '数据关联', 1, 1, NULL, 'eccb2a40-3d48-11ec-9809-df813b08b61b', 'mdi:chart-timeline-variant', NULL, 'Global', 1646624291529, '265dd610-40a8-11ec-afb6-7fee9c405457', '1646624291529', '265dd610-40a8-11ec-afb6-7fee9c405457', 'PC', '/deviceManagement/chartSettings', 'f', 'device-mng.chart-settings'),
('e3017fa0-9ba8-11ec-8347-6366c0cb66a6', 'XTCD1646391020697', '数据关联 - 删除', 1, 1, NULL, '9aa35850-9ba8-11ec-96ee-613d9bcc5d10', NULL, NULL, 'Global', 1646624299556, '265dd610-40a8-11ec-afb6-7fee9c405457', '1646624299556', '265dd610-40a8-11ec-afb6-7fee9c405457', 'PC', NULL, 't', 'action.delete'),
('cf171450-9ba8-11ec-96ee-613d9bcc5d10', 'XTCD1646390987284', '数据关联 - 编辑', 1, 1, NULL, '9aa35850-9ba8-11ec-96ee-613d9bcc5d10', NULL, NULL, 'Global', 1646624306969, '265dd610-40a8-11ec-afb6-7fee9c405457', '1646624306969', '265dd610-40a8-11ec-afb6-7fee9c405457', 'PC', NULL, 't', 'action.edit'),
('b831b880-9ba8-11ec-8347-6366c0cb66a6', 'XTCD1646390948871', '数据关联 - 添加', 1, 1, NULL, '9aa35850-9ba8-11ec-96ee-613d9bcc5d10', NULL, NULL, 'Global', 1646624315774, '265dd610-40a8-11ec-afb6-7fee9c405457', '1646624315774', '265dd610-40a8-11ec-afb6-7fee9c405457', 'PC', NULL, 't', 'action.add'),
('60b4b320-9dd6-11ec-9386-9d079fb5289f', 'XTCD1646630461266', '生产管理 - 管理日历 - 添加', 1, 1, NULL, '2e0c8f60-9dd6-11ec-9386-9d079fb5289f', NULL, NULL, 'Global', 1646630461267, '265dd610-40a8-11ec-afb6-7fee9c405457', '1646630461267', NULL, 'PC', NULL, 't', 'action.add'),
('74608ed0-9dd6-11ec-a79b-07ce2c030081', 'XTCD1646630494268', '生产管理 - 管理日历 - 编辑', 1, 1, NULL, '2e0c8f60-9dd6-11ec-9386-9d079fb5289f', NULL, NULL, 'Global', 1646630494269, '265dd610-40a8-11ec-afb6-7fee9c405457', '1646630494269', NULL, 'PC', NULL, 't', 'action.edit'),
('89e52220-9dd6-11ec-9386-9d079fb5289f', 'XTCD1646630530370', '生产管理 - 管理日历 - 删除', 1, 1, NULL, '2e0c8f60-9dd6-11ec-9386-9d079fb5289f', NULL, NULL, 'Global', 1646630530371, '265dd610-40a8-11ec-afb6-7fee9c405457', '1646630530371', NULL, 'PC', NULL, 't', 'action.delete');

