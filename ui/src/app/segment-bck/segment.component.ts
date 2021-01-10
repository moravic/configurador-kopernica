import { Component, ViewChild, ElementRef } from '@angular/core';
import { FormGroup, FormControl,FormArray, FormBuilder } from '@angular/forms'

@Component({
  selector: 'segment',
  templateUrl: 'segment.component.html',
  styleUrls: ['./segment.component.css']
})
export class SegmentComponent {
  segmentForm: FormGroup;
 
  constructor(private fb:FormBuilder) {
 
    this.segmentForm = this.fb.group({
      segments: this.fb.array([])
    });
  
  }
  
  get segments() : FormArray {
    return this.segmentForm.get("segments") as FormArray;
  }
 
  newSegment(): FormGroup {
    return this.fb.group({
      opcion: '1',
      valor: '2',
    })
  }
 
  addSegments() {
    console.log("addSegments");
    this.segments.push(this.newSegment());
  }
 
  removeSegment(i:number) {
    this.segments.removeAt(i);
  }

}