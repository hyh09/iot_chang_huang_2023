import { Injectable, NgModule } from '@angular/core';
import { CanActivate, Router, RouterModule, Routes } from '@angular/router';
import { UtilsService } from '@app/core/public-api';
import { EntitiesTableComponent } from '../../components/entity/entities-table.component';
import { BindUserTableConfigResolver } from './role-mng/bind-user-table-config.resolver';
import { RoleMngTableConfigResolver } from './role-mng/role-mng-table-config.resolver';
import { UserMngTableConfigResolver } from './user-mng/user-mng-table-config.resolver';

@Injectable()
export class BindUsersGuard implements CanActivate {
  constructor(
    private utils: UtilsService,
    private router: Router
  ) {}
  canActivate() {
    if (this.utils.hasPermission('auth-mng.bind-users', '/authManagement/roleManagemnet')) {
      return true
    }
    this.router.navigateByUrl('/home');
    return false;
  }
}

const routes: Routes = [
  {
    path: 'authManagement',
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
            canActivate: [BindUsersGuard],
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
    BindUserTableConfigResolver,
    BindUsersGuard
  ]
})
export class AuthMngRoutingModule { }