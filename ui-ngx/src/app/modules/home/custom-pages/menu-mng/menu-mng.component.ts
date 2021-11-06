import { TranslateService } from '@ngx-translate/core';
import { Store } from '@ngrx/store';
import { ChangeDetectorRef, Component, Inject } from '@angular/core';
import { AppState } from '@app/core/core.state';
import { EntityComponent } from '@app/modules/home/components/entity/entity.component';
import { Menu, MenuType } from '@app/shared/models/custom/menu-mng.models';
import { EntityTableConfig } from '@app/modules/home/models/entity/entities-table-config.models';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MenuMngService } from '@app/core/http/custom/menu-mng.service';

@Component({
  selector: 'tb-menu-mng',
  templateUrl: './menu-mng.component.html'
})
export class MenuMngComponent extends EntityComponent<Menu> {

  public menuType = MenuType;
  public superMenus = new Array<Menu>();

  constructor(
    protected store: Store<AppState>,
    protected translate: TranslateService,
    @Inject('entity') protected entityValue: Menu,
    @Inject('entitiesTableConfig') protected entitiesTableConfigValue: EntityTableConfig<Menu>,
    protected fb: FormBuilder,
    protected cd: ChangeDetectorRef,
    public menuMngService: MenuMngService
  ) {
    super(store, fb, entityValue, entitiesTableConfigValue, cd);
  }

  buildForm(entity: Menu): FormGroup {
    return this.fb.group(
      {
        id:  [entity ? entity.id : ''],
        level: [entity ? entity.level : ''],
        menuIcon: [entity ? entity.menuIcon : ''],
        menuImages: [entity ? entity.menuImages : ''],
        name: [entity ? entity.name : '', [Validators.required]],
        langKey: [entity ? entity.langKey : '', [Validators.required]], 
        menuType: [entity ? entity.menuType: '', [Validators.required]],
        parentId: [entity ? entity.parentId : ''],
        path: [entity ? entity.path : ''],
        url: [entity ? entity.url : ''],
        isButton: [entity && !!entity.isButton]
      }
    );
  }

  updateForm(entity: Menu) {
    this.getSuperMenus(entity.menuType, () => {
      this.entityForm.patchValue(entity);
    });
  }

  getSuperMenus(menuType?: MenuType, callFn?: Function) {
    this.menuMngService.getSuperMenus(menuType || this.entityForm.get('menuType').value).subscribe(menus => {
      this.superMenus = menus;
      callFn && callFn();
    });
  }

}
