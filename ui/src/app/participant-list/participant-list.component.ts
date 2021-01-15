import { Component, ElementRef, OnInit, Input, AfterViewInit, ViewChild, OnChanges, ChangeDetectorRef, AfterContentChecked } from '@angular/core';
import { MatTableDataSource } from '@angular/material/table';
import { FormArray, FormGroup, FormBuilder, Validators } from '@angular/forms';
import { Participant } from '../participant';
import { MatPaginator} from '@angular/material/paginator';
import { MatSort} from '@angular/material/sort';
import { MatIconRegistry } from "@angular/material/icon";
import { DomSanitizer } from "@angular/platform-browser";
import { Observable} from 'rxjs';
import { map, startWith} from 'rxjs/operators';
import { AbstractControl, FormControl, ReactiveFormsModule} from '@angular/forms';
import { ParticipantService } from '../participant.service';
import { ImportService } from '../import.service';
import { ExportService } from '../export.service';
import { MatDialogOkComponent } from '../mat-dialog-ok/mat-dialog-ok.component';
import { MatDialog } from '@angular/material/dialog';

@Component({
  selector: 'participant-list',
  templateUrl: './participant-list.component.html',
  styleUrls: ['./participant-list.component.css']
})

export class ParticipantListComponent implements OnInit, AfterViewInit {

  title = 'Participants Table';
  @Input()
  participants:Participant[];
  @Input()
  project:string;
  @Input()
  study:string;
   @Input()
  disabled: boolean;
  
  elistMatTableDataSource = new MatTableDataSource<any>();
  displayedColumns: string[];
  todaysDate: Date;
  profileOptions: Observable<string[]>[] = [];
  profileListItems:string[];

  form: FormGroup;
  myProfileControl=[];
  error_str:string;
  formData = new FormData();
    
  constructor(
    private participantService:ParticipantService,
    private importService:ImportService,
    private exportService:ExportService,
    private matIconRegistry: MatIconRegistry,
    private domSanitizer: DomSanitizer,
    private formBuilder: FormBuilder,
    private cdr: ChangeDetectorRef,
    private dialog: MatDialog
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
  @ViewChild('importInput') importInput: ElementRef;
  
  private uniques(arr) {
    var a = [];
    for (var i=0, l=arr.length; i<l; i++)
        if (a.indexOf(arr[i].profile) === -1 && arr[i].profile !== '')
            a.push(arr[i].profile);
    return a;
  }

  ngAfterViewInit() {
    this.elistMatTableDataSource.paginator = this.paginator;
    this.elistMatTableDataSource.sort = this.sort;
  }
  
  ngAfterContentChecked() : void {
     this.cdr.detectChanges();
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
    this.updateParticipants();
  }
  
  updateParticipants() {
    if (!this.participants)
      return;
        
    console.log("updateParticipants " + this.participants);
    
    this.profileListItems = this.uniques(this.participants);
        
    this.form= this.formBuilder.group({
       participants: this.formBuilder.array([])
    });
    
    this.setParticipantsForm();
    /*this.form.get('participants').valueChanges.subscribe(
        participants => {console.log('participants', participants)}
    );*/
    
    this.elistMatTableDataSource = new MatTableDataSource((this.participantFormArray as FormArray).controls);
    
    // adjust filtering and sorting logic to support AbstractControl object.
  	this.elistMatTableDataSource.sortingDataAccessor = (data: AbstractControl, sortHeaderId: string) => {
      const value: any = data.value[sortHeaderId];
      return typeof value === "string" ? value.toLowerCase() : value;
    };
    
    const filterPredicate = this.elistMatTableDataSource.filterPredicate;
    this.elistMatTableDataSource.filterPredicate = (data: AbstractControl, filter) => {
      return filterPredicate.call(this.elistMatTableDataSource, data.value, filter);
    };
    
    this.elistMatTableDataSource.paginator = this.paginator;
    this.elistMatTableDataSource.sort = this.sort;
   
  }
  
  private setParticipantsForm(){
    let index = 0;
    this.participants.forEach((participant)=>{
      console.log("participant.name: " + participant.name);
      this.setFormGroup(participant, index);
      this.participantFormArray.controls[index].valueChanges.subscribe(
        participant => {
			this.saveParticipant(participant);
        	console.log('participant', participant)
      });
      index++;
    })
  };
  
  get participantFormArray():FormArray {
     return this.form.get('participants') as FormArray;
  }
 
  private setFormGroup(participant:Participant, index:number){
      var formGroup = this.setParticipantsFormArray(participant);
      this.participantFormArray.push(formGroup);
      
      this.profileOptions[index] = formGroup.get('profile').valueChanges.pipe(
      	startWith<string | Participant>(''),
	      map(value => typeof value === 'string' ? value : value.profile),
	      map(profile => profile ? this._filter(profile) : this.profileListItems.slice())
      );
  }
  
  private setParticipantsFormArray(participant){
    return this.formBuilder.group({
        id:participant.id,
        name:participant.name,
        email:participant.email,
        age:participant.age, 
        gender:participant.gender,
        profile:participant.profile
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
  
  //importExcel(files):void{
  importExcel():void{
    if (!this.project || !this.study)
      return;
  	console.log('importExcel');
  	//const file = files[0];
    //this.formData.append("file", file );
    //this.importService.importParticipantExcelFile(this.formData)
    this.importService.importParticipantExcelFile(this.project, this.study)
	      .subscribe(data => {
	  console.log('Excel Imported ' + data);
	  this.participants = data;
	  this.updateParticipants();
	  this.openDialog("Info", "Los participantes han sido importados.");
    }, error =>  this.error_str=error.error.message);
  }
  
  exportExcel():void{
    if (!this.project || !this.study)
      return;
    console.log('exportExcel');
    this.exportService.exportParticipantExcelFile(this.project, this.study)
	      .subscribe(data => {
	  this.openDialog("Info", "Los participantes han sido exportados.");
	  console.log('Excel Exported');
    }, error =>  this.error_str=error.error.message);
    
  }
  
  addNew():void{
  	console.log('addNew');
  	const participant:Participant = {
      id:0,
      name:'',
      email:'',
      age:'',
      gender:'',
      profile:''
   	 };
   	 
     //this.elistMatTableDataSource.data.push(participant);
   	 
   	 this.setFormGroup(participant, this.form.controls.participants.value.length);
   	 this.participantFormArray.controls[this.form.controls.participants.value.length-1].valueChanges.subscribe(
        participant => {
		this.saveParticipant(participant);
	 });
	 this.elistMatTableDataSource.filter = "";
  }
  
  deleteParticipant(participant){
  	console.log('deleteParticipant: ' + participant.controls.id.value);
  	var index =  this.participantFormArray.controls.findIndex(fg => fg.value.id === participant.controls.id.value);
    this.participantService.deleteParticipant(participant.controls.id.value).subscribe(data => {
	    console.log("Delete " + data);
     }, error =>  this.error_str=error.error.message);
    // find item and remove ist
    this.elistMatTableDataSource.data.splice(index, 1);
    //this.participantFormArray.controls.splice(index, 1);
    this.profileListItems = this.uniques(this.elistMatTableDataSource.data);
    this.elistMatTableDataSource.filter = "";
  }

  deleteAll():void{
    console.log('deleteAll');
  	this.elistMatTableDataSource.data = [];
  	this.profileListItems = [];
  	this.participantService.deleteAllParticipant().subscribe(data => {
	    console.log("Delete " + data);
	    this.openDialog("Info", "Los participantes han sido borrados.");
     }, error =>  this.error_str=error.error.message);
  }
  
  saveParticipant(participant){
	var index =  this.participantFormArray.value.findIndex(part => part.id === participant.id);
    if ((this.form.controls.participants as FormArray).controls[index].valid) {
      this.participantService.saveParticipant(participant)
      .subscribe(data => {
        console.log("Save " + data);
        /*this.elistMatTableDataSource.data[index].id = data;
        this.elistMatTableDataSource.data[index].name = participant.name;
        this.elistMatTableDataSource.data[index].email = participant.email;
        this.elistMatTableDataSource.data[index].age = participant.age;
        this.elistMatTableDataSource.data[index].gender = participant.gender;
        this.elistMatTableDataSource.data[index].profile = participant.profile;*/
        
        this.profileListItems = this.uniques(this.participantFormArray.value);
            
        if (this.participantFormArray.value[index].id != data) {
            this.participantFormArray.value[index].id = data;
	        this.participantFormArray.controls[index].setValue({
		        id:data,
		        name:participant.name,
		        email:participant.email,
		        age:participant.age, 
		        gender:participant.gender,
		        profile:participant.profile
		    });
	    }
        this.elistMatTableDataSource.paginator = this.paginator;
        this.elistMatTableDataSource.sort = this.sort;
      }, error =>  this.error_str=error.error.message);
    }
  }
  
  onRowChanged(row, index) {
    /*if (this.form.controls.participants.valid) {
      this.participantChanged = this.participantFormArray.value[index];
      this.participantService.saveParticipant(this.participantChanged)
	      .subscribe(data => {
	        console.log("Save " + data);
	        this.elistMatTableDataSource.data[index].id = data;
	        this.elistMatTableDataSource.data[index].name = this.participantChanged.name;
	        this.elistMatTableDataSource.data[index].email = this.participantChanged.email;
	        this.elistMatTableDataSource.data[index].age = this.participantChanged.age;
	        this.elistMatTableDataSource.data[index].gender = this.participantChanged.gender;
	        this.elistMatTableDataSource.data[index].profile = this.participantChanged.profile;
	        this.participantFormArray.value[index].id = data;
	        //this.participantFormArray.controls[index] = this.setParticipantsFormArray(this.elistMatTableDataSource.data[index]);
	        this.participantFormArray.controls[index].setValue({
		        id:data,
		        name:this.participantChanged.name,
		        email:this.participantChanged.email,
		        age:this.participantChanged.age, 
		        gender:this.participantChanged.gender,
		        profile:this.participantChanged.profile
		    });
		    
	        //this.elistMatTableDataSource.filter = "";
	        //this.elistMatTableDataSource.paginator = this.paginator;
            //this.elistMatTableDataSource.sort = this.sort;
	      }, error =>  this.error_str=error.error.message);
    }*/
    //console.log('Row changed');
  }
  
  getActualIndex(index : number)  {
    return index + this.paginator.pageSize * this.paginator.pageIndex;
  }
  
  openDialog(title, content) {
    let dialogRef = this.dialog.open(MatDialogOkComponent, {
      data: {
        title: title,
        content: content
      }
    });
    
    dialogRef.afterClosed().subscribe(result => {
      if (result == 'confirm') {
        console.log('Confirmado');
      }
    })
  }


}

