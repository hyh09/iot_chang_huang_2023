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

import { AfterViewInit, Component, ElementRef, Inject, OnInit, ViewChild } from '@angular/core';
import { fromEvent, Observable } from 'rxjs';
import { select, Store } from '@ngrx/store';
import { debounceTime, distinctUntilChanged, map, tap } from 'rxjs/operators';

import { BreakpointObserver, BreakpointState } from '@angular/cdk/layout';
import { AuthUser, User } from '@shared/models/user.model';
import { PageComponent } from '@shared/components/page.component';
import { AppState } from '@core/core.state';
import { getCurrentAuthState, getCurrentAuthUser, selectAuthUser, selectUserDetails } from '@core/auth/auth.selectors';
import { MediaBreakpoints } from '@shared/models/constants';
import * as _screenfull from 'screenfull';
import { MatSidenav } from '@angular/material/sidenav';
import { AuthState } from '@core/auth/auth.models';
import { WINDOW } from '@core/services/window.service';
import { instanceOfSearchableComponent, ISearchableComponent } from '@home/models/searchable-component.models';
import {DomSanitizer, SafeUrl} from "@angular/platform-browser";
import {
  selectPlatformName,
  selectPlatformVersion,
  selectShowNameVersion,
  selectTenantUI
} from "@core/custom/tenant-ui.selectors";
import { ActivatedRoute, Router } from '@angular/router';
import { MenuService } from '@app/core/public-api';
import { Authority } from '@app/shared/public-api';

const screenfull = _screenfull as _screenfull.Screenfull;

@Component({
  selector: 'tb-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.scss']
})
export class HomeComponent extends PageComponent implements AfterViewInit, OnInit {

  authState: AuthState = getCurrentAuthState(this.store);

  forceFullscreen = this.authState.forceFullscreen;

  activeComponent: any;
  searchableComponent: ISearchableComponent;

  sidenavMode: 'over' | 'push' | 'side' = 'side';
  sidenavOpened = true;

  logo: string | SafeUrl = 'assets/logo_title_black.svg';

  //custom ui 相关属性
  logoHeight = 36;
  name: string;
  version: string;
  showNameVersion: boolean;


  @ViewChild('sidenav')
  sidenav: MatSidenav;

  @ViewChild('searchInput') searchInputField: ElementRef;

  fullscreenEnabled = screenfull.isEnabled;

  authUser$: Observable<any>;
  userDetails$: Observable<User>;
  userDetailsString: Observable<string>;

  searchEnabled = false;
  showSearch = false;
  searchText = '';

  private readonly authUser: AuthUser;

  constructor(protected store: Store<AppState>,
              @Inject(WINDOW) private window: Window,
              public breakpointObserver: BreakpointObserver,
              private sanitizer: DomSanitizer,
              public route: ActivatedRoute,
              protected router: Router,
              private menuService: MenuService) {
    super(store);
    this.initCustomUi();
    this.authUser = getCurrentAuthUser(this.store);
  }

  ngOnInit() {
    // 缓存权限数据
    this.route.data.subscribe(({ permissions }) => {
      if (permissions) {
        sessionStorage.setItem('menuSections', JSON.stringify(permissions.menuSections));
        sessionStorage.setItem('homeSections', JSON.stringify(permissions.homeSections));
        sessionStorage.setItem('menuBtnMap', JSON.stringify(permissions.menuBtnMap));
        this.menuService.buildMenu();
      }
    });

    this.authUser$ = this.store.pipe(select(selectAuthUser));
    this.userDetails$ = this.store.pipe(select(selectUserDetails));
    this.userDetailsString = this.userDetails$.pipe(map((user: User) => {
      return JSON.stringify(user);
    }));

    const isGtSm = this.breakpointObserver.isMatched(MediaBreakpoints['gt-sm']);
    this.sidenavMode = isGtSm ? 'side' : 'over';
    this.sidenavOpened = isGtSm;

    this.breakpointObserver
      .observe(MediaBreakpoints['gt-sm'])
      .subscribe((state: BreakpointState) => {
          if (state.matches) {
            this.sidenavMode = 'side';
            this.sidenavOpened = true;
          } else {
            this.sidenavMode = 'over';
            this.sidenavOpened = false;
          }
        }
      );
  }

  ngAfterViewInit() {
    fromEvent(this.searchInputField.nativeElement, 'keyup')
      .pipe(
        debounceTime(150),
        distinctUntilChanged(),
        tap(() => {
          this.searchTextUpdated();
        })
      )
      .subscribe();
  }

  sidenavClicked() {
    if (this.sidenavMode === 'over') {
      this.sidenav.toggle();
    }
  }

  toggleFullscreen() {
    if (screenfull.isEnabled) {
      screenfull.toggle();
    }
  }

  isFullscreen() {
    return screenfull.isFullscreen;
  }

  goBack() {
    this.window.history.back();
  }

  activeComponentChanged(activeComponent: any) {
    this.showSearch = false;
    this.searchText = '';
    this.activeComponent = activeComponent;
    if (this.activeComponent && instanceOfSearchableComponent(this.activeComponent)) {
      this.searchEnabled = true;
      this.searchableComponent = this.activeComponent;
    } else {
      this.searchEnabled = false;
      this.searchableComponent = null;
    }
  }

  displaySearchMode(): boolean {
    return this.searchEnabled && this.showSearch;
  }

  openSearch() {
    if (this.searchEnabled) {
      this.showSearch = true;
      setTimeout(() => {
        this.searchInputField.nativeElement.focus();
        this.searchInputField.nativeElement.setSelectionRange(0, 0);
      }, 10);
    }
  }

  closeSearch() {
    if (this.searchEnabled) {
      this.showSearch = false;
      if (this.searchText.length) {
        this.searchText = '';
        this.searchTextUpdated();
      }
    }
  }

  private searchTextUpdated() {
    if (this.searchableComponent) {
      this.searchableComponent.onSearchTextUpdated(this.searchText);
    }
  }

  private initCustomUi(){
    //构造方法内添加下面代码
    this.store.pipe(select(selectTenantUI)).subscribe(ui => {
      if(ui){
        this.logo = ui.logoImageUrl ? this.sanitizer.bypassSecurityTrustUrl(ui.logoImageUrl) : 'assets/logo_title_black.svg'
        this.logoHeight = ui.logoImageHeight ? Number(ui.logoImageHeight) : 36
      }else{
        this.logo = 'assets/logo_title_black.svg';
        this.logoHeight = 36;
      }
    })
    this.store.pipe(select(selectPlatformName)).subscribe(res => this.name = res);
    this.store.pipe(select(selectPlatformVersion)).subscribe(res => this.version = res);
    this.store.pipe(select(selectShowNameVersion)).subscribe(res => this.showNameVersion = res);
  }
  
}
