import { Injectable } from "@angular/core";
import { Resolve } from '@angular/router';
import { DateEntityTableColumn, EntityTableColumn, EntityTableConfig } from "@app/modules/home/models/entity/entities-table-config.models";
import { EntityType, entityTypeResources, entityTypeTranslations } from "@app/shared/public-api";
import { TranslateService } from '@ngx-translate/core';
import { DatePipe } from '@angular/common';
import { OrderCapacity } from '@app/shared/models/custom/order-form-mng.models';
import { OrderFormComponent } from '../orders/order-form.component';
import { OrdersFiltersComponent } from '../orders/orders-filters.component';
import { OrderFormService } from '@app/core/http/custom/order-form.service';

@Injectable()
export class OrderCapacityTableConfigResolver implements Resolve<EntityTableConfig<OrderCapacity>> {

  private readonly config: EntityTableConfig<OrderCapacity> = new EntityTableConfig<OrderCapacity>();

  constructor(
    private translate: TranslateService,
    private datePipe: DatePipe,
    private orderFormService: OrderFormService
  ) {
    this.config.entityType = EntityType.ORDER_FORM;
    this.config.entityComponent = OrderFormComponent;
    this.config.filterComponent = OrdersFiltersComponent;
    this.config.entityTranslations = entityTypeTranslations.get(EntityType.ORDER_FORM);
    this.config.entityResources = entityTypeResources.get(EntityType.ORDER_FORM);

    this.config.componentsData = {
      orderNo: '',
      factoryName: '',
      type: ''
    }

    this.config.columns.push(
      new EntityTableColumn<OrderCapacity>('orderNo', 'order.order-no', '50%'),
      new EntityTableColumn<OrderCapacity>('factoryName', 'order.factory-name', '50%', (entity) => (entity.factoryName), () => ({}), false),
      new EntityTableColumn<OrderCapacity>('emergencyDegree', 'order.emergency-degree', '100px'),
      new EntityTableColumn<OrderCapacity>('totalAmount', 'order.total-amount', '80px'),
      new DateEntityTableColumn<OrderCapacity>('intendedTime', 'order.intended-complete-date', this.datePipe, '120px', 'yyyy-MM-dd'),
      new EntityTableColumn<OrderCapacity>('total', 'order.total-count', '80px'),
      new EntityTableColumn<OrderCapacity>('capacities', 'order.capacities', '80px', (entity) => (entity.capacities + ''), () => ({}), false),
      new EntityTableColumn<OrderCapacity>('completeness', 'order.completeness', '80px', (entity) => (entity.completeness + ''), () => ({}), false),
      new EntityTableColumn<OrderCapacity>('creator', 'common.creator', '80px', (entity) => (entity.creator), () => ({}), false),
      new DateEntityTableColumn<OrderCapacity>('createdTime', 'common.created-time', this.datePipe, '120px')
    );
  }

  resolve(): EntityTableConfig<OrderCapacity> {
    this.config.componentsData = {
      orderNo: '',
      factoryName: '',
      type: ''
    }

    this.config.tableTitle = this.translate.instant('order.order-capacity');
    this.config.searchEnabled = false;
    this.config.refreshEnabled = false;
    this.config.addEnabled = false;
    this.config.entitiesDeleteEnabled = false;
    this.config.detailsReadonly = () => (true);

    this.config.entitiesFetchFunction = pageLink => this.orderFormService.getOrderCapacities(pageLink, this.config.componentsData);
    this.config.loadEntity = id => this.orderFormService.getOrderCapacity(id);

    return this.config;
  }

}
