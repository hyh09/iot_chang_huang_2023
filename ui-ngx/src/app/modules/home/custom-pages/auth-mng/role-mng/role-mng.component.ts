import { ChangeDetectorRef, Component, Inject } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { AppState } from '@app/core/core.state';
import { EntityComponent } from '@app/modules/home/components/entity/entity.component';
import { EntityTableConfig } from '@app/modules/home/models/entity/entities-table-config.models';
import { Role } from '@app/shared/models/custom/auth-mng.models';
import { Store } from '@ngrx/store';
import { TranslateService } from '@ngx-translate/core';

@Component({
  selector: 'tb-role-mng',
  templateUrl: './role-mng.component.html'
})
export class RoleMngComponent extends EntityComponent<Role> {

  constructor(
    protected store: Store<AppState>,
    protected translate: TranslateService,
    @Inject('entity') protected entityValue: Role,
    @Inject('entitiesTableConfig') protected entitiesTableConfigValue: EntityTableConfig<Role>,
    protected fb: FormBuilder,
    protected cd: ChangeDetectorRef
  ) {
    super(store, fb, entityValue, entitiesTableConfigValue, cd);
  }

  buildForm(entity: Role): FormGroup {
    return this.fb.group(
      {
        id: [entity && entity.id ? entity.id : null],
        roleCode: [entity && entity.roleCode ? entity.roleCode : this.entitiesTableConfig.componentsData.availableCode, Validators.required],
        roleName: [entity ? entity.roleName : '', Validators.required]
      }
    );
  }

  updateForm(entity: Role) {
    this.entityForm.patchValue(entity);
  }

}
