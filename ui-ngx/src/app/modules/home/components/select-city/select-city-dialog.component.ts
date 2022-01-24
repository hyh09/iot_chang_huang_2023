import { Component } from "@angular/core";
import { MatDialogRef } from "@angular/material/dialog";
import { Router } from "@angular/router";
import { AppState } from "@app/core/core.state";
import { City, GeographyService } from "@app/core/http/custom/geography.service";
import { guid } from "@app/core/utils";
import { DialogComponent } from "@app/shared/components/dialog.component";
import { Store } from "@ngrx/store";

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
