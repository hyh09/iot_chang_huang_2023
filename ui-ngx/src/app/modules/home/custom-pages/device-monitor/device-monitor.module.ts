import { SharedModule } from '../../../../shared/shared.module';
import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HomeComponentsModule } from '../../components/home-components.module';
import { DeviceMonitorRoutingModule } from './device-monitor-routing.module';
import { AlarmRecordFiltersComponent } from './alarm-record/alarm-record-filters.component';
import { RealTimeMonitorComponent } from './real-time-monitor/real-time-monitor.component';

@NgModule({
  imports: [
    CommonModule,
    SharedModule,
    HomeComponentsModule,
    DeviceMonitorRoutingModule
  ],
  declarations: [
    RealTimeMonitorComponent,
    AlarmRecordFiltersComponent
  ]
})
export class DeviceMonitorModule { }
