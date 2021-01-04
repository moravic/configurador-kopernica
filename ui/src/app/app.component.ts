import { Component, OnInit } from '@angular/core';
import { AppService } from "./app.service";
import { MatIconRegistry } from "@angular/material/icon";
import { DomSanitizer } from "@angular/platform-browser";

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
  
  constructor(
    private appService: AppService,
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
      "delete-bin",
      this.domSanitizer.bypassSecurityTrustResourceUrl("../../config-kopernica/assets/delete-bin.svg")
    );
  }
  
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