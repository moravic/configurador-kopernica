import {Component, OnInit, OnChanges, Input} from '@angular/core';
import {FormControl} from '@angular/forms';
import { Protocol } from '../protocol';
import { Observable} from 'rxjs';

@Component({
  selector: 'protocol-tabs',
  templateUrl: 'protocol-tabs.component.html',
  styleUrls: ['protocol-tabs.component.css'],
})

export class ProtocolTabsComponent {
  tabs = ['Protocolo 1'];
  selected = new FormControl(0);
  
  @Input()
  protocols:Protocol[];
    
  constructor(){};
  
  ngOnInit() {
  } 
  
  ngOnChanges() {
     console.log("ngOnChanges Protocol");
     this.tabs = [];
	 this.protocols.forEach( (protocol) => {
	 	this.tabs.push(protocol.name);
	 });
	 
	 this.selected.setValue(0);
  }
  
  addTab() {
  
    var protocol:Protocol = {
	    id: this.protocols.length + 1,
	    name: 'Protocolo ' + (this.protocols.length + 1),
	    segmentListArray: [],
		blockListArray: []
    }
    
    this.tabs.push('Protocolo ' + (this.tabs.length + 1));
        
    this.protocols.push(protocol);
    
	console.log("addTab");
    this.selected.setValue(this.tabs.length - 1);
  }

  removeTab(index: number) {
    this.tabs.splice(index, 1);
  }
  
}
