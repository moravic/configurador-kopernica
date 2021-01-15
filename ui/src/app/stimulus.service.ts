import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})

export class StimulusService {
  private baseUrl = 'http://localhost:8080/config-kopernica/stimuli';

  constructor(private http: HttpClient) { }
  
  saveStimulus(data): Observable<any> {
    const headers = { 'content-type': 'application/json'}  
    const body=JSON.stringify(data);
    console.log(body);
    return this.http.post(`${this.baseUrl}`,body,{'headers':headers});
  }
  
  getStimuliList(project: string, study: string): Observable<any> {
    return this.http.get(`${this.baseUrl}/getStimuli/${project}/${study}`);
  }

  deleteStimulus(id: number): Observable<any> {
    return this.http.delete(`${this.baseUrl}/deleteStimulus/${id}`, { responseType: 'text' });
  }
  
  deleteAllStimulus(): Observable<any> {
    return this.http.delete(`${this.baseUrl}/deleteAllStimulus/`, { responseType: 'text' });
  }
}
