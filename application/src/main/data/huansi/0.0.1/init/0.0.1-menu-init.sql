
--初始化系统菜单脚本
INSERT INTO "public"."tb_menu" ("id", "code", "name", "level", "sort", "url", "parent_id", "menu_icon", "menu_images", "region", "created_time", "created_user", "updated_time", "updated_user", "menu_type", "path", "is_button", "lang_key") VALUES
    ('be0754b0-4c32-11ec-a459-159d9ed885bc', 'XTCD1637654136176', '运行状态', '1', '1', NULL, '42462640-4bff-11ec-ac73-c546a4193fa0', 'mdi:running-state', NULL, 'Global', '1637654136624', '5a797660-4612-11e7-a919-92ebcb67fe33', '1637654136624', NULL, 'PC', '/potency/runningState', 'f', 'potency.running-state');
INSERT INTO "public"."tb_menu" ("id", "code", "name", "level", "sort", "url", "parent_id", "menu_icon", "menu_images", "region", "created_time", "created_user", "updated_time", "updated_user", "menu_type", "path", "is_button", "lang_key") VALUES
    ('2c1c9600-4c23-11ec-b550-4f9d96b0fc51', 'XTCD1637647448914', '能耗分析', '1', '1', NULL, '42462640-4bff-11ec-ac73-c546a4193fa0', 'mdi:energy', NULL, 'Global', '1637647452108', '5a797660-4612-11e7-a919-92ebcb67fe33', '1637647452108', NULL, 'PC', '/potency/energyConsumption', 'f', 'potency.energy-consumption');


