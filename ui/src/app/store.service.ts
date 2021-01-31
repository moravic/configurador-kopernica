import { Injectable } from '@angular/core';
import { Observable, BehaviorSubject } from 'rxjs';
import { Stimulus } from './stimulus';

@Injectable()
export class StoreService {
  private _stimuli = new BehaviorSubject<Stimulus[]>([]);
 
  //stimuli = this._stimuli.asObservable();
  
  private _elementChange = new BehaviorSubject<string>(null);
  private _groupChange = new BehaviorSubject<string>(null);
  
  constructor() { }

  public getStimuli(): Observable<Stimulus[]> {
    return this._stimuli.asObservable();
  }

  public setStimuli(stimuli: Stimulus[]): void {
    this._stimuli.next(stimuli);
  }
  
  public getElementChange(): Observable<string> {
    return this._elementChange.asObservable();
  }

  public broadcastElementChange(elementChange:string): void {
    this._elementChange.next(elementChange);
  }
  
  public getGroupChange(): Observable<string> {
    return this._groupChange.asObservable();
  }

  public broadcastGroupChange(groupChange:string): void {
    this._groupChange.next(groupChange);
  }
}