import { BaseData } from './../base-data';
import { UserInfoId } from '../id/custom/user-mng-id.models';
import { RoleId } from '../id/custom/role-mng.model';

export interface UserInfo extends BaseData<UserInfoId> {
  userCode: string,
  userName: string,
  phoneNumber: string,
  email: string,
  activeStatus: string,
  roleIds: Array<string>
}

export interface Role extends BaseData<RoleId> {

}
