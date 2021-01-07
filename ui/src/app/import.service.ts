import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})

export class ImportService {
  private baseUrl = 'http://localhost:8080/config-kopernica/import-excel';

  constructor(private http: HttpClient) { }
  
  //importParticipantExcelFile(formData): Observable<any> {
    /*return this.http.post(`${this.baseUrl}/participants/`, formData, {  
      reportProgress: true,  
      observe: 'events'
    });*/
  importParticipantExcelFile(project: string, study: string): Observable<any> {
    return this.http.post(`${this.baseUrl}/participants/${project}/${study}`, null);
  }

}
