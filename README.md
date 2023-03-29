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

