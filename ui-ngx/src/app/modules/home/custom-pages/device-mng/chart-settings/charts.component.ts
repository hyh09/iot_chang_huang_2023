import { TranslateService } from '@ngx-translate/core';
import { Store } from '@ngrx/store';
import { ChangeDetectorRef, Component, Inject } from '@angular/core';
import { AppState } from '@app/core/core.state';
import { EntityComponent } from '@app/modules/home/components/entity/entity.component';
import { EntityTableConfig } from '@app/modules/home/models/entity/entities-table-config.models';
import { AbstractControl, FormArray, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Chart, ChartProp } from '@app/shared/models/custom/chart-settings.model';
import { ChartSettingsService } from '@app/core/http/custom/chart-settings.service';

@Component({
  selector: 'tb-charts',
  templateUrl: './charts.component.html'
})
export class ChartsComponent extends EntityComponent<Chart> {

  properties: ChartProp[] = [];
  propertyMap: { [id: string]: ChartProp } = {};
  propExpanded: boolean = true;

  constructor(
    protected store: Store<AppState>,
    protected translate: TranslateService,
    @Inject('entity') protected entityValue: Chart,
    @Inject('entitiesTableConfig') protected entitiesTableConfigValue: EntityTableConfig<Chart>,
    protected fb: FormBuilder,
    protected cd: ChangeDetectorRef,
    private chartSettingsService: ChartSettingsService
  ) {
    super(store, fb, entityValue, entitiesTableConfigValue, cd);
    this.loadChartProps();
  }

  buildForm(entity: Chart): FormGroup {
    const propertyControls: Array<AbstractControl> = [];
    if (entity && entity.properties && entity.properties.length > 0) {
      for (const property of entity.properties) {
        propertyControls.push(this.createPropertyControls(property));
      }
    }
    return this.fb.group({
      name: [entity ? entity.name : '', Validators.required],
      enable: [entity && (entity.enable === true || entity.enable === false) ? entity.enable : true],
      properties: this.fb.array(propertyControls)
    });
  }

  chartPropFormArray(): FormArray {
    return this.entityForm.get('properties') as FormArray;
  }
  createPropertyControls(property?: ChartProp): AbstractControl {
    return this.fb.group({
      id: [property ? property.id : '', Validators.required],
      name: [property ? property.name : ''],
      propertyType: [property ? property.propertyType : ''],
      title: [property ? property.title : ''],
      suffix: [property ? property.suffix: ''],
      unit: [property ? property.unit: '']
    });
  }
  addParam(event: MouseEvent) {
    event.stopPropagation();
    event.preventDefault();
    this.chartPropFormArray().push(this.createPropertyControls());
  }
  removeParam(index: number) {
    this.chartPropFormArray().removeAt(index);
  }

  updateForm(entity: Chart) {
    this.entityForm.patchValue(entity);
    const propertyControls: Array<AbstractControl> = [];
    if (entity && entity.properties && entity.properties.length > 0) {
      for (const property of entity.properties) {
        propertyControls.push(this.createPropertyControls(property));
      }
    }
    this.entityForm.controls.properties = this.fb.array(propertyControls);
    this.entityForm.updateValueAndValidity();
  }

  loadChartProps() {
    this.properties = [];
    this.propertyMap = {};
    this.chartSettingsService.getDeviceDictProps(this.entitiesTableConfig.componentsData.dictDeviceId).subscribe(res => {
      this.properties = res;
      this.properties.forEach(item => {
        this.propertyMap[item.id] = item;
      });
    });
  }

  onSelectionChange(id: string, index: number) {
    if (id && this.propertyMap[id]) {
      const control = this.chartPropFormArray().controls[index];
      control.patchValue(this.propertyMap[id]);
      control.updateValueAndValidity();
    }
  }

}
