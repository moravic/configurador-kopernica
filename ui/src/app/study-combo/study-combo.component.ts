import { Component, OnInit, Input, Output, EventEmitter } from '@angular/core';
import {FormControl, ReactiveFormsModule} from '@angular/forms';
import {Observable} from 'rxjs';
import {map, startWith} from 'rxjs/operators';


@Component({
  selector: 'study-combo',
  templateUrl: './study-combo.component.html',
  styleUrls: ['./study-combo.component.css']
})

export class StudyComboComponent implements OnInit {
  
  constructor() { }

  myStudyControl = new FormControl();
  
  @Input()
  listItems:string[];
  
  @Input()
  selectedItem:string;
  
  filteredOptions: Observable<string[]>;
  notFound:string;
  
  @Output() shareItemToParent = new EventEmitter();
    
  ngOnInit() {
    this.filteredOptions = this.myStudyControl.valueChanges.pipe(
      startWith(''),
      map(value => this._filter(value))
    );
  }
  
  private _filter(value: string): string[] {
    const filterValue = value.toLowerCase();
    
    this.shareItemToParent.emit(filterValue);
    console.log('filterValue ' + filterValue);
    const results = this.listItems.filter(option => option.toLowerCase().indexOf(filterValue) === 0);
    
    if (!results.length && filterValue) this.notFound = 'No existe el estudio ' + filterValue + ', se creará uno nuevo.'
    else this.notFound = '';
    
    return results;
  }
  
}