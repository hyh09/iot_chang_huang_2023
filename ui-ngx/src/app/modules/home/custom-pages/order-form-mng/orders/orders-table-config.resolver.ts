import { Injectable } from "@angular/core";
import { Resolve } from '@angular/router';
import { DateEntityTableColumn, EntityTableColumn, EntityTableConfig } from "@app/modules/home/models/entity/entities-table-config.models";
import { EntityType, entityTypeResources, entityTypeTranslations } from "@app/shared/public-api";
import { TranslateService } from '@ngx-translate/core';
import { DatePipe } from '@angular/common';
import { deepClone, UtilsService } from '@app/core/public-api';
import { OrderForm } from '@app/shared/models/custom/order-form-mng.models';
import { OrderFormComponent } from './order-form.component';
import { OrdersFiltersComponent } from './orders-filters.component';
import { OrderFormService } from '@app/core/http/custom/order-form.service';
import { map } from "rxjs/operators";

@Injectable()
export class OrderTableConfigResolver implements Resolve<EntityTableConfig<OrderForm>> {

  private readonly config: EntityTableConfig<OrderForm> = new EntityTableConfig<OrderForm>();

  constructor(
    private translate: TranslateService,
    private datePipe: DatePipe,
    private utils: UtilsService,
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
      type: '',
      availableOrderNo: ''
    }

    this.config.deleteEntityTitle = order => this.translate.instant('order.delete-order-title', {orderNo: order.orderNo});
    this.config.deleteEntityContent = () => this.translate.instant('order.delete-order-text');
    this.config.deleteEntitiesTitle = count => this.translate.instant('order.delete-orders-title', {count});
    this.config.deleteEntitiesContent = () => this.translate.instant('order.delete-orders-text');

    this.config.columns.push(
      new EntityTableColumn<OrderForm>('orderNo', 'order.order-no', '50%'),
      new EntityTableColumn<OrderForm>('factoryName', 'order.factory-name', '50%'),
      new EntityTableColumn<OrderForm>('emergencyDegree', 'order.emergency-degree', '100px'),
      new EntityTableColumn<OrderForm>('merchandiser', 'order.merchandiser', '100px'),
      new DateEntityTableColumn<OrderForm>('intendedTime', 'order.intended-complete-date', this.datePipe, '150px', 'yyyy-MM-dd'),
      new EntityTableColumn<OrderForm>('creator', 'common.creator', '100px'),
      new DateEntityTableColumn<OrderForm>('createdTime', 'common.created-time', this.datePipe, '150px')
    );
  }

  resolve(): EntityTableConfig<OrderForm> {
    this.config.componentsData = {
      orderNo: '',
      factoryName: '',
      type: '',
      availableOrderNo: ''
    }

    this.setAvailableOrderNo();

    this.config.tableTitle = this.translate.instant('order.orders');
    this.config.searchEnabled = false;
    this.config.refreshEnabled = false;
    this.config.afterResolved = () => {
      this.config.addEnabled = this.utils.hasPermission('order.add-order');
      this.config.entitiesDeleteEnabled = this.utils.hasPermission('action.delete');
      this.config.detailsReadonly = () => (!this.utils.hasPermission('action.edit'));
    }

    this.config.entitiesFetchFunction = pageLink => this.orderFormService.getOrders(pageLink, this.config.componentsData);
    this.config.loadEntity = id => this.orderFormService.getOrderForm(id);
    this.config.saveEntity = orderForm => {
      const form = deepClone(orderForm);
      if (form.takeTime) form.takeTime = new Date(form.takeTime).getTime();
      if (form.intendedTime) form.intendedTime = new Date(form.intendedTime).getTime();
      return this.orderFormService.saveOrderForm(form);
    };
    this.config.entityAdded = () => {
      this.setAvailableOrderNo();
    }
    this.config.deleteEntity = id => {
      return this.orderFormService.deleteOrder(id).pipe(map(result => {
        this.setAvailableOrderNo();
        return result;
      }));
    }

    return this.config;
  }

  setAvailableOrderNo(): void {
    this.orderFormService.getAvailableOrderNo().subscribe(code => {
      this.config.componentsData.availableOrderNo = code;
    });
  }

}
