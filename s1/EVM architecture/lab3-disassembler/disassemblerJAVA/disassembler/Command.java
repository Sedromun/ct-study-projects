package disassembler;

public class Command {
    protected final int address;
    private int instruction;
    private Registr regSource1, regSource2;
    private Registr regDest;
    private int imm31_12, imm11_0, imm11_5, imm4_0;
    private int opcode;
    private Function function;
    private String labelName;
    protected ElfFile elfFile;
    private final boolean isLabel;
    public Command(ElfFile elfFile, int address, int instruction) {
        this.elfFile = elfFile;
        this.address = address;
        this.instruction = instruction;
        this.isLabel = false;
        String line = Integer.toBinaryString(instruction);
        line = "0".repeat(32 - line.length()) + line;
        String opcodeStr = line.substring(32 - 7, 32);
        String rd = line.substring(32 - 12, 32 - 7);
        String funct3 = line.substring(32 - 15, 32 - 12);
        String rs1 = line.substring(32 - 20, 32 - 15);
        String rs2 = line.substring(32 - 25, 32 - 20);
        String funct7 = line.substring(0, 32 - 25);

        opcode = Integer.parseInt(opcodeStr, 2);
        regDest = new Registr(Integer.parseInt(rd, 2));
        regSource1 = new Registr(Integer.parseInt(rs1, 2));
        regSource2 = new Registr(Integer.parseInt(rs2, 2));

        instruction >>= 7;
        imm4_0 = instruction & ((1 << 5) - 1);
        instruction >>= 5;
        imm31_12 = instruction;
        instruction >>= 8;
        imm11_0 = instruction;
        instruction >>= 5;
        imm11_5 = instruction;
        function = new Function(opcode, Integer.parseInt(funct3, 2), Integer.parseInt(funct7, 2));
    }
    public Command(int address, String labelName) {
        this.isLabel = true;
        this.address = address;
        this.labelName = labelName;
    }

    @Override
    public String toString() {
        if (isLabel) {
            return String.format("%08x   <%s>:\n", address, labelName);
        }
        return switch (opcode) {
            case 19 -> String.format("   %05x:\t%08x\t%7s\t%s, %s, %s\n",
                    address, instruction, function, regDest, regSource1, imm11_0);
            case 51 -> String.format("   %05x:\t%08x\t%7s\t%s, %s, %s\n",
                    address, instruction, function, regDest, regSource1, regSource2);
            case 55, 23 -> String.format("   %05x:\t%08x\t%7s\t%s, 0x%s\n",
                    address, instruction, function, regDest, Integer.toHexString(imm31_12));
            case 3, 103 -> String.format("   %05x:\t%08x\t%7s\t%s, %s(%s)\n",
                    address, instruction, function, regDest, imm11_0, regSource1);
            case 35 -> String.format("   %05x:\t%08x\t%7s\t%s, %s(%s)\n",
                    address, instruction, function, regSource2, (imm11_5 << 5) + imm4_0, regSource1);
            case 111 -> String.format("   %05x:\t%08x\t%7s\t%s, %s\n",
                    address, instruction, function, regDest, getLabelJal());
            case 99 -> String.format("   %05x:\t%08x\t%7s\t%s, %s, %s\n",
                    address, instruction, function, regSource1, regSource2, getLabelBranch());
            case 115 -> switch (regSource2.getReg()) {
                case 0 -> String.format("   %05x:\t%08x\t%7s\n",
                        address, instruction, "ecall");
                case 1 -> String.format("   %05x:\t%08x\t%7s\n",
                        address, instruction, "ebreak");
                default -> throw new IllegalStateException("Unexpected value: " + regSource2.getReg());
            };
            case 15 -> String.format("   %05x:\t%08x\t%7s\t%s, %s\n",
                    address, instruction, function, "iorw", "iorw");
            default -> String.format("   %05x:\t%08x\t%7s\n",
                    address, instruction, function);
        };
    }
    private String getLabelBranch() {
        int line = imm11_5;
        int imm10_5 = line & ((1 << 6) - 1);
        line >>= 6;
        int imm12 = line & 1;
        line = imm4_0;
        int imm11 = line & 1;
        line >>= 1;
        int imm4_1 = line & ((1 << 4) - 1);
        int offset = imm4_1 + (imm10_5 << 4) + (imm11 << 10) + (imm12 << 11);
        offset <<= 1;
        if(imm12 == 1) {
            offset |= -(1 << 12);
        }
        int address = this.address + offset;

        return String.format("0x%05x <%s>", address, elfFile.getFunctionName(address));
    }
    private String getLabelJal() {
        int line = imm31_12;
        int imm19_12 = line & ((1 << 8) - 1);
        line >>= 8;
        int imm11 = line & 1;
        line >>= 1;
        int imm10_1 = line & ((1 << 10) - 1);
        line >>= 10;
        int imm20 = line & 1;
        int offset = imm10_1 + (imm11 << 10) + (imm19_12 << 11) + (imm20 << 19);
        offset <<= 1;
        if(imm20 == 1) {
            offset |= -(1 << 21);
        }
        int address = this.address + offset;

        return String.format("0x%05x <%s>", address, elfFile.getFunctionName(address));
    }


}
