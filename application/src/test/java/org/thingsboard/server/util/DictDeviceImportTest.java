package org.thingsboard.server.util;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootContextLoader;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.thingsboard.server.ThingsboardServerApplication;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.controller.AbstractControllerTest;
import org.thingsboard.server.dao.hs.dao.DictDeviceRepository;
import org.thingsboard.server.dao.hs.service.DictDeviceService;

import java.util.UUID;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = ThingsboardServerApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class DictDeviceImportTest {

    @Autowired
    private DictDeviceService dictDeviceService;

    @Autowired
    private DictDeviceRepository dictDeviceRepository;


    @Test
    public void importDictDevices () {
        var tenantId = new  TenantId (UUID.fromString("4808d600-57f7-11ec-83ca-a1d65e730ef4"));
        var dictDevices = this.dictDeviceService.listDictDevices(tenantId);
        System.out.println(dictDevices);
    }
}
