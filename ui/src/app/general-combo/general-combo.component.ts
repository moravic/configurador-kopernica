import { Component, OnInit, Input, Output, EventEmitter } from '@angular/core';


@Component({
  selector: 'general-combo',
  templateUrl: './general-combo.component.html',
  styleUrls: ['./general-combo.component.css']
})

export class GeneralComboComponent implements OnInit {

  constructor() { }

  @Input()
  listItems:String[]=[];

  @Input()
  selectedItem:String;
  
  @Output() shareItemToParent = new EventEmitter();
    
  ngOnInit() {
 
  }
  
  public changeItem(selectedItem) {
     this.shareItemToParent.emit(selectedItem);
     console.log(selectedItem);
  }
}