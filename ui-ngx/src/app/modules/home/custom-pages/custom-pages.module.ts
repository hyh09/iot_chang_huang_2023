import { NgModule } from '@angular/core';
import { DeviceManagementModule } from './device-mng/device-mng.module';
import { MenuManagementModule } from './menu-mng/menu-mng.module';

@NgModule({
  exports: [
    DeviceManagementModule,
    MenuManagementModule
  ]
})
export class CustomPagesModule { }
