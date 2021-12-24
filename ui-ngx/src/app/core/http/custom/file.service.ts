import { HttpClient } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { defaultHttpOptionsFromConfig, RequestConfig } from '../http-utils';
import { ChecksumAlgorithm } from "@app/shared/models/ota-package.models";
import { Observable } from "rxjs";

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

}
