import { Component, OnInit, Input, Output, EventEmitter } from '@angular/core';

@Component({
  selector: 'projectType-radio',
  templateUrl: 'projectType-radio.component.html',
  styleUrls: ['projectType-radio.component.css'],
})
export class ProjectTypeRadioComponent {

  @Input()
  selectedItem:String;
  
  @Input()
  disabled: boolean;

  @Output() shareItemToParent = new EventEmitter();

  ngOnInit() {
      // Valor por defecto
      this.selectedItem = '1';
  }
  
  public changeItem(selectedItem) {
     this.shareItemToParent.emit(selectedItem);
     console.log(selectedItem);
  }

}