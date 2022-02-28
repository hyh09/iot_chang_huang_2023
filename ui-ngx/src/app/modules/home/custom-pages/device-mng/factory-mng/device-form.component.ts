import { ChangeDetectorRef, Component, Inject } from '@angular/core';
import { AbstractControl, FormArray, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { DataDictionaryService } from '@app/core/http/custom/data-dictionary.service';
import { DeviceDictionaryService } from '@app/core/http/custom/device-dictionary.service';
import { AppState, guid } from '@app/core/public-api';
import { EntityComponent } from '@app/modules/home/components/entity/entity.component';
import { EntityTableConfig } from '@app/modules/home/models/entity/entities-table-config.models';
import { DataDictionary, DeviceComp, DeviceCompTreeNode, DeviceData, DeviceDataGroup, DeviceDictionary, DeviceProperty } from '@app/shared/models/custom/device-mng.models';
import { ProdDevice } from '@app/shared/models/custom/factory-mng.models';
import { Store } from '@ngrx/store';
import { TranslateService } from '@ngx-translate/core';
import { DeviceCompFormComponent, DeviceCompDialogData } from '../device-dictionary/device-comp-form.component';

@Component({
  selector: 'tb-device-form',
  templateUrl: './device-form.component.html'
})
export class DeviceFormComponent extends EntityComponent<ProdDevice> {

  mapOfExpandedComp: { [code: string]: DeviceCompTreeNode[] } = {};
  mapOfCompControl: { [code: string]: AbstractControl } = {};
  expandedCompCode: string[] = [];
  deviceDictionaries: DeviceDictionary[] = [];
  dataDictionaries: DataDictionary[] = [];

  constructor(
    protected store: Store<AppState>,
    protected translate: TranslateService,
    @Inject('entity') protected entityValue: ProdDevice,
    @Inject('entitiesTableConfig') protected entitiesTableConfigValue: EntityTableConfig<ProdDevice>,
    protected fb: FormBuilder,
    protected cd: ChangeDetectorRef,
    public dialog: MatDialog,
    private deviceDictionaryService: DeviceDictionaryService,
    private dataDictionaryService: DataDictionaryService
  ) {
    super(store, fb, entityValue, entitiesTableConfigValue, cd);
    this.deviceDictionaryService.getAllDeviceDictionaries().subscribe(res => {
      this.deviceDictionaries = res || [];
      if (this.isAdd) {
        const defaultDicts = this.deviceDictionaries.filter(item => (item.isDefault));
        if (defaultDicts.length > 0) {
          this.entityForm.get('dictDeviceId').setValue(defaultDicts[0].id);
        }
      }
    });
    this.dataDictionaryService.getAllDataDictionaries().subscribe(res => {
      const arr = res || [];
      arr.forEach(item => {
        if (item.unit) {
          item.name += ` (${item.unit})`;
        }
      });
      this.dataDictionaries = arr;
    });
  }

  buildForm(entity: ProdDevice): FormGroup {
    this.mapOfExpandedComp = {};
    this.mapOfCompControl = {};
    this.expandedCompCode = [];
    const { standardPropControls, propertyListControls, groupListControls, compControls } = this.generateFromArray(entity);
    return this.fb.group({
      factoryId: [entity && entity.factoryId ? entity.factoryId : this.entitiesTableConfig.componentsData.factoryId],
      factoryName: [entity && entity.factoryName ? entity.factoryName : this.entitiesTableConfig.componentsData.factoryName],
      workshopId: [entity && entity.workshopId ? entity.workshopId : this.entitiesTableConfig.componentsData.workshopId],
      workshopName: [entity && entity.workshopName ? entity.workshopName : this.entitiesTableConfig.componentsData.workshopName],
      productionLineId: [entity && entity.productionLineId ? entity.productionLineId : this.entitiesTableConfig.componentsData.productionLineId],
      productionLineName: [entity && entity.productionLineName ? entity.productionLineName : this.entitiesTableConfig.componentsData.productionLineName],
      dictDeviceId: [entity && entity.dictDeviceId ? entity.dictDeviceId : ''],
      name: [entity ? entity.name : '', Validators.required],
      deviceNo: [entity ? entity.deviceNo : ''],
      comment: [entity ? entity.comment : ''],
      picture: [entity ? entity.picture : ''],
      fileName: [entity ? entity.fileName : ''],
      icon: [entity ? entity.icon : ''],
      type: [entity ? entity.type : ''],
      supplier: [entity ? entity.supplier : ''],
      model: [entity ? entity.model : ''],
      warrantyPeriod: [entity ? entity.version : ''],
      version: [entity ? entity.version : ''],
      standardPropertyList: this.fb.array(standardPropControls),
      propertyList: this.fb.array(propertyListControls),
      groupList: this.fb.array(groupListControls),
      componentList: this.fb.array(compControls)
    });
  }

  updateForm(entity: ProdDevice) {
    const { standardPropControls, propertyListControls, groupListControls, compControls } = this.generateFromArray(entity);
    this.entityForm.patchValue(entity);
    this.entityForm.controls.standardPropertyList = this.fb.array(standardPropControls);
    this.entityForm.controls.propertyList = this.fb.array(propertyListControls);
    this.entityForm.controls.groupList = this.fb.array(groupListControls);
    this.entityForm.controls.componentList = this.fb.array(compControls);
    this.setMapOfExpandedComp();
    this.entityForm.updateValueAndValidity();
  }

  generateFromArray(entity: ProdDevice): { [key: string]: Array<AbstractControl> } {
    const standardPropControls: Array<AbstractControl> = [];
    if (entity && entity.standardPropertyList && entity.standardPropertyList.length > 0) {
      for (const property of entity.standardPropertyList) {
        standardPropControls.push(this.createDeviceDataControl(property));
      }
    }
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
    const compControls: Array<AbstractControl> = [];
    if (entity && entity.componentList && entity.componentList.length > 0) {
      for (const comp of entity.componentList) {
        compControls.push(this.createCompListControl(comp));
      }
    }
    return { standardPropControls, propertyListControls, groupListControls, compControls }
  }

  onDeviceDicChange(dictDeviceId: string) {
    if (dictDeviceId) {
      this.deviceDictionaryService.getDeviceDictionary(dictDeviceId).subscribe(deviceDictInfo => {
        const { comment, picture, fileName, type, supplier, model, warrantyPeriod, version, standardPropertyList, propertyList, groupList, componentList } = deviceDictInfo;
        this.updateForm({ comment, picture, fileName, type, supplier, model, warrantyPeriod, version, standardPropertyList, propertyList, groupList, componentList });
        this.stopExpandPropagation();
      });
    } else {
      this.updateForm({
        comment: '',
        picture: '',
        fileName: '',
        type: '',
        supplier: '',
        model: '',
        warrantyPeriod: '',
        version: '',
        standardPropertyList: [],
        propertyList: [],
        groupList: [],
        componentList: []
      });
      this.stopExpandPropagation();
    }
    this.entityForm.clearValidators();
  }

  stopExpandPropagation() {
    setTimeout(() => {
      document.querySelectorAll('.ant-table-row-expand-icon').forEach(el => {
        el.removeEventListener('click', ($event: Event) => {$event.stopPropagation()});
        el.addEventListener('click', ($event: Event) => {$event.stopPropagation()});
      });
    });
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

  /**
   * @description 标准能耗相关方法
   */
   standardPropFormArray(): FormArray {
    return this.entityForm.get('standardPropertyList') as FormArray;
  }

  /**
   * @description 设备参数相关方法
   */
  deviceDataGroupFormArray(): FormArray {
    return this.entityForm.get('groupList') as FormArray;
  }
  deviceDataFormArray(groupIndex: number): FormArray {
    return this.deviceDataGroupFormArray().controls[groupIndex].get('groupPropertyList') as FormArray;
  }
  createDeviceDataControl(data?: DeviceData): AbstractControl {
    return this.fb.group({
      name: [data ? data.name : ''],
      content: [data ? data.content: ''],
      title: [data ? data.title : ''],
      dictDataId: [{
        value: data && data.dictDataId ? data.dictDataId : '',
        disabled: true
      }]
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
      name: [dataGroup ? dataGroup.name : '', Validators.required],
      groupPropertyList: this.fb.array(controls),
      isEdit: [false]
    });
  }

  /**
   * @description 部件信息相关
   */
  compListFormArray() {
    return this.entityForm.get('componentList') as FormArray;
  }
  createCompListControl(comp?: DeviceComp): AbstractControl {
    const compControls: Array<AbstractControl> = [];
    if (comp && comp.componentList && comp.componentList.length > 0) {
      for (const subComp of comp.componentList) {
        compControls.push(this.createCompListControl(subComp));
      }
    }
    const propertyListControls: Array<AbstractControl> = [];
    if (comp && comp.propertyList && comp.propertyList.length > 0) {
      for (const property of comp.propertyList) {
        propertyListControls.push(this.createDeviceDataControl(property));
      }
    }
    const control: AbstractControl =  this.fb.group({
      code: [comp.code ? comp.code : guid()],
      comment: [comp ? comp.comment: ''],
      componentList: this.fb.array(compControls),
      dictDeviceId: [comp ? comp.dictDeviceId: ''],
      icon: [comp ? comp.icon: ''],
      id: [comp ? comp.id: ''],
      model: [comp ? comp.model: ''],
      name: [comp ? comp.name: ''],
      parentId: [comp ? comp.parentId: ''],
      picture: [comp ? comp.picture: ''],
      supplier: [comp ? comp.supplier: ''],
      type: [comp ? comp.type: ''],
      version: [comp ? comp.version: ''],
      warrantyPeriod: [comp ? comp.warrantyPeriod: ''],
      propertyList: this.fb.array(propertyListControls)
    });
    this.mapOfCompControl[control.get('code').value] = control;
    return control;
  }
  convertTreeToList(root: DeviceComp): DeviceCompTreeNode[] {
    const stack: DeviceCompTreeNode[] = [];
    const array: DeviceCompTreeNode[] = [];
    const hashMap = {};
    stack.push({ ...root, level: 0, expand: this.expandedCompCode.includes(root.code) });
    while (stack.length !== 0) {
      const node = stack.pop()!;
      if (!hashMap[node.code]) {
        hashMap[node.code] = true;
        array.push(node);
      }
      if (node.componentList) {
        for (let i = node.componentList.length - 1; i >= 0; i--) {
          stack.push({
            ...node.componentList[i],
            level: node.level! + 1,
            expand: this.expandedCompCode.includes(node.componentList[i].code),
            parent: node
          });
        }
      }
    }

    return array;
  }
  setMapOfExpandedComp() {
    const map: { [code: string]: DeviceCompTreeNode[] } = {};
    this.compListFormArray().getRawValue().forEach((item: DeviceComp) => {
      map[item.code] = this.convertTreeToList(item);
    });
    this.mapOfExpandedComp = map;
  }
  collapse(array: Array<DeviceCompTreeNode>, data: DeviceCompTreeNode, $event: boolean) {
    if (!$event) {
      this.expandedCompCode = this.expandedCompCode.filter(code => code !== data.code);
      if (data.componentList) {
        data.componentList.forEach(d => {
          const target = array.find(a => a.code === d.code)!;
          target.expand = false;
          this.collapse(array, target, false);
        });
      } else {
        return;
      }
    } else {
      this.expandedCompCode.push(data.code);
      this.stopExpandPropagation();
    }
  }
  viewDeviceComp(comp: DeviceCompTreeNode) {
    this.dialog.open<DeviceCompFormComponent, DeviceCompDialogData, DeviceComp>(DeviceCompFormComponent, {
      disableClose: true,
      panelClass: ['tb-dialog', 'tb-fullscreen-dialog'],
      data: {
        compInfo: { ...comp, isView: true },
        dataDictionaries: this.dataDictionaries
      }
    });
  }

}
