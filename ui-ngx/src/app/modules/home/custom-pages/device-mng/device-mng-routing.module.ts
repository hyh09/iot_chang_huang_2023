import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { EntitiesTableComponent } from '../../components/entity/entities-table.component';
import { UserMngTableConfigResolver } from '../auth-mng/user-mng/user-mng-table-config.resolver';
import { ChartSettingsTableConfigResolver } from './chart-settings/chart-settings-table-config.resolver';
import { ChartsTableConfigResolver } from './chart-settings/charts-table-config.resolver';
import { DataAuthTableConfigResolver } from './data-auth/data-auth-table-config.resolver';
import { DevicePropTableConfigResolver } from './data-auth/device-prop-table-config.resolver';
import { DataDictionaryTableConfigResolver } from './data-dictionary/data-dictionary-table-config.resolver';
import { DeviceDictionaryTableConfigResolver } from './device-dictionary/device-dictionary-table-config.resolver';
import { FactoryMngComponent } from './factory-mng/factory-mng.component';
import { ProdCapacitySettingsTableConfigResolver } from './prod-capacity-settings/prod-capacity-settings-table-config.resolver';
import { MngCalendarTableConfigResolver } from './prod-mng/mng-calendar-table-config.resolver';
import { ProdMngTableConfigResolver } from './prod-mng/prod-mng-table-config.resolver';

const routes: Routes = [
  {
    path: 'deviceManagement',
    children: [
      {
        path: '',
        redirectTo: 'dataDictionary',
        pathMatch: 'full'
      },
      {
        path: 'dataDictionary',
        component: EntitiesTableComponent,
        data: {
          title: 'device-mng.data-dic',
          breadcrumb: {
            label: 'device-mng.data-dic',
            icon: 'mdi:data-dictionary'
          }
        },
        resolve: {
          entitiesTableConfig: DataDictionaryTableConfigResolver
        }
      },
      {
        path: 'deviceDictionary',
        component: EntitiesTableComponent,
        data: {
          title: 'device-mng.device-dic',
          breadcrumb: {
            label: 'device-mng.device-dic',
            icon: 'mdi:device-dictionary'
          }
        },
        resolve: {
          entitiesTableConfig: DeviceDictionaryTableConfigResolver
        }
      },
      {
        path: 'factoryManagement',
        data: {
          breadcrumb: {
            label: 'device-mng.factory-mng',
            icon: 'mdi:factory-mng'
          }
        },
        children: [
          {
            path: '',
            component: FactoryMngComponent,
            data: {
              title: 'device-mng.factory-mng'
            }
          },
          {
            path: ':factoryId/users',
            component: EntitiesTableComponent,
            data: {
              title: 'device-mng.factory-manager',
              breadcrumb: {
                label: 'device-mng.factory-manager',
                icon: 'people'
              }
            },
            resolve: {
              entitiesTableConfig: UserMngTableConfigResolver
            }
          }
        ]
      },
      {
        path: 'productionCapacitySettings',
        component: EntitiesTableComponent,
        data: {
          title: 'device-mng.prod-capactity-settings',
          breadcrumb: {
            label: 'device-mng.prod-capactity-settings',
            icon: 'mdi:switch-config'
          }
        },
        resolve: {
          entitiesTableConfig: ProdCapacitySettingsTableConfigResolver
        }
      },
      {
        path: 'chartSettings',
        data: {
          breadcrumb: {
            label: 'device-mng.chart-settings',
            icon: 'mdi:chart-timeline-variant'
          }
        },
        children: [
          {
            path: '',
            component: EntitiesTableComponent,
            data: {
              title: 'device-mng.chart-settings'
            },
            resolve: {
              entitiesTableConfig: ChartSettingsTableConfigResolver
            }
          },
          {
            path: ':deviceDictId/charts',
            component: EntitiesTableComponent,
            data: {
              title: 'device-mng.bind-chart',
              breadcrumb: {
                label: 'device-mng.bind-chart',
                icon: 'mdi:bind-chart',
                isMdiIcon: true
              }
            },
            resolve: {
              entitiesTableConfig: ChartsTableConfigResolver
            }
          }
        ]
      },
      {
        path: 'dataAuth',
        data: {
          breadcrumb: {
            label: 'device-mng.data-auth',
            icon: 'mdi:data-auth'
          }
        },
        children: [
          {
            path: '',
            component: EntitiesTableComponent,
            data: {
              title: 'device-mng.data-auth'
            },
            resolve: {
              entitiesTableConfig: DataAuthTableConfigResolver
            }
          },
          {
            path: ':deviceId/properties',
            component: EntitiesTableComponent,
            data: {
              title: 'device-mng.mng-prop-auth',
              breadcrumb: {
                label: 'device-mng.mng-prop-auth',
                icon: 'mdi:switch-settings',
                isMdiIcon: true
              }
            },
            resolve: {
              entitiesTableConfig: DevicePropTableConfigResolver
            }
          }
        ]
      },
      {
        path: 'prodManagement',
        data: {
          breadcrumb: {
            label: 'device-mng.prod-mng',
            icon: 'mdi:prod-mng'
          }
        },
        children: [
          {
            path: '',
            component: EntitiesTableComponent,
            data: {
              title: 'device-mng.prod-mng'
            },
            resolve: {
              entitiesTableConfig: ProdMngTableConfigResolver
            }
          },
          {
            path: ':factoryId/:deviceId/calendars',
            component: EntitiesTableComponent,
            data: {
              title: 'device-mng.mng-calendars',
              breadcrumb: {
                label: 'device-mng.mng-calendars',
                icon: 'mdi:calendar'
              }
            },
            resolve: {
              entitiesTableConfig: MngCalendarTableConfigResolver
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
    DataDictionaryTableConfigResolver,
    DeviceDictionaryTableConfigResolver,
    UserMngTableConfigResolver,
    ProdCapacitySettingsTableConfigResolver,
    ChartSettingsTableConfigResolver,
    ChartsTableConfigResolver,
    ProdMngTableConfigResolver,
    MngCalendarTableConfigResolver,
    DataAuthTableConfigResolver,
    DevicePropTableConfigResolver
  ]
})
export class DeviceMngRoutingModule { }