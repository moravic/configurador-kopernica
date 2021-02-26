import { Component, Input, OnChanges } from '@angular/core';
import { Group } from '../group';
import { GroupList } from '../groupList';
import { FormGroup, FormControl } from '@angular/forms';
import { Observable} from 'rxjs';
import { GroupService } from '../group.service';
import { StoreService } from '../store.service';

@Component({
  selector: 'group',
  templateUrl: 'group.component.html',
  styleUrls: ['./group.component.css']
})
export class GroupComponent {

  constructor(private groupService:GroupService,
              private storeService:StoreService) {
    this.groupService.getGroups()
      .subscribe(data => {
        console.log("getGroups " + data);
        this.groupArray = data;
     }); 
     
    this.groupForm = this.createFormGroup();
    
    storeService.getGroupChange().subscribe(data => {
       console.log("GroupChange");
       this.groupService.getGroups()
	      .subscribe(data => {
	        console.log("getGroups " + data);
	        this.groupArray = data;
	     }); 
	});
  }
  
  groupArray:Group[] = [];
  @Input()
  groupListArray:GroupList[] = [];
  @Input()
  protocolId;
  @Input()
  protocolName;
  @Input()
  disabled;
  
  groupForm: FormGroup;
  error_str:string;
   
  ngOnChanges() {

  }
   
  createFormGroup() {
	  return new FormGroup({
	    id: new FormControl(),
	    name: new FormControl()
	  });
  }

  addGroupList() {
    console.log("addGroupList");
    if (this.groupForm.value.id==null)
      return;
    
    // si existe
    if (this.groupListArray.filter(option => option.group.id === this.groupForm.value.id).length>0)
      return;
    
    var groupList:GroupList = {
        id: null,
	    group: {
	       id: this.groupForm.value.id,
	       name: this.groupArray[this.groupForm.value.id-1].name
	    }
	};
	    
    this.groupService.saveGroupProtocol(this.protocolId, groupList)
      .subscribe(data => {
        this.groupListArray.push(groupList);
        console.log("Save " + data); 
    }); 
    
  }
 
  removeGroupList(event, index) {
    console.log("removeGroupList");
    
    if (this.groupListArray.length==1)
     return;
         
    this.groupService.deleteGroupProtocol(this.protocolId, this.groupArray[index].id)
      .subscribe(data => {
        console.log("Save " + data);
        this.groupListArray.splice(index, 1);
     }); 
    
  }
  
}