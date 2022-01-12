import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { EntitiesTableComponent } from '../../components/entity/entities-table.component';
import { MenuMngTableConfigResolver } from './menu-mng-table-config.resolver';

const routes: Routes = [
  {
    path: 'menuManagement',
    component: EntitiesTableComponent,
    data: {
      title: 'menu-mng.menu-mng',
      breadcrumb: {
        label: 'menu-mng.menu-mng',
        icon: 'mdi:menu2'
      }
    },
    resolve: {
      entitiesTableConfig: MenuMngTableConfigResolver
    }
  }
];

@NgModule({    
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
  providers: [
    MenuMngTableConfigResolver
  ]
})
export class MenuMngRoutingModule { }