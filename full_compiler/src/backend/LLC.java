package backend;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class LLC {
    private List<String> ll_prog = new ArrayList<>();
    private int MemSize = 0;
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

                    List<String> blockContent = new ArrayList<>();
                    for (int j = start; j <= end; j++) {
                        blockContent.add(func.ll_func.get(j));
                    }
                    block.ll_block = blockContent;
                    func.Blocks.add(block);
                }
            }
        }
    }
    private void BuildBlocks(){
        for(SimpleFunc func : GlobalFunction){
           if(func.ll_func != null){
               for(String line: func.ll_func){
                     if(!line.contains("alloca")){
                            if(line.contains("store")){

                            }else if(line.contains("load")){

                            }else if(line.contains("add")){

                            }else if(line.contains("sub")){

                            }else if(line.contains("br")){

                            }else if(line.contains("icmp")){

                            }else if(line.contains("call")){

                            }else if(line.contains("ret")){

                            }
                            // 如果以数字开头
                            else if(line.matches("\\d+.*")){

                            }

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


//        PrintLL();
    }
}
