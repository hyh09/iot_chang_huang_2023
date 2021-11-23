import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { EntitiesTableComponent } from '../../components/entity/entities-table.component';
import { EnergyConsumptionTableConfigResolver } from './energy-consumption/energy-consumption-table-config.resolver';
import { EnergyHistoryTableConfigResolver } from './energy-consumption/energy-history-table-config.resolver';
import { ProductionCapacityTableConfigResolver } from './production-capacity/production-capacity-table-config.resolver';
import { RunningStateComponent } from './running-state/running-state.component';

const routes: Routes = [
  {
    path: 'potency',
    data: {
      breadcrumb: {
        label: 'potency.potency',
        icon: 'mdi:potency'
      }
    },
    children: [
      {
        path: '',
        redirectTo: 'deviceCapacity',
        pathMatch: 'full'
      },
      {
        path: 'deviceCapacity',
        component: EntitiesTableComponent,
        data: {
          title: 'potency.device-capacity',
          breadcrumb: {
            label: 'potency.device-capacity',
            icon: 'mdi:capacity'
          }
        },
        resolve: {
          entitiesTableConfig: ProductionCapacityTableConfigResolver
        }
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
            component: EntitiesTableComponent,
            data: {
              title: 'potency.energy-consumption'
            },
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
        path: 'runningState',
        component: RunningStateComponent,
        data: {
          title: 'potency.running-state',
          breadcrumb: {
            label: 'potency.running-state',
            icon: 'mdi:running-state'
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
    EnergyHistoryTableConfigResolver
  ]
})
export class PotencyRoutingModule { }