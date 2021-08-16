
import {RouterModule, Routes} from "@angular/router";
import {Authority} from "@shared/models/authority.enum";
import {ConfirmOnExitGuard} from "@core/guards/confirm-on-exit.guard";

import { Biz1Component } from "./biz1/biz1.component";
import { Biz2Component } from "./biz2/biz2.component";
import {NgModule} from "@angular/core";



const routes: Routes = [
  {
    path: 'biz',
    data: {
      auth: [Authority.TENANT_ADMIN],
      breadcrumb: {
        label: 'my.biz',
        icon: 'touch_app'
      }
    },
    children: [
      {
        path: '',
        redirectTo: 'biz1',
        pathMatch: 'full'
      },
      {
        path: 'biz1',
        component: Biz1Component,
        canDeactivate: [ConfirmOnExitGuard],
        data: {
          auth: [Authority.TENANT_ADMIN],
          title: 'my.biz1',
          breadcrumb: {
            label: 'my.biz1',
            icon: 'av_timer'
          }
        }
      },
      {
        path: 'biz2',
        component: Biz2Component,
        canDeactivate: [ConfirmOnExitGuard],
        data: {
          auth: [Authority.TENANT_ADMIN],
          title: 'my.biz2',
          breadcrumb: {
            label: 'my.biz2',
            icon: 'disc_full'
          }
        }
      }
    ]
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class MyBizRoutingModule { }
