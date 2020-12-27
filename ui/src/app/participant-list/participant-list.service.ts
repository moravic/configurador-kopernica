import { Participant } from './participant';
import { Injectable } from '@angular/core';

@Injectable()
export class ParticipantDataStoreService {
  private eData: Participant[] = [];

  public getParticipants() {
    const json = `[
            {
              "photo": "http://placehold.it/32x32",
              "age": 25,
              "name": "Aimee Weeks",
              "gender": "female",
              "email": "aimeeweeks@codax.com",
              "dob": "11/08/1988",
              "address": "842 Java Street, Kapowsin, Mississippi, 8052"
            },
            {
              "photo": "http://placehold.it/32x32",
              "age": 22,
              "name": "Vicky Avery",
              "gender": "female",
              "email": "vickyavery@codax.com",
              "dob": "08/11/1988",
              "address": "803 Vanderveer Street, Remington, South Carolina, 1829"
            },
            {
              "photo": "http://placehold.it/32x32",
              "age": 30,
              "name": "Cleveland Vance",
              "gender": "male",
              "email": "clevelandvance@codax.com",
              "dob": "12/21/1986",
              "address": "397 Hamilton Walk, Loretto, Massachusetts, 1096"
            },
            {
              "photo": "http://placehold.it/32x32",
              "age": 40,
              "name": "Craft Frost",
              "gender": "male",
              "email": "craftfrost@codax.com",
              "dob": "02/02/1970",
              "address": "519 Arlington Place, Waukeenah, Delaware, 4549"
            },
            {
              "photo": "http://placehold.it/32x32",
              "age": 23,
              "name": "Debbie Blevins",
              "gender": "female",
              "email": "debbieblevins@codax.com",
              "dob": "02/05/1980",
              "address": "855 Hinckley Place, Edmund, Virginia, 6139"
            },
            {
              "photo": "http://placehold.it/32x32",
              "age": 27,
              "name": "Woodard Lott",
              "gender": "male",
              "email": "woodardlott@codax.com",
              "dob": "01/30/1982",
              "address": "865 Karweg Place, Johnsonburg, Utah, 4270"
            }
          ]`;

    this.eData = JSON.parse(json);

    return this.eData;
  }
}
