import { Component, OnInit } from '@angular/core';
import { MatTableDataSource } from '@angular/material/table';
import { NgForm } from '@angular/forms';
import { Pregunta } from './pregunta';
import { PreguntaDataStoreService } from './pregunta-list.service';

@Component({
  selector: 'pregunta-list',
  templateUrl: './pregunta-list.component.html',
  styleUrls: ['./pregunta-list.component.css']
})
export class PreguntaListComponent implements OnInit {

  title = 'table-validation-demo';
  elistMatTableDataSource = new MatTableDataSource<Pregunta>();
  displayedColumns: string[];

  constructor(private eDataStore: PreguntaDataStoreService) {
    this.displayedColumns = [
      'text'
    ];
  }

  ngOnInit() {
    this.elistMatTableDataSource.data = this.eDataStore.getPreguntas();
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

