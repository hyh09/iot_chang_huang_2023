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
  templateUrl: './order-device-form.component.html',
  styleUrls: ['./order-device-form.component.scss']
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
    const { intendedStartTime, intendedEndTime, actualStartTime, actualEndTime, maintainStartTime, maintainEndTime } = (planDevices || {});
    this.orderDeviceForm = this.fb.group({
      id: [planDevices ? planDevices.id : ''],
      deviceId: [planDevices ? planDevices.deviceId : '', Validators.required],
      intendedCapacity: [planDevices ? planDevices.intendedCapacity : ''],
      actualCapacity: [planDevices ? planDevices.actualCapacity : ''],
      intendedTime: [intendedStartTime && intendedEndTime ? [intendedStartTime, intendedEndTime] : null],
      intendedStartTime: [planDevices ? planDevices.intendedStartTime : null],
      intendedEndTime: [planDevices ? planDevices.intendedEndTime : null],
      actualTime: [actualStartTime && actualEndTime ? [actualStartTime, actualEndTime] : null],
      actualStartTime: [planDevices ? planDevices.actualStartTime : null],
      actualEndTime: [planDevices ? planDevices.actualEndTime : null],
      maintainTime: [maintainStartTime && maintainEndTime ? [maintainStartTime, maintainEndTime] : null],
      maintainStartTime: [planDevices ? planDevices.maintainStartTime : null],
      maintainEndTime: [planDevices ? planDevices.maintainEndTime : null],
      enabled: [planDevices ? planDevices.enabled : false]
    });
    this.orderDeviceForm.get('intendedTime').valueChanges.subscribe(res => {
      const hasVal = res && res.length === 2;
      this.orderDeviceForm.patchValue({
        intendedStartTime: hasVal ? res[0] : null,
        intendedEndTime: hasVal ? res[1] : null
      });
    });
    this.orderDeviceForm.get('actualTime').valueChanges.subscribe(res => {
      const hasVal = res && res.length === 2;
      this.orderDeviceForm.patchValue({
        actualStartTime: hasVal ? res[0] : null,
        actualEndTime: hasVal ? res[1] : null
      });
    });
    this.orderDeviceForm.get('maintainTime').valueChanges.subscribe(res => {
      const hasVal = res && res.length === 2;
      this.orderDeviceForm.patchValue({
        maintainStartTime: hasVal ? res[0] : null,
        maintainEndTime: hasVal ? res[1] : null
      });
    });
    this.orderDeviceForm.get('deviceId').clearValidators();
  }

  cancel() {
    this.dialogRef.close(null);
  }

  save() {
    if (this.orderDeviceForm.valid) {
      const {
        id, deviceId, intendedCapacity, actualCapacity, intendedStartTime,
        intendedEndTime, actualStartTime, actualEndTime, enabled, maintainStartTime, maintainEndTime
      } = this.orderDeviceForm.value
      this.dialogRef.close({
        ...(this.data.planDevices || {}),
        id,
        deviceId,
        intendedCapacity,
        actualCapacity,
        deviceName: this.deviceMap[deviceId].name,
        intendedStartTime: intendedStartTime ? new Date(intendedStartTime).getTime() : null,
        intendedEndTime: intendedEndTime ? new Date(intendedEndTime).getTime() : null,
        actualStartTime: actualStartTime ? new Date(actualStartTime).getTime() : null,
        actualEndTime: actualEndTime ? new Date(actualEndTime).getTime() : null,
        maintainStartTime: maintainStartTime ? new Date(maintainStartTime).getTime() : null,
        maintainEndTime: maintainEndTime ? new Date(maintainEndTime).getTime() : null,
        enabled
      });
    }
  }

}
