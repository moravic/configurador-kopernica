import { Component, OnInit, Input, Output, EventEmitter } from '@angular/core';
import {FormControl, ReactiveFormsModule} from '@angular/forms';
import {Observable} from 'rxjs';
import {map, startWith} from 'rxjs/operators';


@Component({
  selector: 'project-combo',
  templateUrl: './project-combo.component.html',
  styleUrls: ['./project-combo.component.css']
})

export class ProjectComboComponent implements OnInit {
  
  constructor() { }

  myProjectControl = new FormControl();
  
  @Input()
  listItems:string[];
  
  filteredOptions: Observable<string[]>;
  notFound:string;
  filterValueTmp:string;
  
  @Output() shareItemToParent = new EventEmitter();
    
  ngOnInit() {
    this.filteredOptions = this.myProjectControl.valueChanges.pipe(
      startWith(''),
      map(value => this._filter(value))
    );
  }
  
  private _filter(value: string): string[] {
    this.filterValueTmp = value;
    const filterValue = this.filterValueTmp.toLowerCase();
    
    this.shareItemToParent.emit(value);
    console.log('filterValue ' + value);
    const results = this.listItems.filter(option => option.toLowerCase().indexOf(filterValue) === 0);
    
    if (!results.length && filterValue) this.notFound = 'No existe el proyecto ' + filterValue + ', se creará uno nuevo.'
    else this.notFound = '';
    
    return results;
  }
  
}