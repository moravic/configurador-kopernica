import { Component, OnInit, Input, AfterViewInit, ViewChild, OnChanges } from '@angular/core';
import { MatTableDataSource } from '@angular/material/table';
import { FormArray, FormGroup, FormBuilder } from '@angular/forms';
import { Participant } from '../participant';
import { MatPaginator} from '@angular/material/paginator';
import { MatSort} from '@angular/material/sort';
import { MatIconRegistry } from "@angular/material/icon";
import { DomSanitizer } from "@angular/platform-browser";
import { Observable} from 'rxjs';
import { map, startWith} from 'rxjs/operators';
import { FormControl, ReactiveFormsModule} from '@angular/forms';

@Component({
  selector: 'participant-list',
  templateUrl: './participant-list.component.html',
  styleUrls: ['./participant-list.component.css']
})
export class ParticipantListComponent implements OnInit, AfterViewInit {

  title = 'Participants Table';
  @Input()
  participants:Participant[];
  
  elistMatTableDataSource = new MatTableDataSource<Participant>();
  displayedColumns: string[];
  todaysDate: Date;
  profileOptions: Observable<string[]>[] = [];
  profileListItems:string[]=['Vegano','No Vegano', 'Vegana'];
  
  form: FormGroup;
  myProfileControl=[];
    
  constructor(
    private matIconRegistry: MatIconRegistry,
    private domSanitizer: DomSanitizer,
    private formBuilder: FormBuilder
  ){

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
  
  ngAfterViewInit() {
    this.elistMatTableDataSource.paginator = this.paginator;
    this.elistMatTableDataSource.sort = this.sort;
    
  }
    
  private _filter(value: string): string[] {
    const filterValue = value.toLowerCase();
    
    console.log('filterValue ' + filterValue);
    const results = this.profileListItems.filter(option => option.toLowerCase().indexOf(filterValue) === 0);
    
    console.log('results ' + results);
    
    return results;
  }
  
  ngOnInit() {    
  }
  
  ngOnChanges() {
    console.log("ngOnChanges " + this.participants);
    this.elistMatTableDataSource = new MatTableDataSource<Participant>(this.participants);
    
    this.form= this.formBuilder.group({
       participants: this.formBuilder.array([])
    });
    
    this.setParticipantsForm();
    this.form.get('participants').valueChanges.subscribe(
        participants => {console.log('participants', participants)}
    );
  }
  
  private setParticipantsForm(){
    const participantCtrl = this.form.get('participants') as FormArray;
    let index = 0;
    this.elistMatTableDataSource.data.forEach((participant)=>{
      console.log("participant.name: " + participant.name);
      var formGroup = this.setParticipantsFormArray(participant);
      participantCtrl.push(formGroup);
      
      this.profileOptions[index++] = formGroup.get('profile').valueChanges.pipe(
      	startWith<string | Participant>(''),
	      map(value => typeof value === 'string' ? value : value.profile),
	      map(profile => profile ? this._filter(profile) : this.profileListItems.slice())
      );
    })
  };
   
  private setParticipantsFormArray(participant){
    return this.formBuilder.group({
        name:[participant.name],
        email:[participant.email],
        age:[participant.age], 
        gender:[participant.gender],
        profile:[participant.profile]
    });
  }
  
  save(form: FormGroup): void {
    console.log('Form Status', form.valid);
    console.log('Form Touched', form.touched);
    console.log('Form Untouch', form.untouched);
    console.log('Form Invalid', form.invalid);
    console.log('Form Pristine', form.pristine);
    // do API operation
  }
  
  importCsv():void{
  
  console.log('importCsv');
  }
  
  exportCsv():void{
  console.log('exportCsv');
  }
  
  addNew():void{
  	console.log('addNew');
    this.elistMatTableDataSource.data.push({
      name:'',
      email:'',
      age:'',
      gender:'',
      profile:''
   	 });
   	 
   	 //this.elistMatTableDataSource.filter = "";
   	 console.log('addNew 2');
  }
  
  deleteParticipant(item){
  	console.log('deleteParticipant');
    // find item and remove ist
    this.elistMatTableDataSource.data.splice(this.elistMatTableDataSource.data.indexOf(item.id), 1);
    //this.elistMatTableDataSource.filter = "";
  }

  deleteAll():void{
    console.log('deleteAll');
  	this.elistMatTableDataSource.data = [];
  }
  
  onRowChanged(row,index) {
    this.displayedColumns.forEach( (column) => {
    	//console.log("Valid form: ",  this.ngForm.form.controls[column+index].valid);
	});
    
    //console.log('Row changed: ', row);
  }


}

