import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})

export class ProtocolService {
  private baseUrl = 'http://localhost:8080/config-kopernica/protocol';

  constructor(private http: HttpClient) { }
  
  getProtocols(): Observable<any> {
    return this.http.get(`${this.baseUrl}/getProtocols/`);
  }

  getBlockElementsList(): Observable<any> {
    return this.http.get(`${this.baseUrl}/getBlockElementsList/`);
  }
  
  getBlocks(): Observable<any> {
    return this.http.get(`${this.baseUrl}/getBlocks/`);
  }
  
  saveSegmentList(protocolId, protocolName, segmentListArray): Observable<any> {
    console.log(segmentListArray);
    return this.http.put(`${this.baseUrl}/saveSegmentList/${protocolId}/${protocolName}`, segmentListArray);
  }
  
  saveBlockList(protocolId, protocolName, blockList): Observable<any> {
    console.log(blockList);
    return this.http.put(`${this.baseUrl}/saveSegmentList/${protocolId}/${protocolName}`, blockList);
  }
  
}
