import { SharedModule } from '../../../../shared/shared.module';
import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HomeComponentsModule } from '../../components/home-components.module';
import { DeviceMngRoutingModule } from './device-mng-routing.module';
import { DataDictionaryComponent } from './data-dictionary/data-dictionary.component';
import { DeviceDictionaryComponent } from './device-dictionary/device-dictionary.component';
import { DataDictionaryFiltersComponent } from './data-dictionary/data-dictionary-filters.component';
import { DeviceDictionaryFiltersComponent } from './device-dictionary/device-dictionary-filters.component';
import { DeviceCompFormComponent } from './device-dictionary/device-comp-form.component';
import { FactoryMngComponent } from './factory-mng/factory-mng.component';

@NgModule({
  imports: [
    CommonModule,
    SharedModule,
    HomeComponentsModule,
    DeviceMngRoutingModule
  ],
  declarations: [
    DataDictionaryComponent,
    DataDictionaryFiltersComponent,
    DeviceDictionaryComponent,
    DeviceDictionaryFiltersComponent,
    DeviceCompFormComponent,
    FactoryMngComponent
  ]
})
export class DeviceManagementModule { }
