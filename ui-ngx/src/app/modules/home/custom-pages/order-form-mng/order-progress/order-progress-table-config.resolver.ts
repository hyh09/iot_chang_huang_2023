import { Injectable } from "@angular/core";
import { Resolve } from '@angular/router';
import { DateEntityTableColumn, EntityTableColumn, EntityTableConfig } from "@app/modules/home/models/entity/entities-table-config.models";
import { EntityType, entityTypeResources, entityTypeTranslations, HasId, PageLink } from "@app/shared/public-api";
import { TranslateService } from '@ngx-translate/core';
import { OrderProgress } from '@app/shared/models/custom/order-form-mng.models';
import { OrdersProgressFiltersComponent } from './orders-progress-filters.component';
import { OrderFormService } from '@app/core/http/custom/order-form.service';
import { DatePipe } from "@angular/common";
import { FileService } from "@app/core/http/custom/file.service";

@Injectable()
export class OrdersProgressTableConfigResolver implements Resolve<EntityTableConfig<OrderProgress>> {

  private readonly config: EntityTableConfig<OrderProgress> = new EntityTableConfig<OrderProgress>();

  constructor(
    private translate: TranslateService,
    private orderFormService: OrderFormService,
    private fileService: FileService,
    private datePipe: DatePipe
  ) {
    this.config.entityType = EntityType.ORDER_FORM;
    this.config.filterComponent = OrdersProgressFiltersComponent;
    this.config.entityTranslations = entityTypeTranslations.get(EntityType.ORDER_FORM);
    this.config.entityResources = entityTypeResources.get(EntityType.ORDER_FORM);

    this.config.componentsData = {
      sOrderNo: '',
      sCustomerName: '',
      sMaterialName: '',
      sColorName: '',
      dateRange:[],
      exportTableData: null
    }

    this.config.columns.push(
      new EntityTableColumn<OrderProgress>('sorderNo', 'order.order-no', '100px', (entity) => (entity.sorderNo || ''), () => ({}), false),
      new EntityTableColumn<OrderProgress>('scustomerName', 'order.customer', '120px', (entity) => (entity.scustomerName || ''), () => ({}), false),
      new DateEntityTableColumn<OrderProgress>('ddeliveryDate', 'order.delivery-date', this.datePipe, '120px', 'yyyy-MM-dd HH:ss', false),
      new EntityTableColumn<OrderProgress>('smaterialName', 'order.product-name', '300px', (entity) => (entity.smaterialName || ''), () => ({}), false),
      new EntityTableColumn<OrderProgress>('scolorName', 'order.color', '120px', (entity) => (entity.scolorName || ''), () => ({}), false),
      new EntityTableColumn<OrderProgress>('nqty', 'order.order-quantity', '120px', (entity) => (entity.nqty || ''), () => ({}), false),
      new EntityTableColumn<OrderProgress>('sfinishingMethod', 'order.arrangement-requirements', '250px', (entity) => (entity.sfinishingMethod || ''), () => ({}), false),
      new EntityTableColumn<OrderProgress>('wu1', 'order.turnover-cloth', '150px', (entity) => (''), () => ({}), false),
      new EntityTableColumn<OrderProgress>('wu2', 'order.billet-setting', '150px', (entity) => (''), () => ({}), false),
      new EntityTableColumn<OrderProgress>('wu3', 'order.dyeing', '100px', (entity) => (''), () => ({}), false),
      new EntityTableColumn<OrderProgress>('wu4', 'order.Chengding', '100px', (entity) => (''), () => ({}), false),
      new EntityTableColumn<OrderProgress>('wu5', 'order.cloth-checking', '100px', (entity) => (''), () => ({}), false),
      new EntityTableColumn<OrderProgress>('wu6', 'order.warehousing', '100px', (entity) => (''), () => ({}), false)
    );
  }
  

  resolve(): EntityTableConfig<OrderProgress> {
    this.config.componentsData = {
      sOrderNo: '',
      sCustomerName: '',
      sMaterialName: '',
      sColorName: '',
      dateRange:[],
      exportTableData: null
    }

    this.config.tableTitle = this.translate.instant('order.order-progress');
    this.config.addEnabled = false;
    this.config.searchEnabled = false;
    this.config.refreshEnabled = false;
    this.config.detailsPanelEnabled = false;
    this.config.entitiesDeleteEnabled = false;
    this.config.selectionEnabled = false;
    this.config.afterResolved = () => {
      this.config.addEnabled = false;
      this.config.entitiesDeleteEnabled = false;
    }

    // 导出功能
    this.config.componentsData.exportTableData = () => {
      let startTime: number, endTime: number;
      const dateRange = this.config.componentsData.dateRange;
      if (dateRange && dateRange.length === 2) {
        const startDate = this.config.componentsData.dateRange[0] as Date;
        startDate.setSeconds(0);
        startDate.setMilliseconds(0);
        startTime = startDate.getTime();
        const endDate = this.config.componentsData.dateRange[1] as Date;
        endDate.setSeconds(59);
        endDate.setMilliseconds(999);
        endTime = endDate.getTime();
      }
      const { sOrderNo, sCustomerName, sMaterialName, sColorName } = this.config.componentsData;
      this.orderFormService.getOrderProgress(new PageLink(9999999, 0), {
        sOrderNo, sCustomerName, sMaterialName, sColorName,
        dDeliveryDateBegin: startTime || '', dDeliveryDateEnd: endTime || ''
      }).subscribe((res) => {
        const dataList = [];
        if (res.data.length > 0) {
          const titleKeys = ['order.order-no', 'order.customer', 'order.delivery-date', 'order.product-name', 'order.color', 'order.order-quantity', 'order.arrangement-requirements', 'order.turnover-cloth', 'order.billet-setting', 'order.dyeing', 'order.Chengding', 'order.cloth-checking', 'order.warehousing'];
          const titleNames = [];
          titleKeys.forEach(key => {
            titleNames.push(this.translate.instant(key));
          });
          dataList.push(titleNames);
          res.data.forEach(item => {
            dataList.push([item.sorderNo, item.scustomerName, this.datePipe.transform(item.ddeliveryDate, 'yyyy-MM-dd HH:mm'), item.smaterialName, item.scolorName, item.nqty, item.sfinishingMethod, '', '', '', '', '', '']);
          });
        }
        this.fileService.exportTable(this.translate.instant('order.order-progress'), dataList).subscribe();
      })
    }

    this.config.entitiesFetchFunction = pageLink => {
      let startTime: number, endTime: number;
      const dateRange = this.config.componentsData.dateRange;
      if (dateRange && dateRange.length === 2) {
        const startDate = this.config.componentsData.dateRange[0] as Date;
        startDate.setSeconds(0);
        startDate.setMilliseconds(0);
        startTime = startDate.getTime();
        const endDate = this.config.componentsData.dateRange[1] as Date;
        endDate.setSeconds(59);
        endDate.setMilliseconds(999);
        endTime = endDate.getTime();
      }
      const { sOrderNo, sCustomerName, sMaterialName, sColorName } = this.config.componentsData;
      return this.orderFormService.getOrderProgress(pageLink, {
        sOrderNo, sCustomerName, sMaterialName, sColorName,
        dDeliveryDateBegin: startTime || '', dDeliveryDateEnd: endTime || ''
      });
    }
    return this.config;
  }
}
