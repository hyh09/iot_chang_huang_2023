import { Component, Input, AfterViewInit, ElementRef, ViewChild, OnChanges, OnDestroy } from '@angular/core';
import { DeviceProp } from '@app/shared/models/custom/device-monitor.models';
import { TranslateService } from '@ngx-translate/core';
import * as echarts from 'echarts';

@Component({
  selector: 'tb-prop-data-chart',
  template: `<div class="chart-panel">
              <div #propDataChart class="chart-wrapper"></div>
            </div>`,
  styleUrls: ['../chart.component.scss']
})
export class PropDataChartComponent implements AfterViewInit, OnDestroy, OnChanges {
  
  @Input() propName: string;
  @Input() data: DeviceProp[];

  @ViewChild('propDataChart') propDataChart: ElementRef;

  private chart: any;

  constructor(private translate: TranslateService) { }

  ngAfterViewInit() {
    this.chart = echarts.init(this.propDataChart.nativeElement);
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
    const chartData = this.data.map(item => {
      return [new Date(item.createdTime), item.content]
    });
    const option = {
      title: {
        text: `${this.translate.instant('device-monitor.real-time-data-chart')}${this.propName ? ` - ${this.propName}` : ''}`,
        left: 0,
        textStyle: {
          fontSize: 16,
          color: 'rgba(0, 0, 0, 0.87)'
        }
      },
      color: ['#0663ff'],
      grid: {
        bottom: 5,
        right: 30,
        left: 20,
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
        }
      },
      yAxis: {
        type: 'value',
        name: this.data[0] && this.data[0].unit ? this.translate.instant('device-monitor.prop-unit', { unit: this.data[0].unit }) : ''
      },
      series: [
        {
          name: this.propName,
          data: chartData,
          type: 'line',
          symbol: 'none',
          smooth: true,
          tooltip: {
            formatter: '{b}：{c}'
          }
        }
      ]
    };
    this.chart.setOption(option);
    this.chart.resize();
  }

}
