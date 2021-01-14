import { Block } from './block';
import { Segment } from './segment';

export class Protocol {
    id: number;
    protocolName: string;
    segmentArray: Segment[];
	blockArray: Block[];
}
