import { Component, OnInit } from '@angular/core';
import { AppService } from "./app.service";

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
  error_str:string;
  
  constructor(private appService: AppService) { }
  
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
}