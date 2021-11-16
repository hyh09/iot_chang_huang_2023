import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { EntitiesTableComponent } from '../../components/entity/entities-table.component';
import { AlarmRecordTableConfigResolver } from './alarm-record/alarm-record-table-config.resolver';
import { RealTimeMonitorComponent } from './real-time-monitor/real-time-monitor.component';

const routes: Routes = [
  {
    path: 'deviceMonitor',
    data: {
      breadcrumb: {
        label: 'device-monitor.device-monitor',
        icon: 'touch_app'
      }
    },
    children: [
      {
        path: '',
        redirectTo: 'realTimeMonitor',
        pathMatch: 'full'
      },
      {
        path: 'realTimeMonitor',
        component: RealTimeMonitorComponent,
        data: {
          title: 'device-monitor.real-time-monitor',
          breadcrumb: {
            label: 'device-monitor.real-time-monitor',
            icon: 'av_timer'
          }
        }
      },
      {
        path: 'alarmRecord',
        component: EntitiesTableComponent,
        data: {
          title: 'device-monitor.alarm-record',
          breadcrumb: {
            label: 'device-monitor.alarm-record',
            icon: 'disc_full'
          }
        },
        resolve: {
          entitiesTableConfig: AlarmRecordTableConfigResolver
        }
      }
    ]
  }
];

@NgModule({    
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
  providers: [
    AlarmRecordTableConfigResolver
  ]
})
export class DeviceMonitorRoutingModule { }