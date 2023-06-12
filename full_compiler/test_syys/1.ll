declare void @exit(i32)
declare i32 @getint()
declare void @putint(i32)
declare i32 @getch()
declare void @putch(i32)


@c = dso_local global i32 666
@d = dso_local global i32 777


define dso_local void @foo(i32 %0, i32 %1) {
2:										;foo_ENTRY
	%3 = alloca i32
	%4 = alloca i32
	%5 = alloca i32
	store i32 %0, i32* %5
	store i32 %1, i32* %4
	store i32 999, i32* %3
	br label %6
6:										;_WHILE_ENTRY
	%7 = load i32, i32* %3
	%8 = icmp slt i32 %7, 1000
	br i1 %8, label %9, label %15
9:										;_WHILE_BODY
	%10 = load i32, i32* %5
	%11 = load i32, i32* %4
	%12 = add i32 %10, %11
	store i32 %12, i32* @c
	%13 = load i32, i32* %3
	%14 = add i32 %13, 1
	store i32 %14, i32* %3
	br label %6
15:										;_WHILE_EXIT
	ret void
}

define dso_local i32 @main() {
0:										;main_ENTRY
	%1 = alloca i32
	%2 = alloca i32
	%3 = call i32 @getint()
	store i32 %3, i32* %2
	%4 = call i32 @getint()
	store i32 %4, i32* %1
	%5 = load i32, i32* %2
	%6 = load i32, i32* %1
	call void @foo(i32 %5, i32 %6)
	%7 = load i32, i32* @c
	call void @putint(i32 %7)
	call void @exit(i32 0)
	ret i32 0
}

