package passes.ir;

public class Function {
    protected final ir.values.Function rawFunction;

    public Function(ir.values.Function rawFunction){
        this.rawFunction = rawFunction;
    }

    public ir.values.Function getRawFunction() {
        return rawFunction;
    }


}
