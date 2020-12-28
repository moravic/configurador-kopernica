import { Estimulo } from './Estimulo';
import { Injectable } from '@angular/core';

@Injectable()
export class EstimuloDataStoreService {
  private eData: Estimulo[] = [];

  public getEstimulos() {
    const json = `[
            {
              "text": "Video 1, coche rojo"
            },
            {
               "text": "Video 2, coche verde"
            },
            {
               "text": "Sonido 1"
            }
          ]`;

    this.eData = JSON.parse(json);

    return this.eData;
  }
}
