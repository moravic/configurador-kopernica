import { Component, OnInit, Input, Output, EventEmitter } from '@angular/core';
import {FormControl, ReactiveFormsModule} from '@angular/forms';
import {Observable} from 'rxjs';
import {map, startWith} from 'rxjs/operators';
import { StoreService } from '../store.service';

@Component({
  selector: 'project-combo',
  templateUrl: './project-combo.component.html',
  styleUrls: ['./project-combo.component.css']
})

export class ProjectComboComponent implements OnInit {
  
  constructor( private storeService:StoreService) { 
	  storeService.getAddStudy().subscribe(data => {
	         console.log("AddStudy");
	         this.notFound = '';
	  });
  }
  
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
    const exactMatch = this.listItems.filter(option => option.toLowerCase() === filterValue);
    
    if (!exactMatch.length && filterValue) this.notFound = 'No existe el proyecto ' + value + ', se creará uno nuevo.'
    else this.notFound = '';
    
    return results;
  }
  
}