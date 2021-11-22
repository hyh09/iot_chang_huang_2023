import { SharedModule } from '../../../../shared/shared.module';
import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HomeComponentsModule } from '../../components/home-components.module';
import { DeviceMonitorRoutingModule } from './device-monitor-routing.module';
import { AlarmRecordFiltersComponent } from './alarm-record/alarm-record-filters.component';
import { RealTimeMonitorComponent } from './real-time-monitor/real-time-monitor.component';
import { AlarmRulesTabsComponent } from './alarm-rules/alarm-rules-tabs.component';
import { AlarmRulesComponent } from './alarm-rules/alarm-rules.component';
import { AddAlarmRuleDialogComponent } from './alarm-rules/add-alarm-rule-dialog.component';
import { RunStateChartComponent } from './real-time-monitor/run-state-chart.component';
import { WarningStatisticsChartComponent } from './real-time-monitor/warning-statistics-chart.component';
import { DeviceDetailsComponent } from './real-time-monitor/device-details/device-details.component';
import { PropDataChartComponent } from './real-time-monitor/device-details/prop-data-chart.component';

@NgModule({
  imports: [
    CommonModule,
    SharedModule,
    HomeComponentsModule,
    DeviceMonitorRoutingModule
  ],
  declarations: [
    RealTimeMonitorComponent,
    AlarmRecordFiltersComponent,
    AlarmRulesComponent,
    AlarmRulesTabsComponent,
    AddAlarmRuleDialogComponent,
    RunStateChartComponent,
    WarningStatisticsChartComponent,
    DeviceDetailsComponent,
    PropDataChartComponent
  ]
})
export class DeviceMonitorModule { }
