import { Stimulus } from './Stimulus';
import { Injectable } from '@angular/core';

@Injectable()
export class EstimuloDataStoreService {
  private eData: Stimulus[] = [];

  public getStimulus() {
    const json = `[
            {
              "name": "Video 1, coche rojo"
            },
            {
               "name": "Video 2, coche verde"
            },
            {
               "name": "Sonido 1"
            }
          ]`;

    this.eData = JSON.parse(json);

    return this.eData;
  }
}
