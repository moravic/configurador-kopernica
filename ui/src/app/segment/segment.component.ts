import { Component, Input } from '@angular/core';
import { Segment } from '../segment';
import { FormGroup, FormControl } from '@angular/forms';

@Component({
  selector: 'segment',
  templateUrl: 'segment.component.html',
  styleUrls: ['./segment.component.css']
})
export class SegmentComponent {

  constructor() {
    this.typeArray=[
	    {
	      id: 1,
	      type: 'Edad'
	    },
	    {
	      id: 2,
	      type: 'Género'
	    },
	    {
	      id: 3,
	      type: 'Perfil'
	    }
	  ];
	  
	this.genderArray=[
	    {
	      id: 1,
	      type: 'Mujer'
	    },
	    {
	      id: 2,
	      type: 'Hombre'
	    }
	  ];
	  
    this.profileArray=[
	    {
	      id: 1,
	      type: 'Profile1'
	    },
	    {
	      id: 2,
	      type: 'Profile2'
	    }
	  ];
	
    this.segmentForm = this.createFormGroup();
  }
  
  @Input()
  segments:Segment[] = [];
  
  segmentForm: FormGroup;
  typeArray;
  genderArray;
  profileArray;
    
  createFormGroup() {
	  return new FormGroup({
	    id: new FormControl(),
	    type: new FormControl(),
	    value_age_min: new FormControl(),
	    value_age_max: new FormControl(),
	    value_gender: new FormControl(),
	    value_profile: new FormControl()
	  });
  }

  addSegment() {
    console.log("addSegment");
    var segment:Segment = {
       id: 0,
       type: this.segmentForm.value.type,
       value_age_min: this.segmentForm.value.value_age_min,
       value_age_max: this.segmentForm.value.value_age_max,
       value_gender: this.segmentForm.value.value_gender,
       value_profile: this.segmentForm.value.value_profile
    }
    this.segments.push(segment);
  }
 
  removeSegment(event, index) {
    console.log("removeSegment");
    this.segments.splice(index, 1);
  }

}