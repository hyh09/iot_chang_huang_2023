import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { EntitiesTableComponent } from '../../components/entity/entities-table.component';
import { OrderCapacityTableConfigResolver } from './capacity/order-capacity-table-config.resolver';
import { OrderTableConfigResolver } from './orders/orders-table-config.resolver';
import { OrdersProgressTableConfigResolver } from './order-progress/order-progress-table-config.resolver';
import { ProcessCardProgressTableConfigResolver } from './process-card-progress/process-card-progress-table-config.resolver';

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
      },
      // 订单进度
      {
        path: 'ordersProgress',
        component: EntitiesTableComponent,
        data: {
          title: 'order.order-progress',
          breadcrumb: {
            label: 'order.order-progress',
            icon: 'mdi:order-progress'
          }
        },
        resolve: {
          entitiesTableConfig: OrdersProgressTableConfigResolver
        }
      },
      // 行程卡进度
      {
        path: 'processCardProgress',
        component: EntitiesTableComponent,
        data: {
          title: 'order.process-card-progress',
          breadcrumb: {
            label: 'order.process-card-progress',
            icon: 'mdi:process-progress'
          }
        },
        resolve: {
          entitiesTableConfig: ProcessCardProgressTableConfigResolver
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
    OrderCapacityTableConfigResolver,
    OrdersProgressTableConfigResolver,
    ProcessCardProgressTableConfigResolver
  ]
})
export class OrderFormRoutingModule { }