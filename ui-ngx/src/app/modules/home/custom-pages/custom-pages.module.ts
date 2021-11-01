import { NgModule } from '@angular/core';
import { DeviceManagementModule } from './device-mng/device-mng.module';
import { MenuManagementModule } from './menu-mng/menu-mng.module';
import { AuthMngModule } from './auth-mng/auth-mng.module';

@NgModule({
  exports: [
    DeviceManagementModule,
    MenuManagementModule,
    AuthMngModule
  ]
})
export class CustomPagesModule { }
