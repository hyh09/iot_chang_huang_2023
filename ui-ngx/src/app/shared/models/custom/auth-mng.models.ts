import { BaseData } from './../base-data';
import { UserInfoId } from '../id/custom/user-mng-id.models';
import { RoleId } from '../id/custom/role-mng.model';

export interface UserInfo extends BaseData<UserInfoId> {
  factoryId?: string,
  userCode: string,
  userName: string,
  userLevel?: number,
  phoneNumber: string,
  email: string,
  activeStatus: string,
  roleIds: Array<string>
}

export interface Role extends BaseData<RoleId> {
  roleCode: string,
  roleName: string
}
