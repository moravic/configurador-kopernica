import { Component, OnInit } from '@angular/core';
import { AppService } from "./app.service";
import { MatIconRegistry } from "@angular/material/icon";
import { DomSanitizer } from "@angular/platform-browser";
import { Study } from './study';
import { StudyService } from './study.service';
import { ParticipantService } from './participant.service';
import { Participant } from './participant';
import { QuestionService } from './question.service';
import { Question } from './question';
import { StimulusService } from './stimulus.service';
import { Stimulus } from './stimulus';
import { ProtocolService } from './protocol.service';
import { Protocol } from './protocol';
import { ProtocolparticipantService } from './protocolparticipant.service';
import { StoreService } from './store.service';

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
  questions:Question[]=[];
  stimuli:Stimulus[]=[];
  addStudyDisabled=true;
  listComponentsDisabled=true;
  protocols:Protocol[]=[];
  
  constructor(
    private appService: AppService,
    private studyService: StudyService,
    private participantService:ParticipantService,
    private questionService:QuestionService,
    private stimulusService:StimulusService,
    private protocolService:ProtocolService,
    private protocolparticipantService:ProtocolparticipantService,
    private matIconRegistry: MatIconRegistry,
    private domSanitizer: DomSanitizer,
    private storeService: StoreService
  ){
    this.matIconRegistry.addSvgIcon(
      "export-xlsx",
      this.domSanitizer.bypassSecurityTrustResourceUrl("../../config-kopernica/assets/export-xlsx.svg")
    );
    this.matIconRegistry.addSvgIcon(
      "import-xlsx",
      this.domSanitizer.bypassSecurityTrustResourceUrl("../../config-kopernica/assets/import-xlsx.svg")
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
    this.matIconRegistry.addSvgIcon(
      "locked",
      this.domSanitizer.bypassSecurityTrustResourceUrl("../../config-kopernica/assets/locked.svg")
    );
  }

  public addStudy(){
     //console.log("Adding study");
     this.study = new Study();
     this.study.project=this.projectSelected;
     this.study.study=this.studySelected;
     this.study.type=this.typeSelected;
     this.typeDisabled=true;
     this.studyService.addStudy(this.study)
     	.subscribe(resp => {
     	    //console.log(resp);
     	    this.listComponentsDisabled=false;
     	    this.addStudyDisabled=true;
     	    
     	    this.protocolService.getProtocols()
      		.subscribe(data => {
	        		//console.log(data);
	        		this.protocols = data;
	      		}, error =>  {this.error_str=error.error.message; 
	      					  this.protocols.length=0;});
	      }, error =>  this.error_str=error.error.message);
	      this.error_str=""; 
	    
	    //Se actualiza el listado de proyectos
        this.appService.getListProjects()
	      .subscribe(data => {
	        //console.log(data)
	        this.listProjects = data;
	    }, error =>  this.error_str=error.error.message);
	    
	    this.storeService.broadcastAddStudy("S");
   }
   
   public applyConfiguration(){
   		//console.log("Generating Protocol Participants...");
   		this.protocolparticipantService.applyConfiguration()
     	.subscribe(resp => {
     	    console.log(resp);
	      }, error =>  this.error_str=error.error.message);
	      this.error_str=""; 
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
     //console.log("shareProjectToParent " + itemSelected);	
     
       if (itemSelected) {
        this.projectSelected = itemSelected; 
        this.studySelected = "";
        this.listEstudios.length = 0;
        
        this.addStudyDisabled=true;
       	this.appService.getListEstudios(itemSelected)
	      .subscribe(data => {
	        //console.log(data)
	        this.listEstudios = data;
	        this.shareEstudioToParent(data[0]);
	      }, error => this.error_str=error.error.message);
	      this.error_str="";
	    };
   }
  
   shareEstudioToParent(itemSelected:string){
     if (itemSelected) {
     	//console.log("shareEstudioToParent " + itemSelected);	
     	this.studySelected = itemSelected;
     
     	this.addStudyDisabled=true;
     	if (!this.projectSelected || !this.studySelected) return;
     	this.appService.setProperties(this.projectSelected, this.studySelected).subscribe(data => {
	     	//console.log(data);
	     	this.error_str="";
	     	this.studyService.getTypeStudy(this.projectSelected, this.studySelected).subscribe(data => {
	     	if (data == "0" || data == "1"){
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
		 	//console.log("getParticipants");
         	this.participantService.getParticipants(this.projectSelected, this.studySelected)
	      		.subscribe(data => {
	        		//console.log(data);
	        		this.participants = data;
	      		}, error =>  {this.error_str=error.error.message; 
	      					  this.participants.length=0;});
	      	//console.log("getQuestions");
         	this.questionService.getQuestions(this.projectSelected, this.studySelected)
	      		.subscribe(data => {
	        		//console.log(data);
	        		this.questions = data;
	      		}, error =>  {this.error_str=error.error.message; 
	      					  this.questions.length=0;});
	        //console.log("getStimuli");
         	this.stimulusService.getStimuliList(this.projectSelected, this.studySelected)
	      		.subscribe(data => {
	        		//console.log(data);
	        		this.stimuli = data;
	        		
	      		}, error =>  {this.error_str=error.error.message; 
	      				      this.stimuli.length=0;});		
	     		
	      		//console.log("type: " + data);
	     		}, error => {console.log(error);
	        	this.typeSelected='1'; 
		    	this.typeDisabled=false;
		    	this.addStudyDisabled=false;
		    	this.listComponentsDisabled=true;
		    	this.participants.length=0;
		    	this.questions.length=0;
		    	this.stimuli.length=0;});	        		
	  	}, error => {console.log(error);
	      	this.typeSelected='1'; 
		  	this.typeDisabled=false;
		  	this.addStudyDisabled=false;
		  	this.listComponentsDisabled=true;
		  	this.participants.length=0;
		  	this.questions.length=0;
		  	this.questions.length=0;});
		//console.log("getProtocols");
     	this.protocolService.getProtocols()
      		.subscribe(data => {
        		//console.log(data);
        		this.protocols = data;
      		}, error =>  {this.error_str=error.error.message; 
      					  this.protocols.length=0;});
   	   }
   	   this.storeService.broadcastGroupChange("S");
   }
   
   shareChangeType(itemSelected:string){
        //console.log("changeType " + itemSelected);	 
        this.typeSelected = itemSelected;   
   }
   
   refreshParticipants () {
   		this.participantService.getParticipants(this.projectSelected, this.studySelected)
	      		.subscribe(data => {
	        		//console.log(data);
	        		this.participants = data;
	      		}, error =>  {this.error_str=error.error.message; 
	      					  this.participants.length=0;});
	}      					  
   
}