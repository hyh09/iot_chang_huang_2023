import { SharedModule } from '../../../../shared/shared.module';
import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HomeComponentsModule } from '../../components/home-components.module';
import { PotencyRoutingModule } from './potency-routing.module';
import { ProductionCapacityOverviewComponent } from './production-capacity/factory/production-capacity-overview.component';
import { EnergyConsumptionOverviewComponent } from './energy-consumption/factory/energy-consumption-overview.component';
import { RunningStateComponent } from './running-state/running-state.component';
import { RunningStateChartComponent } from './running-state/running-state-chart.component';
import { EnergyHistoryFilterComponent } from './energy-consumption/factory/energy-history-filter.component';
import { GroupProductionFilterComponent } from './production-capacity/group/group-production-filter.component';
import { ProcedureProductionFilterComponent } from './production-capacity/procedure/procedure-production-filter.component';
import { OrderConsumptionFilterComponent } from './energy-consumption/order/order-consumption-filter.component';
import { ProcessCardsComponent } from './energy-consumption/order/process-cards.component';

@NgModule({
  imports: [
    CommonModule,
    SharedModule,
    HomeComponentsModule,
    PotencyRoutingModule
  ],
  declarations: [
    ProductionCapacityOverviewComponent,
    EnergyConsumptionOverviewComponent,
    RunningStateComponent,
    RunningStateChartComponent,
    EnergyHistoryFilterComponent,
    GroupProductionFilterComponent,
    ProcedureProductionFilterComponent,
    OrderConsumptionFilterComponent,
    ProcessCardsComponent
  ]
})
export class PotencyModule { }
