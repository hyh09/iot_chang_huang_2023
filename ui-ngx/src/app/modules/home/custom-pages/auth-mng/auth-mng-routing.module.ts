import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { EntitiesTableComponent } from '../../components/entity/entities-table.component';
import { UserMngTableConfigResolver } from './user-mng/user-mng-table-config.resolver';

const routes: Routes = [
  {
    path: 'authManagement',
    data: {
      breadcrumb: {
        label: 'auth-mng.auth-mng',
        icon: 'verified_user'
      }
    },
    children: [
      {
        path: '',
        redirectTo: 'userManagement',
        pathMatch: 'full'
      },
      {
        path: 'userManagement',
        component: EntitiesTableComponent,
        data: {
          title: 'auth-mng.user-mng',
          breadcrumb: {
            label: 'auth-mng.user-mng',
            icon: 'people'
          }
        },
        resolve: {
          entitiesTableConfig: UserMngTableConfigResolver
        }
      },
      {
        path: 'roleManagemnet',
        component: EntitiesTableComponent,
        data: {
          title: 'auth-mng.role-mng',
          breadcrumb: {
            label: 'auth-mng.role-mng',
            icon: 'person'
          }
        },
        // resolve: {
        //   entitiesTableConfig: 
        // }
      }
    ]
  }
];

@NgModule({    
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
  providers: [
    UserMngTableConfigResolver
  ]
})
export class AuthMngRoutingModule { }