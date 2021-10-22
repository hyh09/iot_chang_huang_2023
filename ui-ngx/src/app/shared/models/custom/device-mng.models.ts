import { BaseData } from './../base-data';
import { DataDictionaryId } from '../id/custom/data-dictionary-id';

export interface DataDictionary extends BaseData<DataDictionaryId> {
  code: string,
  icon: string,
  type: string,
  unit: string,
  comment: string,
  picture: string
}

export interface DeviceDictionary extends BaseData<DataDictionaryId> {
  code: string,
  type: string,
  supplier: string,
  model: string,
  version: string
}
