import { NgModule } from '@angular/core';
import { DeviceManagementModule } from './device-mng/device-mng.module';
import { MenuManagementModule } from './menu-mng/menu-mng.module';
import { AuthMngModule } from './auth-mng/auth-mng.module';
import { DeviceMonitorModule } from './device-monitor/device-monitor.module';

@NgModule({
  exports: [
    DeviceManagementModule,
    MenuManagementModule,
    AuthMngModule,
    DeviceMonitorModule
  ]
})
export class CustomPagesModule { }
