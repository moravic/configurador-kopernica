import { Component, Input, OnChanges } from '@angular/core';
import { SegmentList } from '../segmentList';
import { FormGroup, FormControl } from '@angular/forms';
import { Observable} from 'rxjs';
import { ProtocolService } from '../protocol.service';
import { ParticipantService } from '../participant.service';

@Component({
  selector: 'segment',
  templateUrl: 'segment.component.html',
  styleUrls: ['./segment.component.css']
})
export class SegmentComponent {

  constructor(private participantService:ParticipantService,
              private protocolService:ProtocolService) {
    this.typeArray=[
       {
	      id: 1,
	      type: 'Todos'
	    },
	    {
	      id: 2,
	      type: 'Edad'
	    },
	    {
	      id: 3,
	      type: 'Género'
	    },
	    {
	      id: 4,
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
	  
   	this.participantService.getProfiles()
  		.subscribe(data => {
          //console.log("getProfiles " + data);
          this.profileArray = data;
  		}, error =>  {
  		this.error_str=error.error.message;});
  		
    this.segmentForm = this.createFormGroup();
  }
  
  @Input()
  segmentListArray:SegmentList[] = [];
  @Input()
  protocolId;
  @Input()
  protocolName;
  
  segmentForm: FormGroup;
  typeArray;
  genderArray;
  profileArray;
  error_str:string;
   
  ngOnChanges() {

  }
  
  typeSelection (event){
  	console.log("typeSelection");
  	
    this.segmentForm.controls.valueAgeMin.setValue(null);
    this.segmentForm.controls.valueAgeMax.setValue(null);
    this.segmentForm.controls.valueGender.setValue(null);
    this.segmentForm.controls.valueProfile.setValue(null);
	       
  	this.participantService.getProfiles()
  		.subscribe(data => {
          //console.log("getProfiles " + data);
          this.profileArray = data;
  		}, error =>  {
  		this.error_str=error.error.message;});
  }
  
  createFormGroup() {
	  return new FormGroup({
	    id: new FormControl(),
	    type: new FormControl(),
	    valueAgeMin: new FormControl(),
	    valueAgeMax: new FormControl(),
	    valueGender: new FormControl(),
	    valueProfile: new FormControl()
	  });
  }

  addSegmentList() {
    console.log("addSegmentList");
    var segmentList:SegmentList = {
        id: null,
	    segment: {
	       id: null,
	       type: this.typeArray[this.segmentForm.value.type-1].type,
	       valueAgeMin: this.segmentForm.value.valueAgeMin,
	       valueAgeMax: this.segmentForm.value.valueAgeMax,
	       valueGender: this.segmentForm.value.valueGender,
	       valueProfile: this.segmentForm.value.valueProfile
	    }
	};
	    
    this.protocolService.saveSegmentList(this.protocolId, this.protocolName, segmentList)
      .subscribe(data => {
        segmentList = data;
        this.segmentListArray.push(segmentList);
        console.log("Save " + data); 
    }); 
    
  }
 
  removeSegmentList(event, index) {
    console.log("removeSegmentList");
    
    if (this.segmentListArray.length==1)
     return;
         
    this.protocolService.deleteSegmentList(this.protocolId, this.protocolName, this.segmentListArray[index].id, this.segmentListArray[index].segment.id)
      .subscribe(data => {
        console.log("Save " + data); 
     }); 
    
    this.segmentListArray.splice(index, 1);
  }
  
}