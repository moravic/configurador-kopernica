import { Component, Input, OnChanges } from '@angular/core';
import { Block } from '../block';
import { BlockList } from '../blockList';
import { BlockElement } from '../blockElement';
import { BlockElementList } from '../blockElementList';
import { FormGroup, FormArray, FormControl, ReactiveFormsModule } from '@angular/forms';
import { Observable } from 'rxjs';
import { map, startWith } from 'rxjs/operators';
import { Protocol } from '../protocol';
import { ProtocolService } from '../protocol.service';

@Component({
  selector: 'block-protocol',
  templateUrl: 'block-protocol.component.html',
  styleUrls: ['./block-protocol.component.css']
})

export class BlockProtocolComponent {

  constructor(private protocolService:ProtocolService) {	
        
  }
  
  myBlockControl = new FormControl();
  
  //@Input()
  blocks:Block[] = [];
  blockElementsSelect:BlockElementList[] = [];
  
  @Input()
  blockListArray:BlockList[]=[];
  
  @Input()
  protocolId;
  
  @Input()
  protocolName;
  
  blockForm: FormGroup;
  protocolBlockForm: FormGroup;
  
  filteredOptions: Observable<string[]>;
  notFound:string;
  filterValueTmp:string;
  
  blockElementListSelected;
  
  error_str:string;
  
  ngOnChanges() {
     console.log("ngOnChanges");
     
     this.protocolService.getBlockElementsList()
  		.subscribe(data => {
          //console.log("getBlockElementsList " + data);
          this.blockElementsSelect = data;
          
          this.protocolService.getBlocks()
	  		.subscribe(data => {
	          //console.log("getBlocks " + data);
	          this.blocks = data;
	  		}, error =>  {
	  		this.error_str=error.error.message;});

           this.blockForm = this.createBlockFormGroup();
           this.protocolBlockForm = this.createProtocolBlockFormGroup();
  	}, error =>  {this.error_str=error.error.message;});
  }
  
  
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
		   name: new FormControl(''),
		   blockElementListArray: blockElementListArrayFG
	  });
  }
  
  createProtocolBlockFormGroup() {
      
      var blockListArrayFG=new FormArray([]);
      
      this.blockListArray.forEach( (blockList) => {
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
			   name: new FormControl(blockList.block.name),
			   blockElementListArray: blockElementListArrayFG
			})
		 });
		 
		 blockListArrayFG.push(blockListFG);
      });
      
      var protocolBlockFG = new FormGroup({
	    id: new FormControl(this.protocolId),
	    name: new FormControl(this.protocolName),
	    blockListArray:blockListArrayFG
	  });
	  
	  return protocolBlockFG;
  }
  	
  addBlock() {
    console.log("addBlock");
    
    if (!this.myBlockControl.value)
      return;
    
    const bl = this.blocks.filter(option => option.name.toLowerCase().indexOf(this.myBlockControl.value.toLowerCase()) === 0);
    
    var block:Block;
    
    if (!bl.length || bl.length>1) {
	   block = {
	       id: null,
	       name: this.myBlockControl.value,
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
			   name: new FormControl(block.name),
			   blockElementListArray: blockElementListArrayFG
			})
		 });
		 
	    (this.protocolBlockForm.controls.blockListArray as FormArray).push(blockListFG);
    }
    
    this.save(blockListFG.value);
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
    const results = this.blocks.filter(option => option.name.toLowerCase().indexOf(filterValue) === 0);
    
    if ((!results.length && filterValue) || (results.length>1 && filterValue)) this.notFound = 'No existe el bloque ' + value + ', se creará uno nuevo.'
    else this.notFound = '';
    
    const blockNames = results.map(a => a.name);
    
    return blockNames;
  }
  
  private protocolToForm() {
    this.protocolBlockForm.controls.id.setValue(this.protocolId);
    this.protocolBlockForm.controls.name.setValue(this.protocolName);    
  }
  
  save(saveBlockList){
  	console.log("saveBlockList");
  	
  	this.protocolService.saveBlockList(this.protocolId, this.protocolName, saveBlockList)
      .subscribe(data => {
        console.log("Save " + data); 
    });
  }
  
}