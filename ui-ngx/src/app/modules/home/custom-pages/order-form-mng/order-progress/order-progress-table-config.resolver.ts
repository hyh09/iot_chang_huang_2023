import { Injectable } from "@angular/core";
import { Resolve } from '@angular/router';
import { EntityTableColumn, EntityTableConfig } from "@app/modules/home/models/entity/entities-table-config.models";
import { EntityType, entityTypeResources, entityTypeTranslations, HasId } from "@app/shared/public-api";
import { TranslateService } from '@ngx-translate/core';
import { OrderProgress } from '@app/shared/models/custom/order-form-mng.models';
import { OrdersProgressFiltersComponent } from './orders-progress-filters.component';
import { OrderFormService } from '@app/core/http/custom/order-form.service';
import { ProductionMngService } from '@app/core/http/custom/production-mng.service';

@Injectable()
export class OrdersProgressTableConfigResolver implements Resolve<EntityTableConfig<OrderProgress>> {

  private readonly config: EntityTableConfig<OrderProgress> = new EntityTableConfig<OrderProgress>();

  constructor(
    private translate: TranslateService,
    private OrderProgressService: OrderFormService,
    private productionMngService: ProductionMngService,
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
      exportTableData: null,
      tableList: []
    }

    this.config.columns.push(
      new EntityTableColumn<OrderProgress>('sorderNo', 'order.order-no', '120px'),
      new EntityTableColumn<OrderProgress>('scustomerName', 'order.customer', '120px'),
      new EntityTableColumn<OrderProgress>('ddeliveryDate', 'order.delivery-date', '120px'),
      new EntityTableColumn<OrderProgress>('smaterialName', 'order.product-name', '200px', (entity) => (entity.smaterialName || ''), () => ({}), false),
      new EntityTableColumn<OrderProgress>('scolorName', 'order.colour', '150px'),
      new EntityTableColumn<OrderProgress>('nqty', 'order.order-quantity', '150px'),
      new EntityTableColumn<OrderProgress>('sfinishingMethod', 'order.arrangement-requirements', '150px'),
      new EntityTableColumn<OrderProgress>('wu1', 'order.turnover-cloth', '150px'),
      new EntityTableColumn<OrderProgress>('wu2', 'order.billet-setting', '150px'),
      new EntityTableColumn<OrderProgress>('wu3', 'order.dyeing', '100px'),
      new EntityTableColumn<OrderProgress>('wu4', 'order.Chengding', '100px'),
      new EntityTableColumn<OrderProgress>('emergencyDegree', 'order.cloth-checking', '100px'),
      new EntityTableColumn<OrderProgress>('merchandiser', 'order.warehousing', '100px'),
    );
  }
  

  resolve(): EntityTableConfig<OrderProgress> {
    this.config.componentsData = {
      dateRange: [],
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
      this.config.componentsData.tableList.subscribe((res) => {
        let dataList = []
        let titleList = ['订单号', '客户', '交货日期', '品名', '颜色', '订单数量', '整理要求', '翻布', '坯定', '染色', '成定','验布','入库']
        dataList.push(titleList)
        if (res.data.length > 0) {
          res.data.forEach(item => {
            let itemList = [item.sorderNo, item.scustomerName, item.ddeliveryDate, item.smaterialName, item.scolorName, item.nqty, item.sfinishingMethod, '', '', '', '', item.emergencyDegree, item.merchandiser]
            dataList.push(itemList)
          });
        }
        this.productionMngService.exportPort('订单进度', dataList).subscribe();
      })
    }

    this.config.entitiesFetchFunction = pageLink => {
      let startTime: number, endTime: number;
      const dateRange = this.config.componentsData.dateRange;
      if (dateRange && dateRange.length === 2) {
        startTime = (this.config.componentsData.dateRange[0] as Date).getTime();
        endTime = (this.config.componentsData.dateRange[1] as Date).getTime();
      }
      const { sOrderNo, sCustomerName, sMaterialName, sColorName } = this.config.componentsData;
      let tableList = this.OrderProgressService.getOrderProgress(pageLink, {
        sOrderNo: sOrderNo || '',
        sCustomerName: sCustomerName || '',
        sMaterialName: sMaterialName || '',
        sColorName: sColorName || '',
        dDeliveryDateBegin: startTime || '', dDeliveryDateEnd: endTime || ''
      });
      this.config.componentsData.tableList = tableList
      return tableList
    }
    return this.config;
  }
}
