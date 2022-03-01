import { TranslateService } from '@ngx-translate/core';
import { Store } from '@ngrx/store';
import { ChangeDetectorRef, Component, Inject } from '@angular/core';
import { AppState } from '@app/core/core.state';
import { EntityComponent } from '@app/modules/home/components/entity/entity.component';
import { EntityTableConfig } from '@app/modules/home/models/entity/entities-table-config.models';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Chart, ChartProp } from '@app/shared/models/custom/chart-settings.model';
import { ChartSettingsService } from '@app/core/http/custom/chart-settings.service';

@Component({
  selector: 'tb-charts',
  templateUrl: './charts.component.html'
})
export class ChartsComponent extends EntityComponent<Chart> {

  properties: ChartProp[] = [];
  propertyMap: { [id: string]: ChartProp } = {};

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
    return this.fb.group({
      name: [entity ? entity.name : '', Validators.required],
      enable: [entity && (entity.enable === true || entity.enable === false) ? entity.enable : true],
      propertyIds: [entity ? (entity.properties || []).map(item => (item.id)) : [], Validators.minLength(1)],
      properties: [entity ? (entity.properties || []) : []]
    });
  }

  updateForm(entity: Chart) {
    this.entityForm.patchValue(entity);
    this.entityForm.get('propertyIds').patchValue(entity && entity.properties ? entity.properties.map(item => (item.id)) : []);
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

  onSelectionChange() {
    const arr = [];
    (this.entityForm.get('propertyIds').value as string[]).forEach(id => {
      id && this.propertyMap[id] && arr.push(this.propertyMap[id]);
    });
    this.entityForm.get('properties').patchValue(arr);
  }

}
