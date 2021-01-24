import { Component, ElementRef, OnInit, Input, AfterViewInit, ViewChild, OnChanges, ChangeDetectorRef, AfterContentChecked } from '@angular/core';
import { MatTableDataSource } from '@angular/material/table';
import { FormArray, FormGroup, FormBuilder, Validators } from '@angular/forms';
import { Question } from '../question';
import { PreguntaDataStoreService } from './pregunta-list.service';
import { MatIconRegistry } from "@angular/material/icon";
import { DomSanitizer } from "@angular/platform-browser";
import { Observable} from 'rxjs';
import { AbstractControl, FormControl, ReactiveFormsModule} from '@angular/forms';
import { QuestionService } from '../question.service';
import { ImportService } from '../import.service';
import { ExportService } from '../export.service';
import { MatDialogOkComponent } from '../mat-dialog-ok/mat-dialog-ok.component';
import { MatDialog } from '@angular/material/dialog';

@Component({
  selector: 'pregunta-list',
  templateUrl: './pregunta-list.component.html',
  styleUrls: ['./pregunta-list.component.css']
})
export class PreguntaListComponent implements OnInit, AfterViewInit {

  title = 'Questions Table';
  
  @Input()
  questions:Question[];
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
    private questionService:QuestionService,
    private importService:ImportService,
    private exportService:ExportService,
    private matIconRegistry: MatIconRegistry,
    private domSanitizer: DomSanitizer,
    private formBuilder: FormBuilder,
    private cdr: ChangeDetectorRef,
    private dialog: MatDialog) {
    
    this.displayedColumns = [
      'question',
      'deleteQuestion'
    ];
    
  }

  ngAfterContentChecked() : void {
     this.cdr.detectChanges();
  }
  
  ngAfterViewInit() {
  }
  
  ngOnInit() {
   // this.elistMatTableDataSource.data = this.eDataStore.getPreguntas();
  }

  ngOnChanges() {
    console.log("ngOnChanges " + this.questions);
    this.updateQuestions();
  }
  
   updateQuestions() {
    if (!this.questions)
      return;
        
    console.log("updateQuestions " + this.questions);
    
        
    this.form= this.formBuilder.group({
       questions: this.formBuilder.array([])
    });
    
    this.setQuestionsForm();
    
    this.elistMatTableDataSource = new MatTableDataSource((this.questionFormArray as FormArray).controls);
    
  }
  
  private setQuestionsForm(){
    let index = 0;
    this.questions.forEach((question)=>{
      console.log("question: " + question.question);
      this.setFormGroup(question, index);
      this.questionFormArray.controls[index].valueChanges.subscribe(
        question => {
			this.saveQuestion(question);
        	console.log('question', question)
      });
      index++;
    })
  };
  
  get questionFormArray():FormArray {
     return this.form.get('questions') as FormArray;
  }
 
  private setFormGroup(question:Question, index:number){
      console.log("setFormGroup "+question.question);
      var formGroup = this.setQuestionsFormArray(question);
      this.questionFormArray.push(formGroup);
     
  }
  
  private setQuestionsFormArray(question){
  console.log("setQuestionsFormArray "+ question.question);
    return this.formBuilder.group({
        id:question.id,
        question:question.question
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
  
   saveQuestion(question){
   console.log("saveQuestion ");
	var index =  this.questionFormArray.value.findIndex(part => part.id === question.id);
    if ((this.form.controls.questions as FormArray).controls[index].valid) {
      this.questionService.saveQuestion(question)
      .subscribe(data => {
        console.log("Save " + data);
            
        if (this.questionFormArray.value[index].id != data) {
            this.questionFormArray.value[index].id = data;
	        this.questionFormArray.controls[index].setValue({
		        id:data,
		        question:question.question
		    });
	    }
      }, error =>  this.error_str=error.error.message);
    }
  }
  
   deleteQuestion(question){
  	console.log('deleteQuestion: ' + question.controls.id.value);
  	var index =  this.questionFormArray.controls.findIndex(fg => fg.value.id === question.controls.id.value);
    this.questionService.deleteQuestion(question.controls.id.value).subscribe(data => {
	    console.log("Delete " + data);
     }, error =>  this.error_str=error.error.message);
    // find item and remove ist
    console.log('deleteQuestion splice');
    this.elistMatTableDataSource.data.splice(index, 1);
    //this.questionFormArray.controls.splice(index, 1);
    this.elistMatTableDataSource.filter = "";
  }
  
  deleteAll():void{
    console.log('deleteAll');
  	this.elistMatTableDataSource.data = [];
  	this.questionService.deleteAllQuestion().subscribe(data => {
	    console.log("Delete " + data);
	    this.openDialog("Info", "Las preguntas han sido borradas.");
     }, error =>  this.error_str=error.error.message);
  }
  
   addNew():void{
  	console.log('addNew');
  	
  	this.questionService.getNewId()
      .subscribe(data => {
        const question:Question = {
	      id:data,
	      question:''
	   	 };

   	 this.setFormGroup(question, this.form.controls.questions.value.length);
   	 this.questionFormArray.controls[this.form.controls.questions.value.length-1].valueChanges.subscribe(
        question => {
		this.saveQuestion(question);
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

    this.importService.importQuestionExcelFile(this.project, this.study)
	      .subscribe(data => {
	  console.log('Excel Imported ' + data);
	  this.questions = data;
	  this.updateQuestions();
	  this.openDialog("Info", "Las preguntas han sido importadas.");
    }, error =>  this.error_str=error.error.message);
  }
  
  exportExcel():void{
    if (!this.project || !this.study)
      return;
    console.log('exportExcel');
    this.exportService.exportQuestionExcelFile(this.project, this.study)
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

