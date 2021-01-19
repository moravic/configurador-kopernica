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
	      blockElementListArray: [
	        {
		      	id: 1,
		      	blockElement: {
					id: 1,
					question: {
						id: 1,
						question: 'Question 1'
					},
					stimulus: null	
		      	}
		    },
		    {
				id: 1,
		      	blockElement: {
					id: 2,
					question: {
						id: 2,
						question: 'Question 2'
					},
					stimulus: null	
				}
	      	},
	      	{
				id: 3,
				blockElement: {
					id: 3,
					question: {
						id: 3,
						question: 'Question 3'
					},
					stimulus: null	
	            }
	        }]
	    },
	    {
	        id: 2,
	        blockName: 'Bloque 2',
	        blockElementListArray: [
		        {
					id: 1,
			      	blockElement: {
						id: 2,
						question: {
							id: 2,
							question: 'Question 2'
						},
						stimulus: null
				   }
		      	},
		      	{
					id: 5,
					blockElement: {
						id: 5,
						question: null,
						stimulus: {
							id: 1,
							name: 'Stimulus 1'
						}	
		            }
		       }
		    ]
		  }
	    ];
	
	this.protocol={
	    id: 1,
	    protocolName: 'Protocol 1',
        segmentListArray: [{
            id: 1,
	        segment: {
	            id: 1,
				type: 'Todos',
				value_age_min: null,
				value_age_max: null,
				value_gender: null,
				value_profile: null
	        }
        }],
        blockListArray: [
        {
            id: 1,
	        block: {
		      id: 1,
		      blockName: 'Bloque 1',
		      blockElementListArray: [
		       {
			      	id: 1,
			      	blockElement: {
						id: 1,
						question: {
							id: 1,
							question: 'Question 1'
						},
						stimulus: null	
			      	}
			    },
		        {
					id: 1,
			      	blockElement: {
						id: 2,
						question: {
							id: 2,
							question: 'Question 2'
						},
						stimulus: null
				   }
		      	},
		      	{
					id: 3,
					blockElement: {
						id: 3,
						question: {
							id: 3,
							question: 'Question 3'
						},
						stimulus: null	
		           }
		       }]
		   }
        },
        {
            id: 2,
	        block: {
		        id: 2,
		        blockName: 'Bloque 2',
		        blockElementListArray: [
		           {
						id: 1,
				      	blockElement: {
							id: 2,
							question: {
								id: 2,
								question: 'Question 2'
							},
							stimulus: null	
					    }
			      	},
			      	{
						id: 5,
						blockElement: {
							id: 5,
							question: null,
							stimulus: {
								id: 2,
								name: 'Stimulus 2'
							}	
			           }
			       }
		 	    ]
			}
	    }]
   };
	 
   this.blockElementsSelect=[{
				id: 1,
				blockElement: {
					id: 1,
					question: {
						id: 1,
						question: 'Question 1'
					},
					stimulus: null
				}
	      	},
	      	{
				id: 2,
				blockElement: {
					id: 2,
					question: {
						id: 2,
						question: 'Question 2'
					},
					stimulus: null
				}
	      	},
	      	{
				id: 3,
				blockElement: {
					id: 3,
					question: {
						id: 3,
						question: 'Question 3'
					},
					stimulus: null
				}
	      	},
	      	{
				id: 4,
				blockElement: {
					id: 4,
					question: null,
					stimulus: {
						id: 1,
						name: 'Stimulus 1'
					}
				}
            },
	      	{
				id: 5,
				blockElement: {
					id: 5,
					question: null,
					stimulus: {
						id: 2,
						name: 'Stimulus 2'
					}
				}
            },
	      	{
				id: 6,
				blockElement: {
					id: 6,
					question: null,
					stimulus: {
						id: 3,
						name: 'Stimulus 3'
					}
				}
            }];
	 
     this.blockForm = this.createBlockFormGroup();
     this.protocolBlockForm = this.createProtocolBlockFormGroup();
  }
  
  myBlockControl = new FormControl();
  
  //@Input()
  blocks:Block[] = [];
  blockElementsSelect:BlockElementList[] = [];
  
  protocol:Protocol;
    
  blockForm: FormGroup;
  protocolBlockForm: FormGroup;
  
  filteredOptions: Observable<string[]>;
  notFound:string;
  filterValueTmp:string;
  
  blockElementListSelected;
  
  createBlockFormGroup() {
	  
	  var blockElementListArrayFG=new FormArray([]);
	  
	  this.blockElementsSelect.forEach( (blockElementList) => {
			var blockElementListFG = new FormGroup({
			      id: new FormControl(blockElementList.id),
			      blockElement: new FormGroup({
			        id: new FormControl(blockElementList.blockElement.id),
			        question: new FormGroup({
			        	id: new FormControl(blockElementList.blockElement.question?.id),
			        	question: new FormControl(blockElementList.blockElement.question?.question)
			        }),
			        stimulus: new FormGroup({
			        	id: new FormControl(blockElementList.blockElement.stimulus?.id),
			        	name: new FormControl(blockElementList.blockElement.stimulus?.name)
			        })
		        })
		   });
		   
		   blockElementListArrayFG.push(blockElementListFG);
	  });
	  
	  return new FormGroup({
		   id: new FormControl(),
		   blockName: new FormControl(''),
		   blockElementListArray: blockElementListArrayFG
	  });
  }
  
  createProtocolBlockFormGroup() {
      
      var blockListArrayFG=new FormArray([]);
      
      this.protocol.blockListArray.forEach( (blockList) => {
         var blockElementListArrayFG=new FormArray([]);
         blockList.block.blockElementListArray.forEach( (blockElementList) => {
           var blockElementListFG = new FormGroup({
			      id: new FormControl(blockElementList.id),
			      blockElement: new FormGroup({
			        id: new FormControl(blockElementList.blockElement.id),
			        question: new FormGroup({
			        	id: new FormControl(blockElementList.blockElement.question?.id),
			        	question: new FormControl(blockElementList.blockElement.question?.question)
			        }),
			        stimulus: new FormGroup({
			        	id: new FormControl(blockElementList.blockElement.stimulus?.id),
			        	name: new FormControl(blockElementList.blockElement.stimulus?.name)
			        })
		        })
		   });
		   blockElementListArrayFG.push(blockElementListFG);
         });
          
         var blockListFG =  new FormGroup({
           id: new FormControl(blockList.id),
           block: new FormGroup({
			   id: new FormControl(blockList.block.id),
			   blockName: new FormControl(blockList.block.blockName),
			   blockElementListArray: blockElementListArrayFG
			})
		 });
		 
		 blockListArrayFG.push(blockListFG);
      });
      
      var protocolBlockFG = new FormGroup({
	    id: new FormControl(this.protocol.id),
	    protocolName: new FormControl(this.protocol.protocolName),
	    blockListArray:blockListArrayFG
	  });
	  
	  return protocolBlockFG;
  }
  	
  addBlock() {
    console.log("addBlock");
    
    if (!this.myBlockControl.value)
      return;
    
    const bl = this.blocks.filter(option => option.blockName.toLowerCase().indexOf(this.myBlockControl.value.toLowerCase()) === 0);
    
    var block:Block;
    
    if (!bl.length || bl.length>1) {
	   block = {
	       id: null,
	       blockName: this.myBlockControl.value,
	       blockElementListArray: []
		};
		
	   this.blocks.push(block);
	} else
		block = bl[0];
    
    this.filteredOptions = this.myBlockControl.valueChanges.pipe(
      startWith(''),
      map(value => this._filter(value))
    );
    
	this.addBlockElement(block);
  }
  
  blockElementListSelection (event){
  	console.log("blockElementListSelection");
  	
  	this.blockElementListSelected = this.blockForm.controls.blockElementListArray.value[event.value-1];
  }
  
  addBlockElement(block) {
    console.log("addBlockElement");
    
    var blockElementListArrayFG;
    var blockElementListFG=blockElementListFG = new FormGroup({
      id: new FormControl(this.blockElementListSelected.id),
      blockElement: new FormGroup({
        id: new FormControl(this.blockElementListSelected.blockElement.id),
        question: new FormGroup({
        	id: new FormControl(this.blockElementListSelected.blockElement.question?.id),
        	question: new FormControl(this.blockElementListSelected.blockElement.question?.question)
        }),
        stimulus: new FormGroup({
        	id: new FormControl(this.blockElementListSelected.blockElement.stimulus?.id),
        	name: new FormControl(this.blockElementListSelected.blockElement.stimulus?.name)
        })
      })
    });
    
    var blockFG = (this.protocolBlockForm.controls.blockListArray as FormArray).controls.find(blockElementList => {return blockElementList.value.block.id === block.id});
        
    if (!(blockFG === undefined)) {
        blockElementListArrayFG=((blockFG as FormGroup).controls.block as FormGroup).controls.blockElementListArray;
        blockElementListArrayFG.push(blockElementListFG);
    } else {
    	blockElementListArrayFG=new FormArray([]);
    	blockElementListArrayFG.push(blockElementListFG);
    	var blockListFG =  new FormGroup({
		   id: new FormControl(),
		   block: new FormGroup({
			   id: new FormControl(block.id),
			   blockName: new FormControl(block.blockName),
			   blockElementListArray: blockElementListArrayFG
			})
		 });
		 
	    (this.protocolBlockForm.controls.blockListArray as FormArray).push(blockListFG);
    }
  }
  
  removeBlock(iBlock:number) {
    console.log("removeBlock");
    (this.protocolBlockForm.controls.blockListArray as FormArray).removeAt(iBlock);
  }
  
  removeBlockElement(iBlock, iBlockElement) {
    console.log("removeBlockElement");
    ((((this.protocolBlockForm.controls.blockListArray as FormArray).controls[iBlock] as FormGroup).controls.block as FormGroup).controls.blockElementListArray as FormArray).removeAt(iBlockElement);
  }

  ngOnInit() {
    this.filteredOptions = this.myBlockControl.valueChanges.pipe(
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
    
    if ((!results.length && filterValue) || (results.length>1 && filterValue)) this.notFound = 'No existe el bloque ' + value + ', se creará uno nuevo.'
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