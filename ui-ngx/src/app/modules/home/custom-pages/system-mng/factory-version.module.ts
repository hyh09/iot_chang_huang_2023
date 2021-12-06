import { SharedModule } from '../../../../shared/shared.module';
import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HomeComponentsModule } from '../../components/home-components.module';
import { FactoryVersionFiltersComponent } from './factory-version/factory-version-filters.component';
import { FactoryVersionRoutingModule } from './factory-version-routing.module';

@NgModule({
  imports: [
    CommonModule,
    SharedModule,
    HomeComponentsModule,
    FactoryVersionRoutingModule
  ],
  declarations: [
    FactoryVersionFiltersComponent
  ]
})
export class FactoryVersionModule { }
