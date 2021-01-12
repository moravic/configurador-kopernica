import { Component, Input } from '@angular/core';
import { Block } from '../block';
import { BlockElement } from '../blockElement';
import { BlockElementList } from '../blockElementList';
import { FormGroup, FormControl, ReactiveFormsModule } from '@angular/forms';
import { Observable } from 'rxjs';
import { map, startWith } from 'rxjs/operators';

@Component({
  selector: 'block-protocol',
  templateUrl: 'block-protocol.component.html',
  styleUrls: ['./block-protocol.component.css']
})

export class BlockProtocolComponent {

  constructor() {	
    this.blocks=[
	    {
	      id: 1,
	      blockName: 'Bloque 1',
	      blockElementList: {
	      	id: 1,
	      	blockElementArray: [{
				id: 1,
				questionId: 1,
				stimulusId: 0,
				text: 'Question 1'
	      	},
	        {
				id: 2,
				questionId: 2,
				stimulusId: 0,
				text: 'Question 2'
	      	},
	      	{
				id: 3,
				questionId: 0,
				stimulusId: 1,
				text: 'Stimulus 1'
	      	}]
	      }
	    },
	    {
	        id: 2,
	        blockName: 'Bloque 2',
	        blockElementList: {
		      	id: 2,
		        blockElementArray: [{
					id: 1,
					questionId: 1,
					stimulusId: 0,
					text: 'Question 1'
	        	},
	            {
					id: 3,
					questionId: 0,
					stimulusId: 1,
					text: 'Stimulus 1'
	      	    }]
	         }
	     }];
	  
    this.blockForm = this.createFormGroup();
  }
  
  //@Input()
  blocks:Block[] = [];
  
  blockForm: FormGroup;
  
  filteredOptions: Observable<string[]>;
  notFound:string;
  filterValueTmp:Block;
  
  createFormGroup() {
	  return new FormGroup({
	    id: new FormControl(),
	    blockName: new FormControl(),
	    blockElementList: new FormGroup({
	      id: new FormControl(),
	      blockElement: new FormGroup({
		      id: new FormControl(),
		      questionId: new FormControl(),
			  stimulusId: new FormControl(),
		      text: new FormControl()
	      })
	    })
	  });
  }
  
  addBlock() {
    console.log("addBlock");
    var block:Block = {
       id: this.blockForm.value.id,
       blockName: this.blockForm.value.blockName,
       blockElementList: {
         id: this.blockForm.value.blockElementList.value.id,
         blockElementArray: []
       }
	};

    this.blocks.push(block);
  }
  
  /*addBlockElement() {
    console.log("addBlockElement");
    var blockElement:BlockElement = {
       blockId: this.blockForm.value.id,
       blockName: this.blockForm.value.blockName,
       blockElementList: []
	   }
    };

    this.blockElements.push(blockElement);
  }
 
  removeBlockElement(event, index) {
    console.log("removeBlockElement");
    this.blockElements.splice(index, 1);
  }*/

  ngOnInit() {
    this.filteredOptions = this.blockForm.value.valueChanges.pipe(
      startWith(''),
      map(value => this._filter(<Block>value))
    );
  }
  
  private _filter(value: Block): Block[] {
    this.filterValueTmp = value;
    const filterValue = this.filterValueTmp.blockName.toLowerCase();
    
    console.log('filterValue ' + filterValue);
    const results = this.blocks.filter(option => option.blockName.toLowerCase().indexOf(filterValue) === 0);
    
    if (!results.length && filterValue) this.notFound = 'No existe el bloque ' + filterValue + ', se creará uno nuevo.'
    else this.notFound = '';
    
    return results;
  }
}