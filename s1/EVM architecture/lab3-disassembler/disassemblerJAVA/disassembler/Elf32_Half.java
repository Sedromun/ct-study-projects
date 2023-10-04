package disassembler;

public class Elf32_Half extends AbstractElf32DataTypes {
    public Elf32_Half(Number data) {
        super(data);
    }
    @Override
    public String toString() {
        return String.valueOf(super.getShortData());
    }
}
