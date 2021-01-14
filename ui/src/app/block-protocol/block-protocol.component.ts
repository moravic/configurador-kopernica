import { Component, Input } from '@angular/core';
import { Block } from '../block';
import { BlockElement } from '../blockElement';
import { BlockElementList } from '../blockElementList';
import { FormGroup, FormArray, FormControl, ReactiveFormsModule } from '@angular/forms';
import { Observable } from 'rxjs';
import { map, startWith } from 'rxjs/operators';
import { Protocol } from '../protocol';

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
	
	this.protocol={
	    id: 1,
	    protocolName: 'Protocol 1',
	    segmentArray: [],
	    blockArray: [
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
	     }]
	 };
	 
	 this.blockElementsSelect=[{
					id: 1,
					questionId: 1,
					stimulusId: 0,
					text: 'Question 1'
	        	},{
					id: 2,
					questionId: 2,
					stimulusId: 0,
					text: 'Question 2'
	        	},{
					id: 3,
					questionId: 0,
					stimulusId: 1,
					text: 'Stimulus 1'
	      	    },
	            {
					id: 4,
					questionId: 0,
					stimulusId: 1,
					text: 'Stimulus 2'
	      	    }];
	 
     this.blockForm = this.createFormGroup();
     this.protocolBlockForm = this.createProtocolBlockFormGroup();
  }
  
  //@Input()
  blocks:Block[] = [];
  blockElementsSelect:BlockElement[] = [];
  
  protocol:Protocol;
    
  blockForm: FormGroup;
  protocolBlockForm: FormGroup;
  
  filteredOptions: Observable<string[]>;
  notFound:string;
  filterValueTmp:string;
  
  createFormGroup() {
	  return new FormGroup({
	    id: new FormControl(),
	    blockName: new FormControl(),
	    blockElementList: new FormGroup({
	      id: new FormControl(),
	      blockElement: new FormArray([
			new FormGroup({
		      id: new FormControl(),
		      questionId: new FormControl(),
			  stimulusId: new FormControl(),
		      text: new FormControl()
	      })])
	    })
	  });
  }
  
  createProtocolBlockFormGroup() {
      
      var blockArrayFG=new FormArray([]);
      
      this.protocol.blockArray.forEach( (block) => {
         var blockElementArrayFG=new FormArray([]);
         block.blockElementList.blockElementArray.forEach( (blockElement) => {
           var blockElementFG = new FormGroup({
			      id: new FormControl(blockElement.id),
			      questionId: new FormControl(blockElement.questionId),
				  stimulusId: new FormControl(blockElement.stimulusId),
			      text: new FormControl(blockElement.text)
		      });
		   blockElementArrayFG.push(blockElementFG);
         });
          
         var blockElementListFG = new FormGroup({
         	id: new FormControl(block.blockElementList.id),
         	blockElement: blockElementArrayFG
          });
          
          var blockFG =  new FormGroup({
			   id: new FormControl(block.id),
			   blockName: new FormControl(block.blockName),
			   blockElementList: blockElementListFG
		  });
		  
		  blockArrayFG.push(blockFG);
		  
      });
      
      var protocolBlockFG = new FormGroup({
	    id: new FormControl(this.protocol.id),
	    protocolName: new FormControl(this.protocol.protocolName),
	    blockArray:blockArrayFG
	  });
	  
	  return protocolBlockFG;
	   
	  /*return new FormGroup({
	    id: new FormControl(),
	    protocolName: new FormControl(),
	    blockArray: new FormArray([
	      new FormGroup({
	        id: new FormControl(),
	        blockName: new FormControl(),
	    	blockElementList: new FormGroup({
		      id: new FormControl(),
		      blockElementArray: new FormArray([
		        new FormGroup({
			      id: new FormControl(),
			      questionId: new FormControl(),
				  stimulusId: new FormControl(),
			      text: new FormControl()
		      })])
		    })
		 })])
	  });*/
  }
  	
  addBlock() {
    console.log("addBlock");
    var block:Block = {
       id: this.blockForm.value.id,
       blockName: this.blockForm.value.blockName,
       blockElementList: {
         id: this.blockForm.value.blockElementList.id,
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
  }*/
 
  removeBlockElement(event, index) {
    console.log("removeBlockElement");
    this.blocks[1].blockElementList.blockElementArray.splice(index, 1);
  }

  ngOnInit() {
    this.filteredOptions = this.blockForm.controls.blockName.valueChanges.pipe(
      startWith(''),
      map(value => this._filter(value))
    );
    
    this.protocolToForm();
  }
  
  private _filter(value: string): string[] {
    this.filterValueTmp = value;
    const filterValue = this.filterValueTmp.toLowerCase();
    
    console.log('filterValue ' + filterValue);
    const results = this.blocks.filter(option => option.blockName.toLowerCase().indexOf(filterValue) === 0);
    
    if (!results.length && filterValue) this.notFound = 'No existe el bloque ' + filterValue + ', se creará uno nuevo.'
    else this.notFound = '';
    
    const blockNames = results.map(a => a.blockName);
    
    return blockNames;
  }
  
  private protocolToForm() {
    this.protocolBlockForm.controls.id.setValue(this.protocol.id);
    this.protocolBlockForm.controls.protocolName.setValue(this.protocol.protocolName);
        
  	/*const blockArray = this.protocolBlockForm.controls.blockArray as FormArray;
    blockArray.push(new FormGroup({
       id: this.protocol.id,
	   blockName: this.protocol.protocolName,
	   blockElementList: new FormGroup({
	   
	   }
    })
    );*/
    
  }
}