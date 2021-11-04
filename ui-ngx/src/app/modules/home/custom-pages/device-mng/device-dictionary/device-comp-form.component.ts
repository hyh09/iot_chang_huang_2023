import { Component, Inject, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { Router } from '@angular/router';
import { AppState } from '@app/core/core.state';
import { DeviceComp } from '@app/shared/models/custom/device-mng.models';
import { DialogComponent } from '@app/shared/public-api';
import { Store } from '@ngrx/store';

export interface DeviceCompDialogData extends DeviceComp {
  isEdit?: boolean
}

@Component({
  selector: 'tb-device-comp-form',
  templateUrl: './device-comp-form.component.html'
})
export class DeviceCompFormComponent extends DialogComponent<DeviceCompFormComponent, DeviceCompDialogData> implements OnInit {

  public compForm: FormGroup;
  public isEdit: boolean;

  constructor(
    protected store: Store<AppState>,
    protected router: Router,
    public dialogRef: MatDialogRef<DeviceCompFormComponent, DeviceCompDialogData>,
    protected fb: FormBuilder,
    @Inject(MAT_DIALOG_DATA) protected compInfo: DeviceCompDialogData
  ) {
    super(store, router, dialogRef);
  }

  ngOnInit() {
    this.isEdit = this.compInfo ? this.compInfo.isEdit : false;
    this.buildForm();
  }

  buildForm() {
    this.compForm = this.fb.group({
      name: [this.compInfo ? this.compInfo.name : '', Validators.required],
      type: [this.compInfo ? this.compInfo.type : ''],
      supplier: [this.compInfo ? this.compInfo.supplier : ''],
      model: [this.compInfo ? this.compInfo.model : ''],
      version: [this.compInfo ? this.compInfo.version : ''],
      warrantyPeriod: [this.compInfo ? this.compInfo.warrantyPeriod : ''],
      comment: [this.compInfo ? this.compInfo.comment : ''],
      picture: [this.compInfo ? this.compInfo.picture : '']
    });
  }

  cancel() {
    this.dialogRef.close(null);
  }

  save() {
    if (this.compForm.valid) {
      this.dialogRef.close({
        ...(this.compInfo || {}),
        ...this.compForm.value
      });
    }
  }

}
