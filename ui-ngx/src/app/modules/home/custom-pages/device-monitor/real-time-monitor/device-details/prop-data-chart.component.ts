import { Component, Input, AfterViewInit, ElementRef, ViewChild, OnChanges } from '@angular/core';
import { viewPortResize } from '@app/core/utils';
import { DevicePropHistory } from '@app/shared/models/custom/device-monitor.models';
import { TranslateService } from '@ngx-translate/core';
import * as echarts from 'echarts';

@Component({
  selector: 'tb-prop-data-chart',
  template: `<div class="chart-panel">
              <div #propDataChart class="chart-wrapper"></div>
            </div>`,
  styleUrls: ['../chart.component.scss']
})
export class PropDataChartComponent implements AfterViewInit, OnChanges {
  
  @Input() data: DevicePropHistory;

  @ViewChild('propDataChart') propDataChart: ElementRef;

  private chart: any;

  constructor(private translate: TranslateService) { }

  ngAfterViewInit() {
    this.chart = echarts.init(this.propDataChart.nativeElement);
    viewPortResize.subscribe(() => {
      this.chart.resize();
    });
  }

  ngOnChanges() {
    this.init();
  }

  init() {
    if (!this.chart) return;
    const series = [];
    let latestTime = 0;
    let earliestTime = new Date().getTime();
    let unit = '';
    (this.data.properties || []).forEach(item => {
      if (item.isShowChart) {
        series.push({
          name: item.title || item.name,
          data: (item.tsKvs || []).map(timeVal => {
            return [new Date(timeVal.ts), timeVal.value];
          }),
          type: 'line',
          symbol: 'none',
          smooth: true,
          tooltip: {
            formatter: `{b}：{c}${item.unit}`
          }
        });
        const timeList = item.tsKvs || [];
        const _latestTime = (timeList[0] || {}).ts || 0;
        const _earliestTime = (timeList[timeList.length - 1] || {}).ts || 0;
        latestTime = _latestTime > latestTime ? _latestTime : latestTime;
        earliestTime = _earliestTime < earliestTime ? _earliestTime : earliestTime;
        if (!unit) {
          unit = item.unit;
        }
      }
    });
    const minInterval = latestTime - earliestTime < 600000 ? 10000 : 600000;
    const option = {
      title: {
        text: `${this.translate.instant('device-monitor.real-time-data-chart')}${this.data.name ? ` - ${this.data.name}` : ''}`,
        subtext: unit ? this.translate.instant('device-monitor.prop-unit', { unit: unit }) : '',
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
        left: 0,
        containLabel: true
      },
      tooltip: {
        trigger: 'axis'
      },
      xAxis: {
        type: 'time',
        boundaryGap: false,
        axisLabel: {
          margin: 16
        },
        minInterval
      },
      yAxis: {
        type: 'value'
      },
      series
    };
    this.chart.setOption(option);
    this.chart.resize();
  }

}
