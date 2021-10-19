import { BaseData } from './../base-data';
import { DataDictionaryId } from '../id/custom/data-dictionary-id';

export interface DataDictionary extends BaseData<DataDictionaryId> {
  code: string,
  icon: string,
  dataType: string,
  unit: string
}
