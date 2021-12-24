import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { EntitiesTableComponent } from '../../components/entity/entities-table.component';
import { FactoryVersionTableConfigResolver } from './factory-version/factory-version-table-config.resolver';
import { SystemVersionComponent } from './system-version/system-version.component';

const routes: Routes = [
  {
    path: 'systemManagement',
    children: [
      {
        path: '',
        redirectTo: 'factoryVersion',
        pathMatch: 'full'
      },
      {
        path: 'factoryVersion',
        component: EntitiesTableComponent,
        data: {
          title: 'system-mng.factory-version',
          breadcrumb: {
            label: 'system-mng.factory-version',
            icon: 'mdi:software'
          }
        },
        resolve: {
          entitiesTableConfig: FactoryVersionTableConfigResolver
        }
      },
      {
        path: 'systemVersion',
        component: SystemVersionComponent,
        data: {
          title: 'system-mng.system-version',
          breadcrumb: {
            label: 'system-mng.system-version',
            icon: 'mdi:version'
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
    FactoryVersionTableConfigResolver
  ]
})
export class FactoryVersionRoutingModule { }