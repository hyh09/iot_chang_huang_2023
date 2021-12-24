import { Component, Input, AfterViewInit, ElementRef, ViewChild, OnChanges } from '@angular/core';
import { viewPortResize } from '@app/core/utils';
import { TranslateService } from '@ngx-translate/core';
import * as echarts from 'echarts';

@Component({
  selector: 'tb-running-state-chart',
  template: `<div #runningStateChart class="running-state-chart"></div>`,
  styleUrls: ['./running-state-chart.component.scss']
})
export class RunningStateChartComponent implements AfterViewInit, OnChanges {

  @Input() title: string;
  @Input() unit: string;
  @Input() data: { time: number; value: string; }[];

  @ViewChild('runningStateChart') runningStateChart: ElementRef;

  private chart: any;

  constructor(
    private translate: TranslateService
  ) { }

  ngAfterViewInit() {
    this.chart = echarts.init(this.runningStateChart.nativeElement, null, { locale: 'ZH' });
    viewPortResize.subscribe(() => {
      this.chart.resize();
    });
    setTimeout(() => {
      this.init();
    });
  }

  ngOnChanges() {
    this.init();
  }

  init() {
    if (!this.chart) return;
    const chartData = this.data.map(item => {
      return [new Date(item.time), item.value];
    });
    const option = {
      title: {
        text: this.title,
        subtext: this.unit ? this.translate.instant('device-monitor.prop-unit', { unit: this.unit }) : '',
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
        }
      },
      yAxis: {
        type: 'value'
      },
      dataZoom: [
        {
          type: 'inside',
          start: 0,
          end: 100
        }
      ],
      series: [
        {
          name: this.title,
          data: chartData,
          type: 'line',
          symbol: 'none',
          smooth: true,
          tooltip: {
            formatter: `{b}：{c}${this.unit}`
          }
        }
      ]
    };
    this.chart.setOption(option);
    this.chart.resize();
  }

}
