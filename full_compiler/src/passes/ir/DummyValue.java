package passes.ir;

import ir.Type;
import ir.Value;

/**
 * A dummy Value acting as a placeholder.
 * The purposed is similar to UndefValue in LLVM IR.
 * @see <a href="https://github.com/hdoc/llvm-project/blob/release/14.x/llvm/include/llvm/IR/Constants.h#L1377">
 *     LLVM Source: UndefValue</a>
 * @see <a href="https://llvm.org/docs/LangRef.html#undefined-values">
 *     LLVM Ref: Undefined Values</a>
 */
public class DummyValue extends Value {

    public DummyValue(Type type) {
        super(type);
    }
}
