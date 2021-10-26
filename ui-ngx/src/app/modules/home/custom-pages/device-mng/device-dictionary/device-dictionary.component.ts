import { ChangeDetectorRef, Component, Inject } from '@angular/core';
import { FormBuilder, FormGroup, Validators, AbstractControl, FormArray } from '@angular/forms';
import { AppState } from '@app/core/core.state';
import { EntityComponent } from '@app/modules/home/components/entity/entity.component';
import { EntityTableConfig } from '@app/modules/home/models/entity/entities-table-config.models';
import { DeviceDictionary, DeviceProperty } from '@app/shared/models/custom/device-mng.models';
import { Store } from '@ngrx/store';
import { TranslateService } from '@ngx-translate/core';

@Component({
  selector: 'tb-device-dictionary',
  templateUrl: './device-dictionary.component.html',
  styleUrls: ['./device-dictionary.component.scss']
})
export class DeviceDictionaryComponent extends EntityComponent<DeviceDictionary> {

  public customAttrExpanded = false;

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
    const propertyListControls: Array<AbstractControl> = [];
    if (entity && entity.propertyList && entity.propertyList.length > 0) {
      for (const property of entity.propertyList) {
        propertyListControls.push(this.createPropertyListControl(property));
      }
    } else {
      propertyListControls.push(this.createPropertyListControl())
    }
    return this.fb.group({
      id:  [entity ? entity.id : ''],
      code: [entity && entity.code ? entity.code : this.entitiesTableConfig.componentsData.availableCode, [Validators.required]],
      name: [entity ? entity.name : '', [Validators.required]],
      type: [entity ? entity.type : ''],
      supplier: [entity ? entity.supplier : ''],
      model: [entity ? entity.model : ''],
      version: [entity ? entity.version : ''],
      warrantyPeriod: [entity ? entity.version : ''],
      picture: [entity ? entity.picture : ''],
      propertyList: this.fb.array(propertyListControls)
    });
  }

  updateForm(entity: DeviceDictionary) {
    this.entityForm.patchValue(entity);
  }

  propertyFormArray(): FormArray {
    return this.entityForm.get('propertyList') as FormArray;
  }

  createPropertyListControl(property?: DeviceProperty): AbstractControl {
    return this.fb.group({
      name: [property ? property.name : ''],
      content: [property ? property.content: '']
    });
  }

  addCustomProperty(event: MouseEvent) {
    event.stopPropagation();
    event.preventDefault();
    this.propertyFormArray().push(this.createPropertyListControl());
  }

  removeCustomProperty(index: number) {
    this.propertyFormArray().removeAt(index);
  }

}
