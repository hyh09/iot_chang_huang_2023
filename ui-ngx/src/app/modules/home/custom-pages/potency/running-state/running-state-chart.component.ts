import { Component, Input, AfterViewInit, ElementRef, ViewChild, OnChanges } from '@angular/core';
import { viewPortResize } from '@app/core/utils';
import { RunningState } from '@app/shared/models/custom/potency.models';
import { TranslateService } from '@ngx-translate/core';
import * as echarts from 'echarts';

@Component({
  selector: 'tb-running-state-chart',
  template: `<div #runningStateChart class="running-state-chart"></div>`,
  styleUrls: ['./running-state-chart.component.scss']
})
export class RunningStateChartComponent implements AfterViewInit, OnChanges {

  @Input() data: RunningState;

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
    const series = (this.data.properties || []).map(prop => {
      const chartData = prop.tsKvs.map(item => {
        return [new Date(item.ts), item.value];
      });
      return {
        name: prop.title || prop.name,
        data: chartData,
        type: 'line',
        symbol: 'none',
        smooth: true,
        tooltip: {
          formatter: `{b}：{c}`
        }
      }
    });
    const unit = ((this.data.properties || [])[0] || {}).unit;
    const option = {
      title: {
        text: this.data.tableName,
        subtext: unit ? this.translate.instant('device-monitor.prop-unit', { unit }) : '',
        left: -5,
        textStyle: {
          fontSize: 16,
          color: 'rgba(0, 0, 0, 0.87)'
        }
      },
      color: ['#0663ff', '#ffe148', '#99d5d4', '#fba341', '#5fbc4d', '#ff6c6c'],
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
      series
    };
    this.chart.setOption(option);
    this.chart.resize();
  }

}
