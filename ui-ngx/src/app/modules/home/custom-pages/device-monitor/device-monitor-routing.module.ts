import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { EntitiesTableComponent } from '../../components/entity/entities-table.component';
import { AlarmRecordTableConfigResolver } from './alarm-record/alarm-record-table-config.resolver';
import { AlarmRulesTableConfigResolver } from './alarm-rules/alarm-rules-table-config.resolver';
import { DeviceDetailsComponent } from './real-time-monitor/device-details/device-details.component';
import { RealTimeMonitorComponent } from './real-time-monitor/real-time-monitor.component';

const routes: Routes = [
  {
    path: 'deviceMonitor',
    data: {
      breadcrumb: {
        label: 'device-monitor.device-monitor',
        icon: 'mdi:device-monitor'
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
        data: {
          breadcrumb: {
            label: 'device-monitor.real-time-monitor',
            icon: 'mdi:real-time-monitor'
          }
        },
        children: [
          {
            path: '',
            component: RealTimeMonitorComponent,
            data: {
              title: 'device-monitor.real-time-monitor'
            }
          },
          {
            path: ':deviceId/details',
            component: DeviceDetailsComponent,
            data: {
              title: 'device-monitor.device-details',
              breadcrumb: {
                label: 'device-monitor.device-details',
                icon: 'mdi:device-details'
              }
            }
          }
        ]
      },
      {
        path: 'alarmRecord',
        component: EntitiesTableComponent,
        data: {
          title: 'device-monitor.alarm-record',
          breadcrumb: {
            label: 'device-monitor.alarm-record',
            icon: 'mdi:alarm-records'
          }
        },
        resolve: {
          entitiesTableConfig: AlarmRecordTableConfigResolver
        }
      },
      {
        path: 'alarmRules',
        data: {
          breadcrumb: {
            label: 'device-monitor.alarm-rules',
            icon: 'mdi:alarm-rules'
          }
        },
        children: [
          {
            path: '',
            component: EntitiesTableComponent,
            data: {
              title: 'device-profile.device-profiles'
            },
            resolve: {
              entitiesTableConfig: AlarmRulesTableConfigResolver
            }
          }
        ]
      }
    ]
  }
];

@NgModule({    
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
  providers: [
    AlarmRecordTableConfigResolver,
    AlarmRulesTableConfigResolver
  ]
})
export class DeviceMonitorRoutingModule { }