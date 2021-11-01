import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { SharedModule } from '@app/shared/shared.module';
import { HomeComponentsModule } from '../../components/home-components.module';
import { AuthMngRoutingModule } from './auth-mng-routing.module';
import { RoleMngFiltersComponent } from './role-mng/role-mng-filters.component';
import { RoleMngComponent } from './role-mng/role-mng.component';
import { UserMngFiltersComponent } from './user-mng/user-mng-filters.component';
import { UserMngComponent } from './user-mng/user-mng.component';
import { UserMngChangePwdComponent } from './user-mng/user-mng-change-pwd.component';

@NgModule({
  imports: [
    CommonModule,
    SharedModule,
    HomeComponentsModule,
    AuthMngRoutingModule
  ],
  declarations: [
    UserMngComponent,
    UserMngFiltersComponent,
    RoleMngComponent,
    RoleMngFiltersComponent,
    UserMngChangePwdComponent
  ]
})
export class AuthMngModule { }
