import { Injectable } from "@angular/core";
import { Resolve } from '@angular/router';
import { DateEntityTableColumn, EntityTableColumn, EntityTableConfig, iconCell } from "@app/modules/home/models/entity/entities-table-config.models";
import { EntityType, entityTypeResources, entityTypeTranslations } from "@app/shared/public-api";
import { MenuMngComponent } from "./menu-mng.component";
import { MenuMngFiltersComponent } from "./menu-mng-filters.component";
import { TranslateService } from '@ngx-translate/core';
import { DatePipe } from '@angular/common';
import { MenuMngService } from '@app/core/http/custom/menu-mng.service';
import { Menu, MenuLevel, MenuType } from '@app/shared/models/custom/menu-mng.models';

@Injectable()
export class MenuMngTableConfigResolver implements Resolve<EntityTableConfig<Menu>> {

  private readonly config: EntityTableConfig<Menu> = new EntityTableConfig<Menu>();

  constructor(
    private translate: TranslateService,
    private datePipe: DatePipe,
    private menuMngService: MenuMngService
  ) {
    this.config.entityType = EntityType.MENU;
    this.config.entityComponent = MenuMngComponent;
    this.config.filterComponent = MenuMngFiltersComponent;
    this.config.entityTranslations = entityTypeTranslations.get(EntityType.MENU);
    this.config.entityResources = entityTypeResources.get(EntityType.MENU);

    this.config.componentsData = {
      name: '',
      menuType: MenuType.PC
    }

    this.config.deleteEntityTitle = menu => this.translate.instant('menu-mng.delete-menu-title', {menuName: menu.name});
    this.config.deleteEntityContent = () => this.translate.instant('menu-mng.delete-menu-text');
    this.config.deleteEntitiesTitle = count => this.translate.instant('menu-mng.delete-menus-title', {count});
    this.config.deleteEntitiesContent = () => this.translate.instant('menu-mng.delete-menus-text');

    this.config.columns.push(
      new EntityTableColumn<Menu>('name', 'menu-mng.name', '33.333333%'),
      new EntityTableColumn<Menu>('parentName', 'menu-mng.parentName', '33.333333%'),
      new EntityTableColumn<Menu>('path', 'menu-mng.path', '33.333333%'),
      new EntityTableColumn<Menu>('menuIcon', 'menu-mng.icon', '80px', ({menuIcon}) => {
        return iconCell(menuIcon)
      }),
      new EntityTableColumn<Menu>('isButton', 'menu-mng.is-button-or-not', '80px', ({isButton}) => {
        return isButton ? this.translate.instant('action.yes') : this.translate.instant('action.no');
      }),
      new DateEntityTableColumn<Menu>('createdTime', 'common.created-time', this.datePipe, '150px')
    );
  }

  resolve(): EntityTableConfig<Menu> {
    this.config.tableTitle = this.translate.instant('menu-mng.menu-mng');
    this.config.searchEnabled = false;
    this.config.refreshEnabled = false;

    this.config.entitiesFetchFunction = pageLink => this.menuMngService.getMenus(pageLink, this.config.componentsData);
    this.config.loadEntity = id => this.menuMngService.getMenu(id);
    this.config.saveEntity = menu => {
      menu.level = menu.parentId ? MenuLevel.SECOND : MenuLevel.FIRST;
      return this.menuMngService.saveMenu(menu);
    }
    this.config.deleteEntity = id => this.menuMngService.deleteMenu(id);

    return this.config;
  }

}
