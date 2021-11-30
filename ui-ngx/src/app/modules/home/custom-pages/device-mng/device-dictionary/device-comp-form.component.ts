import { T } from '@angular/cdk/keycodes';
import { Component, Inject, OnInit } from '@angular/core';
import { AbstractControl, FormArray, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { Router } from '@angular/router';
import { AppState } from '@app/core/core.state';
import { DeviceComp, DataDictionary, DeviceData } from '@app/shared/models/custom/device-mng.models';
import { DialogComponent } from '@app/shared/public-api';
import { Store } from '@ngrx/store';

export interface CompInfo extends DeviceComp {
  isEdit?: boolean;
}

export interface DeviceCompDialogData {
  compInfo?: CompInfo;
  dataDictionaries: DataDictionary[];
}

@Component({
  selector: 'tb-device-comp-form',
  templateUrl: './device-comp-form.component.html'
})
export class DeviceCompFormComponent extends DialogComponent<DeviceCompFormComponent, DeviceCompDialogData> implements OnInit {

  compForm: FormGroup;
  isEdit: boolean;
  compDataExpanded: boolean = true;
  dataDictionaries: DataDictionary[] = [];

  constructor(
    protected store: Store<AppState>,
    protected router: Router,
    public dialogRef: MatDialogRef<DeviceCompFormComponent, DeviceCompDialogData>,
    protected fb: FormBuilder,
    @Inject(MAT_DIALOG_DATA) protected data: DeviceCompDialogData
  ) {
    super(store, router, dialogRef);
  }

  ngOnInit() {
    this.isEdit = this.data.compInfo ? this.data.compInfo.isEdit : false;
    this.dataDictionaries = this.data.dataDictionaries;
    this.buildForm();
  }

  buildForm() {
    const compInfo = this.data.compInfo;
    const propertyListControls: Array<AbstractControl> = [];
    if (compInfo && compInfo.propertyList && compInfo.propertyList.length > 0) {
      for (const property of compInfo.propertyList) {
        propertyListControls.push(this.createPropertyListControl(property));
      }
    }
    this.compForm = this.fb.group({
      name: [compInfo ? compInfo.name : '', Validators.required],
      type: [compInfo ? compInfo.type : ''],
      supplier: [compInfo ? compInfo.supplier : ''],
      model: [compInfo ? compInfo.model : ''],
      version: [compInfo ? compInfo.version : ''],
      warrantyPeriod: [compInfo ? compInfo.warrantyPeriod : ''],
      comment: [compInfo ? compInfo.comment : ''],
      picture: [compInfo ? compInfo.picture : ''],
      propertyList: this.fb.array(propertyListControls)
    });
    this.compForm.markAsDirty();
  }

  cancel() {
    this.dialogRef.close(null);
  }

  save() {
    if (this.compForm.valid) {
      this.dialogRef.close({
        ...(this.data.compInfo || {}),
        ...this.compForm.value
      });
    }
  }

  /**
   * @description 部件参数相关方法
   */
  compDataFormArray(): FormArray {
    return this.compForm.get('propertyList') as FormArray;
  }
  createPropertyListControl(data?: DeviceData): AbstractControl {
    return this.fb.group({
      name: [data ? data.name : '', Validators.required],
      content: [data ? data.content : '', Validators.required],
      title: [data ? data.title : ''],
      dictDataId: [data ? data.dictDataId : null]
    });
  }
  addCompData(event: MouseEvent) {
    event.stopPropagation();
    event.preventDefault();
    this.compDataFormArray().push(this.createPropertyListControl());
    this.compForm.updateValueAndValidity();
  }
  removeCompData(index: number) {
    this.compDataFormArray().removeAt(index);
    this.compForm.updateValueAndValidity();
  }

}
