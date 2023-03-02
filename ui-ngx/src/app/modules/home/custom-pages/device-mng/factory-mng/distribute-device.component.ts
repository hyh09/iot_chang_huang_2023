import { Component, Inject, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { Router } from '@angular/router';
import { FactoryMngService } from '@app/core/http/custom/factory-mng.service';
import { ActionNotificationShow } from '@app/core/notification/notification.actions';
import { AppState } from '@app/core/public-api';
import { Factory, ProdLine, WorkShop } from '@app/shared/models/custom/factory-mng.models';
import { DialogComponent } from '@app/shared/public-api';
import { Store } from '@ngrx/store';
import { TranslateService } from '@ngx-translate/core';

export interface DistributeDeviceDialogData {
  factoryOnly?: boolean;
  deviceIdList: string[];
}

@Component({
  selector: 'tb-distribute-device',
  templateUrl: './distribute-device.component.html'
})
export class DistributeDeviceComponent extends DialogComponent<DistributeDeviceComponent, string> implements OnInit {

  public form: FormGroup;
  public factoryList: Factory[];
  public allWorkShopList: WorkShop[];
  public workShopList: WorkShop[];
  public allProdLineList: ProdLine[];
  public prodLineList: ProdLine[];

  constructor(
    protected store: Store<AppState>,
    protected router: Router,
    public dialogRef: MatDialogRef<DistributeDeviceComponent, string>,
    protected fb: FormBuilder,
    protected factoryMngService: FactoryMngService,
    protected translate: TranslateService,
    @Inject(MAT_DIALOG_DATA) public data: DistributeDeviceDialogData
  ) {
    super(store, router, dialogRef);
  }

  ngOnInit() {
    this.fetchData();
    this.buildForm();
  }

  buildForm() {
    if (this.data.factoryOnly) {
      this.form = this.fb.group({
        factoryId: ['', Validators.required],
        workshopId: [''],
        productionLineId: ['']
      });
    } else {
      this.form = this.fb.group({
        factoryId: ['', Validators.required],
        workshopId: ['', Validators.required],
        productionLineId: ['', Validators.required]
      });
    }
    this.form.get('factoryId').valueChanges.subscribe(newFactoryId => {
      this.form.get('workshopId').setValue('');
      this.workShopList = this.allWorkShopList.filter(item => (item.factoryId === newFactoryId));
      this.form.clearValidators();
    });
    this.form.get('workshopId').valueChanges.subscribe(newWorkshopId => {
      this.form.get('productionLineId').setValue('');
      this.prodLineList = this.allProdLineList.filter(item => (item.workshopId === newWorkshopId));
      this.form.clearValidators();
    });
  }

  fetchData() {
    this.factoryMngService.getAllFactories().subscribe(res => {
      this.factoryList = res || [];
    });
    this.factoryMngService.getAllWorkShops().subscribe(res => {
      this.allWorkShopList = res || [];
    });
    this.factoryMngService.getAllProdLines().subscribe(res => {
      this.allProdLineList = res || [];
    });
  }

  cancel() {
    this.dialogRef.close(null);
  }

  save() {
    if (this.form.valid) {
      this.factoryMngService.distributeDevice({
        deviceIdList: this.data.deviceIdList,
        ...this.form.value
      }).subscribe(() => {
        this.dialogRef.close('success');
        this.store.dispatch(new ActionNotificationShow({
          message: this.translate.instant('device-mng.distribute-success'),
          type: 'success',
          duration: 3000
        }));
      });
    }
  }

}
