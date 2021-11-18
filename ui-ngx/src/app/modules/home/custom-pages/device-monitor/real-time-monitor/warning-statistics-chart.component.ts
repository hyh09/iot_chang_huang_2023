import { Component, Input, AfterViewInit, ElementRef, ViewChild } from '@angular/core';
import { AlarmTimesListItem } from '@app/shared/models/custom/device-monitor.models';
import { TranslateService } from '@ngx-translate/core';
import * as echarts from 'echarts';

@Component({
  selector: 'tb-warning-statistics-chart',
  templateUrl: './chart.component.html',
  styleUrls: ['./chart.component.scss']
})
export class WarningStatisticsChartComponent implements AfterViewInit {

  @Input() data: AlarmTimesListItem[] = [];

  @ViewChild('chartWrapper') chartWrapper: ElementRef;

  private chart: any;

  constructor(private translate: TranslateService) { }

  ngAfterViewInit() {
    this.chart = echarts.init(this.chartWrapper.nativeElement);
    window.onresize = () => {
      this.chart.resize();
    }
  }

  ngOnChanges() {
    this.init();
  }

  init() {
    if (!this.chart) return;
    const option = {
      title: {
        text: this.translate.instant('device-monitor.warning-statistics'),
        left: 0
      },
      legend: {
        align: 'right',
        right: 0
      },
      xAxis: {
        type: 'category',
        data: this.data.map(item => (item.time))
      },
      yAxis: {
        type: 'value'
      },
      series: [
        {
          data: this.data.map(item => (item.num)),
          type: 'line'
        }
      ]
    };
    this.chart.setOption(option);
    this.chart.resize();
  }

}
