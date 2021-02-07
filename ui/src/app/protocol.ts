import { BlockList } from './blockList';
import { SegmentList } from './segmentList';
import { GroupList } from './groupList';

export class Protocol {
    id: number;
    name: string;
    segmentListArray: SegmentList[];
    groupListArray: GroupList[];
	blockListArray: BlockList[];
	locked: number;
}
