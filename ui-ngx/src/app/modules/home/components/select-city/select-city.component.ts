import { Component, EventEmitter, Input, Output } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { City } from '@app/core/http/custom/geography.service';
import { SelectCityDialogComponent } from './select-city-dialog.component';

@Component({
  selector: 'tb-select-city',
  templateUrl: './select-city.component.html',
  styleUrls: ['./select-city.component.scss']
})
export class SelectCityComponent {

  @Input()
  disabled: boolean = false;

  @Output()
  onSelect = new EventEmitter<City>();

  constructor(
    private dialog: MatDialog
  ) { }

  openDialog() {
    this.dialog.open<SelectCityDialogComponent, City>(SelectCityDialogComponent, {
      disableClose: true,
      panelClass: ['tb-dialog', 'tb-fullscreen-dialog']
    }).afterClosed().subscribe(res => {
      if (res) {
        this.onSelect.emit(res);
      }
    });
  }

}
