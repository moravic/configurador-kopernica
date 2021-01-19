import { BlockList } from './blockList';
import { SegmentList } from './segmentList';

export class Protocol {
    id: number;
    protocolName: string;
    segmentListArray: SegmentList[];
	blockListArray: BlockList[];
}
