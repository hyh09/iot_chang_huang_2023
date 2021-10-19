import { SharedModule } from '../../../../shared/shared.module';
import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HomeComponentsModule } from '../../components/home-components.module';
import { DeviceManageRoutingModule } from './device-mng-routing.module';
import { DataDictionaryComponent } from './data-dictionary/data-dictionary.component';
import { DeviceDictionaryComponent } from './device-dictionary/device-dictionary.component';
import { DataDictionaryFiltersComponent } from './data-dictionary/data-dictionary-filters.component';

@NgModule({
  imports: [
    CommonModule,
    SharedModule,
    HomeComponentsModule,
    DeviceManageRoutingModule
  ],
  declarations: [
    DataDictionaryComponent,
    DataDictionaryFiltersComponent,
    DeviceDictionaryComponent
  ]
})
export class DeviceManagementModule { }
