import { ChangeDetectorRef, Component, Inject } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { AppState } from '@app/core/public-api';
import { EntityComponent } from '@app/modules/home/components/entity/entity.component';
import { EntityTableConfig } from '@app/modules/home/models/entity/entities-table-config.models';
import { WorkShop } from '@app/shared/models/custom/factory-mng.models';
import { Store } from '@ngrx/store';
import { TranslateService } from '@ngx-translate/core';

@Component({
  selector: 'tb-work-shop-form',
  templateUrl: './work-shop-form.component.html'
})
export class WorkShopFormComponent extends EntityComponent<WorkShop> {

  constructor(
    protected store: Store<AppState>,
    protected translate: TranslateService,
    @Inject('entity') protected entityValue: WorkShop,
    @Inject('entitiesTableConfig') protected entitiesTableConfigValue: EntityTableConfig<WorkShop>,
    protected fb: FormBuilder,
    protected cd: ChangeDetectorRef
  ) {
    super(store, fb, entityValue, entitiesTableConfigValue, cd);
  }

  buildForm(entity: WorkShop): FormGroup {
    return this.fb.group({
      factoryId: [entity && entity.factoryId ? entity.factoryId : this.entitiesTableConfig.componentsData.factoryId],
      factoryName: [entity && entity.factoryName ? entity.factoryName : this.entitiesTableConfig.componentsData.factoryName],
      name: [entity ? entity.name : '', Validators.required],
      remark: [entity ? entity.remark : ''],
      logoImages: [{
        value: entity ? entity.logoImages : '',
        disabled: !this.isEdit
      }],
      bgImages: [{
        value: entity ? entity.bgImages : '',
        disabled: !this.isEdit
      }]
    });
  }

  updateForm(entity: WorkShop) {
    this.entityForm.patchValue(entity);
  }

}
