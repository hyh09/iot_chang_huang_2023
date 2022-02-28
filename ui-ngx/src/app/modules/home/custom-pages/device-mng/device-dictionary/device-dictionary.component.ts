import { ChangeDetectorRef, Component, Inject } from '@angular/core';
import { FormBuilder, FormGroup, Validators, AbstractControl, FormArray } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { AppState } from '@app/core/core.state';
import { UtilsService } from '@app/core/public-api';
import { guid } from '@app/core/utils';
import { EntityComponent } from '@app/modules/home/components/entity/entity.component';
import { EntityTableConfig } from '@app/modules/home/models/entity/entities-table-config.models';
import { DeviceDataGroup, DeviceDictionary, DeviceProperty, DeviceData, DeviceComp, DeviceCompTreeNode, DataDictionary } from '@app/shared/models/custom/device-mng.models';
import { Store } from '@ngrx/store';
import { TranslateService } from '@ngx-translate/core';
import { DeviceCompDialogData, DeviceCompFormComponent } from './device-comp-form.component';
import { DeviceDataGroupNameComponent, DeviceDataGroupNameDialogData } from './device-data-group-name.component';

@Component({
  selector: 'tb-device-dictionary',
  templateUrl: './device-dictionary.component.html',
  styleUrls: ['./device-dictionary.component.scss']
})
export class DeviceDictionaryComponent extends EntityComponent<DeviceDictionary> {

  customPropExpanded: boolean = true;
  groupDataExpanded: boolean = true;
  currentTabIndex: number = 0;
  compExpanded: boolean = true;
  mapOfExpandedComp: { [code: string]: DeviceCompTreeNode[] } = {};
  mapOfCompControl: { [code: string]: AbstractControl } = {};
  expandedCompCode: Array<string> = [];
  initDataGroup: DeviceDataGroup[] = [];
  initDataGroupNames: string[] = [];
  dataDictIds: string[] = [];

  constructor(
    protected store: Store<AppState>,
    protected translate: TranslateService,
    @Inject('entity') protected entityValue: DeviceDictionary,
    @Inject('entitiesTableConfig') protected entitiesTableConfigValue: EntityTableConfig<DeviceDictionary>,
    protected fb: FormBuilder,
    protected cd: ChangeDetectorRef,
    public dialog: MatDialog,
    public utils: UtilsService
  ) {
    super(store, fb, entityValue, entitiesTableConfigValue, cd);
  }

  buildForm(entity: DeviceDictionary): FormGroup {
    this.mapOfExpandedComp = {};
    this.mapOfCompControl = {};
    this.expandedCompCode = [];
    this.setInitDataGroup();
    const { standardPropControls, propertyListControls, groupListControls, compControls } = this.generateFromArray(entity);
    return this.fb.group({
      id:  [entity ? entity.id : ''],
      code: [entity && entity.code ? entity.code : this.entitiesTableConfig.componentsData.availableCode, Validators.required],
      name: [entity ? entity.name : '', Validators.required],
      type: [entity ? entity.type : ''],
      supplier: [entity ? entity.supplier : ''],
      model: [entity ? entity.model : ''],
      version: [entity ? entity.version : ''],
      warrantyPeriod: [entity ? entity.version : ''],
      ratedCapacity: [entity ? entity.ratedCapacity: ''],
      comment: [entity ? entity.comment : ''],
      picture: [{
        value: entity ? entity.picture : '',
        disabled: !this.isEdit
      }],
      deviceModel: [null],
      fileId: [entity ? entity.fileId : ''],
      fileName: [entity ? entity.fileName : ''],
      standardPropertyList: this.fb.array(standardPropControls),
      propertyList: this.fb.array(propertyListControls),
      groupList: this.fb.array(groupListControls),
      componentList: this.fb.array(compControls)
    });
  }

  updateForm(entity: DeviceDictionary) {
    this.dataDictIds = (this.entitiesTableConfig.componentsData.dataDictionaries as DataDictionary[]).map(item => (item.id + ''));
    this.setInitDataGroup();
    this.initDataGroup = [];
    const { standardPropControls, propertyListControls, groupListControls, compControls } = this.generateFromArray(entity);
    this.entityForm.patchValue(entity);
    this.entityForm.get('deviceModel').setValue(null);
    this.entityForm.controls.standardPropertyList = this.fb.array(standardPropControls);
    this.entityForm.controls.propertyList = this.fb.array(propertyListControls);
    this.entityForm.controls.groupList = this.fb.array(groupListControls);
    this.entityForm.controls.componentList = this.fb.array(compControls);
    this.setMapOfExpandedComp();
    this.entityForm.updateValueAndValidity();
    this.stopExpandPropagation();
  }

  stopExpandPropagation() {
    setTimeout(() => {
      document.querySelectorAll('.ant-table-row-expand-icon').forEach(el => {
        el.removeEventListener('click', ($event: Event) => {$event.stopPropagation()});
        el.addEventListener('click', ($event: Event) => {$event.stopPropagation()});
      });
    });
  }

  setInitDataGroup() {
    this.initDataGroup = this.entitiesTableConfig.componentsData.initDataGroup;
    this.initDataGroupNames = this.initDataGroup.map(item => (item.name));
    this.initDataGroup.forEach(group => (group.editable = false));
  }

  generateFromArray(entity: DeviceDictionary): { [key: string]: Array<AbstractControl> } {
    const standardPropControls: Array<AbstractControl> = [];
    if (!this.isEdit && this.initDataGroup && this.initDataGroup.length > 0) {
      for (const property of this.initDataGroup[0].groupPropertyList) {
        standardPropControls.push(this.createDeviceDataControl(property));
      }
    }
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
    if (!this.isEdit && this.initDataGroup && this.initDataGroup.length > 0) {
      for (const group of this.initDataGroup) {
        groupListControls.push(this.createGroupListControl(group));
      }
    }
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
      id: [data ? data.id : null],
      name: [data ? data.name : '', Validators.required],
      content: [data ? data.content: '', Validators.required],
      title: [data ? data.title : ''],
      dictDataId: [{
        value: data && data.dictDataId && this.dataDictIds.includes(data.dictDataId) ? data.dictDataId : '',
        disabled: !this.isEdit
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
      id: [dataGroup ? dataGroup.id : null],
      name: [dataGroup ? dataGroup.name : '', [Validators.required]],
      groupPropertyList: this.fb.array(controls),
      editable: [dataGroup && dataGroup.editable !== undefined ? dataGroup.editable : !this.initDataGroupNames.includes(dataGroup ? dataGroup.name : '')]
    });
  }
  addDeviceData(event: MouseEvent) {
    event.stopPropagation();
    event.preventDefault();
    this.deviceDataFormArray(this.currentTabIndex).push(this.createDeviceDataControl());
    this.entityForm.updateValueAndValidity();
  }
  removeDeviceData(index: number) {
    this.deviceDataFormArray(this.currentTabIndex).removeAt(index);
    this.entityForm.updateValueAndValidity();
  }
  addOrEditDeviceDataGroup(name?: string, event?: MouseEvent) {
    if (event) {
      event.stopPropagation();
      event.preventDefault();
    }
    let existNames = (this.entityForm.get('groupList').value as Array<DeviceDataGroup>).map(group => (group.name));
    if (name) {
      existNames = existNames.filter(item => (item !== name));
    }
    this.dialog.open<DeviceDataGroupNameComponent, DeviceDataGroupNameDialogData>(DeviceDataGroupNameComponent, {
      disableClose: true,
      panelClass: ['tb-dialog', 'tb-fullscreen-dialog'],
      data: {
        name: name || '',
        existNames
      }
    }).afterClosed().subscribe(res => {
      if (res) {
        if (!name) {
          this.deviceDataGroupFormArray().push(this.createGroupListControl({
            name: res || '',
            groupPropertyList: [],
            editable: true
          }));
          setTimeout(() => {
            this.currentTabIndex = this.deviceDataGroupFormArray().controls.length - 1;
            this.cd.markForCheck();
            this.cd.detectChanges();
          });
        } else {
          this.deviceDataGroupFormArray().controls[this.currentTabIndex].get('name').setValue(res);
          this.cd.markForCheck();
          this.cd.detectChanges();
        }
      }
    });
  }
  removeDeviceDataGroup(index: number) {
    this.deviceDataGroupFormArray().removeAt(index);
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
  addDeviceComp(event: MouseEvent, parentComp?: DeviceCompTreeNode) {
    event.stopPropagation();
    event.preventDefault();
    this.dialog.open<DeviceCompFormComponent, DeviceCompDialogData, DeviceComp>(DeviceCompFormComponent, {
      disableClose: true,
      panelClass: ['tb-dialog', 'tb-fullscreen-dialog'],
      data: {
        dataDictionaries: this.entitiesTableConfig.componentsData.dataDictionaries
      }
    }).afterClosed().subscribe(res => {
      if (res) {
        if (parentComp) {
          if (!parentComp.expand) {
            this.expandedCompCode.push(parentComp.code);
          }
          const target = this.mapOfCompControl[parentComp.code].get('componentList') as FormArray;
          target.push(this.createCompListControl(res));
        } else {
          this.compListFormArray().push(this.createCompListControl(res));
        }
        this.setMapOfExpandedComp();
        this.cd.markForCheck();
        this.cd.detectChanges();
      }
    });
  }
  editDeviceComp(event: MouseEvent, comp: DeviceCompTreeNode) {
    event.stopPropagation();
    event.preventDefault();
    this.dialog.open<DeviceCompFormComponent, DeviceCompDialogData, DeviceComp>(DeviceCompFormComponent, {
      disableClose: true,
      panelClass: ['tb-dialog', 'tb-fullscreen-dialog'],
      data: {
        compInfo: { ...comp, isEdit: true },
        dataDictionaries: this.entitiesTableConfig.componentsData.dataDictionaries
      }
    }).afterClosed().subscribe(res => {
      if (res) {
        const target = this.mapOfCompControl[comp.code] as FormGroup;
        target.patchValue(res);
        const propertyListControls: Array<AbstractControl> = [];
        if (res && res.propertyList && res.propertyList.length > 0) {
          for (const property of res.propertyList) {
            propertyListControls.push(this.createDeviceDataControl(property));
          }
        }
        target.controls.propertyList = this.fb.array(propertyListControls);
        target.updateValueAndValidity();
        this.setMapOfExpandedComp();
        this.cd.markForCheck();
        this.cd.detectChanges();
      }
    });
  }
  viewDeviceComp(comp: DeviceCompTreeNode) {
    this.dialog.open<DeviceCompFormComponent, DeviceCompDialogData, DeviceComp>(DeviceCompFormComponent, {
      disableClose: true,
      panelClass: ['tb-dialog', 'tb-fullscreen-dialog'],
      data: {
        compInfo: { ...comp, isView: true },
        dataDictionaries: this.entitiesTableConfig.componentsData.dataDictionaries
      }
    });
  }
  deleteDeviceComp(event: MouseEvent, comp: DeviceCompTreeNode) {
    event.stopPropagation();
    event.preventDefault();
    let array: FormArray;
    if (comp.parent) {
      array = this.mapOfCompControl[comp.parent.code].get('componentList') as FormArray;
    } else {
      array = this.compListFormArray();
    }
    const index = array.controls.findIndex(control => control.get('code').value === comp.code);
    if (index >= 0) {
      array.removeAt(index);
      delete this.mapOfCompControl[comp.code];
      this.setMapOfExpandedComp();
    }
  }

}
