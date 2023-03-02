import { AfterViewInit, Component, ElementRef, ViewChild } from '@angular/core';
import { AppState } from '@app/core/core.state';
import { PotencyService } from '@app/core/public-api';
import { getTheEndOfDay, getTheStartOfDay, viewPortResize } from '@app/core/utils';
import { EntityTableHeaderComponent } from '@app/modules/home/components/entity/entity-table-header.component';
import { Store } from '@ngrx/store';
import { differenceInCalendarDays } from 'date-fns';
import { BehaviorSubject } from 'rxjs';
import * as echarts from 'echarts';
import { DeviceEnergyConsumption, PotencyInterval } from '@app/shared/models/custom/potency.models';

@Component({
  selector: 'tb-energy-history-filter',
  templateUrl: './energy-history-filter.component.html',
  styleUrls: ['./energy-history-filter.component.scss']
})
export class EnergyHistoryFilterComponent extends EntityTableHeaderComponent<DeviceEnergyConsumption> implements AfterViewInit {

  today = new Date();
  deviceId: string = '';
  keyNum: '1' | '2' | '3' = '1';
  chartData: PotencyInterval = [];

  @ViewChild('chartWrapper') chartWrapper: ElementRef;
  private chart: any;

  constructor(
    protected store: Store<AppState>,
    private potencyService: PotencyService
  ) {
    super(store);
  }

  ngAfterViewInit() {
    (this.entitiesTableConfig.componentsData.deviceIdLoaded$ as BehaviorSubject<string>).subscribe(deviceId => {
      this.deviceId = deviceId || '';
      this.getIntervalData();
    });
    this.chart = echarts.init(this.chartWrapper.nativeElement);
    viewPortResize.subscribe(() => {
      this.chart.resize();
    });
  }

  disabledDate = (current: Date): boolean => {
    return differenceInCalendarDays(current, this.today) > 0;
  };

  getIntervalData(deviceId: string = this.deviceId, keyNum: '1' | '2' | '3' = this.keyNum) {
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
        keyNum,
        type: '1'
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
								color: "#0e4ba1" // 0% 处的颜色
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
    this.getIntervalData()
  }

}
