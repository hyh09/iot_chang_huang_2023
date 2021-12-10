import { Component, Input, AfterViewInit, ElementRef, ViewChild, OnChanges, OnDestroy } from '@angular/core';
import { AlarmTimesListItem } from '@app/shared/models/custom/device-monitor.models';
import { TranslateService } from '@ngx-translate/core';
import * as echarts from 'echarts';

@Component({
  selector: 'tb-warning-statistics-chart',
  template: `<div class="chart-panel">
              <div #warningStatisticsChart class="chart-wrapper"></div>
            </div>`,
  styleUrls: ['./chart.component.scss']
})
export class WarningStatisticsChartComponent implements AfterViewInit, OnDestroy, OnChanges {

  @Input() data: AlarmTimesListItem[];

  @ViewChild('warningStatisticsChart') warningStatisticsChart: ElementRef;

  private chart: any;

  constructor(private translate: TranslateService) { }

  ngAfterViewInit() {
    this.chart = echarts.init(this.warningStatisticsChart.nativeElement);
    window.addEventListener('resize', this.chart.resize);
  }

  ngOnDestroy() {
    window.removeEventListener('resize', this.chart.resize);
  }

  ngOnChanges() {
    this.init();
  }

  init() {
    if (!this.chart) return;
    const option = {
      title: {
        text: this.translate.instant('device-monitor.warning-statistics'),
        subtext: this.translate.instant('device-monitor.warning-count'),
        left: -5,
        textStyle: {
          fontSize: 16,
          color: 'rgba(0, 0, 0, 0.87)'
        }
      },
      color: ['#0663ff'],
      grid: {
        bottom: 5,
        right: 25,
        left: 10,
        containLabel: true
      },
      tooltip: {
        trigger: 'axis'
      },
      xAxis: {
        type: 'category',
        boundaryGap: false,
        axisLabel: {
          margin: 16
        },
        data: this.data.map(item => (item.time))
      },
      yAxis: {
        type: 'value'
      },
      series: [
        {
          name: this.translate.instant('device-monitor.warning-count'),
          data: this.data.map(item => (item.num)),
          type: 'line'
        }
      ]
    };
    this.chart.setOption(option);
    this.chart.resize();
  }

}
