import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})

export class QuestionService {
  private baseUrl = 'http://localhost:8080/config-kopernica/questions';

  constructor(private http: HttpClient) { }
  
  saveQuestion(data): Observable<any> {
    const headers = { 'content-type': 'application/json'}  
    const body=JSON.stringify(data);
    console.log(body);
    return this.http.post(`${this.baseUrl}`,body,{'headers':headers});
  }
  
  getQuestions(project: string, study: string): Observable<any> {
    return this.http.get(`${this.baseUrl}/getQuestions/${project}/${study}`);
  }

  getNewId(): Observable<any> {
    return this.http.get(`${this.baseUrl}/getNewId/`);
  }
  
  deleteQuestion(id: number): Observable<any> {
    return this.http.delete(`${this.baseUrl}/deleteQuestion/${id}`, { responseType: 'text' });
  }
  
  deleteAllQuestion(): Observable<any> {
    return this.http.delete(`${this.baseUrl}/deleteAllQuestion/`, { responseType: 'text' });
  }
}
