import { Component, OnInit } from '@angular/core';
import { MatTableDataSource } from '@angular/material/table';
import { NgForm } from '@angular/forms';
import { Estimulo } from './estimulo';
import { EstimuloDataStoreService } from './estimulo-list.service';

@Component({
  selector: 'estimulo-list',
  templateUrl: './estimulo-list.component.html',
  styleUrls: ['./estimulo-list.component.css']
})
export class EstimuloListComponent implements OnInit {

  title = 'table-validation-demo';
  elistMatTableDataSource = new MatTableDataSource<Estimulo>();
  displayedColumns: string[];

  constructor(private eDataStore: EstimuloDataStoreService) {
    this.displayedColumns = [
      'text'
    ];
  }

  ngOnInit() {
    this.elistMatTableDataSource.data = this.eDataStore.getEstimulos();
  }

  save(form: NgForm): void {
    console.log('Form Status', form.valid);
    console.log('Form Touched', form.touched);
    console.log('Form Untouch', form.untouched);
    console.log('Form Invalid', form.invalid);
    console.log('Form Pristine', form.pristine);
    // do API operation
  }
}

