package backend;

import backend.armCode.MCBasicBlock;
import backend.armCode.MCFunction;
import backend.armCode.MCInstruction;
import backend.operand.Label;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

/**
 * This class is a Singleton Pattern class, emitting the memory
 * structure of ARM assemble to a '.s' file
 * following the standard of GNU tool chain. <br/>
 * Usage: MCEmitter.get().emitTo(target, outputPath)
 */
public class MCEmitter {

    //<editor-fold desc="Singleton Pattern">
    private static final MCEmitter emitter = new MCEmitter();

    private MCEmitter(){}

    public static MCEmitter get() {return emitter;}
    //</editor-fold>


    /**
     * Emit a prepared ARM assemble file from memory to a '.s' file <br/><br/>
     *
     * The assembler file consists of
     * <ul>
     *     <li>File description section: include '.cpu', '.fpu', '.arch'</li>
     *     <li>
     *         Text section: include all assembler code
     *         <ul>
     *             <li>Function starts with '.global' directive to define</li>
     *         </ul>
     *     </li>
     *     <li>
     *         Data section: include all global variable
     *         <ul>
     *             <li>Uninitialized variable uses '.comm' directive telling the linker to allocate memory in BSS section, which can be move to '.bss', but I'm lazy</li>
     *             <li>Initialized variable uses '.word', '.zero' directive to initialized a memory in DATA section</li>
     *         </ul>
     *     </li>
     * </ul>
     * @param target target assemble object
     * @param outputPath '.s' file path
     * @see <a href='http://microelectronics.esa.int/erc32/doc/as.pdf'>GNU Assembler Manual</a>
     */
    public void emitTo(ARMAssemble target, String outputPath) throws IOException {
        /* header */
        StringBuilder strBd = new StringBuilder();
        strBd.append("\t.cpu "  + target.cpu + '\n');
        strBd.append("\t.arch " + target.architecture + '\n');
        strBd.append("\t.fpu "  + target.fpu + '\n');
        strBd.append("\n");
        strBd.append("\t.text\n");
        strBd.append("\n");

        /* handle each function */
        for (MCFunction f : target) {
            if (f.isExternal()) continue;
            strBd.append("\t.global " + f.emit() + '\n');
            strBd.append(f.emit() + ":\n");
            if (PrintInfo.printRegInfo) {
                strBd.append("\t\t\t\t\t\t\t\t@spilled: ");
                f.getSpilledNode().forEach(x -> {
                    if(x!=null) strBd.append(x.emit() + ", ");
                });
                strBd.append('\n');
                strBd.append("\t\t\t\t\t\t\t\t@spilled number: " + f.getSpilledNode().size() + '\n');
            }
            /* handle each BasicBlock */
            for (MCBasicBlock bb : f) {
                strBd.append(bb.emit() + ":\n");
                if (PrintInfo.printCFG) {
                    strBd.append("\t\t\t\t\t\t\t\t@predecessors: ");
                    bb.getPredecessors().forEach(pre -> strBd.append(pre.emit() + ", "));
                    strBd.append("\n");
                    if (bb.getTrueSuccessor() != null)
                    strBd.append("\t\t\t\t\t\t\t\t@trueSuccessor: " + bb.getTrueSuccessor().emit() + "\n");
                    if(bb.getFalseSuccessor() != null)
                    strBd.append("\t\t\t\t\t\t\t\t@falseSuccessor: " + bb.getFalseSuccessor().emit() + "\n");
                }
                /* handle each instruction */
                for (MCInstruction mcInstruction : bb) {
                    strBd.append('\t' + mcInstruction.emit() + '\n');
                    if (PrintInfo.printIR && mcInstruction.val != null)
                        strBd.append("\t\t\t\t\t\t\t\t\t@ " + mcInstruction.val + '\n');
                }
            }
            strBd.append("\n\n");
        }

        /* handle each global variable */
        List<Label> globalVars = target.getGlobalVars();
        if (!globalVars.isEmpty()) {
            strBd.append("\t.data\n");
            strBd.append("\t.align 4\n");
            for (Label label : globalVars) {
                if (label.getInitial() == null) {
                    strBd.append("\t.comm\t" + label.emit() + ", " + label.getSize() * 4 + ", 4\n");
                }
                else {
                    strBd.append("\t.global " + label.emit() + '\n');
                    strBd.append(label.emit() + ":\n");

                    int count = 0;
                    if (label.isInt()) {
                        for (Object ini : label.getInitial()) {
                            var tmp = ((Integer) ini);
                            if (tmp == 0)
                                count++;
                            else {
                                if (count != 0) {
                                    strBd.append("\t.zero\t" + count * 4 + '\n');
                                    count = 0;
                                }
                                strBd.append("\t.word\t" + tmp + '\n');
                            }
                        }
                    } else {
                        for (Object ini : label.getInitial()) {
                            var tmp = ((Float) ini);
                            if (tmp == 0.0F)
                                count++;
                            else {
                                if (count != 0) {
                                    strBd.append("\t.zero\t" + count * 4 + '\n');
                                    count = 0;
                                }
                                strBd.append("\t.word\t" + Float.floatToRawIntBits(tmp) + '\n');
                            }
                        }
                    }
                    if (count != 0)
                        strBd.append("\t.zero\t" + count * 4 + '\n');
                }
            }
            strBd.append("\n\n");
        }

        strBd.append("\t.end\n");

        /* write to the file */
        FileWriter fw = new FileWriter(outputPath);
        fw.append(strBd);
        fw.close();
    }
}
