	.cpu cortex-a7
	.arch armv7ve
	.fpu vfpv4

	.text

	.global main
main:
.BLOCK_0:
	PUSH {lr}
	SUB sp, sp, #4
	BL getint
	MOV r0, #0
	ADD sp, sp, #4
	POP {pc}


	.end
