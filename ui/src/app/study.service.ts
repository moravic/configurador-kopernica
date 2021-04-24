import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})

export class StudyService {
  private baseUrl = 'http://localhost:8091/config-kopernica/studies';

  constructor(private http: HttpClient) { }
  
  addStudy(data): Observable<any> {
    const headers = { 'content-type': 'application/json'}  
    const body=JSON.stringify(data);
    console.log(body);
    return this.http.post(`${this.baseUrl}/addStudy`,body,{'headers':headers});
  }
  
  saveStudy(data): Observable<any> {
    const headers = { 'content-type': 'application/json'}  
    const body=JSON.stringify(data);
    console.log(body);
    return this.http.post(`${this.baseUrl}/saveStudy`,body,{'headers':headers});
  }
  
  getStudy(project: string, study: string): Observable<any> {
    //console.log("test " + project + "/" + study);
    return this.http.get(`${this.baseUrl}/getStudy/${project}/${study}`);
  }

}
