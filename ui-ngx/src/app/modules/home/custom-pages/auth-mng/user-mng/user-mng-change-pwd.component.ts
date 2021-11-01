import { Component, Inject, OnInit } from "@angular/core";
import { FormGroup, FormBuilder, Validators, FormControl } from "@angular/forms";
import { MatDialogRef, MAT_DIALOG_DATA } from "@angular/material/dialog";
import { Router } from "@angular/router";
import { AppState } from "@app/core/core.state";
import { UserMngService } from "@app/core/http/custom/user-mng.service";
import { DialogComponent } from "@app/shared/public-api";
import { Store } from "@ngrx/store";
import { DeviceCompDialogData } from "../../device-mng/device-dictionary/device-comp-form.component";

@Component({
  selector: 'tb-user-mng-change-pwd',
  templateUrl: './user-mng-change-pwd.component.html'
})
export class UserMngChangePwdComponent extends DialogComponent<UserMngChangePwdComponent, DeviceCompDialogData> implements OnInit {

  public form: FormGroup;

  constructor(
    protected store: Store<AppState>,
    protected router: Router,
    public dialogRef: MatDialogRef<UserMngChangePwdComponent, DeviceCompDialogData>,
    protected fb: FormBuilder,
    protected userMngService: UserMngService,
    @Inject(MAT_DIALOG_DATA) protected userId: string
  ) {
    super(store, router, dialogRef);
  }

  ngOnInit() {
    this.buildForm();
  }

  buildForm() {
    this.form = this.fb.group({
      password: ['', Validators.required],
      confirmPwd: ['', [Validators.required]]
    });
  }
  // , (control: FormControl) => {
  //   const password = this.form.value.password;
  //   const confirmPwd = control.value;
  //   if (password && confirmPwd && password !== confirmPwd) {
  //     return { pwdNotEqual: true };
  //   }
  // }

  cancel() {
    this.dialogRef.close(null);
  }

  save() {
    if (this.form.valid) {
      this.userMngService.changeUserPwd({
        userId: this.userId,
        password: this.form.value.password
      }).subscribe(res => {
        if (res === 'success') {
          this.dialogRef.close(null);
        }
      });
    }
  }

}
