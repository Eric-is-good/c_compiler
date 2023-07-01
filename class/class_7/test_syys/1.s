.text
    .file    "use_the_eric_compiler, support_the_sy_2022_except_float_and_array, for_the_target_ericOS_with_i686"




    .globl han
    .p2align 4, 0x90
    .type han,@function
han:
.for_han_init: 
    pushl  %eax
    subl   $132,%esp
.han_han_ENTRY:
    movl   140(%esp), %eax
    movl   %eax, 48(%esp)
    movl   144(%esp), %eax
    movl   %eax, 44(%esp)
    movl   148(%esp), %eax
    movl   %eax, 40(%esp)
    movl   152(%esp), %eax
    movl   %eax, 36(%esp)
    jmp    .han_COND_ENTRY
.han_COND_ENTRY:
    movl   48(%esp), %eax
    movl   %eax, 56(%esp)
    movl   56(%esp), %eax
    cmpl   $1, %eax
    je     .han_THEN
    jmp    .han_ELSE
.han_COND_EXIT:
    addl   $132,%esp
    popl  %eax
    retl
.han_THEN:
    movl   44(%esp), %eax
    movl   %eax, 72(%esp)
    movl   72(%esp), %eax
    movl   %eax, 0(%esp)
    calll  putch
    movl   $45, %eax
    movl   %eax, 0(%esp)
    calll  putch
    movl   $62, %eax
    movl   %eax, 0(%esp)
    calll  putch
    movl   40(%esp), %eax
    movl   %eax, 76(%esp)
    movl   76(%esp), %eax
    movl   %eax, 0(%esp)
    calll  putch
    movl   $10, %eax
    movl   %eax, 0(%esp)
    calll  putch
    jmp    .han_COND_EXIT
.han_ELSE:
    movl   48(%esp), %eax
    movl   %eax, 84(%esp)
    movl   84(%esp), %eax
    subl   $1, %eax
    movl   %eax, 88(%esp)
    movl   44(%esp), %eax
    movl   %eax, 92(%esp)
    movl   36(%esp), %eax
    movl   %eax, 96(%esp)
    movl   40(%esp), %eax
    movl   %eax, 100(%esp)
    movl   88(%esp), %eax
    movl   %eax, 0(%esp)
    movl   92(%esp), %eax
    movl   %eax, 4(%esp)
    movl   96(%esp), %eax
    movl   %eax, 8(%esp)
    movl   100(%esp), %eax
    movl   %eax, 12(%esp)
    calll  han
    movl   44(%esp), %eax
    movl   %eax, 104(%esp)
    movl   104(%esp), %eax
    movl   %eax, 0(%esp)
    calll  putch
    movl   $45, %eax
    movl   %eax, 0(%esp)
    calll  putch
    movl   $62, %eax
    movl   %eax, 0(%esp)
    calll  putch
    movl   40(%esp), %eax
    movl   %eax, 108(%esp)
    movl   108(%esp), %eax
    movl   %eax, 0(%esp)
    calll  putch
    movl   $10, %eax
    movl   %eax, 0(%esp)
    calll  putch
    movl   48(%esp), %eax
    movl   %eax, 112(%esp)
    movl   112(%esp), %eax
    subl   $1, %eax
    movl   %eax, 116(%esp)
    movl   36(%esp), %eax
    movl   %eax, 120(%esp)
    movl   40(%esp), %eax
    movl   %eax, 124(%esp)
    movl   44(%esp), %eax
    movl   %eax, 128(%esp)
    movl   116(%esp), %eax
    movl   %eax, 0(%esp)
    movl   120(%esp), %eax
    movl   %eax, 4(%esp)
    movl   124(%esp), %eax
    movl   %eax, 8(%esp)
    movl   128(%esp), %eax
    movl   %eax, 12(%esp)
    calll  han
    jmp    .han_COND_EXIT
.for_han_exit: 
    addl   $132,%esp
    popl  %eax
    retl




    .globl main
    .p2align 4, 0x90
    .type main,@function
main:
.for_main_init: 
    subl   $32,%esp
.main_main_ENTRY:
    calll  getint
    movl   %eax, 24(%esp)
    movl   24(%esp), %eax
    movl   %eax, 20(%esp)
    movl   20(%esp), %eax
    movl   %eax, 28(%esp)
    movl   28(%esp), %eax
    movl   %eax, 0(%esp)
    movl   $65, %eax
    movl   %eax, 4(%esp)
    movl   $66, %eax
    movl   %eax, 8(%esp)
    movl   $67, %eax
    movl   %eax, 12(%esp)
    calll  han
    movl   $0, %eax
    addl   $32,%esp
    retl
.main_FOLLOWING_BLK:
    movl   $0, %eax
    movl   %eax, 0(%esp)
    calll  exit
    movl   $0, %eax
    addl   $32,%esp
    retl
.for_main_exit: 
    addl   $32,%esp
    retl




