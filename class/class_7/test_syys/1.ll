declare void @exit(i32)
declare i32 @getint()
declare void @putint(i32)
declare i32 @getch()
declare void @putch(i32)


define dso_local void @han(i32 %0, i32 %1, i32 %2, i32 %3) {
4:										;han_ENTRY
	%5 = alloca i32
	%6 = alloca i32
	%7 = alloca i32
	%8 = alloca i32
	store i32 %0, i32* %8
	store i32 %1, i32* %7
	store i32 %2, i32* %6
	store i32 %3, i32* %5
	br label %9
9:										;_COND_ENTRY
	%10 = load i32, i32* %8
	%11 = icmp eq i32 %10, 1
	br i1 %11, label %13, label %16
12:										;_COND_EXIT
	ret void
13:										;_THEN
	%14 = load i32, i32* %7
	call void @putch(i32 %14)
	call void @putch(i32 45)
	call void @putch(i32 62)
	%15 = load i32, i32* %6
	call void @putch(i32 %15)
	call void @putch(i32 10)
	br label %12
16:										;_ELSE
	%17 = load i32, i32* %8
	%18 = sub i32 %17, 1
	%19 = load i32, i32* %7
	%20 = load i32, i32* %5
	%21 = load i32, i32* %6
	call void @han(i32 %18, i32 %19, i32 %20, i32 %21)
	%22 = load i32, i32* %7
	call void @putch(i32 %22)
	call void @putch(i32 45)
	call void @putch(i32 62)
	%23 = load i32, i32* %6
	call void @putch(i32 %23)
	call void @putch(i32 10)
	%24 = load i32, i32* %8
	%25 = sub i32 %24, 1
	%26 = load i32, i32* %5
	%27 = load i32, i32* %6
	%28 = load i32, i32* %7
	call void @han(i32 %25, i32 %26, i32 %27, i32 %28)
	br label %12
}

define dso_local i32 @main() {
0:										;main_ENTRY
	%1 = alloca i32
	%2 = call i32 @getint()
	store i32 %2, i32* %1
	%3 = load i32, i32* %1
	call void @han(i32 %3, i32 65, i32 66, i32 67)
	ret i32 0
4:										;_FOLLOWING_BLK
	call void @exit(i32 0)
	ret i32 0
}

