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
import { FactoryFormComponent } from './factory-mng/factory-form.component';
import { WorkShopFormComponent } from './factory-mng/work-shop-form.component';
import { ProdLineFormComponent } from './factory-mng/prod-line-form.component';
import { DeviceFormComponent } from './factory-mng/device-form.component';
import { DistributeDeviceComponent } from './factory-mng/distribute-device.component';
import { DeviceDataGroupNameComponent } from './device-dictionary/device-data-group-name.component';
import { ProdCapacitySettingsFiltersComponent } from './prod-capacity-settings/prod-capacity-settings-filters.component';
import { DistributeConfigComponent } from './device-dictionary/distribute-config.component';
import { DictDeviceTableFilterComponent } from './device-dictionary/dict-device-table-filter.component';
import { DeviceConfigFormComponent } from './device-dictionary/driver-config-form.component';
import { ChartSettingsFiltersComponent } from './chart-settings/chart-settings-filters.component';
import { ChartsComponent } from './chart-settings/charts.component';
import { ProdMngFiltersComponent } from './prod-mng/prod-mng-filters.component';
import { MngCalendarComponent } from './prod-mng/mng-calendar.component';
import { DataAuthFiltersComponent } from './data-auth/data-auth-filters.component';
import { DevicePropFiltersComponent } from './data-auth/device-prop-filters.component';

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
    FactoryMngComponent,
    FactoryFormComponent,
    WorkShopFormComponent,
    ProdLineFormComponent,
    DeviceFormComponent,
    DistributeDeviceComponent,
    DeviceDataGroupNameComponent,
    ProdCapacitySettingsFiltersComponent,
    DistributeConfigComponent,
    DictDeviceTableFilterComponent,
    DeviceConfigFormComponent,
    ChartSettingsFiltersComponent,
    ChartsComponent,
    ProdMngFiltersComponent,
    MngCalendarComponent,
    DataAuthFiltersComponent,
    DevicePropFiltersComponent
  ]
})
export class DeviceManagementModule { }
