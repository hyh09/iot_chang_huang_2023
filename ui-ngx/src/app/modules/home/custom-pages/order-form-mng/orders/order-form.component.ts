import { TranslateService } from '@ngx-translate/core';
import { Store } from '@ngrx/store';
import { ChangeDetectorRef, Component, Inject } from '@angular/core';
import { AppState } from '@app/core/core.state';
import { EntityComponent } from '@app/modules/home/components/entity/entity.component';
import { EntityTableConfig } from '@app/modules/home/models/entity/entities-table-config.models';
import { AbstractControl, FormArray, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { OrderDevice, OrderForm } from '@app/shared/models/custom/order-form-mng.models';
import { Factory, WorkShop, ProdLine } from '@app/shared/models/custom/factory-mng.models';
import { FactoryMngService } from '@app/core/public-api';
import { MatDialog } from '@angular/material/dialog';
import { OrderDeviceDialogData, OrderDeviceFormComponent } from './order-device-form.component';

@Component({
  selector: 'tb-order-form',
  templateUrl: './order-form.component.html'
})
export class OrderFormComponent extends EntityComponent<OrderForm> {

  factoryList: Factory[];
  allWorkShopList: WorkShop[];
  workShopList: WorkShop[];
  allProdLineList: ProdLine[];
  prodLineList: ProdLine[];

  prodPlanExpanded = true;

  constructor(
    protected store: Store<AppState>,
    protected translate: TranslateService,
    @Inject('entity') protected entityValue: OrderForm,
    @Inject('entitiesTableConfig') protected entitiesTableConfigValue: EntityTableConfig<OrderForm>,
    protected fb: FormBuilder,
    protected cd: ChangeDetectorRef,
    protected factoryMngService: FactoryMngService,
    private dialog: MatDialog
  ) {
    super(store, fb, entityValue, entitiesTableConfigValue, cd);
    this.fetchData();
  }

  buildForm(entity: OrderForm): FormGroup {
    const orderDeviceControls: Array<AbstractControl> = [];
    if (entity && entity.planDevices && entity.planDevices.length > 0) {
      for (const device of entity.planDevices) {
        orderDeviceControls.push(this.createOrderDeviceControl(device));
      }
    }
    const form = this.fb.group({
      id: [entity ? entity.id : ''],
      factoryId: [entity ? entity.factoryId : '', Validators.required],
      workshopId: [entity ? entity.workshopId : ''],
      productionLineId: [entity ? entity.productionLineId : ''],
      orderNo: [entity && entity.orderNo ? entity.orderNo : this.entitiesTableConfig.componentsData.availableOrderNo, Validators.required],
      contractNo: [entity ? entity.contractNo : ''],
      refOrderNo: [entity ? entity.refOrderNo : ''],
      takeTime: [entity ? entity.takeTime : ''],
      customerOrderNo: [entity ? entity.customerOrderNo : ''],
      customer: [entity ? entity.customer : ''],
      type: [entity ? entity.type : ''],
      bizPractice: [entity ? entity.bizPractice : ''],
      currency: [entity ? entity.currency : ''],
      exchangeRate: [entity ? entity.exchangeRate : ''],
      taxRate: [entity ? entity.taxRate : ''],
      taxes: [entity ? entity.taxes : ''],
      total: [entity ? entity.total : ''],
      totalAmount: [entity ? entity.totalAmount : ''],
      unit: [entity ? entity.unit : ''],
      unitPriceType: [entity ? entity.unitPriceType : ''],
      additionalAmount: [entity ? entity.additionalAmount : ''],
      paymentMethod: [entity ? entity.paymentMethod : ''],
      emergencyDegree: [entity ? entity.emergencyDegree : ''],
      technologicalRequirements: [entity ? entity.technologicalRequirements : ''],
      season: [entity ? entity.season : ''],
      num: [entity ? entity.num : ''],
      merchandiser: [entity ? entity.merchandiser : ''],
      salesman: [entity ? entity.salesman : ''],
      shortShipment: [entity ? entity.shortShipment : ''],
      overShipment: [entity ? entity.overShipment : ''],
      intendedTime: [entity ? entity.intendedTime : ''],
      standardTimeCosting: [entity ? entity.standardTimeCosting : ''],
      comment: [entity ? entity.comment : ''],
      planDevices: this.fb.array(orderDeviceControls)
    }); 
    form.get('factoryId').valueChanges.subscribe(newFactoryId => {
      form.get('workshopId').setValue('');
      this.workShopList = this.allWorkShopList.filter(item => (item.factoryId === newFactoryId));
      this.orderDeviceFormArray().clear();
      form.updateValueAndValidity();
    });
    form.get('workshopId').valueChanges.subscribe(newWorkshopId => {
      form.get('productionLineId').setValue('');
      this.prodLineList = this.allProdLineList.filter(item => (item.workshopId === newWorkshopId));
      this.orderDeviceFormArray().clear();
      form.updateValueAndValidity();
    });
    form.get('productionLineId').valueChanges.subscribe(() => {
      this.orderDeviceFormArray().clear();
      form.updateValueAndValidity();
    });
    return form;
  }

  orderDeviceFormArray(): FormArray {
    return this.entityForm.get('planDevices') as FormArray;
  }

  createOrderDeviceControl(planDevices?: OrderDevice): AbstractControl {
    return this.fb.group({
      deviceId: [planDevices ? planDevices.deviceId : ''],
      deviceName: [planDevices ? planDevices.deviceName : ''],
      enabled: [planDevices ? planDevices.enabled : false],
      intendedStartTime: [planDevices ? planDevices.intendedStartTime : ''],
      intendedEndTime: [planDevices ? planDevices.intendedEndTime : ''],
      actualStartTime: [planDevices ? planDevices.actualStartTime : ''],
      actualEndTime: [planDevices ? planDevices.actualEndTime : '']
    });
  }

  updateForm(entity: OrderForm) {
    this.entityForm.patchValue(entity);
    const orderDeviceControls: Array<AbstractControl> = [];
    if (entity && entity.planDevices && entity.planDevices.length > 0) {
      for (const device of entity.planDevices) {
        orderDeviceControls.push(this.createOrderDeviceControl(device));
      }
    }
    this.entityForm.controls.planDevices = this.fb.array(orderDeviceControls);
    this.entityForm.updateValueAndValidity();
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

  existDeviceIds(): string[] {
    return (this.orderDeviceFormArray().value as OrderDevice[]).map(item => (item.deviceId));
  }

  addOrderDevice(event: MouseEvent) {
    event.preventDefault();
    event.stopPropagation();
    const { factoryId, workshopId, productionLineId } = this.entityForm.value;
    this.factoryMngService.getDevices({ factoryId, workshopId, productionLineId }).subscribe(res => {
      this.dialog.open<OrderDeviceFormComponent, OrderDeviceDialogData, OrderDevice>(OrderDeviceFormComponent, {
        disableClose: true,
        panelClass: ['tb-dialog', 'tb-fullscreen-dialog'],
        data: {
          devices: res || [],
          existDeviceIds: this.existDeviceIds()
        }
      }).afterClosed().subscribe(res => {
        console.log(res)
        if (res) {
          this.orderDeviceFormArray().push(this.createOrderDeviceControl(res));
          this.cd.markForCheck();
          this.cd.detectChanges();
        }
      });
    });
  }

  editOrderDevice(event: MouseEvent, index: number) {
    event.preventDefault();
    event.stopPropagation();
    if (index < 0) return;
    const target = this.orderDeviceFormArray().controls[index];
    const { factoryId, workshopId, productionLineId } = this.entityForm.value;
    this.factoryMngService.getDevices({ factoryId, workshopId, productionLineId }).subscribe(res => {
      this.dialog.open<OrderDeviceFormComponent, OrderDeviceDialogData, OrderDevice>(OrderDeviceFormComponent, {
        disableClose: true,
        panelClass: ['tb-dialog', 'tb-fullscreen-dialog'],
        data: {
          planDevices: target.value,
          devices: res || [],
          existDeviceIds: this.existDeviceIds()
        }
      }).afterClosed().subscribe(res => {
        if (res) {
          target.patchValue(res);
          this.cd.markForCheck();
          this.cd.detectChanges();
        }
      });
    });
  }

}
