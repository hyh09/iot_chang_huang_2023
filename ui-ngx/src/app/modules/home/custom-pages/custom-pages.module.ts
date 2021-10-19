import { NgModule } from '@angular/core';
import { DeviceManagementModule } from './device-mng/device-mng.module';

@NgModule({
  exports: [
    DeviceManagementModule
  ]
})
export class CustomPagesModule { }
