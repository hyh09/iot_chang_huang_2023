import { Component, Inject, OnInit } from '@angular/core';
import { AbstractControl, FormBuilder, FormGroup, ValidationErrors, ValidatorFn, Validators } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { Router } from '@angular/router';
import { AppState } from '@app/core/core.state';
import { DriverConfig, LittleBig, Operator, PointDataType, ReadWrite, RegisterType } from '@app/shared/models/custom/device-mng.models';
import { DialogComponent } from '@app/shared/public-api';
import { Store } from '@ngrx/store';

export interface DriverConfigDialogData {
  config?: DriverConfig;
  isEdit?: boolean;
  existPointNames: string[];
}

@Component({
  selector: 'tb-driver-config-form',
  templateUrl: './driver-config-form.component.html'
})
export class DeviceConfigFormComponent extends DialogComponent<DeviceConfigFormComponent, DriverConfig> implements OnInit {

  form: FormGroup;
  isEdit: boolean;

  dataType = PointDataType;
  registerType = RegisterType;
  operator = Operator;
  readWrite = ReadWrite;
  littleBig = LittleBig;

  constructor(
    protected store: Store<AppState>,
    protected router: Router,
    public dialogRef: MatDialogRef<DeviceConfigFormComponent, DriverConfig>,
    protected fb: FormBuilder,
    @Inject(MAT_DIALOG_DATA) protected data: DriverConfigDialogData
  ) {
    super(store, router, dialogRef);
    this.isEdit = !!this.data.isEdit;
  }

  ngOnInit() {
    this.buildForm();
  }

  buildForm() {
    const config = this.data.config;
    this.form = this.fb.group({
      pointName: [config && config.pointName ? config.pointName : '', [Validators.required, this.checkIfNameDuplicate()]],
      description: [config && config.description ? config.description : ''],
      category: [config && config.category ? config.category : ''],
      dataType: [config && config.dataType ? config.dataType : '', Validators.required],
      registerType: [config && config.registerType ? config.registerType : '', Validators.required],
      registerAddress: [config && config.registerAddress ? config.registerAddress : '', Validators.required],
      length: [config && config.length ? config.length : '', Validators.required],
      operator: [config && config.operator ? config.operator : '', Validators.required],
      operationValue: [config && config.operationValue ? config.operationValue : '', Validators.required],
      readWrite: [config && config.readWrite ? config.readWrite : '', Validators.required],
      reverse: [config && config.reverse ? config.reverse : '', Validators.required],
      littleEndian: [config && config.littleEndian ? config.littleEndian : '', Validators.required]
    });
    this.form.markAsDirty();
  }

  checkIfNameDuplicate(): ValidatorFn {
    return (control: AbstractControl): ValidationErrors | null => {
      return this.data.existPointNames.includes(control.value) ? { 'duplicate': true } : null;
    }
  }

  cancel() {
    this.dialogRef.close(null);
  }

  save() {
    if (this.form.valid) {
      this.dialogRef.close({
        ...(this.data.config || {}),
        ...this.form.value
      });
    }
  }

}
