SYS_EXIT: equ 60
SYS_READ: equ 0
SYS_WRITE: equ 1

STDIN_FILENO: equ 0
STDOUT_FILENO: equ 1
STDERR_FILENO: equ 2

EXIT_SUCCESS: equ 0
EXIT_FAILURE: equ 1

        section .text
        global _start
_start:
        xor r10, r10 
        xor r8, r8 
; if previous character is a whitespace r8 = 0, else r8 = 1.
read_start:
        mov rax, SYS_READ
        xor rdi, rdi ; STDIN_FILENO
        mov rsi, buff
        mov rdx, buff_size
        syscall
        test rax, rax
        jl read_error
        je print_ans
      
        xor rbx, rbx
char_loop:
        mov r9b, [rsi + rbx]
        cmp r9b, 0x09
        js check
        cmp r9b, 0x0d
        jle incr
check:
        cmp r9b, 0x20
        jne non_incr
incr:
        add r10, r8
        xor r8, r8
        jmp move_loop
non_incr:
        mov r8, 1
move_loop:   
        inc rbx
        cmp rbx, rax
        je read_start
        jmp char_loop

read_error:
        mov rax, SYS_WRITE
        mov rdi, STDERR_FILENO
        mov rsi, error_msg
        mov rdx, error_msg_size
        syscall
        mov rax, SYS_EXIT
        mov rdi, EXIT_FAILURE
        syscall
        
print_ans:
        add r10, r8
        add rsp, buff_size

	lea rsi, [rsp - 1]
	mov byte [rsi], 10
	mov rax, r10
	mov rbx, 10

next_digit:
	xor edx, edx
	div rbx
	add edx, '0'
	dec rsi
	mov [rsi], dl
	test rax, rax
	jnz next_digit

	mov eax, SYS_WRITE
	mov edi, STDOUT_FILENO
	mov rdx, rsp
	sub rdx, rsi
	syscall

	mov eax, SYS_EXIT
	xor edi, edi ; EXIT_SUCCESS
	syscall

        section .bss
buff:   resb 4096
buff_size:  equ $ - buff

        section .rodata
error_msg: db "Read error ocurred",0x0a
error_msg_size: equ $ - error_msg
