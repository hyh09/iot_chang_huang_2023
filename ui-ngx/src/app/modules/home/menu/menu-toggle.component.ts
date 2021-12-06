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

import { ChangeDetectionStrategy, Component, Input, OnInit } from '@angular/core';
import { MenuSection } from '@core/services/menu.models';
import { Router } from '@angular/router';

@Component({
  selector: 'tb-menu-toggle',
  templateUrl: './menu-toggle.component.html',
  styleUrls: ['./menu-toggle.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class MenuToggleComponent implements OnInit {

  @Input() section: MenuSection;

  constructor(private router: Router) {
  }

  ngOnInit() {
  }

  onClick() {
    if (this.section.pages && this.section.pages.length > 0) {
      this.router.navigateByUrl(this.section.pages[0].path);
    }
  }

  sectionActive(): boolean {
    if (this.section.pages && this.section.pages.length > 0) {
      const paths = this.section.pages.map(page => (page.path));
      return paths.includes(window.location.pathname);
    } else {
      return false;
    }
  }

  sectionHeight(): string {
    return this.sectionActive() ? this.section.height : '0px';
  }

  trackBySectionPages(index: number, section: MenuSection){
    return section.id;
  }
}
