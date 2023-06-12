	.cpu i686

	.text

	.global main
main:
.BLOCK_0:
	SUB sp, sp, #12
	ADD VR_0, sp, #0
	ADD VR_1, sp, #4
	ADD VR_2, sp, #8
	BL getint
	MOV VR_3, r0
	STR VR_3, [VR_2]
	BL getint
	MOV VR_4, r0
	STR VR_4, [VR_1]
	MOV VR_5, #0
	STR VR_5, [VR_0]
	B .BLOCK_1
.BLOCK_1:
	LDR VR_6, [VR_0]
	CMP VR_6, #1000
	BLT .BLOCK_2
	BGE .BLOCK_3
.BLOCK_2:
	LDR VR_7, [VR_2]
	LDR VR_8, [VR_1]
	ADD VR_9, VR_7, VR_8
	MOVW VR_10, :lower16:c
	MOVT VR_10, :upper16:c
	STR VR_9, [VR_10]
	LDR VR_11, [VR_0]
	ADD VR_12, VR_11, #1
	STR VR_12, [VR_0]
	B .BLOCK_1
.BLOCK_3:
	MOVW VR_14, :lower16:c
	MOVT VR_14, :upper16:c
	LDR VR_13, [VR_14]
	MOV r0, VR_13
	BL putint
	MOV r0, #0
	BL exit
	MOV r0, #0
	ADD sp, sp, #12
	POP {pc}


	.data
	.align 4
	.global c
c:
	.word	666


	.end
