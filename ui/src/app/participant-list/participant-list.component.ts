import { Component, ElementRef, OnInit, Input, AfterViewInit, ViewChild, OnChanges, ChangeDetectorRef, AfterContentChecked, Output, EventEmitter } from '@angular/core';
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
import { StoreService } from '../store.service';

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
  typeStudy:string;
  @Input()
  disabled: boolean;
  
  @Output() refresh = new EventEmitter();

  
  elistMatTableDataSource = new MatTableDataSource<any>();
  displayedColumns: string[];
  todaysDate: Date;
  profileOptions: Observable<string[]>[] = [];
  profileListItems:string[];
  groupOptions: Observable<string[]>[] = [];
  groupListItems:string[];

  form: FormGroup;
  myProfileControl=[];
  myGroupControl=[];
  error_str:string;
  formData = new FormData();
    
  constructor(
    private participantService:ParticipantService,
    private storeService: StoreService,
    private importService:ImportService,
    private exportService:ExportService,
    private matIconRegistry: MatIconRegistry,
    private domSanitizer: DomSanitizer,
    private formBuilder: FormBuilder,
    private cdr: ChangeDetectorRef,
    private dialog: MatDialog
  ){

    if (this.typeStudy == "0") {
    	this.displayedColumns = [
      		'name',
      		'email',
      		'age',
      		'gender',
      		'profile',
      		'group',
      		'deleteParticipant'
    		];
    } else {
       	this.displayedColumns = [
      		'name',
      		'email',
      		'age',
      		'gender',
      		'profile',
      		'deleteParticipant'
    		];
    }
    		
    this.todaysDate = new Date();
  }

  @ViewChild(MatPaginator) paginator: MatPaginator;
  @ViewChild(MatSort) sort: MatSort;
  @ViewChild('importInput') importInput: ElementRef;
  
  private uniques(arr) {
    var a = [];
    for (var i=0, l=arr.length; i<l; i++)
        if (a.indexOf(arr[i].value.profile) === -1 && arr[i].value.profile !== '')
            a.push(arr[i].value.profile);
    return a;
  }
  
    private uniquesGroup(arr) {
    var a = [];
    for (var i=0, l=arr.length; i<l; i++)
        if (a.indexOf(arr[i].value.group) === -1 && arr[i].value.group !== '')
            a.push(arr[i].value.group);
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
  
    private _filterGroup(value: string): string[] {
    const filterValue = value.toLowerCase();
    
    console.log('filterValue ' + filterValue);
    const results = this.groupListItems.filter(option => option.toLowerCase().indexOf(filterValue) === 0);
    
    console.log('results ' + results);
    
    return results;
  }
  
  ngOnInit() {

  }
  
  ngOnChanges() {
    console.log("ngOnChanges " + this.participants);
    if (this.typeStudy == "0") {
    	this.displayedColumns = [
      		'name',
      		'email',
      		'age',
      		'gender',
      		'profile',
      		'group',
      		'deleteParticipant'
    		];
    } else {
       	this.displayedColumns = [
      		'name',
      		'email',
      		'age',
      		'gender',
      		'profile',
      		'deleteParticipant'
    		];
    }
    this.profileOptions=[];
    this.groupOptions=[];
    this.updateParticipants();
  }
  
  updateParticipants() {
    if (!this.participants)
      return;
        
    console.log("updateParticipants " + this.participants);
            
    this.form= this.formBuilder.group({
       participants: this.formBuilder.array([])
    });
    
    this.setParticipantsForm();
    /*this.form.get('participants').valueChanges.subscribe(
        participants => {console.log('participants', participants)}
    );*/
    
    this.elistMatTableDataSource = new MatTableDataSource((this.participantFormArray as FormArray).controls);
    
    this.profileListItems = this.uniques(this.elistMatTableDataSource.data);
    this.groupListItems = this.uniquesGroup(this.elistMatTableDataSource.data);
    
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
      console.log("setParticipantsForm participant.name: " + participant.name);
      console.log("setParticipantsForm participant.groupId: " + participant.groupId);
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
      
      this.groupOptions[index] = formGroup.get('group').valueChanges.pipe(
      	startWith<string | Participant>(''),
	      map(value => typeof value === 'string' ? value : value.group),
	      map(group => group ? this._filterGroup(group) : this.groupListItems.slice())
      );
  }
  
  private setParticipantsFormArray(participant){
    return this.formBuilder.group({
        id:participant.id,
        name:participant.name,
        email:participant.email,
        age:participant.age, 
        gender:participant.gender,
        profile:participant.profile,
        groupId:participant.groupId,
        group:participant.group
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
      this.storeService.broadcastGroupChange("S");
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
  	
    this.participantService.getNewId()
      .subscribe(data => {
        const participant:Participant = {
	      id:data,
	      name:'',
          email:'',
          age:'',
          gender:'',
          profile:'',
          groupId:0,
          group:''
	   	};
  	 
   	 this.setFormGroup(participant, this.form.controls.participants.value.length);
   	 this.participantFormArray.controls[this.form.controls.participants.value.length-1].valueChanges.subscribe(
        participant => {
		this.saveParticipant(participant);
	 });
	 this.elistMatTableDataSource.filter = "";
	 
	 }); 
  }
  
  deleteParticipant(participant){
  	console.log('deleteParticipant: ' + participant.controls.id.value);
  	
  	this.participantService.isBlockedParticipant(participant.controls.id.value).subscribe(data => {
	    console.log("Participante bloqueado? " + data);
	    if (data >  0) {
	    	this.openDialog("Info", "El participante está bloqueado y no será borrado.");
	    }
	    else {  
  			var index =  this.participantFormArray.controls.findIndex(fg => fg.value.id === participant.controls.id.value);
    		this.participantService.deleteParticipant(participant.controls.id.value).subscribe(data => {
	    		console.log("Delete " + data);
	    		this.storeService.broadcastGroupChange("S");
     		}, error =>  this.error_str=error.error.message);
    		// find item and remove ist
    		this.elistMatTableDataSource.data.splice(index, 1);
    		//this.participantFormArray.controls.splice(index, 1);
    		this.profileListItems = this.uniques(this.elistMatTableDataSource.data);
    		this.groupListItems = this.uniquesGroup(this.elistMatTableDataSource.data);
    		this.elistMatTableDataSource.filter = "";
        }
    }, error =>  this.error_str=error.error.message);
  }

  deleteAll():void{
    console.log('deleteAll');
  	this.elistMatTableDataSource.data = [];
  	this.profileListItems = [];
  	this.groupListItems = [];
     
  	this.participantService.deleteAllParticipant().subscribe(data => {
	    console.log("Delete All" + data);
	    if (data = 0) 
	         this.openDialog("Info", "Los participantes han sido borrados.");
	    else
	    	 this.openDialog("Info", "Los participantes no bloqueados han sido borrados.");     
        this.storeService.broadcastGroupChange("S");
     }, error =>  this.error_str=error.error.message);
     
     this.refresh.emit();
  }
  
  saveParticipant(participant){
    console.log('saveParticipant');
	var index =  this.participantFormArray.value.findIndex(part => part.id === participant.id);
    if ((this.form.controls.participants as FormArray).controls[index].valid) {
      this.participantService.saveParticipant(participant)
      .subscribe(data => {
        console.log("Save " + data);
        /*this.elistMatTableDataSource.data[index].id = data.id;
        this.elistMatTableDataSource.data[index].name = participant.name;
        this.elistMatTableDataSource.data[index].email = participant.email;
        this.elistMatTableDataSource.data[index].age = participant.age;
        this.elistMatTableDataSource.data[index].gender = participant.gender;
        this.elistMatTableDataSource.data[index].profile = participant.profile;
        this.elistMatTableDataSource.data[index].group = data.groupId;*/
        
        this.profileListItems = this.uniques(this.elistMatTableDataSource.data);
        this.groupListItems = this.uniquesGroup(this.elistMatTableDataSource.data);
            
        if (this.participantFormArray.value[index].id != data.id || this.participantFormArray.value[index].groupId != data.groupId ) {
            this.participantFormArray.value[index].id = data.id;
            this.participantFormArray.value[index].groupId = data.groupId;
	        this.participantFormArray.controls[index].setValue({
		        id:data.id,
		        name:participant.name,
		        email:participant.email,
		        age:participant.age, 
		        gender:participant.gender,
		        profile:participant.profile,
		        groupId:data.groupId,
		        group:participant.group
		    });
	    }
        this.elistMatTableDataSource.paginator = this.paginator;
        this.elistMatTableDataSource.sort = this.sort;
        this.storeService.broadcastGroupChange("S");
      }, error =>  this.openDialog("Error", error.error.message));
    } else 
    	(this.form.controls.participants as FormArray).controls[index].markAllAsTouched();
  }
  
  onRowChanged(row, index) {
    /*if (this.form.controls.participants.valid) {
      this.participantChanged = this.participantFormArray.value[index];
      this.participantService.saveParticipant(this.participantChanged)
	      .subscribe(data => {
	        console.log("Save " + data);
	        this.elistMatTableDataSource.data[index].id = data.id;
	        this.elistMatTableDataSource.data[index].name = this.participantChanged.name;
	        this.elistMatTableDataSource.data[index].email = this.participantChanged.email;
	        this.elistMatTableDataSource.data[index].age = this.participantChanged.age;
	        this.elistMatTableDataSource.data[index].gender = this.participantChanged.gender;
	        this.elistMatTableDataSource.data[index].profile = this.participantChanged.profile;
	        this.elistMatTableDataSource.data[index].groupId = data.groupId;
	        this.elistMatTableDataSource.data[index].group = this.participantChanged.group;
	        this.participantFormArray.value[index].id = data;
	        //this.participantFormArray.controls[index] = this.setParticipantsFormArray(this.elistMatTableDataSource.data[index]);
	        this.participantFormArray.controls[index].setValue({
		        id:data.id,
		        name:this.participantChanged.name,
		        email:this.participantChanged.email,
		        age:this.participantChanged.age, 
		        gender:this.participantChanged.gender,
		        profile:this.participantChanged.profile,
		        groupId:data.groupId,
		        group:this.participantChanged.group
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

