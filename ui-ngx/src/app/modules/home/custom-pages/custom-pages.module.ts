import { NgModule } from '@angular/core';
import { DeviceManagementModule } from './device-mng/device-mng.module';
import { MenuManagementModule } from './menu-mng/menu-mng.module';
import { AuthMngModule } from './auth-mng/auth-mng.module';
import { DeviceMonitorModule } from './device-monitor/device-monitor.module';
import { PotencyModule } from './potency/potency.module';
import { FactoryVersionModule } from './system-mng/factory-version.module';

@NgModule({
  exports: [
    DeviceManagementModule,
    MenuManagementModule,
    AuthMngModule,
    DeviceMonitorModule,
    PotencyModule,
    FactoryVersionModule
  ]
})
export class CustomPagesModule { }
