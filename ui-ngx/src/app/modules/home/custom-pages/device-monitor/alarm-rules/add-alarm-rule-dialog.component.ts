///
/// Copyright © 2016-2021 The Thingsboard Authors
///
/// Licensed under the Apache License, Version 2.0 (the "License");
/// you may not use this file except in compliance with the License.
/// You may obtain a copy of the License at
///
///     http://www.apache.org/licenses/LICENSE-2.0
///
/// Unless required by applicable law or agreed to in writing, software
/// distributed under the License is distributed on an "AS IS" BASIS,
/// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
/// See the License for the specific language governing permissions and
/// limitations under the License.
///

import {
  AfterViewInit,
  Component,
  ComponentFactoryResolver,
  Inject,
  Injector,
  SkipSelf,
  ViewChild
} from '@angular/core';
import { ErrorStateMatcher } from '@angular/material/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { Store } from '@ngrx/store';
import { AppState } from '@core/core.state';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { DialogComponent } from '@shared/components/dialog.component';
import { Router } from '@angular/router';
import {
  createDeviceProfileConfiguration,
  createDeviceProfileTransportConfiguration,
  DeviceProfile,
  DeviceProfileType,
  deviceProfileTypeTranslationMap,
  DeviceProvisionConfiguration,
  DeviceProvisionType,
  DeviceTransportType,
  deviceTransportTypeHintMap,
  deviceTransportTypeTranslationMap
} from '@shared/models/device.models';
import { DeviceProfileService } from '@core/http/device-profile.service';
import { EntityType } from '@shared/models/entity-type.models';
import { MatHorizontalStepper } from '@angular/material/stepper';
import { RuleChainId } from '@shared/models/id/rule-chain-id';
import { StepperSelectionEvent } from '@angular/cdk/stepper';
import { deepTrim } from '@core/utils';
import { ServiceType } from '@shared/models/queue.models';
import { DashboardId } from '@shared/models/id/dashboard-id';
import { AlarmRuleInfo } from '@app/shared/models/custom/device-monitor.models';
import { AlarmRuleService } from '@app/core/http/custom/alarm-rule.service';
import { DeviceDictionary } from '@app/shared/models/custom/device-mng.models';
import { DeviceDictionaryService } from '@app/core/http/custom/device-dictionary.service';

export interface AddAlarmRuleDialogData {
  deviceProfileName: string;
  transportType: DeviceTransportType;
}

@Component({
  selector: 'tb-add-alarm-rule-dialog',
  templateUrl: './add-alarm-rule-dialog.component.html',
  styleUrls: ['./add-alarm-rule-dialog.component.scss']
})
export class AddAlarmRuleDialogComponent extends
  DialogComponent<AddAlarmRuleDialogComponent, AlarmRuleInfo> implements AfterViewInit {

  @ViewChild('addAlarmRuleStepper', {static: true}) addAlarmRuleStepper: MatHorizontalStepper;

  selectedIndex = 0;

  showNext = true;

  entityType = EntityType;

  deviceProfileTypes = Object.values(DeviceProfileType);

  deviceProfileTypeTranslations = deviceProfileTypeTranslationMap;

  deviceTransportTypeHints = deviceTransportTypeHintMap;

  deviceTransportTypes = Object.values(DeviceTransportType);

  deviceTransportTypeTranslations = deviceTransportTypeTranslationMap;

  alarmRuleDetailsFormGroup: FormGroup;

  transportConfigFormGroup: FormGroup;

  alarmRulesFormGroup: FormGroup;

  provisionConfigFormGroup: FormGroup;

  serviceType = ServiceType.TB_RULE_ENGINE;

  deviceDictionaries: DeviceDictionary[] = [];
  deviceDictionariesMap: { [id: string]: DeviceDictionary } = {};

  constructor(protected store: Store<AppState>,
              protected router: Router,
              @Inject(MAT_DIALOG_DATA) public data: AddAlarmRuleDialogData,
              public dialogRef: MatDialogRef<AddAlarmRuleDialogComponent, AlarmRuleInfo>,
              private componentFactoryResolver: ComponentFactoryResolver,
              private injector: Injector,
              @SkipSelf() private errorStateMatcher: ErrorStateMatcher,
              private alarmRuleService: AlarmRuleService,
              private deviceDictionaryService: DeviceDictionaryService,
              private fb: FormBuilder) {
    super(store, router, dialogRef);
    this.deviceDictionaryService.getAllDeviceDictionaries().subscribe(res => {
      this.deviceDictionaries = res || [];
      this.deviceDictionariesMap = {};
      this.deviceDictionaries.forEach(item => { this.deviceDictionariesMap[item.id + ''] = item });
    });
    this.alarmRuleDetailsFormGroup = this.fb.group(
      {
        name: [data.deviceProfileName, [Validators.required]],
        type: [DeviceProfileType.DEFAULT, [Validators.required]],
        image: [null, []],
        defaultRuleChainId: [null, []],
        defaultDashboardId: [null, []],
        defaultQueueName: ['', []],
        description: ['', []],
        dictDeviceIdList: [[]]
      }
    );
    this.transportConfigFormGroup = this.fb.group(
      {
        transportType: [data.transportType ? data.transportType : DeviceTransportType.DEFAULT, [Validators.required]],
        transportConfiguration: [createDeviceProfileTransportConfiguration(DeviceTransportType.DEFAULT),
          [Validators.required]]
      }
    );
    this.transportConfigFormGroup.get('transportType').valueChanges.subscribe(() => {
      this.deviceProfileTransportTypeChanged();
    });

    this.alarmRulesFormGroup = this.fb.group(
      {
        alarms: [null]
      }
    );

    this.provisionConfigFormGroup = this.fb.group(
      {
        provisionConfiguration: [{
          type: DeviceProvisionType.DISABLED
        } as DeviceProvisionConfiguration, [Validators.required]]
      }
    );
  }

  private deviceProfileTransportTypeChanged() {
    const deviceTransportType: DeviceTransportType = this.transportConfigFormGroup.get('transportType').value;
    this.transportConfigFormGroup.patchValue(
      {transportConfiguration: createDeviceProfileTransportConfiguration(deviceTransportType)});
  }

  ngAfterViewInit(): void {
  }

  cancel(): void {
    this.dialogRef.close(null);
  }

  previousStep() {
    this.addAlarmRuleStepper.previous();
  }

  nextStep() {
    if (this.selectedIndex < 1) {
      this.addAlarmRuleStepper.next();
    } else {
      this.add();
    }
  }

  selectedForm(): FormGroup {
    switch (this.selectedIndex) {
      case 0:
        return this.alarmRuleDetailsFormGroup;
      case 1:
        return this.alarmRulesFormGroup;
    }
  }

  add(): void {
    if (this.allValid()) {
      const deviceProvisionConfiguration: DeviceProvisionConfiguration = this.provisionConfigFormGroup.get('provisionConfiguration').value;
      const provisionDeviceKey = deviceProvisionConfiguration.provisionDeviceKey;
      delete deviceProvisionConfiguration.provisionDeviceKey;
      const alarmRuleInfo: AlarmRuleInfo = {
        name: this.alarmRuleDetailsFormGroup.get('name').value,
        type: this.alarmRuleDetailsFormGroup.get('type').value,
        image: this.alarmRuleDetailsFormGroup.get('image').value,
        transportType: this.transportConfigFormGroup.get('transportType').value,
        provisionType: deviceProvisionConfiguration.type,
        provisionDeviceKey,
        description: this.alarmRuleDetailsFormGroup.get('description').value,
        profileData: {
          configuration: createDeviceProfileConfiguration(DeviceProfileType.DEFAULT),
          transportConfiguration: this.transportConfigFormGroup.get('transportConfiguration').value,
          alarms: this.alarmRulesFormGroup.get('alarms').value,
          provisionConfiguration: deviceProvisionConfiguration
        },
        dictDeviceIdList: this.alarmRuleDetailsFormGroup.get('dictDeviceIdList').value
      };
      if (this.alarmRuleDetailsFormGroup.get('defaultRuleChainId').value) {
        alarmRuleInfo.defaultRuleChainId = new RuleChainId(this.alarmRuleDetailsFormGroup.get('defaultRuleChainId').value);
      }
      if (this.alarmRuleDetailsFormGroup.get('defaultDashboardId').value) {
        alarmRuleInfo.defaultDashboardId = new DashboardId(this.alarmRuleDetailsFormGroup.get('defaultDashboardId').value);
      }
      this.alarmRuleService.saveAlarmRule(deepTrim(alarmRuleInfo)).subscribe((savedAlarmRule) => {
        this.dialogRef.close(savedAlarmRule);
      });
    }
  }

  getFormLabel(index: number): string {
    switch (index) {
      case 0:
        return 'device-profile.device-profile-details';
      case 1:
        return 'device-profile.alarm-rules';
    }
  }

  changeStep($event: StepperSelectionEvent): void {
    this.selectedIndex = $event.selectedIndex;
    if (this.selectedIndex === this.maxStepperIndex) {
      this.showNext = false;
    } else {
      this.showNext = true;
    }
  }

  private get maxStepperIndex(): number {
    return this.addAlarmRuleStepper?._steps?.length - 1;
  }

  allValid(): boolean {
    return !this.addAlarmRuleStepper.steps.find((item, index) => {
      if (item.stepControl.invalid) {
        item.interacted = true;
        this.addAlarmRuleStepper.selectedIndex = index;
        return true;
      } else {
        return false;
      }
    });
  }
}
