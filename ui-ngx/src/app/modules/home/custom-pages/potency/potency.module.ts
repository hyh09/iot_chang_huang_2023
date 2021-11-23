import { SharedModule } from '../../../../shared/shared.module';
import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HomeComponentsModule } from '../../components/home-components.module';
import { PotencyRoutingModule } from './potency-routing.module';
import { ProductionCapacityOverviewComponent } from './production-capacity/production-capacity-overview.component';
import { EnergyConsumptionOverviewComponent } from './energy-consumption/energy-consumption-overview.component';
import { RunningStateComponent } from './running-state/running-state.component';

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
    RunningStateComponent
  ]
})
export class PotencyModule { }
