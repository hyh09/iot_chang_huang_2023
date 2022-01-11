import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { EntitiesTableComponent } from '../../components/entity/entities-table.component';
import { OrderCapacityTableConfigResolver } from './capacity/order-capacity-table-config.resolver';
import { OrderTableConfigResolver } from './orders/orders-table-config.resolver';

const routes: Routes = [
  {
    path: 'orderFormManagement',
    children: [
      {
        path: '',
        redirectTo: 'orders',
        pathMatch: 'full'
      },
      {
        path: 'orders',
        component: EntitiesTableComponent,
        data: {
          title: 'order.orders',
          breadcrumb: {
            label: 'order.orders',
            icon: 'mdi:order'
          }
        },
        resolve: {
          entitiesTableConfig: OrderTableConfigResolver
        }
      },
      {
        path: 'orderCapacity',
        component: EntitiesTableComponent,
        data: {
          title: 'order.order-capacity',
          breadcrumb: {
            label: 'order.order-capacity',
            icon: 'mdi:order-capacity'
          }
        },
        resolve: {
          entitiesTableConfig: OrderCapacityTableConfigResolver
        }
      }
    ]
  }
];

@NgModule({    
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
  providers: [
    OrderTableConfigResolver,
    OrderCapacityTableConfigResolver
  ]
})
export class OrderFormRoutingModule { }