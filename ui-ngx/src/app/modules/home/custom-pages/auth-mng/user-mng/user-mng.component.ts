import { ChangeDetectorRef, Component, Inject } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { AppState } from '@app/core/core.state';
import { EntityComponent } from '@app/modules/home/components/entity/entity.component';
import { EntityTableConfig } from '@app/modules/home/models/entity/entities-table-config.models';
import { UserInfo } from '@app/shared/models/custom/auth-mng.models';
import { Store } from '@ngrx/store';
import { TranslateService } from '@ngx-translate/core';

@Component({
  selector: 'tb-user-mng',
  templateUrl: './user-mng.component.html'
})
export class UserMngComponent extends EntityComponent<UserInfo> {

  constructor(
    protected store: Store<AppState>,
    protected translate: TranslateService,
    @Inject('entity') protected entityValue: UserInfo,
    @Inject('entitiesTableConfig') protected entitiesTableConfigValue: EntityTableConfig<UserInfo>,
    protected fb: FormBuilder,
    protected cd: ChangeDetectorRef
  ) {
    super(store, fb, entityValue, entitiesTableConfigValue, cd);
  }

  buildForm(entity: UserInfo): FormGroup {
    return this.fb.group(
      {
        id: [entity && entity.id ? entity.id : null],
        userCode: [entity && entity.userCode ? entity.userCode : this.entitiesTableConfig.componentsData.availableCode, Validators.required],
        userName: [entity ? entity.userName : '', Validators.required],
        phoneNumber: [entity ? entity.phoneNumber : '', [Validators.required, Validators.pattern(/^(1)\d{10}$/)]],
        email: [entity ? entity.email : '', [Validators.required, Validators.email]],
        roleIds: [entity && entity.roleIds ? entity.roleIds : []],
        activeStatus: [entity && entity.activeStatus ? entity.activeStatus : '1']
      }
    );
  }

  updateForm(entity: UserInfo) {
    this.entityForm.patchValue(entity);
  }

}
