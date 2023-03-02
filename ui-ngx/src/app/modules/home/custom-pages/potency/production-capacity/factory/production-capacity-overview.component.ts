import { Router } from '@angular/router';
import { ChangeDetectorRef, Component, ElementRef, ViewChild } from '@angular/core';
import { AppState } from '@app/core/core.state';
import { EntityTableHeaderComponent } from '@app/modules/home/components/entity/entity-table-header.component';
import { DeviceCapacity, PotencyInterval, PotencyTop10 } from '@app/shared/models/custom/potency.models';
import { Store } from '@ngrx/store';
import { differenceInCalendarDays } from 'date-fns';
import { BehaviorSubject } from 'rxjs';
import { PotencyService } from '@app/core/http/custom/potency.service';
import * as echarts from 'echarts';
import { viewPortResize, getTheStartOfDay, getTheEndOfDay } from '@app/core/utils';

@Component({
  selector: 'tb-production-capacity-overview',
  templateUrl: './production-capacity-overview.component.html',
  styleUrls: [
    '../../energy-consumption/factory/energy-consumption-overview.component.scss',
    '../../energy-consumption/factory/energy-history-filter.component.scss'
  ]
})
export class ProductionCapacityOverviewComponent extends EntityTableHeaderComponent<DeviceCapacity> {

  today = new Date();
  isHistory:boolean = false;
  factoryId = '';
  top10: PotencyTop10 = [];

  deviceId: string = '';
  chartData: PotencyInterval = [];

  @ViewChild('chartWrapper') chartWrapper: ElementRef;
  private chart: any;

  constructor(
    protected store: Store<AppState>,
    public router: Router,
    private potencyService: PotencyService,
    protected cd: ChangeDetectorRef
  ) {
    super(store);
    this.isHistory = this.router.url.indexOf('history') > -1;
  }

  ngAfterViewInit() {
    if (!this.isHistory) {
      (this.entitiesTableConfig.componentsData.factroryChange$ as BehaviorSubject<string>).subscribe(factoryId => {
        if (factoryId) {
          this.factoryId = factoryId;
          this.getTop10()
        }
      });
    } else {
      (this.entitiesTableConfig.componentsData.deviceIdLoaded$ as BehaviorSubject<string>).subscribe(deviceId => {
        this.deviceId = deviceId || '';
        this.getIntervalData();
      });
      this.chart = echarts.init(this.chartWrapper.nativeElement);
      viewPortResize.subscribe(() => {
        this.chart.resize();
      });
    }
  }

  disabledDate = (current: Date): boolean => {
    return differenceInCalendarDays(current, this.today) > 0;
  };

  getTop10() {
    if (this.factoryId) {
      this.potencyService.getTop10({
        factoryId: this.factoryId,
        keyNum: '',
        type: '0'
      }).subscribe(res => {
        this.top10 = res || [];
        this.cd.markForCheck();
        this.cd.detectChanges();
      })
    }
  }

  getIntervalData(deviceId: string = this.deviceId) {
    const dateRange = this.entitiesTableConfig.componentsData.dateRange;
    if (deviceId && dateRange && dateRange.length === 2) {
      let startTime = null, endTime = null;
      if (this.entitiesTableConfig.componentsData.dateRange) {
        startTime = (getTheStartOfDay(dateRange[0] as Date) as number);
        endTime = (getTheEndOfDay(dateRange[1] as Date) as number);
      }
      this.potencyService.getIntervalData({
        deviceId,
        startTime,
        endTime,
        keyNum: '',
        type: '0'
      }).subscribe(res => {
        this.chartData = res || [];
        this.initChart();
      })
    }
  }

  initChart() {
    if (!this.chart) return;
    const option = {
      grid: {
        top: 10,
        bottom: 20,
        right: 25,
        left: 0,
        containLabel: true
      },
      tooltip: {
        trigger: 'axis'
      },
      xAxis: {
        data: this.chartData.map(item => (item.dateTime)),
        silent: false,
        splitLine: {
          show: false
        },
        splitArea: {
          show: false
        }
      },
      yAxis: {
        splitArea: {
          show: false
        }
      },
      dataZoom: [
        {
          type: 'inside',
          start: 0,
          end: 100
        },
        {
          start: 0,
          end: 100
        }
      ],
      series: [
        {
          type: 'bar',
          data: this.chartData.map(item => (item.capacityOrEnergy)),
          large: true,
          barMaxWidth: 40,
          itemStyle: {
            normal: {
							color: new echarts.graphic.LinearGradient(0, 1, 0, 0, [{
								offset: 0,
								color: "#5fbc4d" // 0% 处的颜色
							}, {
								offset: 1,
								color: "#99d5d4" // 100% 处的颜色
							}], false),
              borderRadius: [4, 4, 0, 0]
						}
          }
        }
      ]
    }
    this.chart.setOption(option);
    this.chart.resize();
  }

  onTimeChange() {
    this.entitiesTableConfig.table.resetSortAndFilter(true);
    if (this.isHistory) {
      this.getIntervalData();
    } else {
      this.getTop10();
    }
  }

  onTabClick(name: string) {
    if (name) {
      this.router.navigateByUrl(`/potency/outputAnalysis/${name}`);
    }
  }

}
