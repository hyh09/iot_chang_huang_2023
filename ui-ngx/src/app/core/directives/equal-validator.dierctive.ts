import { Directive, forwardRef, Attribute } from '@angular/core';
import { Validator, AbstractControl, NG_VALIDATORS } from '@angular/forms';

@Directive({
  selector: '[validateEqual][formControlName],[validateEqual][formControl],[validateEqual][ngModel]',
  providers: [{
    provide: NG_VALIDATORS,
    useExisting: forwardRef(() => EqualValidator), 
    multi: true
  }]
})
export class EqualValidator implements Validator {

  constructor(
    @Attribute('validateEqual') public validateEqual: string,
    @Attribute('reverse') public reverse: string
  ) { }

  private get isReverse() {
    if (!this.reverse) return false;
    return this.reverse === 'true' ? true: false;
  }

  validate(control: AbstractControl): { [key: string]: any } {
    const currVal = control.value; // 获取应用该指令控件的值
    const compareControl = control.root.get(this.validateEqual); // 获取进行值比对的控件

    if (compareControl && compareControl.value && currVal) {
      if (currVal !== compareControl.value && !this.isReverse) {
        return { notEqual: true }
      } else if (currVal === compareControl.value && this.isReverse) {
        if (compareControl.errors !== null) {
          delete compareControl.errors['notEqual'];
          if (!Object.keys(compareControl.errors).length) {
            compareControl.setErrors(null);
          }
        }
      } else if (currVal !== compareControl.value && this.isReverse) {
        compareControl.setErrors({ notEqual: true });
      }
      return null;
    }
    return null;
  }

}
