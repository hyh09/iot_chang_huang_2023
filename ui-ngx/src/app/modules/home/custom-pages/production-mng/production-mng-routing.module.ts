import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { EntitiesTableComponent } from '../../components/entity/entities-table.component';
import { ProdSchedualTableConfigResolver } from './schedual/prod-schedual-table-config.resolver';

const routes: Routes = [
  {
    path: 'productionManagement',
    children: [
      {
        path: '',
        redirectTo: 'schedual',
        pathMatch: 'full'
      },
      {
        path: 'schedual',
        component: EntitiesTableComponent,
        data: {
          title: 'production-mng.prod-schedual',
          breadcrumb: {
            label: 'production-mng.prod-schedual',
            icon: 'mdi:calendar'
          }
        },
        resolve: {
          entitiesTableConfig: ProdSchedualTableConfigResolver
        }
      },
      {
        path: 'report',
        component: EntitiesTableComponent,
        data: {
          title: 'production-mng.prod-report',
          breadcrumb: {
            label: 'production-mng.prod-report',
            icon: 'mdi:report'
          }
        },
        // resolve: {
        //   entitiesTableConfig: ProdReportTableConfigResolver
        // }
      },
      {
        path: 'monitor',
        component: EntitiesTableComponent,
        data: {
          title: 'production-mng.prod-monitor',
          breadcrumb: {
            label: 'production-mng.prod-monitor',
            icon: 'mdi:monitor'
          }
        },
        // resolve: {
        //   entitiesTableConfig: ProdMonitorTableConfigResolver
        // }
      }
    ]
  }
];

@NgModule({    
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
  providers: [
    ProdSchedualTableConfigResolver,
    // ProdReportTableConfigResolver,
    // ProdMonitorTableConfigResolver
  ]
})
export class ProductionMngRoutingModule { }