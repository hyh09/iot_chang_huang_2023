import { ProdCalendar } from './../../../../../shared/models/custom/device-mng.models';
import { Component, Inject, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { Router } from '@angular/router';
import { AppState } from '@app/core/core.state';
import { ProdMngService } from '@app/core/http/custom/prod-mng.service';
import { ProdDevice } from '@app/shared/models/custom/factory-mng.models';
import { OrderDevice } from '@app/shared/models/custom/order-form-mng.models';
import { DialogComponent } from '@app/shared/public-api';
import { Store } from '@ngrx/store';
import { DisabledTimeFn, DisabledTimePartial } from 'ng-zorro-antd/date-picker';

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

  deviceCalendars: ProdCalendar[] = [];
  calendarTimeMap: { [date: string]: { startTime: Date; endTime: Date; }[] } = {};

  constructor(
    protected store: Store<AppState>,
    protected router: Router,
    public dialogRef: MatDialogRef<OrderDeviceFormComponent, OrderDevice>,
    protected fb: FormBuilder,
    @Inject(MAT_DIALOG_DATA) protected data: OrderDeviceDialogData,
    private prodMngService: ProdMngService
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
      if (hasVal) {
        (res[0] as Date).setSeconds(0);
        (res[0] as Date).setMilliseconds(0);
        (res[1] as Date).setSeconds(0);
        (res[1] as Date).setMilliseconds(0);
      }
      this.orderDeviceForm.patchValue({
        intendedStartTime: hasVal ? res[0] : null,
        intendedEndTime: hasVal ? res[1] : null
      });
    });
    this.orderDeviceForm.get('actualTime').valueChanges.subscribe(res => {
      const hasVal = res && res.length === 2;
      if (hasVal) {
        (res[0] as Date).setSeconds(0);
        (res[0] as Date).setMilliseconds(0);
        (res[1] as Date).setSeconds(0);
        (res[1] as Date).setMilliseconds(0);
      }
      this.orderDeviceForm.patchValue({
        actualStartTime: hasVal ? res[0] : null,
        actualEndTime: hasVal ? res[1] : null
      });
    });
    this.orderDeviceForm.get('maintainTime').valueChanges.subscribe(res => {
      const hasVal = res && res.length === 2;
      if (hasVal) {
        (res[0] as Date).setSeconds(0);
        (res[0] as Date).setMilliseconds(0);
        (res[1] as Date).setSeconds(0);
        (res[1] as Date).setMilliseconds(0);
      }
      this.orderDeviceForm.patchValue({
        maintainStartTime: hasVal ? res[0] : null,
        maintainEndTime: hasVal ? res[1] : null
      });
    });
    this.orderDeviceForm.get('deviceId').valueChanges.subscribe(res => {
      this.prodMngService.getAllProdCalendars(res).subscribe(calendars => {
        this.deviceCalendars = calendars;
        this.deviceCalendars.forEach(calendar => {
          const date = new Date(calendar.startTime);
          const year = date.getFullYear();
          const month = date.getMonth();
          const day = date.getDate();
          if (!this.calendarTimeMap[`${year}-${month}-${day}`]) {
            this.calendarTimeMap[`${year}-${month}-${day}`] = [];
          }
          this.calendarTimeMap[`${year}-${month}-${day}`].push({
            startTime: date,
            endTime: new Date(calendar.endTime)
          });
        });
      });
    });
  }

  disabledDate = (current: Date): boolean => {
    if (this.deviceCalendars.length > 0) {
      const currY = current.getFullYear();
      const currM = current.getMonth();
      const currD = current.getDate();
      return !this.deviceCalendars.some(item => {
        const date = new Date(item.startTime);
        const itemY = date.getFullYear();
        const itemM = date.getMonth();
        const itemD = date.getDate();
        return currY === itemY && currM === itemM && currD === itemD;
      });
    }
    return false;
  };

  range(start: number, end: number): number[] {
    const result: number[] = [];
    for (let i = start; i < end; i++) {
      result.push(i);
    }
    return result;
  }

  disabledRangeTime: DisabledTimeFn = (current: Date, type?: DisabledTimePartial) => {
    if (current) {
      const currY = current.getFullYear();
      const currM = current.getMonth();
      const currD = current.getDate();
      const targetCalendar = this.calendarTimeMap[`${currY}-${currM}-${currD}`];
      if (targetCalendar) {
        const hourRanges = targetCalendar.map(calendar => ({ start: calendar.startTime.getHours(), end: calendar.endTime.getHours() }));
        let hours = [];
        hourRanges.forEach(range => {
          hours.push(...this.range(range.start, range.end + 1));
        });
        
        const minRangeMap: { [h: number]: { start: number; end: number; }[] } = {};
        targetCalendar.forEach(calendar => {
          const startH = calendar.startTime.getHours();
          const endH = calendar.endTime.getHours();
          if (!minRangeMap[startH]) {
            minRangeMap[startH] = [];
          }
          minRangeMap[startH].push({ start: calendar.startTime.getMinutes(), end: endH === startH ? calendar.endTime.getMinutes() : 60 });

          if (!minRangeMap[endH]) {
            minRangeMap[endH] = [];
          }
          minRangeMap[endH].push({ start: endH === startH ? calendar.startTime.getMinutes() : 0, end: calendar.endTime.getMinutes() + 1 });
        });

        return {
          nzDisabledHours: () => hours.length > 0 ? this.range(0, 24).filter(hour => (!hours.includes(hour))) : [],
          nzDisabledMinutes: (h) => {
            let mins = [];
            if (minRangeMap[h]) {
              minRangeMap[h].forEach(range => {
                mins.push(...this.range(range.start, range.end));
              });
            }
            return mins.length > 0 ? this.range(0, 60).filter(min => (!mins.includes(min))) : [];
          },
          nzDisabledSeconds: () => []
        };
      }
      return {
        nzDisabledHours: () => [],
        nzDisabledMinutes: () => [],
        nzDisabledSeconds: () => []
      };
    }
    return {
      nzDisabledHours: () => [],
      nzDisabledMinutes: () => [],
      nzDisabledSeconds: () => []
    };
  };

  cancel() {
    this.dialogRef.close(null);
    this.deviceCalendars = [];
    this.calendarTimeMap = {};
  }

  save() {
    if (this.orderDeviceForm.valid) {
      const {
        id, deviceId, intendedCapacity, actualCapacity, intendedStartTime,
        intendedEndTime, actualStartTime, actualEndTime, enabled, maintainStartTime, maintainEndTime
      } = this.orderDeviceForm.getRawValue();
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
