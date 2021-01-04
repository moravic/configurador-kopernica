import { Component, OnInit } from '@angular/core';
import { AppService } from "./app.service";
import { Study } from './study';
import { StudyService } from './study.service';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent implements OnInit{
  title = 'Kopernica Configurator';
  
  listProjects:String[]=[];
  listEstudios:String[]=[];
  projectSelected:string;
  studySelected:string;
  typeSelected:string='1';
  error_str:string;
  study:Study;
  
  public addStudy(){
     console.log("Adding study");
     console.log(this.typeSelected);
     this.study = new Study();
     this.study.project=this.projectSelected;
     this.study.study=this.studySelected;
     this.study.type=this.typeSelected;
     this.studyService.addStudy(this.study)
     	.subscribe(resp => {
     	    console.log(resp)
	      }, error =>  this.error_str=error.error.message);
	      this.error_str=""; 
   }
  
  constructor(private appService: AppService, private studyService: StudyService) { }
  
  ngOnInit() {
  	this.appService.getListProjects()
	      .subscribe(data => {
	        //console.log(data)
	        this.listProjects = data;
	      }, error =>  this.error_str=error.error.message);
	      this.error_str="";
  } 
  
  shareItemToParent(itemSelected:string){
     console.log("shareProjectToParent " + itemSelected);	
     
        this.projectSelected = itemSelected;
       	this.appService.getListEstudios(itemSelected)
	      .subscribe(data => {
	        console.log(data)
	        this.listEstudios = data;
	        this.shareEstudioToParent(data[0]);
	      }, error =>  this.error_str=error.error.message);
	      this.error_str="";
     
   }
   
   shareEstudioToParent(itemSelected:string){
     //console.log("shareEstudioToParent " + itemSelected);	
     this.studySelected = itemSelected;
     
     if (!this.projectSelected || !this.studySelected) return;
     this.appService.setProperties(this.projectSelected, this.studySelected).subscribe(data => {
	     console.log(data);
	     this.error_str="";	 
	  }, error => console.log(error));
   }
   
    shareChangeType(itemSelected:string){
        console.log("changeType " + itemSelected);	 
        this.typeSelected = itemSelected;   
   }
   
}