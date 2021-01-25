import { Component, ElementRef, OnInit, Input, AfterViewInit, ViewChild, OnChanges, ChangeDetectorRef, AfterContentChecked } from '@angular/core';
import { MatTableDataSource } from '@angular/material/table';
import { FormArray, FormGroup, FormBuilder, Validators } from '@angular/forms';
import { NgForm } from '@angular/forms';
import { Stimulus } from '../stimulus';
import { EstimuloDataStoreService } from './estimulo-list.service';
import { MatIconRegistry } from "@angular/material/icon";
import { DomSanitizer } from "@angular/platform-browser";
import { Observable} from 'rxjs';
import { AbstractControl, FormControl, ReactiveFormsModule} from '@angular/forms';
import { StimulusService } from '../stimulus.service';
import { ImportService } from '../import.service';
import { ExportService } from '../export.service';
import { MatDialogOkComponent } from '../mat-dialog-ok/mat-dialog-ok.component';
import { MatDialog } from '@angular/material/dialog';
import { StoreService } from '../store.service';

@Component({
  selector: 'estimulo-list',
  templateUrl: './estimulo-list.component.html',
  styleUrls: ['./estimulo-list.component.css']
})
export class EstimuloListComponent implements OnInit, AfterViewInit {

  title = 'Stimuli Table';
  
  @Input()
  stimuli:Stimulus[];
  @Input()
  project:string;
  @Input()
  study:string;
  @Input()
  disabled: boolean;
  
  elistMatTableDataSource = new MatTableDataSource<any>();
  displayedColumns: string[];
  form: FormGroup;
  error_str:string;
  formData = new FormData();
  

  constructor(
  	private stimulusService: StimulusService,
  	private storeService: StoreService,
    private importService:ImportService,
    private exportService:ExportService,
    private matIconRegistry: MatIconRegistry,
    private domSanitizer: DomSanitizer,
    private formBuilder: FormBuilder,
    private cdr: ChangeDetectorRef,
    private dialog: MatDialog) {
    this.displayedColumns = [
      'stimulus',
      'deleteStimulus'
    ];
  }
  
 ngAfterContentChecked() : void {
     this.cdr.detectChanges();
  }
  
  ngAfterViewInit() {
  }
  
  
  ngOnInit() {
    //this.elistMatTableDataSource.data = this.eDataStore.getEstimulos();
  }

 ngOnChanges() {
    console.log("ngOnChanges " + this.stimuli);
    this.updateStimuli();
  }
  
  updateStimuli() {
    if (!this.stimuli)
      return;
      
    console.log("updateStimuli " + this.stimuli);
    //this.storeService.setStimuli(this.stimuli);
    this.storeService.broadcastElementChange("S");
        
    this.form= this.formBuilder.group({
       stimuli: this.formBuilder.array([])
  });
    
    this.setStimuliForm();
    
    this.elistMatTableDataSource = new MatTableDataSource((this.stimulusFormArray as FormArray).controls);
    
  }
  
  private setStimuliForm(){
    let index = 0;
    this.stimuli.forEach((stimulus)=>{
      console.log("stimulus: " + stimulus.name);
      this.setFormGroup(stimulus, index);
      this.stimulusFormArray.controls[index].valueChanges.subscribe(
        stimulus => {
			this.saveStimulus(stimulus);
        	console.log('stimulus', stimulus)
      });
      index++;
    })
  };
  
  get stimulusFormArray():FormArray {
     return this.form.get('stimuli') as FormArray;
  }
 
  private setFormGroup(stimulus:Stimulus, index:number){
      console.log("setFormGroup "+stimulus.name);
      var formGroup = this.setStimuliFormArray(stimulus);
      this.stimulusFormArray.push(formGroup);
     
  }
  
  private setStimuliFormArray(stimulus){
  console.log("setStimuliFormArray "+ stimulus.name);
    return this.formBuilder.group({
        id:stimulus.id,
        name:stimulus.name
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
  
  saveStimulus(stimulus){
   console.log("saveStimulus ");
	var index =  this.stimulusFormArray.value.findIndex(part => part.id === stimulus.id);
    if ((this.form.controls.stimuli as FormArray).controls[index].valid) {
      this.stimulusService.saveStimulus(stimulus)
      .subscribe(data => {
        console.log("Save " + data);
            
        if (this.stimulusFormArray.value[index].id != data) {
            this.stimulusFormArray.value[index].id = data;
	        this.stimulusFormArray.controls[index].setValue({
		        id:data,
		        name:stimulus.name
		    });
	    }
	    
	    //this.storeService.setStimuli(this.stimuli);
	    this.storeService.broadcastElementChange("S");
      }, error =>  this.error_str=error.error.message);
    }
  }
  
 deleteStimulus(stimulus){
  	console.log('deleteStimulus: ' + stimulus.controls.id.value);
  	var index =  this.stimulusFormArray.controls.findIndex(fg => fg.value.id === stimulus.controls.id.value);
    this.stimulusService.deleteStimulus(stimulus.controls.id.value).subscribe(data => {
	    console.log("Delete " + data);
	    this.stimuli.splice(index, 1);
        //this.storeService.setStimuli(this.stimuli);
	    this.storeService.broadcastElementChange("S");
     }, error =>  this.error_str=error.error.message);
    // find item and remove ist
    console.log('deleteStimulus splice');
    this.elistMatTableDataSource.data.splice(index, 1);
    this.elistMatTableDataSource.filter = "";
  }
  
  deleteAll():void{
    console.log('deleteAll');
  	this.elistMatTableDataSource.data = [];
  	this.stimulusService.deleteAllStimulus().subscribe(data => {
	    console.log("Delete " + data);
	    this.stimuli = [];
        //this.storeService.setStimuli(this.stimuli);
	    this.storeService.broadcastElementChange("S");
	    this.openDialog("Info", "Las preguntas han sido borradas.");
     }, error =>  this.error_str=error.error.message);
   

  }
  
   addNew():void{
  	console.log('addNew');

    //this.elistMatTableDataSource.data.push(participant);
   	
    this.stimulusService.getNewId()
      .subscribe(data => {
        const stimulus:Stimulus = {
	      id:data,
	      name:''
	   	 };
	   	 
	  this.setFormGroup(stimulus, this.form.controls.stimuli.value.length);
	  
	  this.stimulusFormArray.controls[this.form.controls.stimuli.value.length-1].valueChanges.subscribe(
        stimulus => {
		this.saveStimulus(stimulus);
		this.stimuli.push(stimulus);
	   });
	  this.elistMatTableDataSource.filter = "";
     });
  }
  
   onRowChanged(row, index) {
    
  }
  
  getActualIndex(index : number)  {
    return index;
  }
  
   importExcel():void{
    if (!this.project || !this.study)
      return;
  	console.log('importExcel');

    this.importService.importStimulusExcelFile(this.project, this.study)
	      .subscribe(data => {
	  console.log('Excel Imported ' + data);
	  this.stimuli = data;
	  this.updateStimuli();
	  this.openDialog("Info", "Las preguntas han sido importadas.");
    }, error =>  this.error_str=error.error.message);
  }
  
  exportExcel():void{
    if (!this.project || !this.study)
      return;
    console.log('exportExcel');
    this.exportService.exportStimulusExcelFile(this.project, this.study)
	      .subscribe(data => {
	  this.openDialog("Info", "Las preguntas han sido exportadas.");
	  console.log('Excel Exported');
    }, error =>  this.error_str=error.error.message);
    
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

