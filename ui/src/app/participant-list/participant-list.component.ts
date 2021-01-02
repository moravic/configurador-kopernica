import { Component, OnInit, AfterViewInit, ViewChild } from '@angular/core';
import { MatTableDataSource } from '@angular/material/table';
import { NgForm } from '@angular/forms';
import { Participant } from './participant';
import { ParticipantDataStoreService } from './participant-list.service';
import {MatPaginator} from '@angular/material/paginator';
import {MatSort} from '@angular/material/sort';

@Component({
  selector: 'participant-list',
  templateUrl: './participant-list.component.html',
  styleUrls: ['./participant-list.component.css']
})
export class ParticipantListComponent implements OnInit, AfterViewInit {

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
      'profile',
      'deleteParticipant'
    ];
    this.todaysDate = new Date();
  }

  @ViewChild(MatPaginator) paginator: MatPaginator;
  @ViewChild(MatSort) sort: MatSort;
  @ViewChild('form') ngForm: NgForm;
  
  ngAfterViewInit() {
    this.elistMatTableDataSource.paginator = this.paginator;
    this.elistMatTableDataSource.sort = this.sort;
    
    this.ngForm.form.valueChanges.subscribe(x => {
      //console.log(x);
    });
  }
  
  ngOnInit() {
    this.elistMatTableDataSource.data = this.eDataStore.getParticipants();
    
    this.elistMatTableDataSource.data.push({
      name:'',
      email:'',
      age:'',
      gender:'',
      profile:''
    });
    
  }

  save(form: NgForm): void {
    console.log('Form Status', form.valid);
    console.log('Form Touched', form.touched);
    console.log('Form Untouch', form.untouched);
    console.log('Form Invalid', form.invalid);
    console.log('Form Pristine', form.pristine);
    // do API operation
  }
  
  deletePariticipant(item){
    // find item and remove ist
    this.elistMatTableDataSource.data.splice(this.elistMatTableDataSource.data.indexOf(item.id), 1);
  }

  deleteAll():void{
  	this.elistMatTableDataSource.data = [];
  }
  
  onRowChanged(row,index) {
    this.displayedColumns.forEach( (column) => {
    	console.log("Valid form: ",  this.ngForm.form.controls[column+index].valid);
	});
    
    console.log('Row changed: ', row);
  }


}

