import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})

export class ExportService {
  private baseUrl = 'http://localhost:8080/config-kopernica/export-excel';

  constructor(private http: HttpClient) { }
  
  exportParticipantExcelFile(project: string, study: string): Observable<any> {
     return this.http.post(`${this.baseUrl}/participants/${project}/${study}`, null);
  }
  
  exportQuestionExcelFile(project: string, study: string): Observable<any> {
     return this.http.post(`${this.baseUrl}/questions/${project}/${study}`, null);
  }
  
  exportStimulusExcelFile(project: string, study: string): Observable<any> {
     return this.http.post(`${this.baseUrl}/stimuli/${project}/${study}`, null);
  }  
}
