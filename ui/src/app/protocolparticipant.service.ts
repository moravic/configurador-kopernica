import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})

export class ProtocolparticipantService {
  private baseUrl = 'http://localhost:8091/config-kopernica/protocolparticipant';

  constructor(private http: HttpClient) { }
  
  applyConfiguration(): Observable<any> {
     return this.http.post(`${this.baseUrl}/applyConfiguration`, null);
  }
  

}
