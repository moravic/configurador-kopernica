import { Component, OnInit } from '@angular/core';
import { MatTableDataSource } from '@angular/material/table';
import { NgForm } from '@angular/forms';
import { Participant } from './participant';
import { ParticipantDataStoreService } from './participant-list.service';

@Component({
  selector: 'participant-list',
  templateUrl: './participant-list.component.html',
  styleUrls: ['./participant-list.component.css']
})
export class ParticipantListComponent implements OnInit {

  title = 'table-validation-demo';
  elistMatTableDataSource = new MatTableDataSource<Participant>();
  displayedColumns: string[];
  todaysDate: Date;

  constructor(private eDataStore: ParticipantDataStoreService) {
    this.displayedColumns = [
      'name',
      'email',
      'age',
      'gender',
      'profile'
    ];
    this.todaysDate = new Date();
  }

  ngOnInit() {
    this.elistMatTableDataSource.data = this.eDataStore.getParticipants();
  }

  save(form: NgForm): void {
    console.log('Form Status', form.valid);
    console.log('Form Touched', form.touched);
    console.log('Form Untouch', form.untouched);
    console.log('Form Invalid', form.invalid);
    console.log('Form Pristine', form.pristine);
    // do API operation
  }
}

