package passes.ir.analysis;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RelationAnalysis {

    /**
     * Analysis relationship of the given basic blocks.
     * @param basicBlockMap Mapping from raw BB to wrapped BB.
     * @param <BasicBlock> The type of wrapped BB.
     */
    public static <BasicBlock extends IRelationAnalysis<BasicBlock>>
    void analysisBasicBlocks(Map<ir.values.BasicBlock, BasicBlock> basicBlockMap){
        basicBlockMap.values().forEach(basicBlock -> {
            var followingBBs = RelationUtil.getFollowingBB(basicBlock.getRawBasicBlock());
            List<BasicBlock> followingBasicBlocks = new ArrayList<>();
            for (ir.values.BasicBlock followingBB : followingBBs) {
                followingBasicBlocks.add(basicBlockMap.get(followingBB));
            }
            followingBasicBlocks.forEach(followingBasicBlock -> followingBasicBlock.addEntryBlock(basicBlock));
            basicBlock.setExitBlocks(followingBasicBlocks);
        });
    }

}
