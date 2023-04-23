declare i32 @getint()
declare void @putint(i32)
declare float @getfloat()
declare void @putfloat(float)
declare i32 @getch()
declare void @putch(i32)
declare i32 @getarray(i32*)
declare void @putarray(i32, i32*)
declare i32 @getfarray(float*)
declare void @putfarray(i32, float*)
declare void @_sysy_starttime(i32)
declare void @_sysy_stoptime(i32)
declare void @memset(i32*, i32, i32)


@aa = dso_local global [6 x [3 x i32]] [[3 x i32] zeroinitializer, [3 x i32] [i32 1, i32 1, i32 1], [3 x i32] zeroinitializer, [3 x i32] zeroinitializer, [3 x i32] zeroinitializer, [3 x i32] zeroinitializer]


define dso_local i32 @add(i32 %0, i32 %1) {
2:										;add_ENTRY
	%3 = alloca i32
	%4 = alloca i32
	store i32 %0, i32* %4
	store i32 %1, i32* %3
	%5 = load i32, i32* %4
	%6 = load i32, i32* %3
	%7 = add i32 %5, %6
	ret i32 %7
8:										;_FOLLOWING_BLK
	ret i32 0
}

define dso_local i32 @main() {
0:										;main_ENTRY
	%1 = alloca float
	%2 = alloca i32
	%3 = alloca i32
	store i32 1, i32* %3
	store i32 2, i32* %2
	%4 = call i32 @getint()
	store i32 %4, i32* %3
	%5 = load i32, i32* %3
	%6 = load i32, i32* %2
	%7 = call i32 @add(i32 %5, i32 %6)
	store float 0x3ff0000000000000, float* %1
	%8 = load float, float* %1
	%9 = getelementptr [6 x [3 x i32]], [6 x [3 x i32]]* @aa, i32 0, i32 5
	%10 = getelementptr [3 x i32], [3 x i32]* %9, i32 0, i32 2
	%11 = load i32, i32* %10
	%12 = sitofp i32 %11 to float
	%13 = fadd float %8, %12
	store float %13, float* %1
	ret i32 0
}

