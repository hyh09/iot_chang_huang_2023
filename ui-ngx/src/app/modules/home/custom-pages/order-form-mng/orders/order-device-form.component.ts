import { Component, Inject, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { Router } from '@angular/router';
import { AppState } from '@app/core/core.state';
import { ProdDevice } from '@app/shared/models/custom/factory-mng.models';
import { OrderDevice } from '@app/shared/models/custom/order-form-mng.models';
import { DialogComponent } from '@app/shared/public-api';
import { Store } from '@ngrx/store';

export interface OrderDeviceDialogData {
  planDevices?: OrderDevice;
  devices: ProdDevice[];
  existDeviceIds?: string[];
}

@Component({
  selector: 'tb-order-device-form',
  templateUrl: './order-device-form.component.html'
})
export class OrderDeviceFormComponent extends DialogComponent<OrderDeviceFormComponent, OrderDevice> implements OnInit {

  orderDeviceForm: FormGroup;
  isEdit: boolean;
  devices: ProdDevice[] = [];
  deviceMap: { [deviceId: string]: ProdDevice } = {};
  existDeviceIds: string[] = [];

  constructor(
    protected store: Store<AppState>,
    protected router: Router,
    public dialogRef: MatDialogRef<OrderDeviceFormComponent, OrderDevice>,
    protected fb: FormBuilder,
    @Inject(MAT_DIALOG_DATA) protected data: OrderDeviceDialogData
  ) {
    super(store, router, dialogRef);
  }

  ngOnInit() {
    this.isEdit = !!this.data.planDevices;
    this.devices = this.data.devices || [];
    this.data.devices.forEach(device => {
      this.deviceMap[device.id + ''] = device;
    });
    this.existDeviceIds = this.data.existDeviceIds || [];
    this.buildForm();
  }

  buildForm() {
    const planDevices = this.data.planDevices;
    this.orderDeviceForm = this.fb.group({
      deviceId: [planDevices ? planDevices.deviceId : '', Validators.required],
      intendedStartTime: [planDevices ? planDevices.intendedStartTime : ''],
      intendedEndTime: [planDevices ? planDevices.intendedEndTime : ''],
      actualStartTime: [planDevices ? planDevices.actualStartTime : ''],
      actualEndTime: [planDevices ? planDevices.actualEndTime : ''],
      enabled: [planDevices ? planDevices.enabled : false]
    });
    this.orderDeviceForm.markAsDirty();
  }

  cancel() {
    this.dialogRef.close(null);
  }

  save() {
    if (this.orderDeviceForm.valid) {
      const { deviceId, intendedStartTime, intendedEndTime, actualStartTime, actualEndTime, enabled } = this.orderDeviceForm.value
      this.dialogRef.close({
        ...(this.data.planDevices || {}),
        deviceId,
        deviceName: this.deviceMap[deviceId].name,
        intendedStartTime: new Date(intendedStartTime).getTime(),
        intendedEndTime: new Date(intendedEndTime).getTime(),
        actualStartTime: new Date(actualStartTime).getTime(),
        actualEndTime: new Date(actualEndTime).getTime(),
        enabled
      });
    }
  }

}
