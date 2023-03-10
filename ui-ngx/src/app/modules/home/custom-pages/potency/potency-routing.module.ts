import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { EntitiesTableComponent } from '../../components/entity/entities-table.component';
import { EnergyConsumptionTableConfigResolver } from './energy-consumption/factory/energy-consumption-table-config.resolver';
import { EnergyHistoryTableConfigResolver } from './energy-consumption/factory/energy-history-table-config.resolver';
import { OrderConsumptionTableConfigResolver } from './energy-consumption/order/order-consumption-table-config.resolver';
import { ProductionHistoryCapacityTableConfigResolver } from './production-capacity/factory/production-capacity-history-table-config.resolver';
import { ProductionCapacityTableConfigResolver } from './production-capacity/factory/production-capacity-table-config.resolver';
import { GroupProductionTableConfigResolver } from './production-capacity/group/group-production-table-config.resolver';
import { ProcedureProductionTableConfigResolver } from './production-capacity/procedure/procedure-production-table-config.resolver';
import { RunningStateComponent } from './running-state/running-state.component';
import { TechnologyComponent } from './technology/technology.component';

const routes: Routes = [
  {
    path: 'potency',
    children: [
      {
        path: '',
        redirectTo: 'outputAnalysis',
        pathMatch: 'full'
      },
      {
        path: 'outputAnalysis',
        data: {
          breadcrumb: {
            label: 'potency.output-analysis',
            icon: 'mdi:capacity'
          }
        },
        children: [
          {
            path: '',
            redirectTo: 'factory',
            pathMatch: 'full'
          },
          {
            path: 'factory',
            data: {
              breadcrumb: {
                label: 'potency.factory',
                icon: 'mdi:factory'
              }
            },
            children: [
              {
                path: '',
                component: EntitiesTableComponent,
                data: {
                  title: 'potency.factory',
                },
                resolve: {
                  entitiesTableConfig: ProductionCapacityTableConfigResolver
                }
              },
              {
                path: ':deviceId/history',
                component: EntitiesTableComponent,
                data: {
                  title: 'potency.device-capacity-history',
                  breadcrumb: {
                    label: 'potency.device-capacity-history',
                    icon: 'mdi:history-data'
                  }
                },
                resolve: {
                  entitiesTableConfig: ProductionHistoryCapacityTableConfigResolver
                }
              }
            ]
          },
          {
            path: 'group',
            data: {
              breadcrumb: {
                label: 'potency.group',
                icon: 'mdi:group'
              }
            },
            children: [
              {
                path: '',
                component: EntitiesTableComponent,
                data: {
                  title: 'potency.group'
                },
                resolve: {
                  entitiesTableConfig: GroupProductionTableConfigResolver
                }
              }
            ]
          },
          {
            path: 'procedure',
            data: {
              breadcrumb: {
                label: 'potency.procedure',
                icon: 'mdi:procedure'
              }
            },
            children: [
              {
                path: '',
                component: EntitiesTableComponent,
                data: {
                  title: 'potency.procedure'
                },
                resolve: {
                  entitiesTableConfig: ProcedureProductionTableConfigResolver
                }
              }
            ]
          }
        ]
      },
      {
        path: 'energyConsumption',
        data: {
          breadcrumb: {
            label: 'potency.energy-consumption',
            icon: 'mdi:energy'
          }
        },
        children: [
          {
            path: '',
            redirectTo: 'factory',
            pathMatch: 'full'
          },
          {
            path: 'factory',
            data: {
              breadcrumb: {
                label: 'potency.factory',
                icon: 'mdi:factory'
              }
            },
            children: [
              {
                path: '',
                component: EntitiesTableComponent,
                resolve: {
                  entitiesTableConfig: EnergyConsumptionTableConfigResolver
                }
              },
              {
                path: ':deviceId/history',
                component: EntitiesTableComponent,
                data: {
                  title: 'potency.energy-consumption-history',
                  breadcrumb: {
                    label: 'potency.energy-consumption-history',
                    icon: 'mdi:history-data'
                  }
                },
                resolve: {
                  entitiesTableConfig: EnergyHistoryTableConfigResolver
                }
              }
            ]
          },
          {
            path: 'order',
            data: {
              breadcrumb: {
                label: 'potency.order',
                icon: 'mdi:order'
              }
            },
            children: [
              {
                path: '',
                component: EntitiesTableComponent,
                resolve: {
                  entitiesTableConfig: OrderConsumptionTableConfigResolver
                }
              }
            ]
          }
        ]
      },
      {
        path: 'runningState',
        component: RunningStateComponent,
        data: {
          title: 'potency.running-state',
          breadcrumb: {
            label: 'potency.running-state',
            icon: 'mdi:running-state'
          }
        }
      },
      {
        path: 'technology',
        component: TechnologyComponent,
        data: {
          title: 'potency.technology',
          breadcrumb: {
            label: 'potency.technology',
            icon: 'mdi:technology'
          }
        }
      }
    ]
  }
];

@NgModule({    
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
  providers: [
    ProductionCapacityTableConfigResolver,
    EnergyConsumptionTableConfigResolver,
    EnergyHistoryTableConfigResolver,
    ProductionHistoryCapacityTableConfigResolver,
    GroupProductionTableConfigResolver,
    ProcedureProductionTableConfigResolver,
    OrderConsumptionTableConfigResolver
  ]
})
export class PotencyRoutingModule { }