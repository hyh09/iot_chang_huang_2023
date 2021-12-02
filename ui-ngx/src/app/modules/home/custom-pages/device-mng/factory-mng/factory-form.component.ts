import { ChangeDetectorRef, Component, Inject, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { AppState } from '@app/core/public-api';
import { EntityComponent } from '@app/modules/home/components/entity/entity.component';
import { EntityTableConfig } from '@app/modules/home/models/entity/entities-table-config.models';
import { Factory } from '@app/shared/models/custom/factory-mng.models';
import { Store } from '@ngrx/store';
import { TranslateService } from '@ngx-translate/core';

@Component({
  selector: 'tb-factory-form',
  templateUrl: './factory-form.component.html'
})
export class FactoryFormComponent extends EntityComponent<Factory> {

  constructor(
    protected store: Store<AppState>,
    protected translate: TranslateService,
    @Inject('entity') protected entityValue: Factory,
    @Inject('entitiesTableConfig') protected entitiesTableConfigValue: EntityTableConfig<Factory>,
    protected fb: FormBuilder,
    protected cd: ChangeDetectorRef
  ) {
    super(store, fb, entityValue, entitiesTableConfigValue, cd);
  }

  buildForm(entity: Factory): FormGroup {
    return this.fb.group({
      name: [entity ? entity.name : '', Validators.required],
      address: [entity ? entity.address : '', Validators.required],
      postalCode: [entity ? entity.postalCode : ''],
      remark: [entity ? entity.remark : ''],
      logoImages: [entity ? entity.logoImages : '']
    });
  }

  updateForm(entity: Factory) {
    this.entityForm.patchValue(entity);
  }

}
