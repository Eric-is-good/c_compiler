package backend;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * LLC is just the same name with llvm-llc, it converts llvm ir
 * to ams code with target x86(I32)
 */
public class LLC {
    private List<String> ll_prog = new ArrayList<>();
    public StringBuilder strBuilder = new StringBuilder();

    private void Preprocess(String source){
        BufferedReader reader;
        try {
            reader = new BufferedReader(new FileReader(source));
            String line = reader.readLine();
            while (line != null) {
                ll_prog.add(line);
                line = reader.readLine();
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }


        for (int i = 0; i < ll_prog.size(); i++) {
            String line = ll_prog.get(i);
            line = line.trim();
            if (line.length() == 0) {
                ll_prog.remove(i);
                i--;
            }
        }
    }

    public void PrintLL(){
        for (String line : ll_prog) {
            System.out.println(line);
        }
    }

    private int MaxParamNum = 0;

    private List<SimpleVar> GlobalVariable = new ArrayList<>();
    private int GetMemSize(SimpleFunc func){

        int MemSize = 0;

        for (String line : func.ll_func) {
            // If it matches a %+ number, it is a variable,
            // compare it to MemSize and update MemSize if it is greater than MemSize
            Pattern pattern = Pattern.compile("%\\d+");
            Matcher matcher = pattern.matcher(line);
            while (matcher.find()) {
                String varStr = matcher.group();
                int varNum = Integer.parseInt(varStr.substring(1));
                if (varNum > MemSize) {
                    MemSize = varNum;
                }
            }
        }

        return MemSize + 1;
    }

    private void GlobalVarManager(){
        // If the beginning of the string matches @+, MemSize + 1, and add the string to GlobalVariable
        for (String line : ll_prog) {
            if (line.matches("@.*")) {
                SimpleVar var = new SimpleVar();
                String[] tokens = line.split(" ");
                for (String token : tokens) {
                    if (token.matches("@.*")) {
                        token = token.substring(1);
                        var.VarName = token;
                    }
                }
                // If the last token is not a zero initializer, convert the last token to int
                if (!tokens[tokens.length - 1].equals("zeroinitializer")) {
                    var.VarValue = Integer.parseInt(tokens[tokens.length - 1]);
                }else {
                    var.VarValue = 0;
                }

                GlobalVariable.add(var);
            }
        }
    }



    private List<SimpleFunc> GlobalFunction = new ArrayList<>();
    private void FuncManager(){
        // If the beginning matches declare, then it is an extern function
        for (String line : ll_prog) {
            if (line.matches("declare.*")) {
                SimpleFunc func = new SimpleFunc();

                String[] tokens = line.split(" ");
                if (tokens[1].equals("i32")) {
                    func.isVoid = false;
                } else {
                    func.isVoid = true;
                }

                // The string between @ and ) in the line is the function name
                String funcStr = line.substring(line.indexOf("@") + 1, line.indexOf(")"));
                func.FucName = funcStr.substring(0, funcStr.indexOf("("));

                int count = 0;
                if(funcStr.contains("i32")){
                    while (funcStr.contains("i32") && funcStr.length() > "i32".length()) {
                        funcStr = funcStr.substring(funcStr.indexOf("i32") + 1);
                        count++;
                    }
                }
                func.paramNum = count;

                GlobalFunction.add(func);
            }
        }

        // If it starts with define, it means it is a normal function
        List<String> define_func = new ArrayList<>();
        for (String line : ll_prog) {
            if (line.matches("define.*")) {
                SimpleFunc func = new SimpleFunc();

                String[] tokens = line.split(" ");
                if (tokens[2].equals("i32")) {
                    func.isVoid = false;
                } else {
                    func.isVoid = true;
                }

                String funcStr = line.substring(line.indexOf("@") + 1, line.indexOf(")"));
                func.FucName = funcStr.substring(0, funcStr.indexOf("("));

                int count = 0;
                if(funcStr.contains("i32")){
                    while (funcStr.contains("i32") && funcStr.length() > "i32".length()) {
                        funcStr = funcStr.substring(funcStr.indexOf("i32") + 1);
                        count++;
                    }
                }
                func.paramNum = count;

                GlobalFunction.add(func);
                define_func.add(func.FucName);
            }

        }

        List<Integer> funcStart = new ArrayList<>();
        List<Integer> funcEnd = new ArrayList<>();
        for (int i = 0; i < ll_prog.size(); i++) {
            String line = ll_prog.get(i);
            if (line.matches("define.*")) {
                funcStart.add(i);
            }
            if (line.matches("}.*")) {
                funcEnd.add(i);
            }
        }

        // Add the contents of the define_func function to the corresponding SimpleFunc
        for (int i = 0; i < define_func.size(); i++) {
            String funcName = define_func.get(i);
            int start = funcStart.get(i);
            int end = funcEnd.get(i);

            for (SimpleFunc f : GlobalFunction) {
                if (f.FucName.equals(funcName)) {
                    List<String> funcContent = new ArrayList<>();
                    for (int j = start; j <= end; j++) {
                        funcContent.add(ll_prog.get(j));
                    }
                    f.ll_func= funcContent;
                }

            }

        }

        // Set the maximum number of function parameters to MaxParamNum
        for (SimpleFunc func : GlobalFunction) {
            if (func.paramNum > MaxParamNum) {
                MaxParamNum = func.paramNum;
            }
        }

        // Set the MemSize of each function
        for (SimpleFunc func : GlobalFunction) {
            if(func.ll_func != null){
                func.MemSize = GetMemSize(func);
            }
        }

    }

    private void BlockManager(){
        for(SimpleFunc func : GlobalFunction){
            if(func.ll_func != null){
                List<Integer> blockStart = new ArrayList<>();
                for (int i = 0; i < func.ll_func.size(); i++) {
                    String line = func.ll_func.get(i);
                    // If it starts with a number and a colon, it means it is the beginning of a block
                    if (line.matches("\\d+:.*")) {
                        blockStart.add(i);
                    }
                }

                for (int i = 0; i < blockStart.size(); i++) {
                    SimpleBlock block = new SimpleBlock();

                    int start = blockStart.get(i);
                    int end;
                    if (i == blockStart.size() - 1) {
                        end = func.ll_func.size() - 1;
                    } else {
                        end = blockStart.get(i + 1) - 1;
                    }

                    StringBuilder blockName = new StringBuilder();
                    blockName.append(func.FucName);

                    // Then take the last string of the line and split it with ;
                    String[] tokens = func.ll_func.get(start).split(";");
                    if (!tokens[tokens.length - 1].matches("_.*")) {
                        blockName.append("_");
                    }
                    blockName.append(tokens[tokens.length - 1]);

                    block.BlockName = blockName.toString();

                    // Fetch the matching number in front of the line as the block id
                    String[] tokens2 = func.ll_func.get(start).split(":");
                    block.BlockID = Integer.parseInt(tokens2[0]);

                    for (int j = start; j <= end; j++) {
                        block.ll_block.add(func.ll_func.get(j));
                    }
                    func.Blocks.add(block);
                }
            }
        }
    }

    private Operand GetOp(SimpleFunc func, String op){
        Operand operand = new Operand();

        // If the op ends in a comma, remove it first
        if(op.charAt(op.length() - 1) == ','){
            op = op.substring(0, op.length() - 1);
        }

        op = op.trim();

        // If it is a number, it means it is an immediate number
        if(op.matches("\\d+")){
            operand.CONSTANT = Integer.parseInt(op);
        }
        // If it starts with %, it means it is a variable. After removing the % from the op,
        // the remaining number is the id of the variable
        else if(op.matches("%.*")){
                operand.OP = (Integer.parseInt(op.substring(1)) + MaxParamNum) * 4;
        }
        // If it starts with @, it means it is a global variable. After removing the @ from the op,
        // the remaining string is the variable name
        else if(op.matches("@.*")){
            String varName = op.substring(1);
            for(int i = 0; i < GlobalVariable.size(); i++){
                if(GlobalVariable.get(i).VarName.equals(varName)){
                    operand.GLOBAL_VAR = varName;
                }
            }
        }else {
            System.out.println("Error: Operand " + op + " type is not correct!");
            return null;
        }

        if(operand.OP == null)
            return operand;
        else if((operand.OP/4 - MaxParamNum) >= func.paramNum){
            return operand;
        }

        if(func.isVoid){
            operand.OP = operand.OP + 4 + 4 + func.MemSize * 4;
        }else {
            operand.OP = operand.OP + 4 + func.MemSize * 4;
        }
        return operand;

    }

    private String GetBlockName(SimpleFunc func ,int blockID){
        for (SimpleBlock block : func.Blocks){
            if(block.BlockID == blockID){
                return "." + block.BlockName;
            }
        }
        System.out.println("Error: Block " + blockID + " not found!");
        return null;
    }


    private void BuildBlocks(){
        for(SimpleFunc func : GlobalFunction){
           if(func.ll_func != null){
               for (SimpleBlock block : func.Blocks){
                   String icmp = null;

                   for(String line: block.ll_block){
                       line = line.trim();
//                       System.out.println(line);

                       if(!line.contains("alloca")){
                           // If it starts with a number, including the number 0
                           if(line.charAt(0) >= '0' && line.charAt(0) <= '9'){

                           }
                           else if(line.contains("call")){
                               // %8 = call i32 @add3(i32 %5, i32 %6, i32 %7)
                               // call void @add1(i32 %8)

                               String funcName = line.substring(line.indexOf("@") + 1, line.indexOf("("));
                               String params = line.substring(line.indexOf("(") + 1, line.indexOf(")"));
                               String[] tokens = params.split(" ");
                               int pos = 0;
                               for(int i = 0; i < tokens.length; i++){
                                   // If token is not i32 and is not empty, it is a variable
                                   if(!tokens[i].equals("i32") && !tokens[i].equals("")){
                                       Operand op = GetOp(func, tokens[i]);
                                       block.asm_block.add("movl   " + op + ", %eax");
                                       block.asm_block.add("movl   %eax, " + pos  + "(%esp)");
                                       pos += 4;
                                   }
                               }
                               block.asm_block.add("calll  " + funcName);


                               if(line.contains("void")){

                               }else {
                                    Operand op = GetOp(func, line.substring(0, line.indexOf("=")));
                                    block.asm_block.add("movl   %eax, " + op);

                               }

                           }
                           else if(line.contains("store")){
                               // store i32 %12, i32* @c
                               String[] tokens = line.split(" ");
                               Operand op1 = GetOp(func, tokens[2]);
                               Operand op2 = GetOp(func, tokens[4]);
                               block.asm_block.add("movl   " + op1 + ", %eax");
                               block.asm_block.add("movl   %eax, " + op2);

                           }else if(line.contains("load")){
                               // %7 = load i32, i32* %3
                               String[] tokens = line.split(" ");
                               Operand op1 = GetOp(func, tokens[5]);
                               Operand op2 = GetOp(func, tokens[0]);
                               block.asm_block.add("movl   " + op1 + ", %eax");
                               block.asm_block.add("movl   %eax, " + op2);

                           }else if(line.contains("add")){
                               // %12 = add i32 %10, %11
                               String[] tokens = line.split(" ");
                               Operand op1 = GetOp(func, tokens[tokens.length - 2]);
                               Operand op2 = GetOp(func, tokens[tokens.length - 1]);
                               Operand op3 = GetOp(func, tokens[0]);

                               block.asm_block.add("movl   " + op1 + ", %eax");
                               block.asm_block.add("addl   " + op2 + ", %eax");
                               block.asm_block.add("movl   %eax, " + op3);

                           }else if(line.contains("sub")){
                               //%15 = sub i32 %13, %14
                               String[] tokens = line.split(" ");
                               Operand op1 = GetOp(func, tokens[tokens.length - 2]);
                               Operand op2 = GetOp(func, tokens[tokens.length - 1]);
                               Operand op3 = GetOp(func, tokens[0]);

                               block.asm_block.add("movl   " + op1 + ", %eax");
                               block.asm_block.add("subl   " + op2 + ", %eax");
                               block.asm_block.add("movl   %eax, " + op3);

                           }else if(line.contains("mul")){
                               // %7 = mul i32 %5, %6
                               String[] tokens = line.split(" ");
                               Operand op1 = GetOp(func, tokens[tokens.length - 2]);
                               Operand op2 = GetOp(func, tokens[tokens.length - 1]);
                               Operand op3 = GetOp(func, tokens[0]);

                               block.asm_block.add("movl   " + op1 + ", %eax");
                               block.asm_block.add("imull   " + op2 + ", %eax");
                               block.asm_block.add("movl   %eax, " + op3);

                           }else if(line.contains("sdiv")){
                               // %7 = sdiv i32 %5, %6
                               String[] tokens = line.split(" ");
                               Operand op1 = GetOp(func, tokens[tokens.length - 2]);
                               Operand op2 = GetOp(func, tokens[tokens.length - 1]);
                               Operand op3 = GetOp(func, tokens[0]);

                               block.asm_block.add("pushl  %ecx");
                               block.asm_block.add("movl   " + op1 + ", %eax");
                               block.asm_block.add("cltd");
                               block.asm_block.add("movl   " + op2 + ", %ecx");
                               block.asm_block.add("idivl  %ecx");
                               block.asm_block.add("movl   %eax, " + op3);
                               block.asm_block.add("popl  %ecx");

                           }else if(line.contains("br")){
                               //br label %6
                               String[] tokens = line.split(" ");
                                 if(tokens.length == 3) {
                                     Operand op1 = GetOp(func, tokens[2]);
                                     block.asm_block.add("jmp    " + GetBlockName(func,op1.OP/4 - MaxParamNum));
                                 }else if (tokens.length == 7){
                                     // with icmp
                                     // %36 = icmp eq i32 %34, %35
                                     //	br i1 %36, label %38, label %37
                                     String[] icmp_tokens = icmp.split(" ");
                                     Operand icmp_op1 = GetOp(func, icmp_tokens[5]);
                                     Operand icmp_op2 = GetOp(func, icmp_tokens[6]);
                                     String OP = icmp_tokens[3];
                                     Operand br_op1 = GetOp(func, tokens[4]);   // todo: 这里要改
                                     Operand br_op2 = GetOp(func, tokens[6]);
                                     block.asm_block.add("movl   " + icmp_op1 + ", %eax");
                                     block.asm_block.add("cmpl   " + icmp_op2 + ", %eax");
                                     if(OP.equals("eq")){
                                         block.asm_block.add("je     " + GetBlockName(func,br_op1.OP/4 - MaxParamNum));
                                     }else if(OP.equals("ne")){
                                         block.asm_block.add("jne    " + GetBlockName(func,br_op1.OP/4 - MaxParamNum));
                                     }else if(OP.equals("slt")) {
                                         block.asm_block.add("jl     " + GetBlockName(func,br_op1.OP/4 - MaxParamNum));
                                     }else if(OP.equals("sgt")) {
                                         block.asm_block.add("jg     " + GetBlockName(func,br_op1.OP/4 - MaxParamNum));
                                     }else if(OP.equals("sle")) {
                                         block.asm_block.add("jle    " + GetBlockName(func,br_op1.OP/4 - MaxParamNum));
                                     }else if(OP.equals("sge")) {
                                         block.asm_block.add("jge    " + GetBlockName(func,br_op1.OP/4 - MaxParamNum));
                                     }else {
                                         System.out.println("Error: icmp " + icmp);
                                     }

                                     block.asm_block.add("jmp    " + GetBlockName(func,br_op2.OP/4 - MaxParamNum));

                                 }else {
                                     System.out.println("Error: br " + line);
                                 }

                           }else if(line.contains("icmp")){
                               icmp = line;

                           }else if(line.contains("ret")){
                               // ret void
                               // ret i32 %1
                               if(!func.isVoid){
                                   String[] tokens = line.split(" ");
                                   Operand op1 = GetOp(func, tokens[2]);
                                   block.asm_block.add("movl   " + op1 + ", %eax");
                               }
                               int init_heap = (func.MemSize + MaxParamNum) * 4;
                               block.asm_block.add("addl   $" + init_heap + ",%esp");
                               if(func.isVoid){
                                   block.asm_block.add("popl  %eax");
                               }
                               block.asm_block.add("retl");

                           }
                           else if(line.contains("{") || line.contains("}")){

                           }else {
                               System.out.println("It can't match any consequence: " + line);
                           }

                       }
                   }
               }

           }
        }
    }

    private void BuildAsm(){
        strBuilder.append(".text\n");
        strBuilder.append("    .file    \"use_the_eric_compiler, support_the_sy_2022_except_float_and_array, " +
                "for_the_target_ericOS_with_i686\"\n\n\n\n\n");

        for(SimpleFunc func : GlobalFunction){
            if(func.ll_func != null){
                strBuilder.append("    .globl "+ func.FucName+ "\n");
                strBuilder.append("    .p2align 4, 0x90\n");
                strBuilder.append("    .type " + func.FucName+ ",@function\n");


                strBuilder.append(func.FucName + ":\n");

                strBuilder.append(".for_"+ func.FucName + "_init: \n");
                if(func.isVoid){
                    strBuilder.append("    pushl  %eax\n");
                }
                int init_heap = (func.MemSize + MaxParamNum) * 4;
                strBuilder.append("    subl   $" + init_heap + ",%esp\n");

                for(SimpleBlock block : func.Blocks){
                    strBuilder.append("."+block.BlockName+ ":\n");
                    for (String line : block.asm_block){
                        strBuilder.append("    " + line + "\n");
                    }
                }

                strBuilder.append(".for_"+ func.FucName + "_exit: \n");
                strBuilder.append("    addl   $" + init_heap + ",%esp\n");
                if(func.isVoid){
                    strBuilder.append("    popl  %eax\n");
                }
                strBuilder.append("    retl\n\n\n\n\n");
            }

        }


        for (SimpleVar var : GlobalVariable){
            strBuilder.append("    .type " + var.VarName + ",@object\n");
            strBuilder.append("    .globl " + var.VarName + "\n");
            strBuilder.append("    .p2align 2\n");
            strBuilder.append(var.VarName + ":\n");
            strBuilder.append("    .long " + var.VarValue + "\n");
            strBuilder.append("    .size " + var.VarName + ", 4\n\n\n\n\n");
        }


    }

    public void emit(String source, String target) {
        Preprocess(source);
        GlobalVarManager();
        FuncManager();
        BlockManager();
        BuildBlocks();
        BuildAsm();
        try {
            FileWriter writer = new FileWriter(target);
            writer.write(strBuilder.toString());
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }


//        PrintLL();
    }
}
