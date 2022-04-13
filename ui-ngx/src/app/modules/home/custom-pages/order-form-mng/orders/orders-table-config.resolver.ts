import { Injectable } from "@angular/core";
import { Resolve } from '@angular/router';
import { CellActionDescriptor, DateEntityTableColumn, EntityTableColumn, EntityTableConfig, HeaderActionDescriptor } from "@app/modules/home/models/entity/entities-table-config.models";
import { BaseData, EntityType, entityTypeResources, entityTypeTranslations, HasId } from "@app/shared/public-api";
import { TranslateService } from '@ngx-translate/core';
import { DatePipe } from '@angular/common';
import { deepClone, DialogService, UtilsService } from '@app/core/public-api';
import { OrderForm } from '@app/shared/models/custom/order-form-mng.models';
import { OrderFormComponent } from './order-form.component';
import { OrdersFiltersComponent } from './orders-filters.component';
import { OrderFormService } from '@app/core/http/custom/order-form.service';
import { map } from "rxjs/operators";
import { MatDialog } from "@angular/material/dialog";
import { AddEntityDialogComponent } from "@app/modules/home/components/entity/add-entity-dialog.component";
import { AddEntityDialogData } from "@app/modules/home/models/entity/entity-component.models";
import { ImportOrderDialogComponent } from "./import-order-dialog.component";

@Injectable()
export class OrderTableConfigResolver implements Resolve<EntityTableConfig<OrderForm>> {

  private readonly config: EntityTableConfig<OrderForm> = new EntityTableConfig<OrderForm>();

  private lastFinishedOrder: OrderForm;

  constructor(
    private translate: TranslateService,
    private datePipe: DatePipe,
    private utils: UtilsService,
    private orderFormService: OrderFormService,
    private dialog: MatDialog,
    private dialogService: DialogService
  ) {
    this.config.entityType = EntityType.ORDER_FORM;
    this.config.entityComponent = OrderFormComponent;
    this.config.filterComponent = OrdersFiltersComponent;
    this.config.entityTranslations = entityTypeTranslations.get(EntityType.ORDER_FORM);
    this.config.entityResources = entityTypeResources.get(EntityType.ORDER_FORM);

    this.config.addDialogStyle = { width: '960px' };

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
      new EntityTableColumn<OrderForm>('factoryName', 'order.factory-name', '50%', (entity) => (entity.factoryName || ''), () => ({}), false),
      new EntityTableColumn<OrderForm>('emergencyDegree', 'order.emergency-degree', '100px'),
      new EntityTableColumn<OrderForm>('merchandiser', 'order.merchandiser', '100px'),
      new DateEntityTableColumn<OrderForm>('intendedTime', 'order.intended-complete-date', this.datePipe, '150px', 'yyyy-MM-dd'),
      new EntityTableColumn<OrderForm>('creator', 'common.creator', '100px', (entity) => (entity.creator || ''), () => ({}), false),
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
    this.config.addActionDescriptors = this.configureAddActions();
    this.config.afterResolved = () => {
      this.config.cellActionDescriptors = this.configureCellActions();
      this.config.addEnabled = this.utils.hasPermission('order.add-order');
      this.config.entitiesDeleteEnabled = this.utils.hasPermission('action.delete');
      this.config.detailsReadonly = entity => {
        return !this.utils.hasPermission('action.edit') || (this.lastFinishedOrder && this.lastFinishedOrder.id === entity?.id ? this.lastFinishedOrder.isDone : entity?.isDone)
      };
    }

    this.config.entitiesFetchFunction = pageLink => this.orderFormService.getOrders(pageLink, this.config.componentsData);
    this.config.loadEntity = id => this.orderFormService.getOrderForm(id);
    this.config.saveEntity = orderForm => {
      const form = deepClone(orderForm);
      if (form.takeTime) form.takeTime = new Date(form.takeTime).getTime();
      if (form.intendedTime) {
        const date = new Date(form.intendedTime);
        date.setDate(date.getDate() + 1);
        form.intendedTime = date.getTime() - 1;
      }
      return this.orderFormService.saveOrderForm(form);
    };
    this.config.deleteEntity = id => {
      return this.orderFormService.deleteOrder(id).pipe(map(result => {
        this.setAvailableOrderNo();
        return result;
      }));
    }

    return this.config;
  }

  configureAddActions(): Array<HeaderActionDescriptor> {
    const actions: Array<HeaderActionDescriptor> = [];
    actions.push(
      {
        name: this.translate.instant('order.create-order'),
        icon: 'insert_drive_file',
        isEnabled: () => true,
        onAction: () => this.createOrder()
      },
      {
        name: this.translate.instant('order.import-order'),
        icon: 'file_upload',
        isEnabled: () => true,
        onAction: () => this.importOrders()
      }
    );
    return actions;
  }

  configureCellActions(): Array<CellActionDescriptor<OrderForm>> {
    const actions: Array<CellActionDescriptor<OrderForm>> = [];
    if (this.utils.hasPermission('order.finish-order')) {
      actions.push({
        name: this.translate.instant('order.finish-order'),
        icon: 'check_circle',
        isEnabled: (entity) => !!(entity && entity.id && !entity.isDone),
        onAction: ($event, entity) => this.finishOrder($event, entity)
      });
    }
    return actions;
  }

  createOrder() {
    this.dialog.open<AddEntityDialogComponent, AddEntityDialogData<BaseData<HasId>>, BaseData<HasId>>(AddEntityDialogComponent, {
      disableClose: true,
      panelClass: ['tb-dialog', 'tb-fullscreen-dialog'],
      data: {
        entitiesTableConfig: this.config
      }
    }).afterClosed().subscribe(res => {
      if (res) {
        this.setAvailableOrderNo();
        this.config.table.updateData();
      }
    });
  }

  importOrders() {
    this.dialog.open<ImportOrderDialogComponent, void, string>(ImportOrderDialogComponent, {
      disableClose: true,
      panelClass: ['tb-dialog', 'tb-fullscreen-dialog']
    }).afterClosed().subscribe(res => {
      if (res) {
        this.setAvailableOrderNo();
        this.config.table.updateData();
      }
    });
  }

  finishOrder($event: Event, order: OrderForm): void {
    if ($event) {
      $event.stopPropagation();
    }
    this.dialogService.confirm(
      this.translate.instant('order.finish-order-title', {orderNo: order.orderNo || ''}),
      this.translate.instant('order.finish-order-text'),
      this.translate.instant('action.no'),
      this.translate.instant('action.yes'),
      true
    ).subscribe((res) => {
        if (res) {
          this.orderFormService.finishOrder(order.id).subscribe(() => {
            order.isDone = true;
            this.lastFinishedOrder = order;
            this.config.table.updateData();
          });
        }
      }
    );
  }

  setAvailableOrderNo(): void {
    this.orderFormService.getAvailableOrderNo().subscribe(code => {
      this.config.componentsData.availableOrderNo = code;
    });
  }

}
