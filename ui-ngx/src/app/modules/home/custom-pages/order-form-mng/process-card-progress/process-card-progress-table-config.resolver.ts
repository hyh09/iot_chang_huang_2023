import { Injectable } from "@angular/core";
import { Resolve } from '@angular/router';
import { EntityTableColumn, EntityTableConfig } from "@app/modules/home/models/entity/entities-table-config.models";
import { EntityType, entityTypeResources, entityTypeTranslations } from "@app/shared/public-api";
import { TranslateService } from '@ngx-translate/core';
import { OrderForm } from '@app/shared/models/custom/order-form-mng.models';
import { ProcessCardProgressFiltersComponent } from './process-card-progress-filters.component';
import { OrderFormService } from '@app/core/http/custom/order-form.service';

@Injectable()
export class ProcessCardProgressTableConfigResolver implements Resolve<EntityTableConfig<OrderForm>> {

  private readonly config: EntityTableConfig<OrderForm> = new EntityTableConfig<OrderForm>();

  constructor(
    private translate: TranslateService,
    private orderFormService: OrderFormService,
  ) {
    this.config.entityType = EntityType.ORDER_FORM;
    this.config.filterComponent = ProcessCardProgressFiltersComponent;
    this.config.entityTranslations = entityTypeTranslations.get(EntityType.ORDER_FORM);
    this.config.entityResources = entityTypeResources.get(EntityType.ORDER_FORM);

    this.config.componentsData = {
      time:'',
      orderNo: '',
      colour: '',
      customerName: '',
      processCardNo: '',
      productName:''
    }

    this.config.columns.push(
      new EntityTableColumn<OrderForm>('orderNo11', 'order.card-no', '100px'),
      new EntityTableColumn<OrderForm>('orderNo', 'order.order-no', '50%'),
      new EntityTableColumn<OrderForm>('orderNo2', 'order.delivery-date', '150px'),
      new EntityTableColumn<OrderForm>('orderNo1', 'order.customer', '150px'),
      new EntityTableColumn<OrderForm>('orderNo3', 'order.product-name', '150px'),
      new EntityTableColumn<OrderForm>('orderNo4', 'order.colour', '150px'),
      new EntityTableColumn<OrderForm>('orderNo6', 'order.arrangement-requirements', '150px'),
      new EntityTableColumn<OrderForm>('orderNo5', 'order.number-of-cards', '150px'),
      new EntityTableColumn<OrderForm>('orderNo7', 'order.current-operation', '150px'),
      new EntityTableColumn<OrderForm>('orderNo8', 'order.operation-finished-qty', '150px'),
      new EntityTableColumn<OrderForm>('orderNo9', 'order.next-procedure', '150px'),
    );
  }
  resolve(): EntityTableConfig<OrderForm> {
    this.config.componentsData = {
      time:'',
      orderNo: '',
      colour: '',
      customerName: '',
      processCardNo: '',
      productName:''
    }

    this.config.tableTitle = this.translate.instant('order.process-card-progress');
    this.config.searchEnabled = false;
    this.config.refreshEnabled = false;
    this.config.afterResolved = () => {
      this.config.addEnabled = false;
      this.config.entitiesDeleteEnabled = false;
    }

    this.config.entitiesFetchFunction = pageLink => this.orderFormService.getprocessCardProgress(pageLink, this.config.componentsData);
    return this.config;
  }
}
