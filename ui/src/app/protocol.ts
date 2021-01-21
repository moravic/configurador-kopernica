import { BlockList } from './blockList';
import { SegmentList } from './segmentList';

export class Protocol {
    id: number;
    name: string;
    segmentListArray: SegmentList[];
	blockListArray: BlockList[];
}
