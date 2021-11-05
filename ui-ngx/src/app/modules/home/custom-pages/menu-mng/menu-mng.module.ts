import { SharedModule } from '../../../../shared/shared.module';
import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HomeComponentsModule } from '../../components/home-components.module';
import { MenuMngRoutingModule } from './menu-mng-routing.module';
import { MenuMngComponent } from './menu-mng.component';
import { MenuMngFiltersComponent } from './menu-mng-filters.component';

@NgModule({
  imports: [
    CommonModule,
    SharedModule,
    HomeComponentsModule,
    MenuMngRoutingModule
  ],
  declarations: [
    MenuMngFiltersComponent,
    MenuMngComponent
  ]
})
export class MenuManagementModule { }
