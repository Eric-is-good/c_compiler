# c_compiler



**电子科技大学 编译原理挑战课**，为比赛做的基础训练

该**比赛**为 [全国大学生计算机系统能力大赛 ](https://compiler.educg.net)的系统编译方向，由华为主办

由于比赛可以用 java ~~(可惜不能用 python)~~，所以就选择了 java，这样可以专注于指令优化

我们主要目的是学习，~~不是为了拿奖~~，因为我们到处借鉴别人的代码......    @_@





## 内容

根据我的学习进度，分课程的记录学习进度。在 class 文件夹里面，有每一课的学习内容。

我们借鉴了 [这个代码](https://github.com/cabinz/cbias) ，并以此为模板学习。感谢大佬们的开源。

比赛官方也有开源代码学习，我们也可以看看他们的。

如果你想**直接进入比赛相关内容**，建议从 class 4 开始看。



## class

### class 1  实现词法分析器

| **单词**   | **编码** | **单词** | **编码** |
| ---------- | -------- | -------- | -------- |
| **标识符** | **01**   | **/**    | **13**   |
| **常数**   | **02**   | **<**    | **14**   |
| **int**    | **03**   | **<=**   | **15**   |
| **if**     | **04**   | **>**    | **16**   |
| **else**   | **05**   | **>=**   | **17**   |
| **while**  | **06**   | **!=**   | **18**   |
| **for**    | **07**   | **==**   | **19**   |
| **read**   | **08**   | **=**    | **20**   |
| **write**  | **09**   | **(**    | **21**   |
| **+**      | **10**   | **)**    | **22**   |
| **-**      | **11**   | **,**    | **23**   |
| *****      | **12**   | **;**    | **24**   |

![](https://github.com/Eric-is-good/c_compiler/blob/main/imgs/1.png)

但是我为了扩展性，分为了 6 类

```java
* 1表示关键字
* 2表示标识符
* 3表示常数
* 4表示运算符
* 5表示界符
* 6表示字符串
```



### class 2 使用 javacc 生成词法分析器

javacc [官网](https://javacc.github.io/javacc/)

文档写的很好，关于教程，就用官方推荐的三个

- JavaCC [tutorials](https://javacc.github.io/javacc/tutorials/).
- [Introduction to JavaCC](https://www.engr.mun.ca/~theo/JavaCC-Tutorial/javacc-tutorial.pdf) by Theodore S. Norvell.
- [Incorporating language processing into Java applications: a JavaCC tutorial](https://ieeexplore.ieee.org/document/1309649) by Viswanathan Kodaganallur.

我大概思路是遍历两次，第一次查找以下两种错误：

- 非法字符
- 非法数字（多个小数点，数字+非数字）

第二次再输出分词

之所以要两次，是因为没有前缀后缀，非法数字无法识别，我又懒得搞匹配后又退回，多令牌机制也不想碰（叹气）



### class 2 plus 使用 javacc 多文件预处理

多文件编译时，使用递归处理 include 文件。

果然，菜鸡（指我）都喜欢递归。

我们的思路是深度优先，因为程序就是一棵树（以主函数为根节点），我们先使用递归进入下一层，最后再添加本结点内容，这样可以使被调用程序代码在调用代码之前被添加，我们最后把所有文件合成一个大字符串。

```java
// 伪代码
find_include(path){
   while(true){
       word = next_word(); // 打开文件，读入本文件下一个词语
       if word == <EOF>：
           break;
       if word == #include "path_xx"：
           find_include(path_xx);    // 递归
       if word == programe:          //如果读到代码
           total_content.add(word);
   }
}
```



### class 3 使用语法分析器分析一个小程序（无代码）

c- 语法书在 class_3 里面，老师要我们用飞书画思维导图，也就是那个语法树（飞书打钱）

```c
void main(){
	return;
}
```

得到这样的语法树

[思维导图](https://uestc.feishu.cn/mindnotes/bmncnx6rCky2aSf2f9GXDlaXBHd)

![](https://github.com/Eric-is-good/c_compiler/blob/main/class/class_3/program.png)



### class 4 实现 c- 的词法与语法分析器

我们使用 [antlr 4](https://www.antlr.org/)，我感觉可能要抛弃 javacc ？

关于 [**解决左递归**](https://stackoverflow.com/questions/2999755/removing-left-recursion-in-antlr)

我们实现的 c- 语法解析器实现效果（按照 c- 语法书）

```c
int main(int args[]){
    int a;
    a = 6;
    a = a+10;
    if(a>9){
        return a;
    }else{
        int b;
        b = a * a;
    }
}
```

![](https://github.com/Eric-is-good/c_compiler/blob/main/class/class_4/antlr4_parse_tree.png)





### class 5  预备知识 LLVM

有关 LLVM 的资料我放在了 **class_5（预备知识）**里面。

![](https://img2.baidu.com/it/u=983554491,2158979956&fm=253&fmt=auto&app=138&f=JPEG?w=558&h=206)

1. [什么是 SSA](https://blog.csdn.net/qq_29674357/article/details/78731713)  (注意 Φ函数插入算法 和 重命名算法)

1. [支配边界](https://www.zhihu.com/question/293748348/answer/2764418983)

2. 我们的前端目标是生成 LLVM IR，[什么是 LLVM IR](https://github.com/Evian-Zhang/llvm-ir-tutorial) 

2. [IR 的设计](https://blog.csdn.net/fs3296/article/details/127853336)

3. 有关 LLVM 的书籍 [Learn LLVM 12](https://download.packt.com/free-ebook/9781839213502) 我也放在了文件夹里。

   [LLVM 地址](https://github.com/llvm/llvm-project)



### class 5   LLVM IR 的实现

**学习别人的代码**使我们进步，我们以 [该代码](https://github.com/cabinz/cbias) 为模板，进行学习。

#### 入侵式链表

在 class_5\src\utils 里面，实现的就是这个具有通用性的链表。

传统链表

![](https://img-blog.csdnimg.cn/8e9baf611c4949dc824cfa593a43f6f8.png)

入侵式链表

![](https://img-blog.csdnimg.cn/ae71e460f11249e2a85eaef1eb1a698c.png)

我们的实现方式

![](https://github.com/Eric-is-good/c_compiler/blob/main/imgs/instrusive.png)



#### IR 的构成

```c
├───ir
│   │   Module.java
│   │   Type.java
│   │   Use.java
│   │   User.java
│   │   Value.java
│   │
│   ├───types
│   │       ArrayType.java
│   │       FloatType.java
│   │       FunctionType.java
│   │       IntegerType.java
│   │       LabelType.java
│   │       PointerType.java
│   │       VoidType.java
│   │
│   └───values
│       │   BasicBlock.java
│       │   Constant.java
│       │   Function.java
│       │   GlobalVariable.java
│       │   Instruction.java
│       │
│       ├───constants
│       │       ConstArray.java
│       │       ConstFloat.java
│       │       ConstInt.java
│       │
│       └───instructions
│               BinaryOpInst.java
│               CallInst.java
│               CastInst.java
│               GetElemPtrInst.java
│               MemoryInst.java
│               PhiInst.java
│               TerminatorInst.java
│               UnaryOpInst.java
```

- Value类，Value是一个非常基础的基类，一个继承于Value的子类表示它的结果可以被其他地方使用。在我们的实现中 Use 作为 Value 和 User 的桥梁。

- User类，一个继承于User的类表示它会使用一个或多个Value对象。

- Type类，所有 type 继承于此，不同的 type 有其自己独特的属性。在 types 文件夹里面。

- 基于 Value 类 和 User 类，我们实现了从指令集类到代码再到函数的类，在 values 里面。通过继承迭代器，Function 迭代 BasicBlock，BasicBlock 迭代 Instruction。

- Instruction 抽象类，tag 表示指令类型。所有 instruction 继承于它，在 instructions 文件夹里面。

  

- Module类，Module可以理解为一个完整的编译单元。一般来说，这个编译单元就是一个源码文件，如一个后缀为cpp的源文件。
- Function类，这个类顾名思义就是对应于一个函数单元。Function可以描述两种情况，分别是函数定义和函数声明。
- BasicBlock类，这个类表示一个基本代码块，“基本代码块”就是一段没有控制流逻辑的基本流程，相当于程序流程图中的基本过程（矩形表示）。
- Instruction类，指令类就是LLVM中定义的基本操作，比如加减乘除这种算数指令、函数调用指令、跳转指令、返回指令等等。指令中每个变量是个 Value。



### class 6  语义分析  和  生成中间代码（代码依旧 class 5）

比赛需要实现一个语言 SysY2022 ，文档在 class_5/SysY2022 下。

从现在开始，我们使用 java 17 来匹配比赛要求。

1. paser 和 lexer 直接 antlr 生成，官方有指导书，我们就直接用了别人的 g4 文件。在 class_5/src/frontend 下。重点在于 visitor 的代码。visit 一遍后，生成  in-memory IR 。
2. llvm ir 中，在  class_5/src/ir 下实现。
3. 将 in-memory IR 输出，我们有一个 IREmitter 类，在 class_5/src/frontend 下。

这便是我们生成中间代码的主框架

```java
// 1.sy -> 1.ll
public static void main(String[] args) throws Exception{
        CharStream inputFile = CharStreams.fromFileName("test_syys/1.sy");

        /* Lexical analysis */
        SysYLexer lexer = new SysYLexer(inputFile);
        CommonTokenStream tokenStream = new CommonTokenStream(lexer);

        /* Parsing */
        SysYParser parser = new SysYParser(tokenStream);
        ParseTree ast = parser.compUnit(); 

        /* Intermediate code generation */
        // Initialized all the container and tools.
        Module module = new Module();
        Visitor visitor = new Visitor(module);
        // Traversal the ast to build the IR.
        visitor.visit(ast);

        /* Emit the IR text to an output file for testing. */
        IREmitter emitter = new IREmitter("test_syys/1.ll");
        emitter.emit(module, true);
    }
```

而本章的重点在于 frontend 里面的 IRBuilder 和 Visitor。

```c
├───frontend
│       IRBuilder.java
│       IREmitter.java
│       Scope.java
│       SysYBaseVisitor.java
│       SysYLexer.java
│       SysYParser.java
│       SysYVisitor.java
│       Visitor.java
```





### class 7  Mem 2 Reg
