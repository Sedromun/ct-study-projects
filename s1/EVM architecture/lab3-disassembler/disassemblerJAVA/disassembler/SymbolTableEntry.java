package disassembler;

public class SymbolTableEntry {
    private final ElfFile elfFile;
    private final int st_symndx; //index of symbol
    private final Elf32_Word st_name;
    private final Elf32_Addr st_value;
    private final Elf32_Word st_size;
    private final int st_info;
    private final int st_other;
    private final Elf32_Half st_shndx;

    protected Elf32_Addr getSt_value() {
        return st_value;
    }

    protected Elf32_Half getSt_shndx() {
        return st_shndx;
    }

    public SymbolTableEntry(ElfFile elfFile, int st_symndx, Elf32_Word st_name, Elf32_Addr st_value, Elf32_Word st_size, byte st_info, byte st_other, Elf32_Half st_shndx) {
        this.elfFile = elfFile;
        this.st_symndx = st_symndx;
        this.st_name = st_name;
        this.st_value = st_value;
        this.st_size = st_size;
        this.st_info = st_info;
        this.st_other = st_other;
        this.st_shndx = st_shndx;
    }

    @Override
    public String toString() {
        return String.format("[%4d] 0x%-15X %5d %-8s %-8s %-8s %6s %s\n",
                st_symndx, st_value.getIntData(), st_size.getIntData(),
                getType(), getBind(), getVis(), getIndex(), getName()
        );
    }

    private String getIndex() {
        return switch (st_shndx.getIntData()) {
            case 0 -> "UNDEF";
            case -15 -> "ABS";
            default -> st_shndx.toString();
        };
    }
    private String getBind() {
        return switch (st_info >> 4) {
            case 0 -> "LOCAL";
            case 1 -> "GLOBAL";
            case 2 -> "WEAK";
            case 13 -> "LOPROC";
            case 15 -> "HIPROC";
            default -> throw new IllegalStateException("Unexpected value: " + st_info);
        };
    }

    private String getVis() {
        return switch (st_other) {
            case 0 -> "DEFAULT";
            case 1 -> "INTERNAL";
            case 2 -> "HIDDEN";
            case 3 -> "PROTECTED";
            default -> throw new IllegalStateException("Unexpected value: " + st_other);
        };
    }

    protected String getName() {
        if (getType().equals("SECTION")) {
            return elfFile.getNameByOffset(elfFile.sectionsHeaders.get(st_shndx.getIntData()).sh_name.getIntData() + elfFile.shStrTab.sh_offset.getIntData());
        } else {
            return elfFile.getNameByOffset(elfFile.strTab.sh_offset.getIntData() + st_name.getIntData());
        }
    }

    protected String getType() {
        return switch (st_info & 0xf) {
            case 0 -> "NOTYPE";
            case 1 -> "OBJECT";
            case 2 -> "FUNC";
            case 3 -> "SECTION";
            case 4 -> "FILE";
            case 13 -> "LOPROC";
            case 15 -> "HIPROC";
            default -> throw new IllegalStateException("Unexpected value: " + st_info);
        };
    }
}
