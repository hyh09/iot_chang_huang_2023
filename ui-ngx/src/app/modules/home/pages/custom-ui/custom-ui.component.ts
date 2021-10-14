import { AfterViewInit, Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup } from '@angular/forms';
import { environment as env } from '@env/environment';
import { TranslateService } from '@ngx-translate/core';
import { DashboardService } from '@core/http/dashboard.service';
import { UIInfo } from '@shared/models/dashboard.models';
import { Store } from '@ngrx/store';
import { AppState } from '@core/core.state';
import { HasDirtyFlag } from '@core/guards/confirm-on-exit.guard';
import { PageComponent } from '@shared/components/page.component';
import {ActionTenantUIChangeAll} from "@core/custom/tenant-ui.actions";
import {TenantUIState} from "@core/custom/tenant-ui.models";
import {initialState} from "@core/custom/tenant-ui.reducer";

@Component({
  selector: 'tb-custom-ui',
  templateUrl: './custom-ui.component.html',
  styleUrls: ['./custom-ui.component.scss']
})
export class CustomUiComponent extends PageComponent implements OnInit, HasDirtyFlag,AfterViewInit {
  isDirty = false;
  faviconMaxKBytes = 256000;
  logoMaxKBytes = 4096000;
  customUiFormGroup: FormGroup;
  initData: any;
  previousData: any;

  constructor(
    protected store: Store<AppState>,
    private translate: TranslateService,
    private dashboardService: DashboardService,
    private fb: FormBuilder
  ) {
    super(store);
    this.initForm();
    this.writeFormByHttp();
  }

  ngAfterViewInit() {
  }

  ngOnInit(): void {
    this.customUiFormGroup.valueChanges.subscribe(data => {
      Reflect.ownKeys(data).forEach(key => data[key.toString()] = data[key.toString()] === '' ? null : data[key.toString()]);
      if(JSON.stringify(this.initData) !== JSON.stringify(data)){
        this.isDirty = true;
        this.previousData = data;
        this.store.dispatch(new ActionTenantUIChangeAll(data));
      }else{
        this.isDirty = false;
        if(JSON.stringify(this.previousData) !== JSON.stringify(data)){
          this.store.dispatch(new ActionTenantUIChangeAll(data));
        }
      }
    });
  }

  writeFormByHttp() {
    this.dashboardService.getTenantUIInfo().subscribe(ui => {
      this.patchFormValue(ui);
      this.initData = this.customUiFormGroup.value;
      this.previousData = this.customUiFormGroup.value;
    });
  }

  patchFormValue(ui: UIInfo | TenantUIState) {
    this.customUiFormGroup.get('applicationTitle').patchValue(ui.applicationTitle);
    this.customUiFormGroup.get('iconImageUrl').patchValue(ui.iconImageUrl);
    this.customUiFormGroup.get('logoImageUrl').patchValue(ui.logoImageUrl);
    this.customUiFormGroup.get('logoImageHeight').patchValue(ui.logoImageHeight);
    this.customUiFormGroup.get('platformMainColor').patchValue(ui.platformMainColor);
    this.customUiFormGroup.get('platformTextMainColor').patchValue(ui.platformTextMainColor);
    this.customUiFormGroup.get('platformButtonColor').patchValue(ui.platformButtonColor);
    this.customUiFormGroup.get('platformMenuColorActive').patchValue(ui.platformMenuColorActive);
    this.customUiFormGroup.get('platformMenuColorHover').patchValue(ui.platformMenuColorHover);
    this.customUiFormGroup.get('showNameVersion').patchValue(ui.showNameVersion);
    this.customUiFormGroup.get('platformName').patchValue(ui.platformName);
    this.customUiFormGroup.get('platformVersion').patchValue(ui.platformVersion);
  }

  //恢复到tb原始设置
  reset($event: Event) {
    if ($event) {
      $event.stopPropagation();
    }
    this.patchFormValue(initialState);
    this.store.dispatch(new ActionTenantUIChangeAll(this.customUiFormGroup.value));
    this.isDirty = true;
  }
  //撤销本次操作
  cancel($event: Event) {
    if ($event) {
      $event.stopPropagation();
    }
    this.writeFormByHttp();
  }

  //初始化表单
  initForm() {
    this.customUiFormGroup = this.fb.group({
      applicationTitle: [null, []],
      iconImageUrl: [null, []],
      logoImageUrl: [null, []],
      logoImageHeight: [null, []],
      platformMainColor: [null, []],
      platformTextMainColor: [null, []],
      platformButtonColor: [null, []],
      platformMenuColorActive: [null, []],
      platformMenuColorHover: [null, []],
      showNameVersion: [false, []],
      platformName: [env.appTitle, []],
      platformVersion: [env.tbVersion, []]
    });
    this.initData = this.customUiFormGroup.value;
    this.previousData = this.customUiFormGroup.value;
  }

  submit($event: Event) {
    if ($event) {
      $event.stopPropagation();
    }
    this.dashboardService.saveTenantUIInfo(this.customUiFormGroup.value as UIInfo).subscribe(res => {
    });
    this.store.dispatch(new ActionTenantUIChangeAll(this.customUiFormGroup.value));
    this.isDirty = false;
  }

  formatSlider(value: number) {
    return value + 'px';
  }

  // advancedCssClick() {
  //
  // }
}
