import { Component, OnInit } from '@angular/core';
import { AppService } from "./app.service";
import { MatIconRegistry } from "@angular/material/icon";
import { DomSanitizer } from "@angular/platform-browser";
import { Study } from './study';
import { StudyService } from './study.service';
import { ParticipantService } from './participant.service';
import { Participant } from './participant';

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
  typeDisabled=false;
  participants:Participant[]=[];
  addStudyDisabled=true;
  listComponentsDisabled=true;
  
  constructor(
    private appService: AppService,
    private studyService: StudyService,
    private participantService:ParticipantService,
    private matIconRegistry: MatIconRegistry,
    private domSanitizer: DomSanitizer
  ){
    this.matIconRegistry.addSvgIcon(
      "export-csv",
      this.domSanitizer.bypassSecurityTrustResourceUrl("../../config-kopernica/assets/export-csv.svg")
    );
    this.matIconRegistry.addSvgIcon(
      "import-csv",
      this.domSanitizer.bypassSecurityTrustResourceUrl("../../config-kopernica/assets/import-csv.svg")
    );
    this.matIconRegistry.addSvgIcon(
      "add-new",
      this.domSanitizer.bypassSecurityTrustResourceUrl("../../config-kopernica/assets/add-new.svg")
    );
    this.matIconRegistry.addSvgIcon(
      "minus-delete",
      this.domSanitizer.bypassSecurityTrustResourceUrl("../../config-kopernica/assets/minus-delete.svg")
    );
    this.matIconRegistry.addSvgIcon(
      "delete-bin",
      this.domSanitizer.bypassSecurityTrustResourceUrl("../../config-kopernica/assets/delete-bin.svg")
    );
  }

  public addStudy(){
     console.log("Adding study");
     console.log(this.typeSelected);
     this.study = new Study();
     this.study.project=this.projectSelected;
     this.study.study=this.studySelected;
     this.study.type=this.typeSelected;
     this.studyService.addStudy(this.study)
     	.subscribe(resp => {
     	    console.log(resp);
     	    this.listComponentsDisabled=false;
     	    this.addStudyDisabled=true;
	      }, error =>  this.error_str=error.error.message);
	      this.error_str=""; 
	      
	//Se actualiza el listado de proyectos
     this.appService.getListProjects()
	      .subscribe(data => {
	        //console.log(data)
	        this.listProjects = data;
	      }, error =>  this.error_str=error.error.message);
   }
  
  ngOnInit() {
    this.addStudyDisabled=true;
  	this.appService.getListProjects()
	      .subscribe(data => {
	        //console.log(data)
	        this.listProjects = data;
	      }, error =>  this.error_str=error.error.message);
	      this.error_str="";
  } 
  
  shareItemToParent(itemSelected:string){
     console.log("shareProjectToParent " + itemSelected);	
     
       if (itemSelected) {
        console.log("ITEM SELECTED");
        this.projectSelected = itemSelected; 
        this.studySelected = "";
        this.listEstudios.length = 0;
        
        this.addStudyDisabled=true;
       	this.appService.getListEstudios(itemSelected)
	      .subscribe(data => {
	        console.log(data)
	        this.listEstudios = data;
	        this.shareEstudioToParent(data[0]);
	      }, error => this.error_str=error.error.message);
	      this.error_str="";
	      }
     
   }
  
   shareEstudioToParent(itemSelected:string){
     if (itemSelected) {
     	console.log("shareEstudioToParent " + itemSelected);	
     	this.studySelected = itemSelected;
     
     	this.addStudyDisabled=true;
     	if (!this.projectSelected || !this.studySelected) return;
     	this.appService.setProperties(this.projectSelected, this.studySelected).subscribe(data => {
	     	console.log(data);
	     	this.error_str="";
	     	this.studyService.getTypeStudy(this.projectSelected, this.studySelected).subscribe(data => {
	     	if (data){
		    	this.typeSelected=""+data; 
		    	this.typeDisabled=true;
		    	this.addStudyDisabled=true;
		    	this.listComponentsDisabled=false;
		 	} else {
		    	this.typeSelected='1'; 
		    	this.typeDisabled=false;
		    	this.addStudyDisabled=false;
		    	this.listComponentsDisabled=false;
		 	}
		 	console.log("getParticipants");
         	this.participantService.getParticipants(this.projectSelected, this.studySelected)
	      		.subscribe(data => {
	        		console.log(data);
	        		this.participants = data;
	      		}, error =>  this.error_str=error.error.message);
	      
	      		console.log("type: " + data);
	     		}, error => {console.log(error);
	        	this.typeSelected='1'; 
		    	this.typeDisabled=false;
		    	this.addStudyDisabled=false;
		    	this.listComponentsDisabled=true;
		    	this.participants.length=0;});
	  	}, error => {console.log(error);
	      	this.typeSelected='1'; 
		  	this.typeDisabled=false;
		  	this.addStudyDisabled=false;
		  	this.listComponentsDisabled=true;
		  	this.participants.length=0;});
   	  }
   }
   
    shareChangeType(itemSelected:string){
        console.log("changeType " + itemSelected);	 
        this.typeSelected = itemSelected;   
   }
   
}