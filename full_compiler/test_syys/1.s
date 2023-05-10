	.cpu cortex-a7
	.arch armv7ve
	.fpu vfpv4

	.text

	.global add
add:
.BLOCK_0:
	SUB sp, sp, #8
	MOV VR_3, r1
	MOV VR_2, r0
	ADD VR_0, sp, #0
	ADD VR_1, sp, #4
	STR VR_2, [VR_1]
	STR VR_3, [VR_0]
	LDR VR_4, [VR_1]
	LDR VR_5, [VR_0]
	ADD VR_6, VR_4, VR_5
	MOV r0, VR_6
	ADD sp, sp, #8
	BX lr
.BLOCK_1:
	MOV r0, #0
	ADD sp, sp, #8
	BX lr


	.global main
main:
.BLOCK_2:
	SUB sp, sp, #12
	ADD VR_0, sp, #0
	ADD VR_1, sp, #4
	ADD VR_2, sp, #8
	MOV VR_3, #1
	STR VR_3, [VR_2]
	MOV VR_4, #2
	STR VR_4, [VR_1]
	BL getint
	MOV VR_5, r0
	STR VR_5, [VR_2]
	LDR VR_6, [VR_2]
	LDR VR_7, [VR_1]
	MOV r1, VR_7
	MOV r0, VR_6
	BL add
	MOV VR_8, r0
	MOV VR_9, #1065353216
	STR VR_9, [VR_0]
	VLDR EVR_0, [VR_0]
	MOVW VR_10, :lower16:aa
	MOVT VR_10, :upper16:aa
	ADD VR_11, VR_10, #60
	ADD VR_12, VR_11, #8
	LDR VR_13, [VR_12]
	VMOV EVR_1, VR_13
	VCVT.F32.S32 EVR_2, EVR_1
	VADD.F32 EVR_3, EVR_0, EVR_2
	VSTR EVR_3, [VR_0]
	MOV r0, #0
	ADD sp, sp, #12
	POP {pc}


	.data
	.align 4
	.global aa
aa:
	.zero	12
	.word	1
	.word	1
	.word	1
	.zero	48


	.end
