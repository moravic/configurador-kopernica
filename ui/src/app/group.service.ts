import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})

export class GroupService {
  private baseUrl = 'http://localhost:8091/config-kopernica/group';

  constructor(private http: HttpClient) { }
  
  getGroups(): Observable<any> {
    return this.http.get(`${this.baseUrl}/getGroups/`);
  }
  
  saveGroupProtocol(protocolId, groupListArray): Observable<any> {
    console.log(groupListArray);
    return this.http.put(`${this.baseUrl}/saveGroupProtocol/${protocolId}`, groupListArray);
  }
  
  deleteGroupProtocol(protocolId, groupId): Observable<any> {
    return this.http.delete(`${this.baseUrl}/deleteGroupProtocol/${protocolId}/${groupId}`);
  }
  
}
