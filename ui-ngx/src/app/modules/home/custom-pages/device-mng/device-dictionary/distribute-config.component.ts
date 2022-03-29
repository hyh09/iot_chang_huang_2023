import { SelectionModel } from '@angular/cdk/collections';
import { Component, Inject, OnInit } from '@angular/core';
import { AbstractControl, FormArray, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MatDialog, MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { Router } from '@angular/router';
import { DeviceDictionaryService } from '@app/core/http/custom/device-dictionary.service';
import { ActionNotificationShow } from '@app/core/notification/notification.actions';
import { AppState, guid } from '@app/core/public-api';
import { EntityTableColumn, EntityTableConfig } from '@app/modules/home/models/entity/entities-table-config.models';
import { DictDevice, DriverConfig, ProtocolType } from '@app/shared/models/custom/device-mng.models';
import { DialogComponent, EntityType, entityTypeResources, entityTypeTranslations } from '@app/shared/public-api';
import { Store } from '@ngrx/store';
import { TranslateService } from '@ngx-translate/core';
import { DictDeviceTableFilterComponent } from './dict-device-table-filter.component';
import { DeviceConfigFormComponent, DriverConfigDialogData } from './driver-config-form.component';

export interface DistributeConfigDialogData {
  deviceDictId: string;
  deviceDictName: string;
}

@Component({
  selector: 'tb-distribute-config',
  templateUrl: './distribute-config.component.html',
  styleUrls: ['./distribute-config.component.scss']
})
export class DistributeConfigComponent extends DialogComponent<DistributeConfigComponent, string> implements OnInit {

  public readonly deviceTableConfig: EntityTableConfig<DictDevice> = new EntityTableConfig<DictDevice>();

  deviceForm: FormGroup;
  form: FormGroup;

  configTableColumns = ['select', 'pointName', 'description', 'category', 'dataType', 'registerType', 'registerAddress',
  'length', 'operate', 'readWrite', 'reverse', 'littleEndian', 'actions'];
  selection = new SelectionModel<DriverConfig>(true, []);

  protocolType = ProtocolType;

  constructor(
    protected store: Store<AppState>,
    protected router: Router,
    public dialogRef: MatDialogRef<DistributeConfigComponent, string>,
    protected fb: FormBuilder,
    @Inject(MAT_DIALOG_DATA) public deviceDictInfo: DistributeConfigDialogData,
    private deviceDictionaryService: DeviceDictionaryService,
    public dialog: MatDialog,
    private translate: TranslateService
  ) {
    super(store, router, dialogRef);

    this.deviceTableConfig.entityType = EntityType.DEVICE;
    this.deviceTableConfig.filterComponent = DictDeviceTableFilterComponent;
    this.deviceTableConfig.entityTranslations = entityTypeTranslations.get(EntityType.DEVICE);
    this.deviceTableConfig.entityResources = entityTypeResources.get(EntityType.DEVICE);

    this.deviceTableConfig.addEnabled = false;
    this.deviceTableConfig.searchEnabled = false;
    this.deviceTableConfig.refreshEnabled = false;
    this.deviceTableConfig.detailsPanelEnabled = false;
    this.deviceTableConfig.padding = '0';
    this.deviceTableConfig.titleVisible = false;
    this.deviceTableConfig.groupActionEnabled = false;
    this.deviceTableConfig.entitiesDeleteEnabled = false;
    this.deviceTableConfig.selectionAlwaysEnabled = true;
    this.deviceTableConfig.displayPagination = false;

    this.deviceTableConfig.componentsData = {
      factoryName: '',
      workshopName: '',
      productionLineName: '',
      deviceName: '',
      gatewayName: ''
    };

    this.deviceTableConfig.columns.push(
      new EntityTableColumn<DictDevice>('name', 'device-mng.device-name', '20%'),
      new EntityTableColumn<DictDevice>('factoryName', 'system-mng.factory-belong', '20%'),
      new EntityTableColumn<DictDevice>('workshopName', 'system-mng.work-shop-belong', '20%'),
      new EntityTableColumn<DictDevice>('productionLineName', 'system-mng.prod-line-belong', '20%'),
      new EntityTableColumn<DictDevice>('gatewayName', 'gateway.gateway-belong', '20%')
    );

    this.deviceTableConfig.entitiesFetchFunction = () => this.deviceDictionaryService.getDictDevices({
      dictDeviceId: this.deviceDictInfo.deviceDictId,
      ...this.deviceTableConfig.componentsData
    });
  }

  ngOnInit() {
    this.buildForm();
  }

  buildForm() {
    this.deviceForm = this.fb.group({
      deviceList: [[], Validators.required]
    });
    const driverConfigListControls: Array<AbstractControl> = [];
    this.form = this.fb.group({
      deviceList: [[], Validators.required],
      type: ['', Validators.required],
      driverVersion: ['', Validators.required],
      driverConfigList: this.fb.array(driverConfigListControls, Validators.required)
    });
    this.deviceDictionaryService.getDeviceDictPros(this.deviceDictInfo.deviceDictId).subscribe(res => {
      res.forEach(prop => {
        const { name: pointName, title: description, type: category } = prop;
        this.driverConfigFormArray().push(this.createdriverConfigListControl({ pointName, description, category }));
      });
      this.form.updateValueAndValidity();
    });
  }

  isAllSelected() {
    const numSelected = this.selection.selected.length;
    const numRows = this.driverConfigFormArray().value.length;
    return numSelected === numRows;
  }

  masterToggle() {
    if (this.isAllSelected()) {
      this.selection.clear();
      return;
    }

    this.selection.select(...this.driverConfigFormArray().value);
  }

  driverConfigFormArray(): FormArray {
    return this.form.get('driverConfigList') as FormArray;
  }

  createdriverConfigListControl(data?: DriverConfig): AbstractControl {
    return this.fb.group({
      pointName: [data && data.pointName ? data.pointName : '', Validators.required],
      description: [data && data.description ? data.description : '', Validators.required],
      category: [data && data.category ? data.category : '', Validators.required],
      dataType: [data && data.dataType ? data.dataType : '', Validators.required],
      registerType: [data && data.registerType ? data.registerType : '', Validators.required],
      registerAddress: [data && data.registerAddress ? data.registerAddress : '', Validators.required],
      length: [data && data.length ? data.length : '', Validators.required],
      operator: [data && data.operator ? data.operator : '', Validators.required],
      operationValue: [data && data.operationValue ? data.operationValue : '', Validators.required],
      readWrite: [data && data.readWrite ? data.readWrite : '', Validators.required],
      reverse: [data && data.reverse ? data.reverse : '', Validators.required],
      littleEndian: [data && data.littleEndian ? data.littleEndian : '', Validators.required]
    });
  }

  onSelectedDevicesChange(devices: DictDevice[]) {
    this.deviceForm.get('deviceList').patchValue(devices.map(item => ({ deviceName: item.name, gatewayId: item.gatewayId })));
  }

  addConfig() {
    this.dialog.open<DeviceConfigFormComponent, DriverConfigDialogData, DriverConfig>(DeviceConfigFormComponent, {
      disableClose: true,
      panelClass: ['tb-dialog', 'tb-fullscreen-dialog'],
      data: {
        existPointNames: (this.driverConfigFormArray().value as DriverConfig[]).map(item => (item.pointName))
      }
    }).afterClosed().subscribe(res => {
      if (res) {
        this.driverConfigFormArray().push(this.createdriverConfigListControl(res));
        this.form.updateValueAndValidity();
      }
    });
  }

  editConfig(config: DriverConfig, index: number) {
    this.dialog.open<DeviceConfigFormComponent, DriverConfigDialogData, DriverConfig>(DeviceConfigFormComponent, {
      disableClose: true,
      panelClass: ['tb-dialog', 'tb-fullscreen-dialog'],
      data: {
        config,
        isEdit: true,
        existPointNames: (this.driverConfigFormArray().value as DriverConfig[]).filter(item => (item.pointName !== config.pointName)).map(item => (item.pointName))
      }
    }).afterClosed().subscribe(res => {
      if (res) {
        this.driverConfigFormArray().controls[index].patchValue(res);
        this.form.updateValueAndValidity();
      }
    });
  }

  removeConfig(index: number) {
    this.driverConfigFormArray().removeAt(index);
    this.form.updateValueAndValidity();
  }

  batchRemoveConfig() {
    const arr: DriverConfig[] = [];
    const delPointNames = this.selection.selected.map(item => (item.pointName));
    (this.driverConfigFormArray().value as DriverConfig[]).forEach(item => {
      if (!delPointNames.includes(item.pointName)) {
        arr.push(item);
      }
    });
    this.driverConfigFormArray().clear();
    arr.forEach(item => {
      this.driverConfigFormArray().push(this.createdriverConfigListControl(item));
    });
    this.form.updateValueAndValidity();
    this.selection.clear();
  }

  cancel() {
    this.dialogRef.close(null);
  }

  save() {
    if (this.form.valid) {
      this.deviceDictionaryService.sendDriverConfig(this.form.value).subscribe(() => {
        this.dialogRef.close('success');
        this.store.dispatch(new ActionNotificationShow({
          message: this.translate.instant('device-mng.send-driver-config-success'),
          type: 'success',
          duration: 3000
        }));
      });
    }
  }

}
