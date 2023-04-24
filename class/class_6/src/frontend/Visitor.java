package frontend;

import ir.Module;
import ir.Value;
import ir.types.*;
import ir.Type;
import ir.values.*;
import ir.values.Instruction.InstCategory;
import ir.values.constants.ConstArray;
import ir.values.constants.ConstFloat;
import ir.values.constants.ConstInt;
import ir.values.instructions.BinaryOpInst;
import ir.values.instructions.GetElemPtrInst;
import ir.values.instructions.MemoryInst;
import ir.values.instructions.TerminatorInst;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * Visitor can be regarded as a tiny construction worker traveling
 * on the parse tree, using his tools (Builder and Scope) to build
 * in-memory IR constructs one by one during the traversal :D
 */
public class Visitor extends SysYBaseVisitor<Void> {

    private final IRBuilder builder;
    private final Scope scope = new Scope();

    //<editor-fold desc="Back-patching infrastructures for WHILE statements.">
    private final BasicBlock BREAK = new BasicBlock("BRK_PLACEHOLDER");
    private final BasicBlock CONTINUE = new BasicBlock("CONT_PLACEHOLDER");

    /**
     * Stack for back-patching break and continue statements.
     */
    Stack<ArrayList<TerminatorInst.Br>> bpStk = new Stack<>();
    //</editor-fold>

    //<editor-fold desc="Environment variables indicating the building status">
    private final boolean ON = true;
    private final boolean OFF = false;

    /**
     * If the visitor is currently in a constant folding progression.
     */
    private boolean envConstFolding = OFF;

    /**
     * Set the environment variable of constant folding.
     * @param stat ON / OFF
     */
    private void setConstFolding(boolean stat) {
        envConstFolding = stat;
    }

    /**
     * If the visitor is currently in a constant folding progression.
     * @return Yes or no.
     */
    public boolean inConstFolding() {
        return envConstFolding;
    }

    /**
     * If the visitor is currently building a function call (invocation).
     */
    private boolean envBuildFCall = OFF;

    /**
     * Set the environment variable of building function call.
     * @param stat ON / OFF
     */
    private void setBuildFCall(boolean stat) {
        envBuildFCall = stat;
    }

    /**
     * If the visitor is currently building a function call (invocation).
     * @return Yes or no.
     */
    private boolean inBuildFCall() {
        return envBuildFCall;
    }

    /**
     * The enum is for indicating which data type returned from the lower layer
     * for visiting method. (INT -> read retInt_, FLT -> read retFlt_)
     */
    private enum DataType {FLT, INT}

    /**
     * Represents data type returned from the lower layer of visiting method.
     * Only for passing data in primitive types int and float (by retInt_ and retFloat_)
     */
    private DataType envConveyedType = null;

    private DataType getConveyedType() {
        return envConveyedType;
    }

    private void setConveyedType(DataType dataType) {
        envConveyedType = dataType;
    }

    //</editor-fold>

    //<editor-fold desc="Variables storing returned data from the lower layers of visiting.">
    private Value retVal_;
    private ArrayList<Value> retValList_;
    private Type retType_;
    private ArrayList<Type> retTypeList_;
    private int retInt_;
    private float retFloat_;
    //</editor-fold>


    public Visitor (Module module) {
        builder = new IRBuilder(module);
        this.initRuntimeFunctions();
    }


    /**
     * Add declarations of the runtime library functions to symbol table
     * whose definitions will be linked in after assembling.
     * This method is called by constructor and can be called only once.
     */
    private void initRuntimeFunctions() {
        // Return types.
        Type i32Ty = IntegerType.getI32();
        Type floatTy = FloatType.getType();
        Type voidTy = VoidType.getType();
        Type ptrI32Ty = PointerType.getType(i32Ty);
        Type ptrFloatTy = PointerType.getType(floatTy);
        Type ptrVoidTy = PointerType.getType(voidTy);
        // Argument type lists.
        ArrayList<Type> emptyArgTypeList = new ArrayList<>();
        ArrayList<Type> intArgTypeList = new ArrayList<>() {{add(i32Ty);}};
        ArrayList<Type> floatArgTypeList = new ArrayList<>() {{add(floatTy);}};

        // i32 getint()
        scope.addDecl("getint",
                builder.buildFunction("getint", FunctionType.getType(
                        i32Ty , emptyArgTypeList
                ), true)
        );
        // void putint(i32)
        scope.addDecl("putint",
                builder.buildFunction("putint", FunctionType.getType(
                        voidTy, intArgTypeList
                ), true)
        );
        // i32 getfloat()
        scope.addDecl("getfloat",
                builder.buildFunction("getfloat", FunctionType.getType(
                        floatTy , emptyArgTypeList
                ), true)
        );
        // void putfloat(float)
        scope.addDecl("putfloat",
                builder.buildFunction("putfloat", FunctionType.getType(
                        voidTy, floatArgTypeList
                ), true)
        );
        // i32 getch()
        scope.addDecl("getch",
                builder.buildFunction("getch", FunctionType.getType(
                        i32Ty, emptyArgTypeList
                ), true)
        );
        // void putch(i32)
        scope.addDecl("putch",
                builder.buildFunction("putch", FunctionType.getType(
                        voidTy, intArgTypeList
                ), true)
        );
        // i32 getarray(i32*)
        scope.addDecl("getarray",
                builder.buildFunction("getarray", FunctionType.getType(
                        i32Ty, new ArrayList<>() {{add(ptrI32Ty);}}
                ), true)
        );
        // void putarray(i32, i32*)
        scope.addDecl("putarray",
                builder.buildFunction("putarray", FunctionType.getType(
                        voidTy, new ArrayList<>() {{add(i32Ty); add(ptrI32Ty);}}
                ), true)
        );

        // i32 getfarray(float*)
        scope.addDecl("getfarray",
                builder.buildFunction("getfarray", FunctionType.getType(
                        i32Ty, new ArrayList<>() {{add(ptrFloatTy);}}
                ), true)
        );
        // void putfarray(i32, float*)
        scope.addDecl("putfarray",
                builder.buildFunction("putfarray", FunctionType.getType(
                        voidTy, new ArrayList<>() {{add(i32Ty); add(ptrFloatTy);}}
                ), true)
        );
        // void starttime() -> _sysy_starttime(i32)
        // See special case in visitFcall.
        scope.addDecl("starttime",
                builder.buildFunction("_sysy_starttime", FunctionType.getType(
                        voidTy, intArgTypeList
                ), true)
        );
        // void stoptime() -> _sysy_stoptime(i32)
        // See special case in visitFcall.
        scope.addDecl("stoptime",
                builder.buildFunction("_sysy_stoptime", FunctionType.getType(
                        voidTy, intArgTypeList
                ), true)
        );
        // void* memset(void* str, int c, size_t n)
        scope.addDecl("memset",
                builder.buildFunction("memset", FunctionType.getType(
                        voidTy,  new ArrayList<>() {{add(ptrI32Ty); add(i32Ty); add(i32Ty);}}
                ), true));
//        // void* memcpy(void* dest, const void* src, size_t num)
//        scope.addDecl("memcpy",
//                builder.buildFunction("memcpy", FunctionType.getType(
//                        ptrVoidTy,  new ArrayList<>() {{add(ptrVoidTy); add(ptrVoidTy); add(i32Ty);}}
//                ), true));
    }



    /*
    Visit methods overwritten.
     */

    /**
     * compUnit : (decl | funcDef)* EOF
     * -------------------------------------------
     * decl : constDecl | varDecl
     */
    @Override
    public Void visitCompUnit(SysYParser.CompUnitContext ctx) {
        super.visitCompUnit(ctx);
        return null;
    }

    /**
     * constDef : Identifier '=' constInitVal # scalarConstDef
     * -------------------------------------------------------
     * constDecl : 'const' bType constDef (',' constDef)* ';'
     */
    @Override
    public Void visitScalarConstDef(SysYParser.ScalarConstDefContext ctx) {
        // Retrieve the name of the variable defined and check for duplication.
        String varName = ctx.Identifier().getText();
        if (scope.duplicateDecl(varName)) {
            throw new RuntimeException("Duplicate definition of constant name: " + varName);
        }

        /*
        Since SysY does NOT support pointer (meaning we don't have to worry abt memory
        address of different constants), a constant scalar can be directly referenced as an
        instant number by other Value (e.g. instructions), w/o the need of building an
        Alloca instruction like variable.
         */

        // Retrieve the initialized value (as a Constant) by visiting child (scalarConstDef).
        visit(ctx.constInitVal());
        Value initVal = retVal_;

        // Type matching check and implicit type conversion.
        String bType = ctx.getParent().getChild(1).getText();
        switch(bType) {
            case "int" -> {
                if (initVal.getType().isFloatType()) {
                    float numericVal = ((ConstFloat) initVal).getVal();
                    initVal = builder.buildConstant((int) numericVal);
                }
            }
            case "float" -> {
                if (initVal.getType().isIntegerType()) {
                    int numericVal = ((ConstInt) initVal).getVal();
                    initVal = builder.buildConstant((float) numericVal);
                }
            }
        }

        // Update the symbol table.
        scope.addDecl(varName, initVal);

        return null;
    }

    /**
     * constInitVal: constExp # scalarConstInitVal
     * --------------------------------------------
     * constExp : addExp
     */
    @Override
    public Void visitScalarConstInitVal(SysYParser.ScalarConstInitValContext ctx) {
        this.setConstFolding(ON);

        super.visitScalarConstInitVal(ctx);

        this.setConstFolding(OFF);

        // Convert the constant value aft folding as a Constant IR Value.
        switch (getConveyedType()) {
            case INT -> retVal_ = builder.buildConstant(retInt_);
            case FLT -> retVal_ = builder.buildConstant(retFloat_);
        }
        return null;
    }

    /**
     * constDef : Identifier ('[' constExp ']')+ '=' constInitVal   # arrConstDef
     * constInitVal : '{' (constInitVal (',' constInitVal)* )? '}'  # arrConstInitVal
     */
    @Override
    public Void visitArrConstDef(SysYParser.ArrConstDefContext ctx) {
        // Scan to retrieve the length of each dimension, storing them in a list.
        ArrayList<Integer> dimLens = new ArrayList<>();
        for (SysYParser.ConstExpContext constExpContext : ctx.constExp()) {
            visit(constExpContext);
            int dimLen = ((ConstInt) retVal_).getVal();
            dimLens.add(dimLen);
        }

        // The type of the basic element in the array.
        Type tmpType = null;
        // Retrieve the basic element type.
        String bType = ctx.getParent().getChild(1).getText();
        switch (bType) {
            case "int" -> tmpType = IntegerType.getI32();
            case "float" -> tmpType = FloatType.getType();
        }
        // Build the final type of the array
        // by looping through the dimLens from the inside out.
        for (int i = dimLens.size(); i > 0; i--) {
            tmpType = ArrayType.getType(tmpType, dimLens.get(i - 1));
        }
        ArrayType arrType = (ArrayType) tmpType;

        /*
        In SysY, no explicit addresses or pointers is allowed, because of which
        all const local arrays can be directly promoted to be located in global space.
        (no one cares whether its address is on stack or global segment).

        Thus, all const arrays are treated as global arrays.
        But promoted local arrays needs to be reassigned a name before emitted to prevent
        conflicts with variables having the same names in the outer scopes.
         */

        String gvName = scope.isGlobal() ? ctx.Identifier().getText() : null;
        GlobalVariable arr;

        // With Initialization.
        if (ctx.constInitVal() != null) {
            // Pass down the lengths of each dimension.
            // Visit constInitVal (ArrConstInitVal) to generate the initial list for the array
            // which will be filled with 0 by visitArrConstInitVal if the number of given initial
            // values is not enough.
            ctx.constInitVal().dimLens = dimLens;
            ctx.constInitVal().sizCurDepth = getProductOf(dimLens);

            this.setConstFolding(ON);
            visit(ctx.constInitVal());
            this.setConstFolding(OFF);

            // ArrConstInitVal will generate an array of Values,
            // convert them into Constants and build a ConstArray.
            ArrayList<Constant> initList = new ArrayList<>();
            for (Value val : retValList_) {
                initList.add((ConstInt) val);
            }
            ConstArray initArr = builder.buildConstArr(arrType, initList);
            // Build the ConstArray a global variable.
            arr = builder.buildGlbVar(gvName, initArr);
        }
        // W/o initialization.
        else {
            arr = builder.buildGlbVar(gvName, arrType);
        }

        // This line will also check if a glb var is successfully built.
        // A java exception will be thrown if arr == null.
        arr.setConstant();
        // Add the array into the symbol table.
        scope.addDecl(ctx.Identifier().getText(), arr);

        return null;
    }

    /**
     * constInitVal : '{' (constInitVal (',' constInitVal)* )? '}'  # arrConstInitVal
     */
    @Override
    public Void visitArrConstInitVal(SysYParser.ArrConstInitValContext ctx) {
        // For arr[3][2] with initialization { {1,2}, {3,4}, {5,6} },
        // the dimLen = 3 and sizSublistInitNeeded = 2.
        int dimLen = ctx.dimLens.get(0);

        ArrayList<Value> initArr = new ArrayList<>();
        for (SysYParser.ConstInitValContext constInitValContext : ctx.constInitVal()) {
            // If the one step lower level still isn't the atom element layer.
            if (constInitValContext instanceof SysYParser.ArrConstInitValContext) {
                constInitValContext.dimLens = new ArrayList<>(
                        ctx.dimLens.subList(1, ctx.dimLens.size()));
                constInitValContext.sizCurDepth = ctx.sizCurDepth / dimLen;
                visit(constInitValContext);
                initArr.addAll(retValList_);
            }
            // If it is the lowest layer.
            else {
                visit(constInitValContext);
                initArr.add(retVal_);
            }
        }

        // Security check.
        if (initArr.size() > ctx.sizCurDepth) {
            throw new RuntimeException("The length of initList (" + initArr.size() + ")" +
                    " exceeds the maximum size of current depth (" + ctx.sizCurDepth + ")");
        }

        // Fill the initialized list with enough 0.
        int sizToFillTo = (ctx.getParent() instanceof SysYParser.ArrConstDefContext) ?
                ctx.sizCurDepth : ctx.dimLens.get(ctx.dimLens.size() - 1);
        for (int i = initArr.size(); i < sizToFillTo; i++) {
            initArr.add(builder.buildConstant(0));
        }
        retValList_ = initArr;

        return null;
    }

    /**
     * varDef : Identifier ('=' initVal)? # scalarVarDef
     * --------------------------------------------------------
     * varDecl : bType varDef (',' varDef)* ';'
     * initVal : expr # scalarInitVal
     */
    @Override
    public Void visitScalarVarDef(SysYParser.ScalarVarDefContext ctx) {
        // The text of the grammar symbol bType ("int" / "float")
        String bType = ctx.getParent().getChild(0).getText();

        // Retrieve the name of the variable defined and check for duplication.
        String varName = ctx.Identifier().getText();
        if (scope.duplicateDecl(varName)) {
            throw new RuntimeException("Duplicate definition of variable name: " + varName);
        }

        /*
        A global variable.
         */
        if (scope.isGlobal()) {
            GlobalVariable glbVar;

            // With initialization.
            if (ctx.initVal() != null) {
                visit(ctx.initVal());
                Value initVal = retVal_;
                // Type matching check and conversion.
                switch (bType) {
                    case "int" -> {
                        if (initVal.getType().isFloatType()) {
                            float numericVal = ((ConstFloat) initVal).getVal();
                            initVal = builder.buildConstant((int) numericVal);
                        }
                    }
                    case "float" -> {
                        if (initVal.getType().isIntegerType()) {
                            int numericVal = ((ConstInt) initVal).getVal();
                            initVal = builder.buildConstant((float) numericVal);
                        }
                    }
                }
                // Build the glb var.
                glbVar = builder.buildGlbVar(varName, (Constant) initVal);
            }

            // W/o initialization.
            else {
                switch (bType) {
                    case "int" -> glbVar = builder.buildGlbVar(varName, IntegerType.getI32());
                    case "float" -> glbVar = builder.buildGlbVar(varName, FloatType.getType());
                    default -> throw new RuntimeException("Unsupported type."); // Impossible case.
                }
            }

            // Update the symbol table.
            scope.addDecl(varName, glbVar);
        }

        /*
        A local variable.
         */
        else {
            MemoryInst.Alloca addrAllocated;
            switch (bType) {
                case "int" -> addrAllocated = builder.buildAlloca(IntegerType.getI32());
                case "float" -> addrAllocated = builder.buildAlloca(FloatType.getType());
                default -> throw new RuntimeException("Unsupported type."); // Impossible case.
            }
            scope.addDecl(varName, addrAllocated);
            // If it's a definition with initialization.
            if (ctx.initVal() != null) {
                // Retrieve the Value for initialization.
                visit(ctx.initVal());
                Value initVal = retVal_;
                // Implicit type conversion.
                if (initVal.getType().isIntegerType() && addrAllocated.getAllocatedType().isFloatType()) {
                    initVal = builder.buildSitofp(initVal);
                }
                else if(initVal.getType().isFloatType() && addrAllocated.getAllocatedType().isIntegerType()) {
                    initVal = builder.buildFptosi(initVal, (IntegerType) addrAllocated.getAllocatedType());
                }
                // Assignment by building a Store inst.
                builder.buildStore(initVal, addrAllocated);
            }
        }

        return null;
    }

    /**
     * initVal : expr # scalarInitVal
     * ------------------------------------
     * expr : addExp
     */
    @Override
    public Void visitScalarInitVal(SysYParser.ScalarInitValContext ctx) {
        // Turn on constant folding switch.
        if (scope.isGlobal()) {
            this.setConstFolding(ON);
        }
        super.visitScalarInitVal(ctx);
        // Turn off constant folding switch.
        if (scope.isGlobal()) {
            switch (getConveyedType()) {
                case INT -> retVal_ = builder.buildConstant(retInt_);
                case FLT -> retVal_ = builder.buildConstant(retFloat_);
            }
            setConstFolding(OFF);
        }

        return null;
    }

    /**
     * initVal : '{' (initVal (',' initVal)* )? '}'  # arrInitval
     */
    @Override
    public Void visitArrInitval(SysYParser.ArrInitvalContext ctx) {

        // For arr[3][2] with initialization { {1,2}, {3,4}, {5,6} },
        // the curDimLen = 3 and sizSublistInitNeeded = 2.
        int curDimLen = ctx.dimLens.get(0);

        ArrayList<Value> initArr = new ArrayList<>();
        for (SysYParser.InitValContext initValContext : ctx.initVal()) {
            // If the one step lower level still isn't the atom element layer.
            if (initValContext instanceof SysYParser.ArrInitvalContext) {
                initValContext.dimLens = new ArrayList<>(
                        ctx.dimLens.subList(1, ctx.dimLens.size()));
                initValContext.sizCurDepth = ctx.sizCurDepth / curDimLen;

                visit(initValContext);
                initArr.addAll(retValList_);
            }
            // If it is the lowest layer of an atom element.
            else {
                visit(initValContext);
                initArr.add(retVal_);
            }
        }

        // Security check.
        if (initArr.size() > ctx.sizCurDepth) {
            throw new RuntimeException("The length of initList (" + initArr.size() + ")" +
                    " exceeds the maximum size of current depth (" + ctx.sizCurDepth + ")");
        }

        // Fill the initialized list of current layer with enough 0.
        // NOTICE1: This step is necessary for dealing with the "{}" initializer in SysY.
        // TODO: But this can be a performance bottle neck with big "{}". same as visitArrConstInitVal
        // NOTICE2: Only for the outer-most initializer should fill the return list up to the sizCurDepth required.
        // Any atom elements in any nested sub-initializer (inner layer) are regarded as the inner-most layer elements,
        // where the layer should be filled up to only the size of last dimension.
        int sizToFillTo = (ctx.getParent() instanceof SysYParser.ArrVarDefContext) ?
                ctx.sizCurDepth : ctx.dimLens.get(ctx.dimLens.size() - 1);
        for (int i = initArr.size(); i < sizToFillTo; i++) {
            initArr.add(builder.buildConstant(0));
        }
        retValList_ = initArr;

        return null;
    }

    /**
     * varDef : Identifier ('[' constExp ']')+ ('=' initVal)?  # arrVarDef
     */
    @Override
    public Void visitArrVarDef(SysYParser.ArrVarDefContext ctx) {
        // Get all lengths of dimension by looping through the constExp list.
        ArrayList<Integer> dimLens = new ArrayList<>();
        for (SysYParser.ConstExpContext constExpContext : ctx.constExp()) {
            this.setConstFolding(ON);
            visit(constExpContext);
            this.setConstFolding(OFF);

            int dimLen = retInt_;
            dimLens.add(dimLen);
        }

        Type tmpType = null;
        // Retrieve the basic element type.
        String bType = ctx.getParent().getChild(0).getText();
        switch (bType) {
            case "int" -> tmpType = IntegerType.getI32();
            case "float" -> tmpType = FloatType.getType();
        }
        // Build the arrType bottom-up (reversely).
        for (int i = dimLens.size(); i > 0; i--) {
            tmpType = ArrayType.getType(tmpType, dimLens.get(i - 1));
        }
        ArrayType arrType = (ArrayType) tmpType;

        /*
        Global array.
         */
        if (scope.isGlobal()) {
            // With initialization.
            if (ctx.initVal() != null) {
                // Pass down dim info.
                // Visit child to retrieve the initialized Value list (stored in retValList_).
                ctx.initVal().dimLens = dimLens;
                ctx.initVal().sizCurDepth = getProductOf(dimLens);

                this.setConstFolding(ON);
                visit(ctx.initVal());
                this.setConstFolding(OFF);

                // Convert the Values returned into Constants.
                ArrayList<Constant> initList = new ArrayList<>();
                for (Value val : retValList_) {
                    initList.add((Constant) val);
                }
                // Build the const array, set it to be a global variable and put it into the symbol table.
                ConstArray initArr = builder.buildConstArr(arrType, initList);
                GlobalVariable arr = builder.buildGlbVar(ctx.Identifier().getText(), initArr);
                scope.addDecl(ctx.Identifier().getText(), arr);
            }
            // W/o initialization.
            else {
                GlobalVariable arr = builder.buildGlbVar(ctx.Identifier().getText(), arrType);
                scope.addDecl(ctx.Identifier().getText(), arr);
            }
        }
        /*
        Local array.
         */
        else {
            var alloca = builder.buildAlloca(arrType);
            scope.addDecl(ctx.Identifier().getText(), alloca);

            // If there's initialization, translate it as several GEP & Store combos.
            if (ctx.initVal() != null) {
                // Compute and pass down dimensional info, visit child to generate initialization assignments.
                ctx.initVal().dimLens = dimLens;
                ctx.initVal().sizCurDepth = getProductOf(dimLens);

                visit(ctx.initVal());
                int zeroTail = getZeroTailLen(retValList_);

                /*
                Indexing array with any number of dimensions with GEP in 1-d array fashion.
                 */
                // Dereference the pointer returned by Alloca to be an 1-d array address.
                ArrayList<Value> zeroIndices = new ArrayList<>() {{
                    add(builder.buildConstant(0));
                    add(builder.buildConstant(0));
                }};
                GetElemPtrInst ptr1d = builder.buildGEP(alloca, zeroIndices);
                for (int i = 1; i < dimLens.size(); i++) {
                    ptr1d = builder.buildGEP(ptr1d, zeroIndices);
                }

                /*
                Firstly fill the memory block with zero using memset.
                 */
                // Args of memset.
                // For arg str: Cast float* to i32* if needed.
                GetElemPtrInst startPoint = ptr1d;
                Value str;
                if (startPoint.getType().getPointeeType().isI32()) {
                    str = startPoint;
                }
                else {
                    str = builder.buildBitcast(startPoint, PointerType.getType(IntegerType.getI32()));
                }
                ConstInt c = builder.buildConstant(0);
                // For arg n: In SysY, both supported data types (int/float) are 4 bytes.
                ConstInt n = builder.buildConstant(4 * arrType.getAtomLen());

                //Call memset.
                builder.buildCall((Function) scope.getValByName("memset"), new ArrayList<>(){{
                    add(str);
                    add(c);
                    add(n);
                }});


                // Initialize linearly using the 1d pointer and offset.
                GetElemPtrInst gep = ptr1d;
                for (int i = 0; i < retValList_.size() - zeroTail; i++) {
                    Value initVal = retValList_.get(i);

                    // If the initial Value is a Constant zero (literal 0 or .0f),
                    // skip this round to not generate any Store instruction.
                    if (initVal instanceof ConstInt && initVal == IntegerType.getI32().getZero()
                            || initVal instanceof ConstFloat && initVal == FloatType.getType().getZero()) {
                        continue;
                    }

                    // Index the address of the cell to store initial data.
                    if (i > 0) {
                        int offset1d = i;
                        gep = builder.buildGEP(ptr1d, new ArrayList<>() {{
                            add(builder.buildConstant(offset1d));
                        }});
                    }

                    // Type matching check and conversion.
                    if (initVal.getType().isIntegerType() && arrType.getElemType().isFloatType()) {
                        initVal = builder.buildSitofp(initVal);
                    }
                    else if (initVal.getType().isFloatType() && arrType.getElemType().isIntegerType()) {
                        initVal = builder.buildFptosi(initVal, (IntegerType) arrType.getElemType());
                    }

                    // Assign the initial value with a Store.
                    builder.buildStore(initVal, gep);
                }


            }
        }
        return null;
    }

    private int getProductOf(List<Integer> list) {
        int prod = 1;
        for (Integer i : list) {
            prod *= i;
        }
        return prod;
    }

    /**
     * Get number of zeros at the end of the list.
     * @param list A list containing Values.
     * @return The length of the zero tail.
     */
    private int getZeroTailLen(List<Value> list) {
        int len = 0;
        for (int i = list.size() - 1; i > 0; i--) {
            var elem = list.get(i);
            if (!(elem instanceof Constant)) {
                break;
            }
            var constElem = (Constant) elem;
            if (constElem.getType().isFloatType() && !constElem.isZero()
                    || constElem.getType().isIntegerType() && !constElem.isZero() ) {
                break;
            }
            len++;
        }
        return len;
    }

    /**
     * funcDef : funcType Identifier '(' (funcFParams)? ')' block
     */
    @Override
    public Void visitFuncDef(SysYParser.FuncDefContext ctx) {

        /*
        Collect object info.
         */

        // Get the function name.
        String funcName = ctx.Identifier().getText();

        // Get the return type. (funcType identifier)
        Type retType;
        String strRetType = ctx.funcType().getText();
        switch (strRetType) {
            case "int" -> retType = IntegerType.getI32();
            case "float" -> retType = FloatType.getType();
            case "void" -> retType = VoidType.getType();
            default -> throw new RuntimeException("Unsupported function return type.");
        }

        // Get the argument list. (Visiting child)
        ArrayList<Type> argTypes = new ArrayList<>();
        if (ctx.funcFParams() != null) {
            visit(ctx.funcFParams());
            argTypes.addAll(retTypeList_);
        }
        retTypeList_ = new ArrayList<>(); // Clear the list for next func def.

        /*
        Build IR.
         */
        // Security check (allow no nested definition of functions)
        if (!scope.isGlobal()) {
            throw new RuntimeException("Nested definition of function: " + funcName);
        }
        // Insert a function into the module and symbol table.
        FunctionType funcType = FunctionType.getType(retType, argTypes);
        Function function = builder.buildFunction(funcName, funcType, false);
        scope.addDecl(funcName, function);

        // Insert a basic block. Then scope in.
        BasicBlock bb = builder.buildBB(funcName + "_ENTRY");
        scope.scopeIn();

        /*
        Allocate all the formal arguments INSIDE the scope of the function.
         */
        for (int i = 0; i < function.getArgs().size(); i++) {
            Function.FuncArg arg = function.getArgs().get(i);
            // Allocate a local memory on the stack for the arg.
            MemoryInst.Alloca localVar = builder.buildAlloca(arg.getType());
            // Add the memory allocated to the symbol table.
            // It's an ugly way to retrieve the name of the args
            // since no elegant way is found so far.
            String argName = null;
            if (ctx.funcFParams().funcFParam(i) instanceof SysYParser.ScalarFuncFParamContext) {
                var ctxArg = (SysYParser.ScalarFuncFParamContext) ctx.funcFParams().funcFParam(i);
                argName = ctxArg.Identifier().getText();
            } else if (ctx.funcFParams().funcFParam(i) instanceof SysYParser.ArrFuncFParamContext) {
                var ctxArg = (SysYParser.ArrFuncFParamContext) ctx.funcFParams().funcFParam(i);
                argName = ctxArg.Identifier().getText();
            }
            scope.addDecl(argName, localVar);
            // Copy the value to the local memory.
            builder.buildStore(arg, localVar);
        }

        /*
        Process function body. (Visiting child)
         */
        visit(ctx.block());

        /*
        Check the last basic block of the function to see if there is a
        return statement given in the source.
        If not, insert a terminator to the end of it.
         */
        Instruction tailInst = builder.getCurBB().getLastInst();
        // If no instruction in the bb, or the last instruction is not a terminator.
        if (tailInst == null || !tailInst.getTag().isTerminator()) {
            if (function.getType().getRetType().isVoidType()) {
                builder.buildRet();
            }
            else if (function.getType().getRetType().isIntegerType()) {
                builder.buildRet(builder.buildConstant(0)); // Return 0 by default.
            }
            else if (function.getType().getRetType().isFloatType()) {
                builder.buildRet(builder.buildConstant(.0f)); // Return 0.0f by default.
            }
        }

        /*
        Scope out.
         */
        scope.scopeOut();

        /*
        Check the function just built.
         */
        checkFunc(builder.getCurFunc());

        return null;
    }

    /**
     * Check if anything wrong in the Function generated.
     * @param func The function to be checked.
     */
    private void checkFunc(Function func) {
        for (BasicBlock bb : func) {
            if (bb.isEmpty()) {
                throw new RuntimeException("There is an empty BasicBlock in the function generated.");
            }
            else if (!bb.getLastInst().getTag().isTerminator()) {
                throw new RuntimeException("There is a BasicBlock with non-terminator at the end.");
            }
        }
    }

    /**
     * funcFParams : funcFParam (',' funcFParam)*
     */
    @Override
    public Void visitFuncFParams(SysYParser.FuncFParamsContext ctx) {
        retTypeList_ = new ArrayList<>();
        for (SysYParser.FuncFParamContext funcFParamContext : ctx.funcFParam()) {
            visit(funcFParamContext);
            retTypeList_.add(retType_);
        }
        return null;
    }


    /**
     * funcFParam : bType Identifier  # scalarFuncFParam
     */
    @Override
    public Void visitScalarFuncFParam(SysYParser.ScalarFuncFParamContext ctx) {
        String bType = ctx.bType().getText();
        switch (bType) {
            case "int" -> retType_ = IntegerType.getI32();
            case "float" -> retType_ = FloatType.getType();
            default -> throw new RuntimeException("Supported function argument type.");
        }

        return null;
    }

    /**
     * funcFParam : bType Identifier '[' ']' ('[' expr ']')*  # arrFuncFParam
     */
    @Override
    public Void visitArrFuncFParam(SysYParser.ArrFuncFParamContext ctx) {
        ArrayList<Integer> dimLens = new ArrayList<>();

        // Retrieve dimLens info of the array as arg.
        // 'expr' as array lengths for func formal args should be constants (Turn on ConstFolding)
        setConstFolding(ON);
        for (SysYParser.ExprContext exprContext : ctx.expr()) {
            visit(exprContext);
            dimLens.add(retInt_);
        }
        setConstFolding(OFF);

        // Build the ArrayType of the function argument.
        Type arrType;
        String bType = ctx.bType().getText();
        switch (bType) {
            case "int" -> arrType = IntegerType.getI32();
            case "float" -> arrType = FloatType.getType();
            default -> throw new RuntimeException("Supported function argument type.");
        }
        for (int i = dimLens.size(); i > 0; i--) {
            arrType = ArrayType.getType(arrType, dimLens.get(i - 1));
        }

        retType_ = PointerType.getType(arrType);
        return null;
    }


    /**
     * block : '{' (blockItem)* '}'
     * blockItem : decl | stmt
     */
    @Override
    public Void visitBlock(SysYParser.BlockContext ctx) {
        scope.scopeIn(); // Add a new layer of scope (a new symbol table).
        ctx.blockItem().forEach(this::visit);
        scope.scopeOut(); // Pop it out before exiting the scope.
        return null;
    }

    /**
     * stmt : 'return' (expr)? ';'
     */
    @Override
    public Void visitRetStmt(SysYParser.RetStmtContext ctx) {
        // If there is an expression component to be returned,
        // visit child to retrieve it.
        if (ctx.expr() != null) {
            visit(ctx.expr());

            // Return type matching check and conversion.
            Value retVal = retVal_;
            Type retType = builder.getCurFunc().getType().getRetType(); // The return type defined in the prototype.
            if (retVal.getType().isIntegerType() && retType.isFloatType()) {
                retVal = builder.buildSitofp(retVal);
            }
            else if (retVal.getType().isFloatType() && retType.isIntegerType()) {
                retVal = builder.buildFptosi(retVal, (IntegerType) retType);
            }

            builder.buildRet(retVal);
        }
        // If not, return void.
        else {
            builder.buildRet();
        }
        // Add a dead block for possible remaining dead code.
        builder.buildBB("_FOLLOWING_BLK");
        return null;
    }

    /**
     * stmt : 'if' '(' cond ')' stmt ('else' stmt)? # condStmt
     * --------------------------------------------------------
     * cond : lOrExp
     */
    @Override
    public Void visitCondStmt(SysYParser.CondStmtContext ctx) {
        /*
        Store current block to add on it a Br to entryBlk.
        And then build the entry block of the condition statement.
         */
        BasicBlock preBlk = builder.getCurBB();
        BasicBlock entryBlk = builder.buildBB("_COND_ENTRY");
        // Add a Br from the old preBlk to the new entryBlk.
        builder.setCurBB(preBlk);
        builder.buildBr(entryBlk);

        /*
        Build an EXIT block no matter if it may become dead code
        that cannot be reached in the CFG.
         */
        BasicBlock exitBlk = builder.buildBB("_COND_EXIT");

        /*
        Build the TRUE branch (a block for jumping if condition is true).
        Fill it by visiting child (the 1st stmt, the true branch).
         */
        BasicBlock trueEntryBlk = builder.buildBB("_THEN");
        visit(ctx.stmt(0));
        BasicBlock trueExitBlk = builder.getCurBB();
        // Get trueBlkEndWithTerminator flag.
        Instruction trueExitBlkLastInst = trueExitBlk.getLastInst();
        boolean trueBlkEndWithTerminator = trueExitBlkLastInst != null &&
                trueExitBlkLastInst.getTag().isTerminator();

        /*
        Build the FALSE branch (a block for jumping if condition is false),
        if there is the 2nd stmt, meaning that it's an IF-ELSE statement.
        Otherwise, it's an IF statement (w/o following ELSE), and
        falseEntryBlk will remain null.

        : if(falseEntryBlk != null) -> IF-ELSE statement
        : if(falseEntryBlk == null) -> IF statement w/o ELSE
         */
        BasicBlock falseEntryBlk = null;
        BasicBlock falseExitBlk = null;
        boolean falseBlkEndWithTerminator = false;
        if (ctx.stmt(1) != null) {
            falseEntryBlk = builder.buildBB("_ELSE");
            visit(ctx.stmt(1)); // Fill the block by visiting child.
            falseExitBlk = builder.getCurBB();
            // Get falseBlkEndWithTerminator flag.
            Instruction falseExitBlkLastInst = falseExitBlk.getLastInst();
            falseBlkEndWithTerminator = falseExitBlkLastInst != null &&
                    falseExitBlkLastInst.getTag().isTerminator();
        }

        /*
        Add Br terminator for trueExitBlock and falseExitBlock if needed (if both branches
        end with Ret terminators.
         */
        // The exit block will be built when:
        // "!trueBlkEndWithTerminator && !falseBlkEndWithTerminator" (under IF-ELSE)
        // or "!trueBlkEndWithTerminator && no falseBlock" (i.e. IF w/o ELSE)
        if (!trueBlkEndWithTerminator) {
            builder.setCurBB(trueExitBlk);
            builder.buildBr(exitBlk);
        }
        if (falseEntryBlk != null && !falseBlkEndWithTerminator) {
            builder.setCurBB(falseExitBlk);
            builder.buildBr(exitBlk);
        }

        /*
        Cope with the condition expression by visiting child cond.
         */
        builder.setCurBB(entryBlk);
        // Pass down blocks as inherited attributes for short-circuit evaluation.
        ctx.cond().lOrExp().trueBlk = trueEntryBlk;
        ctx.cond().lOrExp().falseBlk = (falseEntryBlk != null) ? falseEntryBlk : exitBlk;

        visit(ctx.cond());

        /*
        Force the BB pointer to point to the exitBlk, which will serve as the upstream
        block for processing the following content.
        Even if the exitBlk is a dead entry that cannot be reached, all the content will
        still be processed. These dead basic blocks can be removed in the following
        CFG analysis by the optimizer.
         */
        builder.setCurBB(exitBlk);

        return null;
    }

    /**
     * lOrExp : lAndExp ('||' lAndExp)*
     * ---------------------------------
     * cond : lOrExp
     */
    @Override
    public Void visitLOrExp(SysYParser.LOrExpContext ctx) {
        //<editor-fold desc="For first N-1 lAndExp blocks.">
        for(int i = 0; i < ctx.lAndExp().size() - 1; i++) {
            BasicBlock curLOrBlk = builder.getCurBB();
            BasicBlock nxtLOrBlk = builder.buildBB();

            // Pass down blocks as inherited attributes for short-circuit evaluation.
            ctx.lAndExp(i).falseBlk = nxtLOrBlk;
            ctx.lAndExp(i).trueBlk = ctx.trueBlk;

            builder.setCurBB(curLOrBlk);
            visit(ctx.lAndExp(i));
            builder.setCurBB(nxtLOrBlk);
        }
        //</editor-fold>


        //<editor-fold desc="For the last lAndExp block.">
        ctx.lAndExp(ctx.lAndExp().size() - 1).falseBlk = ctx.falseBlk;
        ctx.lAndExp(ctx.lAndExp().size() - 1).trueBlk = ctx.trueBlk;
        visit(ctx.lAndExp(ctx.lAndExp().size() - 1));
        //</editor-fold>

        return null;
    }

    /**
     * lAndExp : eqExp ('&&' eqExp)*
     * ---------------------------------
     * lOrExp : lAndExp ('||' lAndExp)*
     * eqExp : relExp (('==' | '!=') relExp)*
     * --------------------------------------------------
     * lAndExp is the smallest unit for short-circuit
     * evaluation.
     * <br>
     * It's noteworthy that although nonterminals lAndExp, lOrExp, eqExp,
     * relExp have names indicating logical AND/OR expression, equal
     * expression, relational expression respectively, these grammar
     * symbols also covered patterns of scalar (a single number) without
     * logical/relational composition.
     * <br>
     * Technically, logical operations (covered by lAndExp, lOrExp) and
     * condition expression (corresponding to cond) should be guaranteed
     * to yield a boolean type (i1) value.
     * <br>
     * However, in SysY, all the cases above appear only in the
     * "cond-lOrExp-lAndExp(-eqExp-relExp)" chain, therefore we only
     * need to add one building action of "icmp" in the
     * middle of the chain to make sure the cond node produces i1 value.
     * Since logical AND is the atom for short circuit evaluation,
     * we add icmp here.
     */
    @Override
    public Void visitLAndExp(SysYParser.LAndExpContext ctx) {
        for(int i = 0; i < ctx.eqExp().size(); i++) {
            visit(ctx.eqExp(i));

            /*
            Type conversions of the condition.
             */
            if(retVal_.getType().isI32()) { // i32 -> i1
                // If eqExp gives a number (i32), cast it to be a boolean by NE comparison.
                retVal_ = builder.buildComparison("!=", retVal_, ConstInt.getI32(0));
            }
            else if (retVal_.getType().isFloatType()) { // float -> i1
                retVal_ = builder.buildComparison("!=", retVal_, ConstFloat.get(.0f));
            }

            /*
            Build the branching.
             */
            // For the first N-1 eqExp blocks.
            if(i < ctx.eqExp().size() - 1) {
                // Build following blocks for short-circuit evaluation.
                BasicBlock originBlk = builder.getCurBB();
                BasicBlock nxtAndBlk = builder.buildBB();
                // Add a branch instruction to terminate this block.
                builder.setCurBB(originBlk);
                builder.buildBr(retVal_, nxtAndBlk, ctx.falseBlk);
                builder.setCurBB(nxtAndBlk);
            }
            // For the last eqExp blocks.
            else {
                builder.buildBr(retVal_, ctx.trueBlk, ctx.falseBlk);
            }
        }

        return null;
    }

    /**
     * eqExp : relExp (('==' | '!=') relExp)*
     * --------------------------------------------------
     * relExp : addExp (('<' | '>' | '<=' | '>=') addExp)*
     * <br>
     * For "rel1 == rel2 == rel3", the executing order is
     * "(rel1 == rel2) === rel3"
     */
    @Override
    public Void visitEqExp(SysYParser.EqExpContext ctx) {
        // Retrieve left operand by visiting child.
        visit(ctx.relExp(0));
        Value lOp = retVal_;

        for (int i = 1; i < ctx.relExp().size(); i++) {
            // Retrieve the next relExp as the right operand by visiting child.
            visit(ctx.relExp(i));
            Value rOp = retVal_;

            /*
            Implicit type conversions.
             */
            if (lOp.getType().isFloatType() && !rOp.getType().isFloatType()) {
                rOp = builder.buildSitofp(rOp);
            }
            else if (!lOp.getType().isFloatType() && rOp.getType().isFloatType()) {
                lOp = builder.buildSitofp(lOp);
            }
            else {
                // Extend if one Opd is i32 and another is i1.
                if(lOp.getType().isI32() && rOp.getType().isI1()) {
                    rOp = builder.buildZExt(rOp);
                }
                if(rOp.getType().isI32() && lOp.getType().isI1()) {
                    lOp = builder.buildZExt(lOp);
                }
            }

            /*
            Build a comparison instruction, which yields a result
            to be the left operand for the next round.
             */
            String opr = ctx.getChild(2 * i - 1).getText(); // The comparison operator.
            lOp = builder.buildComparison(opr, lOp, rOp);
        }
        // The final result is stored in the last left operand.
        retVal_ = lOp;

        return null;
    }

    /**
     * relExp : addExp (('<' | '>' | '<=' | '>=') addExp)*
     * ----------------------------------------------------------
     * <br>
     * For "addExp1 < addExp2 >= addExp3", the executing order is
     * "(rel1 < rel2) >= rel3"
     */
    @Override
    public Void visitRelExp(SysYParser.RelExpContext ctx) {
        // Retrieve left operand by visiting child.
        visit(ctx.addExp(0));
        Value lOp = retVal_;

        for (int i = 1; i < ctx.addExp().size(); i++) {
            // Retrieve the next addExp as the right operand by visiting child.
            visit(ctx.addExp(i));
            Value rOp = retVal_;

            /*
            Implicit type conversions.
             */
            if (lOp.getType().isFloatType() && !rOp.getType().isFloatType()) {
                rOp = builder.buildSitofp(rOp);
            }
            else if (!lOp.getType().isFloatType() && rOp.getType().isFloatType()) {
                lOp = builder.buildSitofp(lOp);
            }
            else {
                // Same as visitEqExp above: Extend if one Opd is i32 and another is i1.
                if (lOp.getType().isI32() && rOp.getType().isI1()) {
                    rOp = builder.buildZExt(rOp);
                }
                if (rOp.getType().isI32() && lOp.getType().isI1()) {
                    lOp = builder.buildZExt(lOp);
                }
            }

            /*
            Build a comparison instruction, which yields a result
            to be the left operand for the next round.
             */
            String opr = ctx.getChild(2 * i - 1).getText(); // The comparison operator.
            lOp = builder.buildComparison(opr, lOp, rOp);
        }
        // The final result is stored in the last left operand.
        retVal_ = lOp;

        return null;
    }

    /**
     * intConst
     *     : DecIntConst
     *     | OctIntConst
     *     | HexIntConst
     *     ;
     */
    @Override
    public Void visitIntConst(SysYParser.IntConstContext ctx) {
        int ret;

        /*
            Use BigInteger but not Integer::parseInt because of INT_MIN.

            Especially notice that INT_MAX+1 (2147483648, 0x80000000) will be normally parsed
            and a BigInteger will be constructed, in which however BigInteger::intValue() will return
            an abnormal value INT_MIN (-2147483648).
            Since INT_MAX+1 is not a legal value as an immediate in SysY (and won't
            exist in test cases), it will definitely be converted as -(INT_MAX+1) with a negation
            operator, which is exactly the bit value of INT_MIN.
            And INT_MIN = -INT_MIN (they have the same bit pattern), the value of the immediate
            is correct after the negation in constant folding.
         */

        // DecIntConst: Integer in decimal format, parse directly.
        if (ctx.DecIntConst() != null) {
            ret = new BigInteger(ctx.DecIntConst().getText(), 10).intValue();
        }
        // OctIntConst: Integer in octal format, parse directly in radix of 8.
        else if (ctx.OctIntConst() != null) {
            ret = new BigInteger(ctx.OctIntConst().getText(), 8).intValue();
        }
        // HexIntConst: Integer in hexadecimal format, drop the first two characters '0x'
        else {
            ret = new BigInteger(ctx.HexIntConst().getText().substring(2), 16).intValue();
        }

        setConveyedType(DataType.INT);
        retInt_ = ret;

        return null;
    }

    /**
     * floatConst
     *     : DecFloatConst
     *     | HexFloatConst
     */
    @Override
    public Void visitFloatConst(SysYParser.FloatConstContext ctx) {
        float ret = Float.parseFloat(ctx.getChild(0).getText());

        setConveyedType(DataType.FLT);
        retFloat_ = ret;

        return null;
    }

    /**
     * unaryExp : unaryOp unaryExp # oprUnaryExp
     */
    @Override
    public Void visitOprUnaryExp(SysYParser.OprUnaryExpContext ctx) {
        /*
        Global expression: Compute value of the expr w/o instruction generation.
         */
        if (this.inConstFolding()) {
            // Retrieve the value of unaryExp() by visiting child.
            visit(ctx.unaryExp());
            switch (getConveyedType()) {
                // Integer constant folding.
                case INT -> {
                    switch (ctx.unaryOp().getText()) {
                        case "-" -> retInt_ = -retInt_;
                        case "!" -> retInt_ = (retInt_ == 0) ? 0 : 1;
                        case "+" -> {}
                    }
                }

                // Float constant folding.
                case FLT -> {
                    switch (ctx.unaryOp().getText()) {
                        case "-" -> retFloat_ = -retFloat_;
                        case "!" -> retFloat_ = (retFloat_ == .0f) ? 0 : 1;
                        case "+" -> {}
                    }
                }

                default -> {}
            }
        }
        /*
        Local expression: Instructions will be generated.
         */
        else {
            // Retrieve the expression by visiting child.
            visit(ctx.unaryExp());
            // Integer.
            if (retVal_.getType().isIntegerType()) {
                // Conduct zero extension on i1.
                if (retVal_.getType().isI1()) {
                    retVal_ = builder.buildZExt(retVal_);
                }
                // Unary operators.
                switch (ctx.unaryOp().getText()) {
                    case "-" -> retVal_ = builder.buildSub(builder.buildConstant(0), retVal_);
                    case "!" -> retVal_ = builder.buildComparison("==", builder.buildConstant(0), retVal_);
                    case "+" -> {}
                }
            }
            // Float.
            else {
                switch (ctx.unaryOp().getText()) {
                    case "-" -> retVal_ = builder.buildUnary(InstCategory.FNEG, retVal_);
                    case "!" -> retVal_ = builder.buildComparison("==", builder.buildConstant(.0f), retVal_);
                    case "+" -> {}
                }
            }
        }
        return null;
    }

    /**
     * addExp : mulExp (('+' | '-') mulExp)*
     */
    @Override
    public Void visitAddExp(SysYParser.AddExpContext ctx) {
        /*
        Global expression: Compute value of the expr w/o instruction generation.
         */
        if (this.inConstFolding()) {
            int rOpInt = 0;
            float rOpFloat = 0;

            // Retrieve the value of the 1st unaryExp.
            visit(ctx.mulExp(0));
            // A variable capturing the datatype of the left operand.
            var curType = this.getConveyedType();

            switch (curType) {
                // When the 1st operand is an integer,
                // there might be implicit type promotion during the computation.
                case INT -> {
                    rOpInt = retInt_;
                    for (int i = 1; i < ctx.mulExp().size(); i++) {
                        visit(ctx.mulExp(i));

                        // Auto type promotion.
                        if (this.getConveyedType() == DataType.FLT) {
                            if (curType == DataType.INT) {
                                rOpFloat = rOpInt;
                                curType = DataType.FLT;
                            }
                            // Arithmetics.
                            switch (ctx.getChild(i * 2 - 1).getText()) {
                                case "+" -> rOpFloat += retFloat_;
                                case "-" -> rOpFloat -= retFloat_;
                                default -> throw new RuntimeException("Unsupported operation in visitMulExp().");
                            }
                        }
                        // Otherwise (conveyedType == INT)
                        else {
                            if (curType == DataType.INT) {
                                switch (ctx.getChild(i * 2 - 1).getText()) {
                                    case "+" -> rOpInt += retInt_;
                                    case "-" -> rOpInt -= retInt_;
                                }
                            }
                            else {
                                switch (ctx.getChild(i * 2 - 1).getText()) {
                                    case "+" -> rOpFloat += retInt_;
                                    case "-" -> rOpFloat -= retInt_;
                                    default -> throw new RuntimeException("Unsupported operation in visitMulExp().");
                                }
                            }
                        }
                    }
                }

                // When the 1st operand is a float, no auto type promotion.
                case FLT -> {
                    rOpFloat = retFloat_;
                    for (int i = 1; i < ctx.mulExp().size(); i++) {
                        visit(ctx.mulExp(i));

                        if (this.getConveyedType() == DataType.INT) {
                            switch (ctx.getChild(i * 2 - 1).getText()) {
                                case "+" -> rOpFloat += retInt_;
                                case "-" -> rOpFloat -= retInt_;
                            }
                        } else {
                            switch (ctx.getChild(i * 2 - 1).getText()) {
                                case "+" -> rOpFloat += retFloat_;
                                case "-" -> rOpFloat -= retFloat_;
                            }
                        }
                    }
                }

                // Error.
                default ->
                        throw new RuntimeException("Unsupported Datatype in visitMulExp().");
            }

            // Set the conveyedType and store the return value.
            this.setConveyedType(curType);
            switch (curType) {
                case INT -> retInt_ = rOpInt;
                case FLT -> retFloat_ = rOpFloat;
            }
        }


        /*
        Local expression: Instructions will be generated.
         */
        else {
            // Retrieve the 1st mulExp (as the left operand) by visiting child.
            visit(ctx.mulExp(0));
            Value lOp = retVal_;

            // The 2nd and possibly more MulExp.
            for (int i = 1; i < ctx.mulExp().size(); i++) {
                // Retrieve the next mulExp (as the right operand) by visiting child.
                visit(ctx.mulExp(i));
                Value rOp = retVal_;

                // Check if the lOp/rOp is a pointer. if it is, load it up.
                if (lOp.getType().isPointerType()) {
                    lOp = builder.buildLoad(((PointerType) lOp.getType()).getPointeeType(), lOp);
                }
                if (rOp.getType().isPointerType()) {
                    rOp = builder.buildLoad(((PointerType) rOp.getType()).getPointeeType(), rOp);
                }


                // Auto type promotion. (i1 -> i32, i32 -> float)
                if (lOp.getType().isI1()) {
                    lOp = builder.buildZExt(lOp);
                }
                if (rOp.getType().isI1()) {
                    rOp = builder.buildZExt(rOp);
                }
                if (lOp.getType().isIntegerType() && rOp.getType().isFloatType()) {
                    lOp = builder.buildSitofp(lOp);
                }
                else if (lOp.getType().isFloatType() && rOp.getType().isIntegerType()) {
                    rOp = builder.buildSitofp(rOp);
                }

                // Generate an instruction to compute result of left and right operands
                // as the new left operand for the next round.
                switch (ctx.getChild(2 * i - 1).getText()) {
                    case "+" -> lOp = builder.buildAdd(lOp, rOp);
                    case "-" -> lOp = builder.buildSub(lOp, rOp);
                    default -> {}
                }
            }

            retVal_ = lOp;
        }

        return null;
    }


    /**
     * mulExp : unaryExp (('*' | '/' | '%') unaryExp)*
     */
    @Override
    public Void visitMulExp(SysYParser.MulExpContext ctx) {
        /*
        Global expression: Compute value of the expr w/o instruction generation.
         */
        if (this.inConstFolding()) {
            int rOpInt = 0;
            float rOpFloat = 0;

            // Retrieve the value of the 1st unaryExp.
            visit(ctx.unaryExp(0));
            // A variable capturing the datatype of the left operand.
            var curType = this.getConveyedType();

            switch (curType) {
                // When the 1st operand is an integer,
                // there might be implicit type promotion during the computation.
                case INT -> {
                    rOpInt = retInt_;
                    for (int i = 1; i < ctx.unaryExp().size(); i++) {
                        visit(ctx.unaryExp(i));

                        // Auto type promotion.
                        if (this.getConveyedType() == DataType.FLT) {
                            if (curType == DataType.INT) {
                                rOpFloat = rOpInt;
                                curType = DataType.FLT;
                            }
                            // Arithmetics.
                            switch (ctx.getChild(i * 2 - 1).getText()) {
                                case "*" -> rOpFloat *= retFloat_;
                                case "/" -> rOpFloat /= retFloat_;
                                default -> throw new RuntimeException("Unsupported operation in visitMulExp().");
                            }
                        }
                        // Otherwise (conveyedType == INT)
                        else {
                            if (curType == DataType.INT) {
                                switch (ctx.getChild(i * 2 - 1).getText()) {
                                    case "*" -> rOpInt *= retInt_;
                                    case "/" -> rOpInt /= retInt_;
                                    case "%" -> rOpInt %= retInt_;
                                }
                            }
                            else {
                                switch (ctx.getChild(i * 2 - 1).getText()) {
                                    case "*" -> rOpFloat *= retInt_;
                                    case "/" -> rOpFloat /= retInt_;
                                    default -> throw new RuntimeException("Unsupported operation in visitMulExp().");
                                }
                            }
                        }
                    }
                }

                // When the 1st operand is a float, no auto type promotion.
                case FLT -> {
                    rOpFloat = retFloat_;
                    for (int i = 1; i < ctx.unaryExp().size(); i++) {
                        visit(ctx.unaryExp(i));

                        if (this.getConveyedType() == DataType.INT) {
                            switch (ctx.getChild(i * 2 - 1).getText()) {
                                case "*" -> rOpFloat *= retInt_;
                                case "/" -> rOpFloat /= retInt_;
                            }
                        } else {
                            switch (ctx.getChild(i * 2 - 1).getText()) {
                                case "*" -> rOpFloat *= retFloat_;
                                case "/" -> rOpFloat /= retFloat_;
                            }
                        }
                    }
                }

                // Error.
                default ->
                    throw new RuntimeException("Unsupported Datatype in visitMulExp().");
            }

            // Set the conveyedType and store the return value.
            this.setConveyedType(curType);
            switch (curType) {
                case INT -> retInt_ = rOpInt;
                case FLT -> retFloat_ = rOpFloat;
            }
        }

        /*
        Local expression: Instructions will be generated.
         */
        else {
            Value lOp;

            // Retrieve the 1st unaryExp (as the left operand) by visiting child.
            visit(ctx.unaryExp(0));
            lOp = retVal_;

            // The 2nd and possibly more MulExp.
            for (int i = 1; i < ctx.unaryExp().size(); i++) {
                // Retrieve the next unaryExp (as the right operand) by visiting child.
                visit(ctx.unaryExp(i));
                Value rOp = retVal_;
                // Check if the lOp/rOp is a pointer. if it is, load it up.
                if (lOp.getType().isPointerType()) {
                    lOp = builder.buildLoad(((PointerType) lOp.getType()).getPointeeType(), lOp);
                }
                if (rOp.getType().isPointerType()) {
                    rOp = builder.buildLoad(((PointerType) rOp.getType()).getPointeeType(), rOp);
                }

                // Auto type promotion.
                if (lOp.getType().isI1()) {
                    lOp = builder.buildZExt(lOp);
                }
                if (rOp.getType().isI1()) {
                    rOp = builder.buildZExt(rOp);
                }
                if (lOp.getType().isI32() && rOp.getType().isFloatType()) {
                    lOp = builder.buildSitofp(lOp);
                }
                else if (lOp.getType().isFloatType() && rOp.getType().isI32()) {
                    rOp = builder.buildSitofp(rOp);
                }

                // Generate an instruction to compute result of left and right operands
                // as the new left operand for the next round.
                switch (ctx.getChild(2 * i - 1).getText()) {
                    case "/" -> lOp = builder.buildDiv(lOp, rOp);
                    case "*" -> lOp = builder.buildMul(lOp, rOp);
                    case "%" -> { // l % r => l - (l/r)*r [FOR i32 ONLY]
                        BinaryOpInst div = builder.buildDiv(lOp, rOp); // l/r
                        BinaryOpInst mul = builder.buildMul(div, rOp); // (l/r)*r
                        lOp = builder.buildSub(lOp, mul);
                    }
                }
            }
            retVal_ = lOp;
        }

        return null;
    }

    /**
     * lVal : Identifier ('[' expr ']')*
     * ------------------------------------------
     * stmt : lVal '=' expr ';'     # assignStmt
     * primaryExp : lVal            # primExpr2
     * ------------------------------------------
     * Notice that besides being a left value for
     * assignment lVal can be a primary expression.
     */
    @Override
    public Void visitScalarLVal(SysYParser.ScalarLValContext ctx) {
        /*
        Retrieve the value defined previously from the symbol table.
         */
        String name  = ctx.Identifier().getText();
        Value val = scope.getValByName(name);

        /*
        If the value does not exist, report the semantic error.
         */
        if (val == null) {
            throw new RuntimeException("Undefined value: " + name);
        }

        /*
        There are two cases for lVal as a grammar symbol:
        1.  If a lVal can be reduced to a primaryExp,
            in this case it is a scalar value (IntegerType or FloatType)
            thus the value can be returned directly, which will then
            be handled by visitPrimExpr2().
        2.  Otherwise, a lVal represents a left value,
            which generates an address (PointerType Value)
            designating a memory block for assignment.
         */
        // Case 1, return directly.
        if (val.getType().isIntegerType() || val.getType().isFloatType()) {
            retVal_ = val;
            return null;
        }
        // Case 2, return a PointerType Value.
        if (val.getType().isPointerType()) {
            Type pointeeType = ((PointerType) val.getType()).getPointeeType();
            // i32**: Return i32*.
            if (pointeeType.isPointerType()) {
                retVal_ = builder.buildLoad(pointeeType, val);
            }
            // [2 x i32]*: Return i32*
            else if (pointeeType.isArrayType()) {
                retVal_ = builder.buildGEP(val, new ArrayList<>(){{
                    add(builder.buildConstant(0));
                    add(builder.buildConstant(0));
                }});
            }
            // i32* / float*.
            else {
                // Load it up when being a real argument of a function call.
                // Otherwise, return directly for being a left value.
                if (inBuildFCall()) {
                    val = builder.buildLoad(pointeeType, val);
                }
                retVal_ = val;
            }
            return null;
        }
        return null;
    }

    /**
     * lVal : Identifier ('[' expr ']')+  # arrLVal
     */
    @Override
    public Void visitArrLVal(SysYParser.ArrLValContext ctx) {
        /*
        Retrieve the value defined previously from the symbol table.
         */
        String name  = ctx.Identifier().getText();
        Value val = scope.getValByName(name);

        /*
        Security Checks.
         */
        if (val == null) {
            throw new RuntimeException("Undefined value: " + name);
        }


        if (this.inConstFolding()) {
            // All const arrays should be promoted as global in this::visitArrConstInitVal.
            if (!(val instanceof GlobalVariable)
                    || !((GlobalVariable) val).isConstant()
                    || !((GlobalVariable) val).isArray()) {
                throw new RuntimeException("Try to fold a non-constant value.");
            }

            ConstArray arr = ((ConstArray) ((GlobalVariable) val).getInitVal());
            ArrayList<Integer> indices = new ArrayList<>();
            for (var exprContext : ctx.expr()) {
                visit(exprContext);
                indices.add(retInt_);
            }
            retVal_ = arr.getElemByIndex(indices);
        }
        else {
            /*
            Retrieve the array element.
             */
            Type valType = ((PointerType) val.getType()).getPointeeType();
            // An array.
            if (valType.isArrayType()) {
                for (SysYParser.ExprContext exprContext : ctx.expr()) {
                    visit(exprContext);
                    val = builder.buildGEP(val, new ArrayList<>() {{
                        add(builder.buildConstant(0));
                        add(retVal_);
                    }});
                }
            }
            // A pointer (An array passed into as an argument in a function / A glb var)
            else {
                MemoryInst.Load load = builder.buildLoad(
                        ((PointerType) val.getType()).getPointeeType(),
                        val
                );
                visit(ctx.expr(0));
                val = builder.buildGEP(load, new ArrayList<>() {{
                    add(retVal_);
                }});

                for (int i = 1; i < ctx.expr().size(); i++) {
                    visit(ctx.expr(i));
                    val = builder.buildGEP(val, new ArrayList<>() {{
                        add(builder.buildConstant(0));
                        add(retVal_);
                    }});
                }
            }

            retVal_ = val;
        }

        return null;
    }

    /**
     * primaryExp
     *     : '(' expr ')'  # primExpr1
     *     | lVal          # primExpr2
     *     | number        # primExpr3
     * ---------------------------------------------------
     * unaryExp
     *     : primaryExp                         # primUnaryExp
     *     | Identifier '(' (funcRParams)? ')'  # fcallUnaryExp
     *     | unaryOp unaryExp                   # oprUnaryExp
     * ---------------------------------------------------
     * Primary expression represents an independent value
     * that can involve in future operations / computation
     * in the source program.
     * <br>
     * Since primaryExp is a grammar symbol representing
     * pure value, when an lVal (whose IR construct may
     * be in PointerType) is reduced to primExp, it should
     * be checked, and possibly a Load instruction is need
     * to read the memory block onto register to be a pure
     * value for instant use.
     */
    @Override
    public Void visitPrimExpr2(SysYParser.PrimExpr2Context ctx) {
        /*
        Global expression: Compute value of the expr w/o instruction generation.
         */
        if (this.inConstFolding()) {
            visit(ctx.lVal());
            if (retVal_.getType().isIntegerType()) {
                retInt_ = ((ConstInt) retVal_).getVal();
                setConveyedType(DataType.INT);
            }
            else if (retVal_.getType().isFloatType()) {
                retFloat_ = ((ConstFloat) retVal_).getVal();
                setConveyedType(DataType.FLT);
            }
            else {
                throw new RuntimeException("Unsupported folding type: " + retVal_.getType());
            }
        }
        /*
        Local expression: Instructions will be generated.
         */
        else {
            visit(ctx.lVal());
            // If it's not in a function call,
            // load the memory block pointed by the PointerType Value retrieved from lVal.
            if (!inBuildFCall() && retVal_.getType().isPointerType()) {
                Type pointeeType = ((PointerType) retVal_.getType()).getPointeeType();
                retVal_ = builder.buildLoad(pointeeType, retVal_);
            }

        }
        return null;
    }

    /**
     * number
     *     : intConst
     *     | floatConst
     * -------------------------------------
     * primaryExp : number # primExpr3
     */
    @Override
    public Void visitNumber(SysYParser.NumberContext ctx) {
        super.visitNumber(ctx);
        if (!this.inConstFolding()) {
            switch (getConveyedType()) {
                case INT -> retVal_ = builder.buildConstant(retInt_);
                case FLT -> retVal_ = builder.buildConstant(retFloat_);
            }
        }
        return null;
    }

    /**
     * stmt : lVal '=' expr ';' # assignStmt
     */
    @Override
    public Void visitAssignStmt(SysYParser.AssignStmtContext ctx) {
        // Retrieve left value (the address to store) by visiting child.
        // Retrieve the value to be stored by visiting child.
        visit(ctx.lVal());
        Value addr = retVal_;
        visit(ctx.expr());
        Value val = retVal_;

        // Type matching check and implicit type conversions.
        Type destType = ((PointerType) addr.getType()).getPointeeType();
        if (destType.isFloatType() && val.getType().isIntegerType()) {
            val = builder.buildSitofp(val);
        }
        else if (destType.isIntegerType() && val.getType().isFloatType()) {
            val = builder.buildFptosi(val, (IntegerType) destType);
        }

        // Build the Store instruction.
        builder.buildStore(val, addr);
        return null;
    }

    /**
     * unaryExp : Identifier '(' (funcRParams)? ')'  # fcallUnaryExp
     * -------------------------------------------------------------
     * funcRParams : funcRParam (',' funcRParam)*
     */
    @Override
    public Void visitFcallUnaryExp(SysYParser.FcallUnaryExpContext ctx) {
        setBuildFCall(ON);

        // The identifier needs to be previously defined as a function
        // and in the symbol table.
        String funcName = ctx.Identifier().getText();
        Value val = scope.getValByName(funcName);
        if (val == null) {
            throw new RuntimeException("Undefined name: " + funcName + ".");
        }
        if (!val.getType().isFunctionType()) {
            throw new RuntimeException(funcName + " is not a function and cannot be invoked.");
        }
        Function func = (Function) val;

        // If the function has argument(s) passed, retrieve them by visiting child(ren).
        ArrayList<Value> args = new ArrayList<>();
        if (ctx.funcRParams() != null) {
            var argCtxs = ctx.funcRParams().funcRParam();
            ArrayList<Type> argTypes = func.getType().getArgTypes();
            // Loop through both the lists of context and type simultaneously.
            for (int i = 0; i < argCtxs.size(); i++) {
                var argCtx = argCtxs.get(i);
                Type typeRequired = argTypes.get(i);
                // Visit child RParam.
                visit(argCtx);
                Value realArg = retVal_;

                /*
                Argument type matching check.
                 */

                // If typeRequired asks for an immediate scalar while argCtx (lVal)
                // returns a pointer, load it up.
                if (!typeRequired.isPointerType() && realArg.getType().isPointerType()) {
                    realArg = builder.buildLoad(typeRequired, realArg);
                }

                // If typeRequired asks for a pointer
                // while argCtx returns a pointer to an array having dimension that doesn't match
                // (e.g. i32* is required, but [2 x i32]* is given)
                // (e.g. [2 x i32]* is required, but [3 x [2 x i32]* is given)
                // GEP to keep de-referencing until a pointer in correct type retrieved.
                if (typeRequired.isPointerType() && realArg.getType().isPointerType()) {
                    while (realArg.getType() != typeRequired) {
                        realArg = builder.buildGEP(realArg, new ArrayList<>() {{
                            add(builder.buildConstant(0));
                            add(builder.buildConstant(0));
                        }});
                        // Prevent infinite loop caused by bad code input.
                        // (Stop the loop when realArg is already a non-nested pointer)
                        if (!((PointerType) realArg.getType()).getPointeeType().isPointerType()) {
                            break;
                        }
                    }
                }

                // sitofp, fptosi and ZExt
                if (realArg.getType().isI1()) {
                    // Technically, i1 Values are not allowed to be as parameters in function calls
                    // according to SysY semantic constraints (cuz opr "!" occurs only in conditional
                    // statement). But we still do the check for safety.
                    realArg = builder.buildZExt(realArg);
                }
                if (typeRequired.isI32() && realArg.getType().isFloatType()) {
                    realArg = builder.buildFptosi(realArg, (IntegerType) typeRequired);
                }
                else if (typeRequired.isFloatType() && realArg.getType().isI32()) {
                    realArg = builder.buildSitofp(realArg);
                }


                // Add the argument Value retrieved by visiting to the container.
                args.add(realArg);
            }
        }

        // Special cases for start/stoptime() macro:
        // pass a arbitrary number (i32 0) to timer functions.
        else if (funcName.equals("starttime") || funcName.equals("stoptime")) {
            args.add(ConstInt.getI32(0));
        }

        // Build a Call instruction.
        retVal_ = builder.buildCall(func, args);

        setBuildFCall(OFF);
        return null;
    }

    /**
     * funcRParam
     *     : expr    # exprRParam
     *     | STRING  # strRParam
     */
    @Override
    public Void visitStrRParam(SysYParser.StrRParamContext ctx) {
        /*
        We don't have to cope with string factually.
         */
        retVal_ = null;

        return null;
    }

    /**
     * stmt : 'while' '(' cond ')' stmt # whileStmt
     */
    @Override
    public Void visitWhileStmt(SysYParser.WhileStmtContext ctx) {
        // Deepen by one layer of nested loop.
        bpStk.push(new ArrayList<>());

        /*
        - Store current block to add on it a Br to entryBlk.
        - Start a new block as the entry of loop continuing check for
        jumping back at the end of the loop body, which is also the
        entry block of the while statement.
         */
        BasicBlock preBlk = builder.getCurBB();

        // NOTICE: A new block as condEntryBlk will be created no matter
        // whether the preBlk is empty or not.
        BasicBlock condEntryBlk = builder.buildBB("_WHILE_ENTRY");
        // Add a Br from the old preBlk to the new entryBlk.
        builder.setCurBB(preBlk);
        builder.buildBr(condEntryBlk);

        /*
        Build an EXIT block no matter if it may become dead code
        that cannot be reached in the CFG.
         */
        BasicBlock bodyEntryBlk = builder.buildBB("_WHILE_BODY");
        BasicBlock exitBlk = builder.buildBB("_WHILE_EXIT");

        /*
        Cope with the condition expression by visiting child cond.
         */
        // Pass down blocks as inherited attributes for short-circuit evaluation.
        ctx.cond().lOrExp().trueBlk = bodyEntryBlk;
        ctx.cond().lOrExp().falseBlk = exitBlk;

        builder.setCurBB(condEntryBlk);
        visit(ctx.cond());

        /*
        Build the loop BODY.
         */
        builder.setCurBB(bodyEntryBlk);
        visit(ctx.stmt());
        BasicBlock bodyExitBlk = builder.getCurBB();
        // If the loop body doesn't end with Ret,
        // add a Br jumping back to the conditional statement.
        if (bodyExitBlk.isEmpty()
                || !bodyExitBlk.getLastInst().getTag().isTerminator()) {
            builder.setCurBB(bodyExitBlk);
            builder.buildBr(condEntryBlk);
        }

        /*
        Force the BB pointer to point to the exitBlk just as the conditional
        statement regardless of dead code prevention.
         */
        builder.setCurBB(exitBlk);

        // Pop the back-patching layer out.
        for (TerminatorInst.Br br : bpStk.pop()) {
            if (br.getOperandAt(0) == BREAK) {
                br.setOperandAt(0, exitBlk);
            }
            else if (br.getOperandAt(0) == CONTINUE) {
                br.setOperandAt(0, condEntryBlk);
            }
            else {
                throw new RuntimeException("Invalid block placeholder occurs in the stack.");
            }
        }

        return null;
    }

    /**
     * stmt : 'break' ';' # breakStmt
     */
    @Override
    public Void visitBreakStmt(SysYParser.BreakStmtContext ctx) {
        bpStk.peek().add(builder.buildBr(BREAK));
        // Add a dead block for possible remaining dead code.
        builder.buildBB("_FOLLOWING_BLK");
        return null;
    }

    /**
     * stmt : 'continue' ';' # contStmt
     */
    @Override
    public Void visitContStmt(SysYParser.ContStmtContext ctx) {
        bpStk.peek().add(builder.buildBr(CONTINUE));
        // Add a dead block for possible remaining dead code.
        builder.buildBB("_FOLLOWING_BLK");
        return null;
    }
}
