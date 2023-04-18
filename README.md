# c_compiler



**电子科技大学 编译原理挑战课**，为比赛做的基础训练

该**比赛**为 [全国大学生计算机系统能力大赛 ](https://compiler.educg.net)的系统编译方向，由华为主办

由于比赛可以用 java ~~（可惜不能用 python）~~，所以就选择了 java，这样可以专注于指令优化

老师上课用的 c++，我用 java 重写一遍



## 内容

分为两个部分，上课代码和完整的编译器代码，老师提供了 c++ 代码，我重写的 java



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



### class 2 plus 使用 javacc 预处理

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





### class 5  预备知识 LLVM IR

我们的前端目标是生成 LLVM IR，[这里](https://github.com/Evian-Zhang/llvm-ir-tutorial) 有一个简单教程。

[LLVM 地址](https://github.com/llvm/llvm-project)

有关 LLVM IR 的资料我放在了 **class_5（预备知识）**里面。

![](https://img2.baidu.com/it/u=983554491,2158979956&fm=253&fmt=auto&app=138&f=JPEG?w=558&h=206)





### class 5 语义分析

比赛需要实现一个语言 SysY2022 ，文档在 class_5/SysY2022 下。

从现在开始，我们使用 java 17 来匹配比赛要求。

我们先实现他的  lexer 和 parser 部分，生成对应的 antlr visitor。

接下来是重中之重，**建立符号表** 和 **语法制导翻译**。

1. 构建语法树

   注意到 SysY 中的 LVal ，我们为了区分，引入了 rVal。

   它可以出现在很多地方, 用作不同的用途. 更具体地说, 它可以有三个用法:

   1. 用作定义/声明: 如出现在函数形参/数组定义中

   2. 用作左值: 如出现在赋值语句左边

   3. 用作右值: 如出现在表达式中

      

   

2. 重构语法树

   我们需要在树的节点里面添加一些属性，例如该节点的代码，属性等等，因此，我们自定义了树节点 Node。同时，我们通过 antlr visit 这棵树，拿到了他的拓扑排序，方便我们**自底向上**的遍历这颗树生成中间代码。

   ```java
   // full_tree = hash_tree + leaves
   public HashMap<Integer,ParseTree> full_tree = new HashMap<>();   // 完整的树
   public ArrayList<Integer> tree_TSort = new ArrayList<>();   // full_tree 树的拓扑排序
   
   public HashMap<Integer,Node> hash_tree = new HashMap<>();   // hash 树 （不含叶子节点）
   public ArrayList<Integer> leaves = new ArrayList<>();         // 叶子节点
   public Queue<ParseTree> tree_nodes_q = new LinkedList<>();
   ```

   在 visit 构建树的同时，我们构建了迭代的空符号表

   ```java
   public Integer visitBlock(SysYParser.BlockContext ctx) {
           。。。。
           symbol_tables.put(table.hashCode(), table);   // 添加符号表
           。。。。
       }
   ```

   

2. 从子节点生成代码和符号表
