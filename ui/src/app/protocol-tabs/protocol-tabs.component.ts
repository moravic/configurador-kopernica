import {Component, OnInit, OnChanges, Input, ChangeDetectorRef } from '@angular/core';
import {FormControl} from '@angular/forms';
import { Protocol } from '../protocol';
import { Observable} from 'rxjs';
import { ProtocolService } from '../protocol.service';
import { SegmentList } from '../segmentList';
import { GroupList } from '../groupList';
import { BlockList } from '../blockList';

@Component({
  selector: 'protocol-tabs',
  templateUrl: 'protocol-tabs.component.html',
  styleUrls: ['protocol-tabs.component.css'],
})

export class ProtocolTabsComponent implements OnInit, OnChanges{
  tabs = ['Protocolo 1'];
  
  public selectedIndex: number = 0;
  
  error_str:string;
  
  @Input()
  protocols:Protocol[];
  
  @Input()
  typeSelected;
  
  protocolIdMax=0;
  
  constructor(
    private protocolService:ProtocolService,
    private change: ChangeDetectorRef
  ){};
  
  ngOnInit() {

  } 
  
  ngOnChanges(changes:any) {
     console.log("ngOnChanges Protocol");
     this.tabs = [];
     this.protocolIdMax=0;
	 this.protocols.forEach( (protocol) => {
	    if (this.protocolIdMax<protocol.id)
	       this.protocolIdMax=protocol.id;
	       
	 	this.tabs.push(protocol.name);
	 });
	 
  }
     
  addTab() {
    
   console.log("addTab");
   
   var segmentList:SegmentList[]=[];
   var groupList:GroupList[]=[];
   var blockList:BlockList[]=[];
   
    this.protocolIdMax++;
    var protocol:Protocol = {
	    id: this.protocolIdMax,
	    name: 'Protocolo ' + (this.protocolIdMax),
	    segmentListArray: segmentList,
	    groupListArray: groupList,
		blockListArray: blockList,
		locked: 0
    }
    
    this.protocols.push(protocol);
    
    this.tabs.push(protocol.name);
    
    window.setTimeout(()=>{
       this.selectedIndex = this.tabs.length-1;
       this.change.markForCheck();
    });
 
  }

  removeTab(index: number) {
  
	  this.protocolService.deleteProtocol( this.protocols[index].id)
	  		.subscribe(data => {
	    		//console.log(data);
			    this.tabs.splice(index, 1);
			    this.protocols.splice(index, 1);
	  		}, error =>  {this.error_str=error.error.message;
	  });
  }
  
  onTabChanged ($event){
    console.log("onTabChanged");
  }
  
  changeNameProtocol($event, index){
    this.protocols[index].name=$event.target.value;
    
    this.protocolService.saveProtocol(this.protocols[index])
	  		.subscribe(data => {
	    		//console.log(data);
	  	}, error =>  {this.error_str=error.error.message;
	 });
  }
}
