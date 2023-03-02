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

import { ChangeDetectorRef, Component, Inject, Input, Optional } from '@angular/core';
import { Store } from '@ngrx/store';
import { AppState } from '@core/core.state';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { TranslateService } from '@ngx-translate/core';
import { EntityTableConfig } from '@home/models/entity/entities-table-config.models';
import {
  createDeviceProfileConfiguration,
  createDeviceProfileTransportConfiguration,
  DeviceProfileData,
  DeviceProfileType,
  deviceProfileTypeConfigurationInfoMap,
  deviceProfileTypeTranslationMap,
  DeviceProvisionConfiguration,
  DeviceProvisionType,
  DeviceTransportType,
  deviceTransportTypeConfigurationInfoMap,
  deviceTransportTypeTranslationMap
} from '@shared/models/device.models';
import { EntityType } from '@shared/models/entity-type.models';
import { RuleChainId } from '@shared/models/id/rule-chain-id';
import { ServiceType } from '@shared/models/queue.models';
import { EntityId } from '@shared/models/id/entity-id';
import { OtaUpdateType } from '@shared/models/ota-package.models';
import { DashboardId } from '@shared/models/id/dashboard-id';
import { EntityComponent } from '@app/modules/home/components/entity/entity.component';
import { AlarmRuleInfo } from '@app/shared/models/custom/device-monitor.models';
import { DeviceDictionary } from '@app/shared/models/custom/device-mng.models';
import { DeviceDictionaryService } from '@app/core/http/custom/device-dictionary.service';

@Component({
  selector: 'tb-alarm-rules',
  templateUrl: './alarm-rules.component.html',
  styleUrls: ['./alarm-rules.component.scss']
})
export class AlarmRulesComponent extends EntityComponent<AlarmRuleInfo> {

  @Input()
  standalone = false;

  entityType = EntityType;

  deviceProfileTypes = Object.values(DeviceProfileType);

  deviceProfileTypeTranslations = deviceProfileTypeTranslationMap;

  deviceTransportTypes = Object.values(DeviceTransportType);

  deviceTransportTypeTranslations = deviceTransportTypeTranslationMap;

  displayProfileConfiguration: boolean;

  displayTransportConfiguration: boolean;

  serviceType = ServiceType.TB_RULE_ENGINE;

  deviceProfileId: EntityId;

  otaUpdateType = OtaUpdateType;

  deviceDictionaries: DeviceDictionary[] = [];
  deviceDictionariesMap: { [id: string]: DeviceDictionary } = {};

  constructor(protected store: Store<AppState>,
              protected translate: TranslateService,
              @Optional() @Inject('entity') protected entityValue: AlarmRuleInfo,
              @Optional() @Inject('entitiesTableConfig') protected entitiesTableConfigValue: EntityTableConfig<AlarmRuleInfo>,
              private deviceDictionaryService: DeviceDictionaryService,
              protected fb: FormBuilder,
              protected cd: ChangeDetectorRef) {
    super(store, fb, entityValue, entitiesTableConfigValue, cd);
    this.deviceDictionaryService.getAllDeviceDictionaries().subscribe(res => {
      this.deviceDictionaries = res || [];
      this.deviceDictionariesMap = {};
      this.deviceDictionaries.forEach(item => { this.deviceDictionariesMap[item.id + ''] = item });
    });
  }

  hideDelete() {
    if (this.entitiesTableConfig) {
      return !this.entitiesTableConfig.deleteEnabled(this.entity);
    } else {
      return false;
    }
  }

  buildForm(entity: AlarmRuleInfo): FormGroup {
    this.deviceProfileId = entity?.id ? entity.id : null;
    this.displayProfileConfiguration = entity && entity.type &&
      deviceProfileTypeConfigurationInfoMap.get(entity.type).hasProfileConfiguration;
    this.displayTransportConfiguration = entity && entity.transportType &&
      deviceTransportTypeConfigurationInfoMap.get(entity.transportType).hasProfileConfiguration;
    const deviceProvisionConfiguration: DeviceProvisionConfiguration = {
      type: entity?.provisionType ? entity.provisionType : DeviceProvisionType.DISABLED,
      provisionDeviceKey: entity?.provisionDeviceKey,
      provisionDeviceSecret: entity?.profileData?.provisionConfiguration?.provisionDeviceSecret
    };
    const form = this.fb.group(
      {
        name: [entity ? entity.name : '', [Validators.required]],
        type: [entity ? entity.type : null, [Validators.required]],
        image: [entity ? entity.image : null],
        transportType: [entity ? entity.transportType : null, [Validators.required]],
        profileData: this.fb.group({
          configuration: [entity && !this.isAdd ? entity.profileData?.configuration : {}, Validators.required],
          transportConfiguration: [entity && !this.isAdd ? entity.profileData?.transportConfiguration : {}, Validators.required],
          alarms: [entity && !this.isAdd ? entity.profileData?.alarms : []],
          provisionConfiguration: [deviceProvisionConfiguration, Validators.required]
        }),
        defaultRuleChainId: [entity && entity.defaultRuleChainId ? entity.defaultRuleChainId.id : null, []],
        defaultDashboardId: [entity && entity.defaultDashboardId ? entity.defaultDashboardId.id : null, []],
        defaultQueueName: [entity ? entity.defaultQueueName : '', []],
        firmwareId: [entity ? entity.firmwareId : null],
        softwareId: [entity ? entity.softwareId : null],
        description: [entity ? entity.description : '', []],
        // dictDeviceIdList: [entity ? (entity.dictDeviceIdList || []) : []]
      }
    );
    form.get('type').valueChanges.subscribe(() => {
      this.deviceProfileTypeChanged(form);
    });
    form.get('transportType').valueChanges.subscribe(() => {
      this.deviceProfileTransportTypeChanged(form);
    });
    this.checkIsNewDeviceProfile(entity, form);
    return form;
  }

  private checkIsNewDeviceProfile(entity: AlarmRuleInfo, form: FormGroup) {
    if (entity && !entity.id) {
      form.get('type').patchValue(DeviceProfileType.DEFAULT, {emitEvent: true});
      form.get('transportType').patchValue(DeviceTransportType.DEFAULT, {emitEvent: true});
      form.get('provisionType').patchValue(DeviceProvisionType.DISABLED, {emitEvent: true});
    }
  }

  private deviceProfileTypeChanged(form: FormGroup) {
    const deviceProfileType: DeviceProfileType = form.get('type').value;
    this.displayProfileConfiguration = deviceProfileType &&
      deviceProfileTypeConfigurationInfoMap.get(deviceProfileType).hasProfileConfiguration;
    let profileData: DeviceProfileData = form.getRawValue().profileData;
    if (!profileData) {
      profileData = {
        configuration: null,
        transportConfiguration: null
      };
    }
    profileData.configuration = createDeviceProfileConfiguration(deviceProfileType);
    form.patchValue({profileData});
  }

  private deviceProfileTransportTypeChanged(form: FormGroup) {
    const deviceTransportType: DeviceTransportType = form.get('transportType').value;
    this.displayTransportConfiguration = deviceTransportType &&
      deviceTransportTypeConfigurationInfoMap.get(deviceTransportType).hasProfileConfiguration;
    let profileData: DeviceProfileData = form.getRawValue().profileData;
    if (!profileData) {
      profileData = {
        configuration: null,
        transportConfiguration: null
      };
    }
    profileData.transportConfiguration = createDeviceProfileTransportConfiguration(deviceTransportType);
    form.patchValue({profileData});
  }

  updateForm(entity: AlarmRuleInfo) {
    this.deviceProfileId = entity.id;
    this.displayProfileConfiguration = entity.type &&
      deviceProfileTypeConfigurationInfoMap.get(entity.type).hasProfileConfiguration;
    this.displayTransportConfiguration = entity.transportType &&
      deviceTransportTypeConfigurationInfoMap.get(entity.transportType).hasProfileConfiguration;
    const deviceProvisionConfiguration: DeviceProvisionConfiguration = {
      type: entity?.provisionType ? entity.provisionType : DeviceProvisionType.DISABLED,
      provisionDeviceKey: entity?.provisionDeviceKey,
      provisionDeviceSecret: entity?.profileData?.provisionConfiguration?.provisionDeviceSecret
    };
    this.entityForm.patchValue({name: entity.name});
    this.entityForm.patchValue({type: entity.type}, {emitEvent: false});
    this.entityForm.patchValue({image: entity.image}, {emitEvent: false});
    this.entityForm.patchValue({transportType: entity.transportType}, {emitEvent: false});
    this.entityForm.patchValue({provisionType: entity.provisionType}, {emitEvent: false});
    this.entityForm.patchValue({provisionDeviceKey: entity.provisionDeviceKey}, {emitEvent: false});
    this.entityForm.patchValue({profileData: {
      configuration: entity.profileData?.configuration,
      transportConfiguration: entity.profileData?.transportConfiguration,
      alarms: entity.profileData?.alarms,
      provisionConfiguration: deviceProvisionConfiguration
    }}, {emitEvent: false});
    this.entityForm.patchValue({defaultRuleChainId: entity.defaultRuleChainId ? entity.defaultRuleChainId.id : null}, {emitEvent: false});
    this.entityForm.patchValue({defaultDashboardId: entity.defaultDashboardId ? entity.defaultDashboardId.id : null}, {emitEvent: false});
    this.entityForm.patchValue({defaultQueueName: entity.defaultQueueName}, {emitEvent: false});
    this.entityForm.patchValue({firmwareId: entity.firmwareId}, {emitEvent: false});
    this.entityForm.patchValue({softwareId: entity.softwareId}, {emitEvent: false});
    this.entityForm.patchValue({description: entity.description}, {emitEvent: false});
    // this.entityForm.patchValue({dictDeviceIdList: entity.dictDeviceIdList || []}, {emitEvent: false});
  }

  prepareFormValue(formValue: any): any {
    if (formValue.defaultRuleChainId) {
      formValue.defaultRuleChainId = new RuleChainId(formValue.defaultRuleChainId);
    }
    if (formValue.defaultDashboardId) {
      formValue.defaultDashboardId = new DashboardId(formValue.defaultDashboardId);
    }
    const deviceProvisionConfiguration: DeviceProvisionConfiguration = formValue.profileData.provisionConfiguration;
    formValue.provisionType = deviceProvisionConfiguration.type;
    formValue.provisionDeviceKey = deviceProvisionConfiguration.provisionDeviceKey;
    delete deviceProvisionConfiguration.provisionDeviceKey;
    return super.prepareFormValue(formValue);
  }

}
