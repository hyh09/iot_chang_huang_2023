import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { SharedModule } from '@app/shared/shared.module';
import { HomeComponentsModule } from '../../components/home-components.module';
import { OrderFormRoutingModule } from './order-form-mng-routing.module';
import { OrderFormComponent } from './orders/order-form.component';
import { OrdersFiltersComponent } from './orders/orders-filters.component';
import { OrderDeviceFormComponent } from './orders/order-device-form.component';

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
    OrderDeviceFormComponent
  ]
})
export class OrderFormModule { }
