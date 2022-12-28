import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { SharedModule } from '@app/shared/shared.module';
import { HomeComponentsModule } from '../../components/home-components.module';
import { OrderFormRoutingModule } from './order-form-mng-routing.module';
import { OrderFormComponent } from './orders/order-form.component';
import { OrdersFiltersComponent } from './orders/orders-filters.component';
import { OrderDeviceFormComponent } from './orders/order-device-form.component';
import { ImportOrderDialogComponent } from './orders/import-order-dialog.component';
import { OrdersProgressFiltersComponent } from './order-progress/orders-progress-filters.component';
import { ProcessCardProgressFiltersComponent } from './process-card-progress/process-card-progress-filters.component';

@NgModule({
  imports: [
    CommonModule,
    SharedModule,
    HomeComponentsModule,
    OrderFormRoutingModule
  ],
  declarations: [
    OrderFormComponent,
    OrdersFiltersComponent,
    OrderDeviceFormComponent,
    ImportOrderDialogComponent,
    OrdersProgressFiltersComponent,
    ProcessCardProgressFiltersComponent
  ]
})
export class OrderFormModule { }
