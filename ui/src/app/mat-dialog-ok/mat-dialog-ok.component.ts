import {Component, Inject} from '@angular/core';
import { MAT_DIALOG_DATA } from '@angular/material/dialog';

export interface DialogData {
  title: '';
  content: '';
}

@Component({
  selector: 'mat-dialog-ok',
  templateUrl: './mat-dialog-ok.component.html',
  styleUrls: ['./mat-dialog-ok.component.css']
})

export class MatDialogOkComponent {
	constructor(@Inject(MAT_DIALOG_DATA) public data: DialogData) {}
}