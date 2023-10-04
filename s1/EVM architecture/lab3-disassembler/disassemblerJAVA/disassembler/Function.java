package disassembler;

public class Function {
    private final int opcode;
    private final int funct3;
    private final int funct7;
    public Function(int opcode, int funct3, int funct7) {
        this.opcode = opcode;
        this.funct3 = funct3;
        this.funct7 = funct7;
    }

    private String getFunctionName() {
        return switch (opcode) {
            case 19 -> switch (funct3) {
                case 0 -> "addi";
                case 2 -> "slti";
                case 3 -> "sltiu";
                case 4 -> "xori";
                case 6 -> "ori";
                case 7 -> "andi";
                case 1 -> "slli";
                case 5 -> switch (funct7) {
                    case 0 -> "srli";
                    case 32 -> "srai";
                    default -> "unknown_instruction";
                };
                default -> "unknown_instruction";
            };
            case 51 -> switch (funct3) {
                case 0 -> switch (funct7) {
                    case 0 -> "add";
                    case 1 -> "mul";
                    case 32 -> "sub";
                    default -> "unknown_instruction";
                };
                case 1 -> switch (funct7) {
                    case 0 -> "sll";
                    case 1 -> "mulh";
                    default -> "unknown_instruction";
                };
                case 2 -> switch (funct7) {
                    case 0 -> "slt";
                    case 1 -> "mulhsu";
                    default -> "unknown_instruction";
                };
                case 3 -> switch (funct7) {
                    case 0 -> "sltu";
                    case 1 -> "mulhu";
                    default -> "unknown_instruction";
                };
                case 4 -> switch (funct7) {
                    case 0 -> "xor";
                    case 1 -> "div";
                    default -> "unknown_instruction";
                };
                case 5 -> switch (funct7) {
                    case 0 -> "srl";
                    case 1 -> "divu";
                    case 32 -> "sra";
                    default -> "unknown_instruction";
                };
                case 6 -> switch (funct7) {
                    case 0 -> "or";
                    case 1 -> "rem";
                    default -> "unknown_instruction";
                };
                case 7 -> switch (funct7) {
                    case 0 -> "and";
                    case 1 -> "remu";
                    default -> "unknown_instruction";
                };
                default -> "unknown_instruction";
            };
            case 55 -> "lui";
            case 23 -> "auipc";
            case 3 -> switch (funct3) {
                case 0 -> "lb";
                case 1 -> "lh";
                case 2 -> "lw";
                case 4 -> "lbu";
                case 5 -> "lhu";
                default -> "unknown_instruction";
            };
            case 103 -> "jalr";
            case 35 -> switch (funct3) {
                case 0 -> "sb";
                case 1 -> "sh";
                case 2 -> "sw";
                default -> "unknown_instruction";
            };
            case 111 -> "jal";
            case 99 -> switch (funct3) {
                case 0 -> "beq";
                case 1 -> "bne";
                case 4 -> "blt";
                case 5 -> "bge";
                case 6 -> "bltu";
                case 7 -> "bgeu";
                default -> "unknown_instruction";
            };
            case 15 -> "fence";
            default -> "unknown_instruction";
        };
    }

    @Override
    public String toString() {
        return getFunctionName();
    }
}
