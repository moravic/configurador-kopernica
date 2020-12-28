import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class AppService {

  private baseUrl = 'http://localhost:8080/config-kopernica/app/';

  constructor(private http: HttpClient) { }

  getListProjects(): Observable<any> {
    return this.http.get(`${this.baseUrl}`);
  } 
  
  getListEstudios(project: String): Observable<any> {
    return this.http.get(`${this.baseUrl}/estudios/${project}`);
  } 
  
  setProperties(project: string, study: string): Observable<Object> {
    //console.log("test " + project + "/" + study);
    return this.http.get(`${this.baseUrl}properties/${project}/${study}`);
  }
  
}