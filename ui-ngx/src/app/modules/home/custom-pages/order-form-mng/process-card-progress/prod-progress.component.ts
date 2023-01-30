import { TranslateService } from '@ngx-translate/core';
import { Component, Inject } from "@angular/core";
import { MatDialogRef, MAT_DIALOG_DATA } from "@angular/material/dialog";
import { Router } from "@angular/router";
import { AppState } from "@app/core/core.state";
import { OrderFormService } from '@app/core/http/custom/order-form.service';
import { DialogComponent, EntityType, entityTypeResources, entityTypeTranslations } from "@app/shared/public-api";
import { Store } from "@ngrx/store";
import { ProdProgress } from '@app/shared/models/custom/order-form-mng.models';
import { EntityTableColumn, EntityTableConfig } from '@app/modules/home/models/entity/entities-table-config.models';

@Component({
  selector: 'tb-select-users',
  templateUrl: './prod-progress.component.html'
})
export class SelectProdProgressComponent extends DialogComponent<SelectProdProgressComponent, ProdProgress[]> {

  public readonly config: EntityTableConfig<ProdProgress> = new EntityTableConfig<ProdProgress>();

  constructor(
    protected store: Store<AppState>,
    protected router: Router,
    public dialogRef: MatDialogRef<SelectProdProgressComponent, ProdProgress[]>,
    protected OrderFormService: OrderFormService,
    protected translate: TranslateService,
    @Inject(MAT_DIALOG_DATA) protected sorderNo: string
  ) {
    super(store, router, dialogRef);

    this.config.entityType = EntityType.USER_MNG;
    this.config.entityTranslations = entityTypeTranslations.get(EntityType.USER_MNG);
    this.config.entityResources = entityTypeResources.get(EntityType.USER_MNG);

    this.config.addEnabled = false;
    this.config.searchEnabled = false;
    this.config.refreshEnabled = false;
    this.config.detailsPanelEnabled = false;
    this.config.padding = '0';
    this.config.titleVisible = false;
    this.config.groupActionEnabled = false;
    this.config.entitiesDeleteEnabled = false;
    this.config.selectionAlwaysEnabled = false;


    this.config.columns.push(
      new EntityTableColumn<ProdProgress>('sworkingProcedureNo', 'potency.process-no', '100px'),
      new EntityTableColumn<ProdProgress>('sworkingProcedureName', 'potency.process-name', '100px'),
      new EntityTableColumn<ProdProgress>('tfactStartTime', 'common.start-time',  '100px'),
      new EntityTableColumn<ProdProgress>('tfactEndTime', 'common.end-time',  '100px'),
      new EntityTableColumn<ProdProgress>('sequipmentName', 'potency.production-machine',  '100px'),
      new EntityTableColumn<ProdProgress>('ntrackQty', 'potency.production-quantity',  '100px'),
      new EntityTableColumn<ProdProgress>('npercentValue', 'potency.completion-rate',  '100px'),
      new EntityTableColumn<ProdProgress>('slocation', 'potency.cloth-car-number',  '100px'),
      new EntityTableColumn<ProdProgress>('sWorkerGroupName', 'potency.production-team',  '100px'),
      new EntityTableColumn<ProdProgress>('sworkerNameList', 'potency.staff-on-duty',  '100px')
    );
    this.config.entitiesFetchFunction = pageLink => this.OrderFormService.getProdProgressBySorderNo(pageLink, {
      sOrderNo: this.sorderNo
    });
  }

  cancel() {
    this.dialogRef.close(null);
  }
}
