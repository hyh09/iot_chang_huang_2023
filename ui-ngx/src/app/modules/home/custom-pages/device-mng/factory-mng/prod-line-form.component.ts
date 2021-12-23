import { ChangeDetectorRef, Component, Inject } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { AppState } from '@app/core/public-api';
import { EntityComponent } from '@app/modules/home/components/entity/entity.component';
import { EntityTableConfig } from '@app/modules/home/models/entity/entities-table-config.models';
import { ProdLine } from '@app/shared/models/custom/factory-mng.models';
import { Store } from '@ngrx/store';
import { TranslateService } from '@ngx-translate/core';

@Component({
  selector: 'tb-prod-line-form',
  templateUrl: './prod-line-form.component.html'
})
export class ProdLineFormComponent extends EntityComponent<ProdLine> {

  constructor(
    protected store: Store<AppState>,
    protected translate: TranslateService,
    @Inject('entity') protected entityValue: ProdLine,
    @Inject('entitiesTableConfig') protected entitiesTableConfigValue: EntityTableConfig<ProdLine>,
    protected fb: FormBuilder,
    protected cd: ChangeDetectorRef
  ) {
    super(store, fb, entityValue, entitiesTableConfigValue, cd);
  }

  buildForm(entity: ProdLine): FormGroup {
    return this.fb.group({
      factoryId: [entity && entity.factoryId ? entity.factoryId : this.entitiesTableConfig.componentsData.factoryId],
      factoryName: [entity && entity.factoryName ? entity.factoryName : this.entitiesTableConfig.componentsData.factoryName],
      workshopId: [entity && entity.workshopId ? entity.workshopId : this.entitiesTableConfig.componentsData.workshopId],
      workshopName: [entity && entity.workshopName ? entity.workshopName : this.entitiesTableConfig.componentsData.workshopName],
      name: [entity ? entity.name : '', Validators.required],
      remark: [entity ? entity.remark : '']
    });
  }

  updateForm(entity: ProdLine) {
    this.entityForm.patchValue(entity);
  }

}
