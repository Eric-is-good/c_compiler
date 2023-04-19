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
	%1 = alloca i32
	%2 = alloca i32
	store i32 1, i32* %2
	store i32 2, i32* %1
	%3 = call i32 @getint()
	store i32 %3, i32* %2
	%4 = load i32, i32* %2
	%5 = load i32, i32* %1
	%6 = call i32 @add(i32 %4, i32 %5)
	ret i32 0
}

