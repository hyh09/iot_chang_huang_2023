import { Component, ElementRef, ViewChild, AfterViewInit, OnDestroy, Output, EventEmitter } from '@angular/core';

@Component({
  selector: 'tb-slide-validator',
  templateUrl: './slide-validator.component.html',
  styleUrls: ['./slide-validator.component.scss']
})
export class SlideValidatorComponent implements AfterViewInit, OnDestroy {

  @Output('success') successEmitter: EventEmitter<void> = new EventEmitter<void>();

  @ViewChild('slideValidator') private box: ElementRef;
  @ViewChild('slideBg') private bg: ElementRef;
  @ViewChild('slider') private slider: ElementRef;

  private slideToRightIcon = 'mdi:slide-to-right';
  private verifiedIcon = 'mdi:verified';
  slideIcon = this.slideToRightIcon;

  private slideToRightTxt = 'login.slide-to-validate';
  private verifiedTxt = 'login.slide-verified';
  slideTxt = this.slideToRightTxt;

  private $box: HTMLElement;
  private $bg: HTMLElement;
  private $slider: HTMLElement;

  private downX: number;

  private successMoveDistance: number;

  isSuccess = false;

  constructor() { }

  ngAfterViewInit() {
    this.$box = this.box.nativeElement;
    this.$bg = this.bg.nativeElement;
    this.$slider = this.slider.nativeElement;

    this.setSuccessDistance();
    window.addEventListener('resize', this.setSuccessDistance);

    this.$slider.onmousedown = event => { this.mousedownHandler(event); };
  }

  ngOnDestroy() {
    window.removeEventListener('resize', this.setSuccessDistance);
  }

  private setSuccessDistance() {
    this.successMoveDistance = this.$box.offsetWidth - this.$slider.offsetWidth;
  }

  private mousedownHandler(event: MouseEvent) {
    this.$bg.style.transition = '';
    this.$slider.style.transition = '';
    this.downX = event.clientX;
    document.onmousemove = event => { this.mousemoveHandler(event); };
    document.onmouseup = event => { this.mouseupHandler(event); };
  }

  private mousemoveHandler(event: MouseEvent) {
    event.preventDefault();
    const moveX = event.clientX;
    const offsetX = this.getOffsetX(moveX - this.downX, 0, this.successMoveDistance);
    this.$bg.style.width = `${offsetX}px`;
    this.$slider.style.left = `${offsetX}px`;

    if (offsetX == this.successMoveDistance) {
      this.success();
    }
  }

  private mouseupHandler(event: MouseEvent) {
    if (!this.isSuccess) {
      this.$bg.style.width = '0';
      this.$slider.style.left = '0';
      this.$bg.style.transition = 'width 0.3s linear';
      this.$slider.style.transition = 'left 0.3s linear';
    }
    document.onmousemove = null;
    document.onmouseup = null;
  }

  private getOffsetX(offset: number, min: number, max: number): number {
    if (offset < min) {
      offset = min;
    } else if(offset > max){
      offset = max;
    }
    return offset;
  }

  success() {
    this.isSuccess = true;
    this.slideTxt = this.verifiedTxt;
    this.$bg.style.backgroundColor ="lightgreen";
    this.$slider.className = "slider active";
    this.slideIcon = this.verifiedIcon;
    this.$slider.onmousedown = null;
    document.onmousemove = null;
    this.successEmitter.emit();
  }

}
