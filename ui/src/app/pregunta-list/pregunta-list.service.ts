import { Pregunta } from './pregunta';
import { Injectable } from '@angular/core';

@Injectable()
export class PreguntaDataStoreService {
  private eData: Pregunta[] = [];

  public getPreguntas() {
    const json = `[
            {
              "text": "¿Qué le pareció el producto?"
            },
            {
               "text": "¿Compraría el producto?"
            },
            {
               "text": "¿Recomendaría el producto?"
            }
          ]`;

    this.eData = JSON.parse(json);

    return this.eData;
  }
}
