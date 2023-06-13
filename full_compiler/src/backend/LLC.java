package backend;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class LLC {
    private List<String> ll_prog = new ArrayList<>();
    private int MemSize = 0;
    private int MaxParamNum = 0;
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

    private List<SimpleVar> GlobalVariable = new ArrayList<>();
    private void MemManager(){
        for (String line : ll_prog) {
            // 如果匹配到 %+数字, 则说明是一个变量，和 MemSize 做比较，如果大于 MemSize 则更新 MemSize
            if (line.matches(".*%\\d+.*")) {
                String[] tokens = line.split(" ");
                for (String token : tokens) {
                    if (token.matches("%\\d+")) {
                        int num = Integer.parseInt(token.substring(1));
                        if (num > MemSize) {
                            MemSize = num;
                        }
                    }
                }
            }
        }

        // 如果开头匹配到@+字符串, MemSize + 1, 并且将该字符串加入 GlobalVariable
        for (String line : ll_prog) {
            if (line.matches("@.*")) {
                SimpleVar var = new SimpleVar();
                String[] tokens = line.split(" ");
                for (String token : tokens) {
                    if (token.matches("@.*")) {
                        token = token.substring(1);
                        var.VarName = token;
                        MemSize++;
                    }
                }
                // 如果最后一个token不是zeroinitializer, 把最后一个token转成int
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
        // 如果开头匹配到declare, 则说明是一个函数，将该函数加入 GlobalFunction
        for (String line : ll_prog) {
            if (line.matches("declare.*")) {
                SimpleFunc func = new SimpleFunc();

                String[] tokens = line.split(" ");
                if (tokens[1].equals("i32")) {
                    func.isVoid = false;
                } else {
                    func.isVoid = true;
                }

                // line 中 从@到)之间的字符串就是函数名
                String funcStr = line.substring(line.indexOf("@") + 1, line.indexOf(")"));
                func.FucName = funcStr.substring(0, funcStr.indexOf("("));
                int paramNum = 0;
                for (int i = 0; i < funcStr.length(); i++) {
                    if (funcStr.charAt(i) == ',') {
                        paramNum++;
                    }
                }
                func.paramNum = paramNum + 1;

                GlobalFunction.add(func);
            }
        }

        // 如果开头是define
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
                int paramNum = 0;
                for (int i = 0; i < funcStr.length(); i++) {
                    if (funcStr.charAt(i) == ',') {
                        paramNum++;
                    }
                }
                func.paramNum = paramNum + 1;

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

        // 将在define_func函数的内容加入到对应的 SimpleFunc 中
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

        // 将最大函数参数个数设为 MaxParamNum
        for (SimpleFunc func : GlobalFunction) {
            if (func.paramNum > MaxParamNum) {
                MaxParamNum = func.paramNum;
            }
        }

    }

    private void BlockManager(){
        for(SimpleFunc func : GlobalFunction){
            if(func.ll_func != null){
                List<Integer> blockStart = new ArrayList<>();
                for (int i = 0; i < func.ll_func.size(); i++) {
                    String line = func.ll_func.get(i);
                    //如果以数字和冒号开头，说明是一个block的开始
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

                    //再取出该行用 ; 分割的最后那个字符串
                    String[] tokens = func.ll_func.get(start).split(";");
                    if (!tokens[tokens.length - 1].matches("_.*")) {
                        blockName.append("_");
                    }
                    blockName.append(tokens[tokens.length - 1]);

                    block.BlockName = blockName.toString();

                    //取出该行前面匹配的数字作为block的id
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

    private Operand GetOpAsmName(String op){
        // 如果op最后是,要先去掉
        if(op.charAt(op.length() - 1) == ','){
            op = op.substring(0, op.length() - 1);
        }
        //去掉前后的空格
        op = op.trim();

        // 如果是数字，说明是一个立即数
        if(op.matches("\\d+")){
            Operand operand = new Operand();
            operand.CONSTANT = Integer.parseInt(op);
            return operand;
        }
        // 如果以%开头，说明是一个变量，把op去掉%后，剩下的数字就是变量的id
        else if(op.matches("%.*")){
                Operand operand = new Operand();
                operand.OP = (Integer.parseInt(op.substring(1)) + MaxParamNum) * 4;
                return operand;
        }
        // 如果以@开头，说明是一个全局变量，把op去掉@后，剩下的字符串就是变量名
        else if(op.matches("@.*")){
            String varName = op.substring(1);
            Operand operand = new Operand();
            for(int i = 0; i < GlobalVariable.size(); i++){
                if(GlobalVariable.get(i).VarName.equals(varName)){
                    operand.OP = (MemSize - GlobalVariable.size() + i + MaxParamNum) * 4;
                    return operand;
                }
            }
            System.out.println("Error: Global Variable " + varName + " not found!");
        }

        System.out.println("Error: Operand " + op + " not found!");
        return null;
    }

    private String GetBlockName(int blockID){
        for(SimpleFunc func : GlobalFunction){
            if(func.ll_func != null){
                for (SimpleBlock block : func.Blocks){
                    if(block.BlockID == blockID){
                        return block.BlockName;
                    }
                }
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

                       if(!line.contains("alloca")){
                           // 如果以数字开头,包括数字0开头
                           if(line.charAt(0) >= '0' && line.charAt(0) <= '9'){

                           }
                           else if(line.contains("store")){
                               // store i32 %12, i32* @c
                               String[] tokens = line.split(" ");
                               Operand op1 = GetOpAsmName(tokens[2]);
                               Operand op2 = GetOpAsmName(tokens[4]);
                               block.asm_block.add("movl   " + op1 + ", %eax");
                               block.asm_block.add("movl   %eax, " + op2);

                           }else if(line.contains("load")){
                               // %7 = load i32, i32* %3
                               String[] tokens = line.split(" ");
                               Operand op1 = GetOpAsmName(tokens[5]);
                               Operand op2 = GetOpAsmName(tokens[0]);
                               block.asm_block.add("movl   " + op1 + ", %eax");
                               block.asm_block.add("movl   %eax, " + op2);

                           }else if(line.contains("add")){
                               // %12 = add i32 %10, %11
                               String[] tokens = line.split(" ");
                               Operand op1 = GetOpAsmName(tokens[tokens.length - 2]);
                               Operand op2 = GetOpAsmName(tokens[tokens.length - 1]);
                               Operand op3 = GetOpAsmName(tokens[0]);
                               block.asm_block.add("movl   " + op1 + ", %eax");
                               block.asm_block.add("addl   " + op2 + ", %eax");
                               block.asm_block.add("movl   %eax, " + op3);

                           }else if(line.contains("sub")){
                               //%15 = sub i32 %13, %14
                               String[] tokens = line.split(" ");
                               Operand op1 = GetOpAsmName(tokens[tokens.length - 2]);
                               Operand op2 = GetOpAsmName(tokens[tokens.length - 1]);
                               Operand op3 = GetOpAsmName(tokens[0]);
                               block.asm_block.add("movl   " + op1 + ", %eax");
                               block.asm_block.add("subl   " + op2 + ", %eax");
                               block.asm_block.add("movl   %eax, " + op3);

                           }else if(line.contains("br")){
                               //br label %6
                               String[] tokens = line.split(" ");
                                 if(tokens.length == 3) {
                                     Operand op1 = GetOpAsmName(tokens[2]);
                                     block.asm_block.add("jmp    " + GetBlockName(op1.OP/4)); // TODO: 这里要改
                                 }else if (tokens.length == 7){
                                     // 结合 icmp
                                     // %36 = icmp eq i32 %34, %35
                                     //	br i1 %36, label %38, label %37
                                     String[] icmp_tokens = icmp.split(" ");
                                     Operand icmp_op1 = GetOpAsmName(icmp_tokens[4]);
                                     Operand icmp_op2 = GetOpAsmName(icmp_tokens[5]);
                                     String OP = icmp_tokens[2];
                                     Operand br_op1 = GetOpAsmName(tokens[4]);   // todo: 这里要改
                                     Operand br_op2 = GetOpAsmName(tokens[6]);
                                     block.asm_block.add("movl   " + icmp_op1 + ", %eax");
                                     block.asm_block.add("cmpl   " + icmp_op2 + ", %eax");
                                     if(OP.equals("eq")){
                                         block.asm_block.add("je     " + GetBlockName(br_op1.OP/4));
                                     }else if(OP.equals("ne")){
                                         block.asm_block.add("jne    " + GetBlockName(br_op1.OP/4));
                                     }else if(OP.equals("slt")) {
                                         block.asm_block.add("jl     " + GetBlockName(br_op1.OP/4));
                                     }else if(OP.equals("sgt")) {
                                         block.asm_block.add("jg     " + GetBlockName(br_op1.OP/4));
                                     }else if(OP.equals("sle")) {
                                         block.asm_block.add("jle    " + GetBlockName(br_op1.OP/4));
                                     }else if(OP.equals("sge")) {
                                         block.asm_block.add("jge    " + GetBlockName(br_op1.OP/4));
                                     }else {
                                         System.out.println("Error: icmp " + icmp);
                                     }

                                     block.asm_block.add("jmp    " + GetBlockName(br_op2.OP/4));

                                 }else {
                                     System.out.println("Error: br " + line);
                                 }

                           }else if(line.contains("icmp")){
                               icmp = line;

                           }else if(line.contains("call")){
                               // call void @foo(i32 %9, i32 %10, i32 %11, i32 %12, i32 %13, i32 %14)
                               String FuncName = line.substring(line.indexOf("@") + 1, line.indexOf("("));
                               int params_heap = 0;
                               String[] params = line.substring(line.indexOf("(") + 1, line.indexOf(")")).split(" ");
                               for (int i = 1; i < params.length; i=i+2) {
                                   Operand param = GetOpAsmName(params[i]);
                                   block.asm_block.add("movl   " + param + ", %eax");
                                   block.asm_block.add("movl   %eax, " + params_heap + "(%esp)");
                                   params_heap += 4;
                               }

                               block.asm_block.add("call   " + FuncName);

                               String[] tokens = line.split(" ");
                               // 如果tokens[1] == void
                               if(tokens[1].equals("void")){
                                      // do nothing
                               }

                               // 如果tokens[3] == i32
                               else if(tokens[3].equals("i32")){
                                   Operand op = GetOpAsmName(tokens[0]);
                                   block.asm_block.add("movl   %eax, " + op);
                               }

                           }else if(line.contains("ret")){
                                // ret i32 %7
                                String[] tokens = line.split(" ");

                                // ret void
                                if(tokens.length == 2){
                                    block.asm_block.add("retl");
                                    continue;
                                }

                                Operand op = GetOpAsmName(tokens[tokens.length - 1]);
                                block.asm_block.add("movl   " + op + ", %eax");
                                block.asm_block.add("retl");
                           }
                           else {

                           }

                       }
                   }
               }

           }
        }
    }

    private void BuildAsm(){
        for(SimpleFunc func : GlobalFunction){
            if(func.ll_func != null){
                for(SimpleBlock block : func.Blocks){
                    for (String line : block.asm_block){
                        
                    }
                }
            }
        }
    }

    public void emit(String source, String target) {
        Preprocess(source);
        MemManager();
        FuncManager();
        BlockManager();
        BuildBlocks();
        BuildAsm();


//        PrintLL();
    }
}
