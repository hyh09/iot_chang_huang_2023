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

import { Component, OnInit } from '@angular/core';
import { AuthService } from '@core/auth/auth.service';
import {select, Store} from '@ngrx/store';
import { AppState } from '@core/core.state';
import { PageComponent } from '@shared/components/page.component';
import { FormBuilder } from '@angular/forms';
import { HttpErrorResponse } from '@angular/common/http';
import { Constants } from '@shared/models/constants';
import { Router } from '@angular/router';
import { OAuth2ClientInfo } from '@shared/models/oauth2.models';
import {combineLatest} from "rxjs";
import {selectAuthUser, selectIsUserLoaded} from "@core/auth/auth.selectors";
import {distinctUntilChanged, filter, map, skip} from "rxjs/operators";
import { ActionTenantUIChangeAll } from '@core/custom/tenant-ui.actions';
import { DashboardService } from '@core/http/dashboard.service';
import { Authority } from '@shared/models/authority.enum';

@Component({
  selector: 'tb-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss']
})
export class LoginComponent extends PageComponent implements OnInit {

  loginFormGroup = this.fb.group({
    username: '',
    password: ''
  });
  oauth2Clients: Array<OAuth2ClientInfo> = null;

  constructor(protected store: Store<AppState>,
              private authService: AuthService,
              public fb: FormBuilder,
              private router: Router,
              private dashboardService: DashboardService) {
    super(store);
  }

  ngOnInit() {
    this.oauth2Clients = this.authService.oauth2Clients;
  }

  login(): void {
    if (this.loginFormGroup.valid) {
      this.authService.login(this.loginFormGroup.value).subscribe(
        () => {},
        (error: HttpErrorResponse) => {
          if (error && error.error && error.error.errorCode) {
            if (error.error.errorCode === Constants.serverErrorCode.credentialsExpired) {
              this.router.navigateByUrl(`login/resetExpiredPassword?resetToken=${error.error.resetToken}`);
            }
          }
        }
      );
    } else {
      Object.keys(this.loginFormGroup.controls).forEach(field => {
        const control = this.loginFormGroup.get(field);
        control.markAsTouched({onlySelf: true});
      });
    }
  }

  private loginCustomUI(): void{
    combineLatest([
      this.store.pipe(select(selectAuthUser)),
      this.store.pipe(select(selectIsUserLoaded))]
    ).pipe(
      map(results => ({ authUser: results[0], isUserLoaded: results[1] })),
      distinctUntilChanged(),
      filter((data) => data.isUserLoaded),
      skip(1)
    ).subscribe((data) => {
      if(data.authUser.authority === Authority.TENANT_ADMIN){
        this.dashboardService.getTenantUIInfo().subscribe(ui => {
          this.store.dispatch(new ActionTenantUIChangeAll(ui));
        });
      }
    });
  }

}
