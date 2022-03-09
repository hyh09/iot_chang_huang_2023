import { ChangeDetectorRef, Component, Inject } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { AppState } from '@app/core/core.state';
import { UtilsService } from '@app/core/services/utils.service';
import { EntityComponent } from '@app/modules/home/components/entity/entity.component';
import { EntityTableConfig } from '@app/modules/home/models/entity/entities-table-config.models';
import { ProdCalendar } from '@app/shared/models/custom/device-mng.models';
import { Store } from '@ngrx/store';
import { TranslateService } from '@ngx-translate/core';

@Component({
  selector: 'tb-mng-calendar',
  templateUrl: './mng-calendar.component.html',
  styleUrls: ['./mng-calendar.component.scss']
})
export class MngCalendarComponent extends EntityComponent<ProdCalendar> {

  canSetPermission = false;

  constructor(
    protected store: Store<AppState>,
    protected translate: TranslateService,
    @Inject('entity') protected entityValue: ProdCalendar,
    @Inject('entitiesTableConfig') protected entitiesTableConfigValue: EntityTableConfig<ProdCalendar>,
    protected fb: FormBuilder,
    protected cd: ChangeDetectorRef,
    public utils: UtilsService
  ) {
    super(store, fb, entityValue, entitiesTableConfigValue, cd);
  }

  buildForm(entity: ProdCalendar): FormGroup {
    const { startTime, endTime } = entity || {};
    const form = this.fb.group(
      {
        id: [entity && entity.id ? entity.id : null],
        factoryId: [entity ? entity.factoryId : this.entitiesTableConfig.componentsData.factoryId],
        deviceId: [entity ? entity.deviceId : this.entitiesTableConfig.componentsData.deviceId],
        startTime: [startTime || null, Validators.required],
        endTime: [endTime || null, Validators.required],
        shift: [startTime && endTime ? [new Date(startTime), new Date(endTime)] : null]
      }
    );
    form.get('shift').valueChanges.subscribe(res => {
      const hasVal = res && res.length === 2;
      form.patchValue({
        startTime: hasVal ? res[0].getTime() : null,
        endTime: hasVal ? res[1].getTime() : null
      });
    });
    return form;
  }

  updateForm(entity: ProdCalendar) {
    this.entityForm.patchValue(entity);
  }

}
