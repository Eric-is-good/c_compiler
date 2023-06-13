declare void @exit(i32)
declare i32 @getint()
declare void @putint(i32)
declare i32 @getch()
declare void @putch(i32)


@c = dso_local global i32 666
@d = dso_local global i32 777


define dso_local i32 @foo(i32 %0, i32 %1, i32 %2, i32 %3, i32 %4, i32 %5) {
6:										;foo_ENTRY
	%7 = alloca i32
	%8 = alloca i32
	%9 = alloca i32
	%10 = alloca i32
	%11 = alloca i32
	%12 = alloca i32
	store i32 %0, i32* %12
	store i32 %1, i32* %11
	store i32 %2, i32* %10
	store i32 %3, i32* %9
	store i32 %4, i32* %8
	store i32 %5, i32* %7
	%13 = load i32, i32* %12
	ret i32 %13
14:										;_FOLLOWING_BLK
	ret i32 0
}

define dso_local i32 @test_ret() {
0:										;test_ret_ENTRY
	ret i32 99
1:										;_FOLLOWING_BLK
	ret i32 0
}

define dso_local void @test_ret_2() {
0:										;test_ret_2_ENTRY
	%1 = alloca i32
	store i32 123, i32* %1
	ret void
}

define dso_local i32 @main() {
0:										;main_ENTRY
	%1 = alloca i32
	%2 = alloca i32
	%3 = alloca i32
	%4 = alloca i32
	%5 = alloca i32
	%6 = alloca i32
	%7 = call i32 @getint()
	store i32 %7, i32* %6
	%8 = call i32 @getint()
	store i32 %8, i32* %5
	store i32 99, i32* %4
	store i32 7, i32* %3
	%9 = load i32, i32* %6
	%10 = load i32, i32* @c
	%11 = load i32, i32* %3
	%12 = load i32, i32* %2
	%13 = load i32, i32* %1
	%14 = call i32 @foo(i32 %9, i32 89, i32 %10, i32 %11, i32 %12, i32 %13)
	store i32 %14, i32* %6
	%15 = call i32 @test_ret()
	store i32 %15, i32* %5
	call void @test_ret_2()
	%16 = load i32, i32* @c
	call void @putint(i32 %16)
	call void @exit(i32 0)
	ret i32 0
}

