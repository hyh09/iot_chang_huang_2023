import { TranslateService } from '@ngx-translate/core';
import { Store } from '@ngrx/store';
import { ChangeDetectorRef, Component, Inject } from '@angular/core';
import { AppState } from '@app/core/core.state';
import { EntityComponent } from '@app/modules/home/components/entity/entity.component';
import { DataDictionary } from '@app/shared/models/custom/device-mng.models';
import { EntityTableConfig } from '@app/modules/home/models/entity/entities-table-config.models';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';

@Component({
  selector: 'tb-data-dictionary',
  templateUrl: './data-dictionary.component.html'
})
export class DataDictionaryComponent extends EntityComponent<DataDictionary> {

  constructor(
    protected store: Store<AppState>,
    protected translate: TranslateService,
    @Inject('entity') protected entityValue: DataDictionary,
    @Inject('entitiesTableConfig') protected entitiesTableConfigValue: EntityTableConfig<DataDictionary>,
    protected fb: FormBuilder,
    protected cd: ChangeDetectorRef
  ) {
    super(store, fb, entityValue, entitiesTableConfigValue, cd);
  }

  buildForm(entity: DataDictionary): FormGroup {
    return this.fb.group(
      {
        id:  [entity ? entity.id : ''],
        code: [entity && entity.code ? entity.code : this.entitiesTableConfig.componentsData.availableCode, [Validators.required]],
        name: [entity ? entity.name : '', [Validators.required]],
        type: [entity ? entity.type : null, [Validators.required]],
        unit: [entity ? entity.unit : ''],
        comment: [entity ? entity.comment : ''],
        icon: [entity ? entity.icon : ''],
        picture: [entity ? entity.picture : '']
      }
    );
  }

  updateForm(entity: DataDictionary) {
    this.entityForm.patchValue(entity);
    console.log(this.entityForm.get('id').value)
  }

}
