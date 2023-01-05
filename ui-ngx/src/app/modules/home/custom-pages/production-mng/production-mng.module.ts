import { SharedModule } from '../../../../shared/shared.module';
import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HomeComponentsModule } from '../../components/home-components.module';
import { ProductionMngRoutingModule } from './production-mng-routing.module';
import { ProdSchedualFilterComponent } from './schedual/prod-schedual-filter.component';

@NgModule({
  imports: [
    CommonModule,
    SharedModule,
    HomeComponentsModule,
    ProductionMngRoutingModule
  ],
  declarations: [
    ProdSchedualFilterComponent
  ]
})
export class ProductionMngModule { }
