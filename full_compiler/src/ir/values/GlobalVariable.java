package ir.values;

import ir.Type;
import ir.User;
import ir.types.PointerType;

/**
 * A GlobalVariable refers to a block of memory that can be determined at compilation time.
 * <br>
 * A global variable may have an initial value. Global Constants are required to have
 * initializers.
 * <br>
 * Type for GlobalVariable is PointerType of type of the memory block referenced.
 * @see <a href="https://llvm.org/docs/LangRef.html#global-variables">
 *     LLVM LangRef: Global Variable</a>
 * @see <a href="https://github.com/hdoc/llvm-project/blob/release/13.x/llvm/include/llvm/IR/GlobalVariable.h#L40">
 *     LLVM IR Source: GlobalVariable</a>
 */
public class GlobalVariable extends User {


    /**
     * An anonymous glb var can be recognized
     * and assigned a valid name before emitted.
     */
    private boolean isAnonymous = false;

    public boolean isAnonymous() {
        return isAnonymous;
    }

    /**
     * If it is a global constant.
     */
    private boolean isGlbConst = false;

    /**
     * If it is a global constant.
     * @return True if it is. Otherwise, false.
     */
    public boolean isConstant() {
        return isGlbConst;
    }

    /**
     * Set the GlbVar to be a Constant.
     */
    public void setConstant() {
        this.isGlbConst = true;
    }

    /**
     * Represent the initial value specified in the statement.
     * <br>
     * The actual value of a non-constant glb var can be change
     * by other instructions, after which the initVal makes no
     * sense to this Value no more.
     * <br>
     * NOTICE: We use NO Gv::isInitialized field for indicating
     * if a gv is initialized like in LLVM. All uninitialized gv
     * will be assigned with a corresponding zero value.
     * But if in the future, multi-unit compilation requires to be
     * supported, the field needs to be added to help backend determine
     * whether to place gv on .bss or .data section.
     */
    private final Constant initVal;

    public Constant getInitVal() {
        return initVal;
    }


    private void setGvName(String name) {
        if (name == null) {
            this.isAnonymous = true;
        }
        else {
            this.setName("@" + name);
        }
    }

    /**
     * Construct a GlbVar w/o initialization.
     * <br>
     * The initial value will automatically be assigned as zero of the type.
     * @param name The name of the Gv. If it's null, an anonymous one will be constructed.
     * @param type The type of the memory block it references.
     */
    public GlobalVariable(String name, Type type) {
        super(PointerType.getType(type));
        this.setGvName(name);

        this.initVal = type.getZero();
    }

    /**
     * Construct a GlbVar with initialization.
     * <br>
     * The type will automatically be assigned according to
     * the given initial Constant.
     * @param name The name of the Gv. If it's null, an anonymous one will be constructed.
     * @param init The initial value.
     */
    public GlobalVariable(String name, Constant init) {
        super(PointerType.getType(init.getType()));
        this.setGvName(name);
        this.initVal = init;
    }

    /**
     * Check if the Glb Var is an array.
     * @return Yes or no.
     */
    public boolean isArray() {
        return this.getType().getPointeeType().isArrayType();
    }

    /**
     * Get the type of the memory referenced by the GlobalVariable.
     * @return The type of the memory.
     */
    public Type getVariableType() {
        return this.getType().getPointeeType();
    }

    @Override
    public String toString() {
        // e.g. "@a = dso_local [global | constant] i32 1"
        StringBuilder strBuilder = new StringBuilder();
        strBuilder.append(this.getName()).append(" = dso_local ") // "@Glb = dso_local "
                .append(this.isConstant() ? "constant " : "global "); // "[global | constant] "

        if (initVal.isZero()) { // Uninitialized / zero values
            Type arrType = this.getType().getPointeeType();
            strBuilder.append(arrType)
                    .append(" zeroinitializer");
        }
        else {  // Initialized values.
            strBuilder.append(initVal);
        }

        return strBuilder.toString();
    }

    /**
     * Get the PointerType to the memory block referenced.
     * (To get Type of the variable directly, use Gv::getVariableType instead.)
     * @return Corresponding PointerType of the GlbVar.
     */
    @Override
    public PointerType getType() {
        return (PointerType) super.getType();
    }
}
