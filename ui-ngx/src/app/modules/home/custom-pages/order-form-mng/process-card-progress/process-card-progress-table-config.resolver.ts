import { Injectable } from "@angular/core";
import { Resolve } from '@angular/router';
import { EntityTableColumn, EntityTableConfig, CellActionDescriptor } from "@app/modules/home/models/entity/entities-table-config.models";
import { EntityType, entityTypeResources, entityTypeTranslations } from "@app/shared/public-api";
import { TranslateService } from '@ngx-translate/core';
import { processCardProgress } from '@app/shared/models/custom/order-form-mng.models';
import { ProcessCardProgressFiltersComponent } from './process-card-progress-filters.component';
import { OrderFormService } from '@app/core/http/custom/order-form.service';
import { ProductionMngService } from '@app/core/http/custom/production-mng.service';
import { MatDialog } from "@angular/material/dialog";
import { SelectProdProgressComponent } from "./prod-progress.component";

@Injectable()
export class ProcessCardProgressTableConfigResolver implements Resolve<EntityTableConfig<processCardProgress>> {

  private readonly config: EntityTableConfig<processCardProgress> = new EntityTableConfig<processCardProgress>();

  constructor(
    private translate: TranslateService,
    private processCardProgressService: OrderFormService,
    private productionMngService: ProductionMngService,
    public dialog: MatDialog
  ) {
    this.config.entityType = EntityType.PROCESS_CARD;
    this.config.filterComponent = ProcessCardProgressFiltersComponent;
    this.config.entityTranslations = entityTypeTranslations.get(EntityType.PROCESS_CARD);
    this.config.entityResources = entityTypeResources.get(EntityType.PROCESS_CARD);

    this.config.componentsData = {
      time: '',
      orderNo: '',
      colour: '',
      customerName: '',
      processCardNo: '',
      productName: ''
    }

    this.config.columns.push(
      new EntityTableColumn<processCardProgress>('scardNo', 'potency.card-no', '120px'),
      new EntityTableColumn<processCardProgress>('sorderNo', 'order.order-no', '120px'),
      new EntityTableColumn<processCardProgress>('ddeliveryDate', 'order.delivery-date', '120px'),
      new EntityTableColumn<processCardProgress>('scustomerName', 'order.customer', '120px'),
      new EntityTableColumn<processCardProgress>('smaterialName', 'order.product-name', '200px', (entity) => (entity.smaterialName || ''), () => ({}), false),
      new EntityTableColumn<processCardProgress>('scolorName', 'order.colour', '150px'),
      new EntityTableColumn<processCardProgress>('sfinishingMethod', 'order.arrangement-requirements', '150px'),
      new EntityTableColumn<processCardProgress>('nplanOutputQty', 'order.number-of-cards', '150px'),
      new EntityTableColumn<processCardProgress>('sworkingProcedureName', 'order.current-operation', '150px'),
      new EntityTableColumn<processCardProgress>('wu', 'order.operation-finished-qty', '150px'),
      new EntityTableColumn<processCardProgress>('sworkingProcedureNameNext', 'order.next-procedure', '150px'),
    );
  }
  resolve(): EntityTableConfig<processCardProgress> {
    this.config.componentsData = {
      sOrderNo: '',
      sCustomerName: '',
      sMaterialName: '',
      sColorName: '',
      exportTableData: null,
      dateRange: [],
      tableList: []
    }

    this.config.tableTitle = this.translate.instant('order.process-card-progress');
    this.config.detailsPanelEnabled = false;
    this.config.searchEnabled = false;
    this.config.refreshEnabled = false;
    this.config.afterResolved = () => {
      this.config.addEnabled = false;
      this.config.entitiesDeleteEnabled = false;
      this.config.cellActionDescriptors = this.configureCellActions();
    }

    // 导出功能
    this.config.componentsData.exportTableData = () => {
      this.config.componentsData.tableList.subscribe((res) => {
        let dataList = []
        let titleList = ['卡号', '订单号', '交货日期', '客户', '品名', '颜色', '整理要求', '卡数量', '当前工序', '工序完工数量', '下道工序']
        dataList.push(titleList)
        if (res.data.length > 0) {
          res.data.forEach(item => {
            let itemList = [item.scardNo, item.sorderNo, item.ddeliveryDate, item.scustomerName, item.smaterialName, item.scolorName, item.sfinishingMethod, item.nplanOutputQty, item.sworkingProcedureName, '', item.sworkingProcedureNameNext]
            dataList.push(itemList)
          });
        }
        this.productionMngService.exportPort('流程卡进度', dataList).subscribe();
        console.log(dataList)
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
      let tableList =  this.processCardProgressService.getprocessCardProgress(pageLink, {
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


  configureCellActions(): Array<CellActionDescriptor<processCardProgress>> {
    const actions: Array<CellActionDescriptor<processCardProgress>> = [];
    actions.push({
      name: this.translate.instant('device-mng.production-progress'),
      icon: 'more_horiz',
      isEnabled: () => true,
      onAction: ($event, entity) => this.selectProdCard($event, entity.sorderNo)
    });
    return actions;
  }

  selectProdCard($event: Event, sorderNo: string): void {
    if ($event) {
      $event.stopPropagation();
    }
    this.dialog.open<SelectProdProgressComponent, string>(SelectProdProgressComponent, {
      disableClose: true,
      panelClass: ['tb-dialog', 'tb-fullscreen-dialog'],
      data: sorderNo
    });
  }


}
