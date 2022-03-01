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
  private lineDataMap = {};

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

  private init() {
    if (!this.chart) return;
    this.lineDataMap = {};
    const series = [];
    let latestTime = 0;
    let earliestTime = new Date().getTime();
    let unit = '';
    (this.data.properties || []).forEach(item => {
      if (item.isShowChart) {
        this.lineDataMap[item.name] = (item.tsKvs || []).map(timeVal => {
          return [new Date(timeVal.ts), timeVal.value];
        });
        series.push({
          name: item.title || item.name,
          data: this.lineDataMap[item.name],
          type: 'line',
          showSymbol: false,
          smooth: true,
          tooltip: {
            formatter: `{b}：{c}`
          },
          animation: false,
          emphasis: {
            disabled: true
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
        text: `${this.data.name || this.translate.instant('device-monitor.real-time-data-chart')}`,
        subtext: unit ? this.translate.instant('device-monitor.prop-unit', { unit }) : '',
        left: -5,
        textStyle: {
          fontSize: 16,
          color: 'rgba(0, 0, 0, 0.87)'
        }
      },
      color: ['#0663ff', '#99D5D4', '#5FBC4D', '#C5DE66', '#FFE148', '#FBA341', '#FF6C6C', '#F14444', '#C19461', '#913030'],
      grid: {
        bottom: 9,
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
        minInterval,
        splitLine: {
          show: false
        }
      },
      yAxis: {
        type: 'value',
        splitLine: {
          show: false
        }
      },
      dataZoom: [
        {
          type: 'inside',
          start: 90,
          end: 100
        },
        {
          start: 90,
          end: 100
        }
      ],
      series
    };
    this.chart.clear();
    this.chart.setOption(option);
    this.chart.resize();
  }

  public pushData(propName: string, data: { ts: number; value: string; }) {
    if (this.chart && propName && this.lineDataMap[propName] && data) {
      this.lineDataMap[propName].splice(0, 0, [new Date(data.ts), data.value]);
      this.chart.setOption({
        series: Object.values(this.lineDataMap).map(dataSet => ({ data: dataSet }))
      });
    }
  }

}
