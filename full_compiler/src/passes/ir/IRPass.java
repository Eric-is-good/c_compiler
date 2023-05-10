package passes.ir;

import ir.Module;

public interface IRPass {
    void runOnModule(Module module);
}
