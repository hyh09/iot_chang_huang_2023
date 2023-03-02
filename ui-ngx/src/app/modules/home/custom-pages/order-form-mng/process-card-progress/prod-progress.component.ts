import { TranslateService } from '@ngx-translate/core';
import { Component, Inject } from "@angular/core";
import { MatDialogRef, MAT_DIALOG_DATA } from "@angular/material/dialog";
import { Router } from "@angular/router";
import { AppState } from "@app/core/core.state";
import { OrderFormService } from '@app/core/http/custom/order-form.service';
import { DialogComponent, EntityType, entityTypeResources, entityTypeTranslations } from "@app/shared/public-api";
import { Store } from "@ngrx/store";
import { ProcessCardProgress, ProdProgress } from '@app/shared/models/custom/order-form-mng.models';
import { DateEntityTableColumn, EntityTableColumn, EntityTableConfig } from '@app/modules/home/models/entity/entities-table-config.models';
import { DatePipe } from '@angular/common';

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
    private datePipe: DatePipe,
    @Inject(MAT_DIALOG_DATA) public processCard: ProcessCardProgress
  ) {
    super(store, router, dialogRef);

    this.config.entityType = EntityType.PROCEDURE;
    this.config.entityTranslations = entityTypeTranslations.get(EntityType.PROCEDURE);
    this.config.entityResources = entityTypeResources.get(EntityType.PROCEDURE);

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
      new EntityTableColumn<ProdProgress>('sworkingProcedureNo', 'potency.procedure-no', '100px', (entity) => (entity.sworkingProcedureNo || ''), () => ({}), false),
      new EntityTableColumn<ProdProgress>('sworkingProcedureName', 'potency.procedure-name','100px', (entity) => (entity.sworkingProcedureName || ''), () => ({}), false),
      new DateEntityTableColumn<ProdProgress>('tfactStartTime', 'common.start-time', this.datePipe, '120px', 'yyyy-MM-dd HH:mm:ss', false),
      new DateEntityTableColumn<ProdProgress>('tfactEndTime', 'common.end-time', this.datePipe, '120px', 'yyyy-MM-dd HH:mm:ss', false),
      new EntityTableColumn<ProdProgress>('sequipmentName', 'potency.device-name', '100px', (entity) => (entity.sequipmentName || ''), () => ({}), false),
      new EntityTableColumn<ProdProgress>('ntrackQty', 'potency.production-quantity', '100px', (entity) => (entity.ntrackQty || ''), () => ({}), false),
      new EntityTableColumn<ProdProgress>('npercentValue', 'potency.completion-rate', '100px', (entity) => (entity.npercentValue || ''), () => ({}), false),
      new EntityTableColumn<ProdProgress>('slocation', 'potency.cloth-car-number', '100px', (entity) => (entity.slocation || ''), () => ({}), false),
      new EntityTableColumn<ProdProgress>('sworkerGroupName', 'potency.team-name', '100px', (entity) => (entity.sworkerGroupName || ''), () => ({}), false),
      new EntityTableColumn<ProdProgress>('sworkerNameList', 'potency.team-members', '150px', (entity) => (entity.sworkerNameList || ''), () => ({}), false)
    );
    this.config.entitiesFetchFunction = pageLink => this.OrderFormService.getProdProgressBySorderNo(pageLink, {
      sCardNo: this.processCard.scardNo
    });
  }

  cancel() {
    this.dialogRef.close(null);
  }
}
