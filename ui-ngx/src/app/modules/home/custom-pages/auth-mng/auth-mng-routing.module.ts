import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { EntitiesTableComponent } from '../../components/entity/entities-table.component';
import { BindUserTableConfigResolver } from './role-mng/bind-user-table-config.resolver';
import { RoleMngTableConfigResolver } from './role-mng/role-mng-table-config.resolver';
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
        data: {
          breadcrumb: {
            label: 'auth-mng.role-mng',
            icon: 'mdi:shield-account',
            isMdiIcon: true
          }
        },
        children: [
          {
            path: '',
            component: EntitiesTableComponent,
            data: {
              title: 'auth-mng.role-mng'
            },
            resolve: {
              entitiesTableConfig: RoleMngTableConfigResolver
            }
          },
          {
            path: ':roleId/users',
            component: EntitiesTableComponent,
            data: {
              title: 'auth-mng.bind-users',
              breadcrumb: {
                label: 'auth-mng.bind-users',
                icon: 'account_circle'
              }
            },
            resolve: {
              entitiesTableConfig: BindUserTableConfigResolver
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
    UserMngTableConfigResolver,
    RoleMngTableConfigResolver,
    BindUserTableConfigResolver
  ]
})
export class AuthMngRoutingModule { }