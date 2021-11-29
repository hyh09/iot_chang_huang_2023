import { TranslateService } from '@ngx-translate/core';
import { AfterViewInit, Component, ElementRef, Input, ViewChild, OnChanges, OnDestroy } from '@angular/core';
import * as echarts from 'echarts';

@Component({
  selector: 'tb-run-state-chart',
  template: `<div class="chart-panel">
              <div #runStateChart class="chart-wrapper"></div>
            </div>`,
  styleUrls: ['./chart.component.scss']
})
export class RunStateChartComponent implements AfterViewInit, OnDestroy, OnChanges {

  @Input() data: {
    onLineDeviceCount: number;
    offLineDeviceCount: number;
  };

  @ViewChild('runStateChart') runStateChart: ElementRef;

  private chart: any;

  constructor(private translate: TranslateService) { }

  ngAfterViewInit() {
    this.chart = echarts.init(this.runStateChart.nativeElement);
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
        text: this.translate.instant('device-monitor.run-state-overview'),
        subtext: this.translate.instant('device-monitor.running-devices'),
        left: 0,
        textStyle: {
          fontSize: 16,
          color: 'rgba(0, 0, 0, 0.87)'
        }
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
          radius: '60%',
          center: ['50%', '55%'],
          minAngle: 5,
          data: [
            { value: this.data.onLineDeviceCount || 0, name: this.translate.instant('device-monitor.on-line-device') },
            { value: this.data.offLineDeviceCount || 0, name: this.translate.instant('device-monitor.off-line-device') }
          ],
          tooltip: {
            formatter: `{b}：{c}${this.translate.instant('device-monitor.device-count-unit')} ({d}%)`
          },
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
