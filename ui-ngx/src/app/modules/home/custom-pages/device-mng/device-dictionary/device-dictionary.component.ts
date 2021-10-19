import { ChangeDetectorRef, Component, Inject } from '@angular/core';
import { FormBuilder, FormGroup } from '@angular/forms';
import { AppState } from '@app/core/core.state';
import { EntityComponent } from '@app/modules/home/components/entity/entity.component';
import { EntityTableConfig } from '@app/modules/home/models/entity/entities-table-config.models';
import { DeviceDictionary } from '@app/shared/models/custom/device-mng.models';
import { Store } from '@ngrx/store';
import { TranslateService } from '@ngx-translate/core';

@Component({
  selector: 'tb-device-dictionary',
  templateUrl: './device-dictionary.component.html',
  styleUrls: ['./device-dictionary.component.scss']
})
export class DeviceDictionaryComponent extends EntityComponent<DeviceDictionary> {

  constructor(
    protected store: Store<AppState>,
    protected translate: TranslateService,
    @Inject('entity') protected entityValue: DeviceDictionary,
    @Inject('entitiesTableConfig') protected entitiesTableConfigValue: EntityTableConfig<DeviceDictionary>,
    protected fb: FormBuilder,
    protected cd: ChangeDetectorRef
  ) {
    super(store, fb, entityValue, entitiesTableConfigValue, cd);
  }

  buildForm(entity: DeviceDictionary): FormGroup {
    return this.fb.group({
      
    });
  }

  updateForm(entity: DeviceDictionary) {
    this.entityForm.patchValue(entity)
  }

}
