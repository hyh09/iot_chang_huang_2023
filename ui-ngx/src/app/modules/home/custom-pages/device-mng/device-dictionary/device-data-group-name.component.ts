import { Component, Inject, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, ValidatorFn, Validators, AbstractControl, ValidationErrors } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { Router } from '@angular/router';
import { AppState } from '@app/core/public-api';
import { DialogComponent } from '@app/shared/public-api';
import { Store } from '@ngrx/store';

export interface DeviceDataGroupNameDialogData {
  name: string;
  existNames: string[];
}

@Component({
  selector: 'tb-device-data-group-name',
  templateUrl: './device-data-group-name.component.html'
})
export class DeviceDataGroupNameComponent extends DialogComponent<DeviceDataGroupNameComponent, string> implements OnInit {

  form: FormGroup;
  isEdit: boolean;

  constructor(
    protected store: Store<AppState>,
    protected router: Router,
    public dialogRef: MatDialogRef<DeviceDataGroupNameComponent, string>,
    protected fb: FormBuilder,
    @Inject(MAT_DIALOG_DATA) public data: DeviceDataGroupNameDialogData
  ) {
    super(store, router, dialogRef);
    this.isEdit = !!(this.data && this.data.name);
  }

  ngOnInit() {
    this.buildForm();
  }

  buildForm() {
    this.form = this.fb.group({
      name: [this.data ? (this.data.name || '') : '', [Validators.required, this.checkIfNameDuplicate()]]
    });
  }

  checkIfNameDuplicate(): ValidatorFn {
    return (control: AbstractControl): ValidationErrors | null => {
      return this.data.existNames.includes(control.value) ? { 'duplicate': true } : null;
    }
  }

  cancel() {
    this.dialogRef.close(null);
  }

  save() {
    if (this.form.valid) {
      this.dialogRef.close(this.form.get('name').value);
    }
  }

}
