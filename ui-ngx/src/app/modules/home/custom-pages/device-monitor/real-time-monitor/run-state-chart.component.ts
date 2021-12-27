import { TranslateService } from '@ngx-translate/core';
import { AfterViewInit, Component, ElementRef, Input, ViewChild, OnChanges } from '@angular/core';
import * as echarts from 'echarts';
import { viewPortResize } from '@app/core/utils';

@Component({
  selector: 'tb-run-state-chart',
  template: `<div class="chart-panel">
              <div #runStateChart class="chart-wrapper"></div>
            </div>`,
  styleUrls: ['./chart.component.scss']
})
export class RunStateChartComponent implements AfterViewInit, OnChanges {

  @Input() data: {
    onLineDeviceCount: number;
    offLineDeviceCount: number;
  };

  @ViewChild('runStateChart') runStateChart: ElementRef;

  private chart: any;

  constructor(private translate: TranslateService) { }

  ngAfterViewInit() {
    this.chart = echarts.init(this.runStateChart.nativeElement);
    viewPortResize.subscribe(() => {
      this.chart.resize();
      setTimeout(() => {
        this.chart.resize();
      }, 100);
    });
  }

  ngOnChanges() {
    this.init();
  }

  init() {
    if (!this.chart) return;
    const online = this.translate.instant('device-monitor.on-line-device');
    const offline = this.translate.instant('device-monitor.off-line-device');
    const chartData = [
      { value: this.data.onLineDeviceCount || 0, name: online },
      { value: this.data.offLineDeviceCount || 0, name: offline }
    ];
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
        align: 'left',
        right: 0,
        formatter: (name: string) => {
          switch(name) {
            case online:
              return this.translate.instant('device-monitor.on-line-device-with-value', { count: chartData[0].value });
            case offline:
              return this.translate.instant('device-monitor.off-line-device-with-value', { count: chartData[1].value });
            default:
              return '';
          }
        }
      },
      series: [
        {
          type: 'pie',
          radius: '60%',
          center: ['50%', '55%'],
          minAngle: 5,
          data: chartData,
          tooltip: {
            formatter: `{b}${this.translate.instant('common.colon')}{c}${this.translate.instant('device-monitor.device-count-unit')} ({d}%)`
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
