import { SharedModule } from '../../../../shared/shared.module';
import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HomeComponentsModule } from '../../components/home-components.module';
import { ProductionMngRoutingModule } from './production-mng-routing.module';
import { ProdSchedualFilterComponent } from './schedual/prod-schedual-filter.component';
import { ProductionReportFilterComponent } from './report/production-report-filter.component';
import { ProdMonitorFilterComponent } from './monitor/production-monitor-filter.component';

@NgModule({
  imports: [
    CommonModule,
    SharedModule,
    HomeComponentsModule,
    ProductionMngRoutingModule
  ],
  declarations: [
    ProdSchedualFilterComponent,
    ProductionReportFilterComponent,
    ProdMonitorFilterComponent
  ]
})
export class ProductionMngModule { }
