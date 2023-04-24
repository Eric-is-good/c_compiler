package frontend;

import ir.Module;
import ir.values.Function;
import ir.values.BasicBlock;
import ir.values.GlobalVariable;
import ir.values.Instruction;

import java.io.FileWriter;
import java.io.IOException;

/**
 * An IREmitter object is to output the in-memory IR data structures to a file in plain-text form.
 * @see <a href="https://llvm.org/docs/LangRef.html">
 *     LLVM Language Reference</a>
 */
public class IREmitter {
    private final String targetFilePath;
    /**
     * Value of the name to be retrieved by getNewName().
     * It's an incremental counter.
     */
    private int nextName = 0;

    private final String bbInfoIndent = "\t".repeat(10);


    /**
     * The IR text will cover the original content if the file already exists.
     * @param targetFilePath Path of the file where output lands.
     */
    public IREmitter(String targetFilePath) {
        this.targetFilePath = targetFilePath;
    }

    private String getNewName() {
        return String.valueOf(nextName++);
    }

    /**
     * Naming a module, ie. to assign a identifier for each element in the module.
     * <br>
     * Unnamed temporaries are numbered sequentially (using a per-function incrementing counter,
     * starting with 0). Note that basic blocks and unnamed function parameters are included in
     * this numbering.
     * @see <a href="https://llvm.org/docs/LangRef.html#identifiers">
     *     LLVM LangRef: Identifiers</a>
     * @param m The module object to be named through.
     */
    private void nameModule(Module m) {
        /*
        Give names to global variables.
         */
        for (GlobalVariable gv : m.getGlobalVariables()) {
            if (gv.isAnonymous()) {
                gv.setName("@" + this.getNewName());
            }
        }

        /*
        Gives names to local variables and blocks.
         */
        for (Function func : m.functions) {
            nextName = 0; // Reset the counter for a new environment.
            // Only functions defined in compile unit need to be named through,
            // while an external function having only declaration other than
            // function body need no naming.
            if (!func.isExternal()) {
                // Assign names for each function argument.
                for (var arg : func.getArgs()) {
                    arg.setName("%" + getNewName());
                }
                // Assign names (Lx) for each basic block in the function,
                // and each instruction yielding results (%x) in basic blocks.
                for (BasicBlock bb : func) {
                    bb.setName(getNewName());
                    for (Instruction inst : bb) {
                        if (inst.hasResult()) {
                            inst.setName("%" + getNewName());
                        }
                    }
                }
            }
        }
    }

    /**
     * Emit a module (compile unit) as LLVM-IR in plain-text form.
     * @param m The module to be emitted.
     * @throws IOException Exception when reading/writing the output .ll file.
     */
    public void emit(Module m, boolean emitInfo) throws IOException {

        // Initialize the module by assigning names.
        nameModule(m);
        // Initialize a string builder.
        StringBuilder strBuilder = new StringBuilder();

        /*
         Build the whole file as a string in the string builder object.
         */
        // Emit external functions with only declaration in compile unit.
        for (Function func : m.externFunctions) {
            strBuilder.append("declare ").append(func).append("\n");
        }
        if (m.externFunctions.size() > 0) {
            strBuilder.append("\n\n");
        }

        // Emit global variable declarations.
        for (GlobalVariable glbVar : m.getGlobalVariables()) {
            strBuilder.append(glbVar).append("\n");
        }
        if (m.getGlobalVariables().size() > 0) {
            strBuilder.append("\n\n");
        }

        // Emit all the functions defined in the module.
        for (Function func : m.functions) {
            // Head of a function: prototype of it.
            strBuilder.append("define dso_local ")
                    .append(func.toString())
                    .append(" {\n");
            // Body of a function: basic blocks in it.
            for (BasicBlock bb : func) {
                // Label of the block.
                strBuilder.append(bb.getName()).append(":")
                        .append(emitInfo ? (bbInfoIndent + ";" + bb.getInfo()) : "")
                        .append("\n");
                // Content (instructions) in the block. [Intended \t x 1]
                for (Instruction inst : bb) {
                    strBuilder.append("\t")
                            .append(inst.toString()).append("\n");
                }
            }
            // Tail of a function: A right bracket to close it.
            strBuilder.append("}\n\n");
        }

        /*
        Write the text in the string builder object into the target text file.
        The existed content in the file will be covered.
         */
        FileWriter fileWriter = new FileWriter(targetFilePath);
        fileWriter.append(strBuilder);
        fileWriter.close();
    }

}
