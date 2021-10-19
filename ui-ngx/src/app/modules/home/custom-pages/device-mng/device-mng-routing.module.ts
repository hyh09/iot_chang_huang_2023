import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { EntitiesTableComponent } from '../../components/entity/entities-table.component';
import { DataDictionaryTableConfigResolver } from './data-dictionary/data-dictionary-table-config.resolver';
import { DeviceDictionaryTableConfigResolver } from './device-dictionary/device-dictionary-table-config.resolver';

const routes: Routes = [
  {
    path: 'deviceManagement',
    data: {
      breadcrumb: {
        label: 'device-mng.device-mng',
        icon: 'devices'
      }
    },
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
            icon: 'book'
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
            icon: 'book'
          }
        },
        resolve: {
          entitiesTableConfig: DeviceDictionaryTableConfigResolver
        }
      }
    ]
  }
];

@NgModule({    
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
  providers: [
    DataDictionaryTableConfigResolver,
    DeviceDictionaryTableConfigResolver
  ]
})
export class DeviceManageRoutingModule { }