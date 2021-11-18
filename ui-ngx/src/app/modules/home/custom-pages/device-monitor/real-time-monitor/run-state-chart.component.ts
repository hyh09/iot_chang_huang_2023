import { TranslateService } from '@ngx-translate/core';
import { AfterViewInit, Component, ElementRef, Input, ViewChild, OnChanges } from '@angular/core';
import * as echarts from 'echarts';

@Component({
  selector: 'tb-run-state-chart',
  templateUrl: './chart.component.html',
  styleUrls: ['./chart.component.scss']
})
export class RunStateChartComponent implements AfterViewInit, OnChanges {

  @Input() data: {
    onLineDeviceCount: number;
    offLineDeviceCount: number;
  } = {
    onLineDeviceCount: 0,
    offLineDeviceCount: 0
  };

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
        text: this.translate.instant('device-monitor.run-state-overview'),
        subtext: this.translate.instant('device-monitor.running-devices'),
        left: 0
      },
      grid: {
        width: '100%',
        height: '100%',
        left: 0,
        right: 0
      },
      color: ['#2367fa', '#ff9d4d'],
      tooltip: {
        trigger: 'item'
      },
      legend: {
        align: 'right',
        right: 0
      },
      series: [
        {
          type: 'pie',
          radius: '50%',
          minAngle: 10,
          data: [
            { value: this.data.onLineDeviceCount || 0, name: this.translate.instant('device-monitor.on-line-device') },
            { value: this.data.offLineDeviceCount || 0, name: this.translate.instant('device-monitor.off-line-device') }
          ],
          emphasis: {
            itemStyle: {
              shadowBlur: 10,
              shadowOffsetX: 0,
              shadowColor: 'rgba(0, 0, 0, 0.5)'
            }
          }
        }
      ]
    };
    this.chart.setOption(option);
    this.chart.resize();
  }

}
