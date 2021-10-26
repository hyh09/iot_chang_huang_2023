import { ChangeDetectorRef, Component, Inject } from '@angular/core';
import { FormBuilder, FormGroup, Validators, AbstractControl, FormArray } from '@angular/forms';
import { AppState } from '@app/core/core.state';
import { EntityComponent } from '@app/modules/home/components/entity/entity.component';
import { EntityTableConfig } from '@app/modules/home/models/entity/entities-table-config.models';
import { DeviceDataGroup, DeviceDictionary, DeviceProperty, DeviceData } from '@app/shared/models/custom/device-mng.models';
import { Store } from '@ngrx/store';
import { TranslateService } from '@ngx-translate/core';
import { data } from 'jquery';

@Component({
  selector: 'tb-device-dictionary',
  templateUrl: './device-dictionary.component.html',
  styleUrls: ['./device-dictionary.component.scss']
})
export class DeviceDictionaryComponent extends EntityComponent<DeviceDictionary> {

  public customPropExpanded = false;
  public groupPropExpanded = false;
  public currentTabIndex = 0;

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
    }

    const groupListControls: Array<AbstractControl> = [];
    if (entity && entity.groupList && entity.groupList.length > 0) {
      for (const group of entity.groupList) {
        groupListControls.push(this.createGroupListControl(group));
      }
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
      propertyList: this.fb.array(propertyListControls),
      groupList: this.fb.array(groupListControls)
    });
  }

  updateForm(entity: DeviceDictionary) {
    this.entityForm.patchValue(entity);
  }

  /**
   * @description 设备属性相关方法
   */
  devicePropFormArray(): FormArray {
    return this.entityForm.get('propertyList') as FormArray;
  }
  createPropertyListControl(property?: DeviceProperty): AbstractControl {
    return this.fb.group({
      name: [property ? property.name : ''],
      content: [property ? property.content: '']
    });
  }
  addDeviceProperty(event: MouseEvent) {
    event.stopPropagation();
    event.preventDefault();
    this.devicePropFormArray().push(this.createPropertyListControl());
  }
  removeDeviceProperty(index: number) {
    this.devicePropFormArray().removeAt(index);
  }

  /**
   * @description 设备参数相关方法
   */
  deviceDataGroupFormArray(): FormArray {
    return this.entityForm.get('groupList') as FormArray;
  }
  deviceDatFormArray(groupIndex: number): FormArray {
    return this.deviceDataGroupFormArray()[groupIndex].get('groupPropertyList') as FormArray;
  }
  createDeviceDataControl(data?: DeviceData): AbstractControl {
    return this.fb.group({
      name: [data ? data.name : ''],
      content: [data ? data.content: '']
    })
  }
  createGroupListControl(dataGroup?: DeviceDataGroup): AbstractControl {
    const controls: Array<AbstractControl> = [];
    if (dataGroup && dataGroup.groupPropertyList && dataGroup.groupPropertyList.length > 0) {
      for (const data of dataGroup.groupPropertyList) {
        controls.push(this.createDeviceDataControl(data));
      }
    }
    return this.fb.group({
      name: [dataGroup ? dataGroup.name : ''],
      groupPropertyList: this.fb.array(controls)
    });
  }
  addDeviceData(event: MouseEvent) {
    event.stopPropagation();
    event.preventDefault();
    // this.deviceDataGroupFormArray()[$index].push(this.createDeviceDataControl())
  }
  removeDeviceData(groupIndex: number, $index: number) {
    this.deviceDatFormArray(groupIndex).removeAt($index);
  }
  addDeviceDataGroup() {
    this.deviceDataGroupFormArray().push(this.createGroupListControl());
  }
  removeDeviceDataGroup($index: number) {
    this.deviceDataGroupFormArray().removeAt($index);
  }

}
