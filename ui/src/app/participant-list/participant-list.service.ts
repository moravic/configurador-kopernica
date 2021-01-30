import { Participant } from './participant';
import { Injectable } from '@angular/core';

@Injectable()
export class ParticipantDataStoreService {
  private eData: Participant[] = [];

  public getParticipants() {
    const json = `[
            {
              "age": 25,
              "name": "Aimee Weeks",
              "gender": "Mujer",
              "email": "aimeeweeks@codax.com",
              "profile": "Vegana",
              "group": "grupo1"
            },
            {
              "age": 22,
              "name": "Vicky Avery",
              "gender": "Mujer",
              "email": "vickyavery@codax.com",
              "profile": "Vegana",
              "group": "grupo1"
            },
            {
              "age": 30,
              "name": "Cleveland Vance",
              "gender": "Hombre",
              "email": "clevelandvance@codax.com",
              "profile": "Vegano",
              "group": "grupo1"
            },
            {
              "age": 40,
              "name": "Craft Frost",
              "gender": "Hombre",
              "email": "craftfrost@codax.com",
              "profile": "No Vegano",
              "group": "grupo1"
            },
            {
              "age": 23,
              "name": "Debbie Blevins",
              "gender": "Mujer",
              "email": "debbieblevins@codax.com",
              "profile": "No Vegana",
              "group": "grupo1"
            },
            {
              "age": 27,
              "name": "Woodard Lott",
              "gender": "Hombre",
              "email": "woodardlott@codax.com",
              "profile": "No Vegano",
              "group": "grupo1"
            }
          ]`;

    this.eData = JSON.parse(json);

    return this.eData;
  }
}
