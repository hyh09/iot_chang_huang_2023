import { Component } from "@angular/core";
import { MatDialogRef } from "@angular/material/dialog";
import { Router } from "@angular/router";
import { AppState } from "@app/core/core.state";
import { DialogComponent } from "@app/shared/public-api";
import { Store } from "@ngrx/store";
import { City, GeographyService, guid } from '@app/core/public-api';

@Component({
  selector: 'tb-select-city-dialog',
  templateUrl: './select-city-dialog.component.html',
  styleUrls: ['./select-city.component.scss']
})
export class SelectCityDialogComponent extends DialogComponent<SelectCityDialogComponent, City> {

  searchKey: string = '';
  cities: City[] = [];
  cityMap: { [id: string]: City } = {};
  selectedId: string;

  constructor(
    protected store: Store<AppState>,
    protected router: Router,
    public dialogRef: MatDialogRef<SelectCityDialogComponent, City>,
    private geographyService: GeographyService
  ) {
    super(store, router, dialogRef);
  }

  queryCity() {
    this.cityMap = {};
    this.selectedId = '';
    if (!this.searchKey) {
      this.cities = [];
    } else {
      this.geographyService.queryCity(this.searchKey).subscribe(res => {
        this.cities = res || [];
        this.cities.forEach(city => {
          city.id = guid();
          this.cityMap[city.id] = city;
        });
      });
    }
  }

  cancel() {
    this.dialogRef.close(null);
  }

  save() {
    if (this.selectedId) {
      this.dialogRef.close(this.cityMap[this.selectedId]);
    }
  }

}
