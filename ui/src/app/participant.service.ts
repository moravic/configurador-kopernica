import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})

export class ParticipantService {
  private baseUrl = 'http://localhost:8080/config-kopernica/participants';

  constructor(private http: HttpClient) { }
  
  saveParticipant(data): Observable<any> {
    const headers = { 'content-type': 'application/json'}  
    const body=JSON.stringify(data);
    console.log(body);
    return this.http.post(`${this.baseUrl}`,body,{'headers':headers});
  }
  
  getParticipants(project: string, study: string): Observable<any> {
    return this.http.get(`${this.baseUrl}/getParticipants/${project}/${study}`);
  }

  deleteParticipant(id: number): Observable<any> {
    return this.http.delete(`${this.baseUrl}/deleteParticipant/${id}`, { responseType: 'text' });
  }
  
  deleteAllParticipant(): Observable<any> {
    return this.http.delete(`${this.baseUrl}/deleteAllParticipant/`, { responseType: 'text' });
  }
}
