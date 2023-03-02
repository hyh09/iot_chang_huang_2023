import { Injectable } from "@angular/core";
import { Resolve } from '@angular/router';
import { EntityTableColumn, EntityTableConfig, CellActionDescriptor, DateEntityTableColumn } from "@app/modules/home/models/entity/entities-table-config.models";
import { EntityType, entityTypeResources, entityTypeTranslations, PageLink } from "@app/shared/public-api";
import { TranslateService } from '@ngx-translate/core';
import { ProcessCardProgress } from '@app/shared/models/custom/order-form-mng.models';
import { ProcessCardProgressFiltersComponent } from './process-card-progress-filters.component';
import { OrderFormService } from '@app/core/http/custom/order-form.service';
import { MatDialog } from "@angular/material/dialog";
import { SelectProdProgressComponent } from "./prod-progress.component";
import { DatePipe } from "@angular/common";
import { FileService } from "@app/core/http/custom/file.service";

@Injectable()
export class ProcessCardProgressTableConfigResolver implements Resolve<EntityTableConfig<ProcessCardProgress>> {

  private readonly config: EntityTableConfig<ProcessCardProgress> = new EntityTableConfig<ProcessCardProgress>();

  constructor(
    private translate: TranslateService,
    private orderFormService: OrderFormService,
    private fileService: FileService,
    public dialog: MatDialog,
    private datePipe: DatePipe
  ) {
    this.config.entityType = EntityType.PROCESS_CARD;
    this.config.filterComponent = ProcessCardProgressFiltersComponent;
    this.config.entityTranslations = entityTypeTranslations.get(EntityType.PROCESS_CARD);
    this.config.entityResources = entityTypeResources.get(EntityType.PROCESS_CARD);

    this.config.componentsData = {
      sCardNo: '',
      sOrderNo: '',
      sCustomerName: '',
      sMaterialName: '',
      sColorName: '',
      exportTableData: null,
      dateRange: []
    }

    this.config.columns.push(
      new EntityTableColumn<ProcessCardProgress>('scardNo', 'potency.process-card-no', '100px', (entity) => (entity.scardNo || ''), () => ({}), false),
      new EntityTableColumn<ProcessCardProgress>('sorderNo', 'order.order-no', '100px', (entity) => (entity.sorderNo || ''), () => ({}), false),
      new EntityTableColumn<ProcessCardProgress>('scustomerName', 'order.customer', '120px', (entity) => (entity.scustomerName || ''), () => ({}), false),
      new DateEntityTableColumn<ProcessCardProgress>('ddeliveryDate', 'order.delivery-date', this.datePipe, '120px', 'yyyy-MM-dd HH:mm', false),
      new EntityTableColumn<ProcessCardProgress>('smaterialName', 'order.product-name', '300px', (entity) => (entity.smaterialName || ''), () => ({}), false),
      new EntityTableColumn<ProcessCardProgress>('scolorName', 'order.color', '120px', (entity) => (entity.scolorName || ''), () => ({}), false),
      new EntityTableColumn<ProcessCardProgress>('sfinishingMethod', 'order.arrangement-requirements', '250px', (entity) => (entity.sfinishingMethod || ''), () => ({}), false),
      new EntityTableColumn<ProcessCardProgress>('nplanOutputQty', 'order.number-of-cards', '100px', (entity) => (entity.nplanOutputQty || ''), () => ({}), false),
      new EntityTableColumn<ProcessCardProgress>('sworkingProcedureName', 'order.current-operation', '120px', (entity) => (entity.sworkingProcedureName || ''), () => ({}), false),
      new EntityTableColumn<ProcessCardProgress>('wu', 'order.operation-finished-qty', '150px', (entity) => (''), () => ({}), false),
      new EntityTableColumn<ProcessCardProgress>('sworkingProcedureNameNext', 'order.next-procedure', '120px', (entity) => (entity.sworkingProcedureNameNext || ''), () => ({}), false)
    );
  }
  resolve(): EntityTableConfig<ProcessCardProgress> {
    this.config.componentsData = {
      sCardNo: '',
      sOrderNo: '',
      sCustomerName: '',
      sMaterialName: '',
      sColorName: '',
      exportTableData: null,
      dateRange: []
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
      const { sCardNo, sOrderNo, sCustomerName, sMaterialName, sColorName } = this.config.componentsData;
      this.orderFormService.getprocessCardProgress(new PageLink(9999999, 0), {
        sCardNo, sOrderNo, sCustomerName, sMaterialName, sColorName,
        dDeliveryDateBegin: startTime || '', dDeliveryDateEnd: endTime || ''
      }).subscribe((res) => {
        const dataList = [];
        if (res.data.length > 0) {
          const titleKeys = ['potency.process-card-no', 'order.order-no', 'order.customer', 'order.delivery-date', 'order.product-name', 'order.color', 'order.arrangement-requirements', 'order.number-of-cards', 'order.current-operation', 'order.operation-finished-qty', 'order.next-procedure'];
          const titleNames = [];
          titleKeys.forEach(key => {
            titleNames.push(this.translate.instant(key));
          });
          dataList.push(titleNames);
          res.data.forEach(item => {
            dataList.push([item.scardNo, item.sorderNo, item.scustomerName, this.datePipe.transform(item.ddeliveryDate, 'yyyy-MM-dd HH:mm'), item.smaterialName, item.scolorName, item.sfinishingMethod, item.nplanOutputQty, item.sworkingProcedureName, '', item.sworkingProcedureNameNext]);
          });
        }
        this.fileService.exportTable(this.translate.instant('order.process-card-progress'), dataList).subscribe();
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
      const { sCardNo, sOrderNo, sCustomerName, sMaterialName, sColorName } = this.config.componentsData;
      return this.orderFormService.getprocessCardProgress(pageLink, {
        sCardNo, sOrderNo, sCustomerName, sMaterialName, sColorName,
        dDeliveryDateBegin: startTime || '', dDeliveryDateEnd: endTime || ''
      });
    }
    return this.config;
  }


  configureCellActions(): Array<CellActionDescriptor<ProcessCardProgress>> {
    const actions: Array<CellActionDescriptor<ProcessCardProgress>> = [];
    actions.push({
      name: this.translate.instant('order.view-production-progress'),
      mdiIcon: 'mdi:progress',
      isEnabled: () => true,
      onAction: ($event, entity) => this.selectProdCard($event, entity)
    });
    return actions;
  }

  selectProdCard($event: Event, processCard: ProcessCardProgress): void {
    if ($event) {
      $event.stopPropagation();
    }
    this.dialog.open<SelectProdProgressComponent, ProcessCardProgress>(SelectProdProgressComponent, {
      disableClose: true,
      panelClass: ['tb-dialog', 'tb-fullscreen-dialog'],
      data: processCard
    });
  }


}
