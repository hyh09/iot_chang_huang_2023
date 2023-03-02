import { HttpClient } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { defaultHttpOptionsFromConfig, RequestConfig } from '../http-utils';
import { ChecksumAlgorithm } from "@app/shared/models/ota-package.models";
import { Observable } from "rxjs";
import { tap } from "rxjs/operators";

@Injectable({
  providedIn: 'root'
})

export class FileService {

  constructor(
    private http: HttpClient
  ) { }

  // 上传文件
  public uploadFile(file: File, checksum?: string, checksumAlgorithm?: ChecksumAlgorithm): Observable<any> {
    const formData = new FormData();
    formData.append('file', file);
    checksum && formData.append('checksum', checksum);
    checksumAlgorithm && formData.append('checksumAlgorithm', checksumAlgorithm);
    return this.http.post(`/api/file`, formData, { responseType: 'text' });
  }

  // 删除文件
  public deleteFile(id: string, config?: RequestConfig) {
    return this.http.delete(`/api/file/${id}`, defaultHttpOptionsFromConfig(config));
  }

  // 导出表格数据
  public exportTable(fileName: string, dataList: Array<any>) {
    return this.http.post(`/api/excel/export`, {
      title: fileName || '',
      dataList: dataList || []
    }, { responseType: 'arraybuffer' }).pipe(tap(res => {
      var blob = new Blob([res], { type: 'application/vnd.ms-excel;' });
      var link = document.createElement('a');
      var href = window.URL.createObjectURL(blob);
      link.href = href;
      link.download = fileName;
      document.body.appendChild(link);
      link.click();
      document.body.removeChild(link);
      window.URL.revokeObjectURL(href);
    }));
  }

}
