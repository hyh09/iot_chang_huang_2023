import { TranslateService } from '@ngx-translate/core';
import { Component, Inject } from "@angular/core";
import { MatDialogRef, MAT_DIALOG_DATA } from "@angular/material/dialog";
import { Router } from "@angular/router";
import { AppState } from "@app/core/core.state";
import { DialogComponent, EntityType, entityTypeResources, entityTypeTranslations, HasId } from "@app/shared/public-api";
import { Store } from "@ngrx/store";
import { EntityTableColumn, EntityTableConfig } from '@app/modules/home/models/entity/entities-table-config.models';
import { OrderConsumption, OrderProcessCard } from '@app/shared/models/custom/potency.models';
import { PotencyService } from '@app/core/http/custom/potency.service';

@Component({
  selector: 'tb-process-cards',
  templateUrl: './process-cards.component.html'
})
export class ProcessCardsComponent extends DialogComponent<ProcessCardsComponent, OrderProcessCard[]> {

  public readonly config: EntityTableConfig<OrderProcessCard> = new EntityTableConfig<OrderProcessCard>();

  constructor(
    protected store: Store<AppState>,
    protected router: Router,
    public dialogRef: MatDialogRef<ProcessCardsComponent, OrderProcessCard[]>,
    private potencyService: PotencyService,
    protected translate: TranslateService,
    @Inject(MAT_DIALOG_DATA) public order: OrderConsumption
  ) {
    super(store, router, dialogRef);

    this.config.entityType = EntityType.PROCESS_CARD;
    this.config.entityTranslations = entityTypeTranslations.get(EntityType.PROCESS_CARD);
    this.config.entityResources = entityTypeResources.get(EntityType.PROCESS_CARD);

    this.config.addEnabled = false;
    this.config.searchEnabled = false;
    this.config.refreshEnabled = false;
    this.config.detailsPanelEnabled = false;
    this.config.padding = '0';
    this.config.titleVisible = false;
    this.config.groupActionEnabled = false;
    this.config.entitiesDeleteEnabled = false;

    this.config.componentsData = {
      scardNo: ''
    }

    this.config.columns.push(
      new EntityTableColumn<OrderProcessCard>('deviceName', 'potency.device-name', '120px', (entity) => (entity.deviceName || ''), () => ({}), false),
      new EntityTableColumn<OrderProcessCard>('scardNo', 'potency.process-card-no', '120px', (entity) => (entity.scardNo || ''), () => ({}), false),
      new EntityTableColumn<OrderProcessCard>('materialName', 'potency.material-name', '100px', (entity) => (entity.materialName || ''), () => ({}), false),
      new EntityTableColumn<OrderProcessCard>('colorName', 'potency.color', '100px', (entity) => (entity.colorName || ''), () => ({}), false),
      new EntityTableColumn<OrderProcessCard>('workerGroupName', 'potency.team-name', '120px', (entity) => (entity.workerGroupName || ''), () => ({}), false),
      new EntityTableColumn<OrderProcessCard>('workerName', 'potency.team-members', '180px', (entity) => (entity.workerName || ''), () => ({}), false),
      new EntityTableColumn<OrderProcessCard>('nTrackQty', 'potency.capacity', '120px', (entity) => (entity.nTrackQty || ''), () => ({}), false),
      new EntityTableColumn<OrderProcessCard>('sRemark', 'order.remarks', '200px', (entity) => (entity.sRemark || ''), () => ({}), false)
    );

    this.config.entitiesFetchFunction = pageLink => this.potencyService.getOrderProcessCards(pageLink, {
      ...this.config.componentsData,
      uGuid: this.order.uguid
    });
  }

  close() {
    this.dialogRef.close(null);
  }

}
