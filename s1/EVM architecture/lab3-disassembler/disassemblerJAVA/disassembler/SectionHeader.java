package disassembler;

import java.nio.ByteBuffer;

public class SectionHeader {
    protected final Elf32_Word sh_name; // Section name
    protected final Elf32_Word sh_type; // Section type
    protected final Elf32_Word sh_flags; // Section flags
    protected final Elf32_Addr sh_addr; // Address of first byte
    protected final Elf32_Off sh_offset; // Offset
    protected final Elf32_Word sh_size; // Size
    protected final Elf32_Word sh_link; // Link
    protected final Elf32_Word sh_info; // Info
    protected final Elf32_Word sh_addralign; // Addralign
    protected final Elf32_Word sh_entsize; // entry size

    public SectionHeader(ByteBuffer elfSectionHeader) {
        sh_name = new Elf32_Word(elfSectionHeader.getInt());
        sh_type = new Elf32_Word(elfSectionHeader.getInt());
        sh_flags = new Elf32_Word(elfSectionHeader.getInt());
        sh_addr = new Elf32_Addr(elfSectionHeader.getInt());
        sh_offset = new Elf32_Off(elfSectionHeader.getInt());
        sh_size = new Elf32_Word(elfSectionHeader.getInt());
        sh_link = new Elf32_Word(elfSectionHeader.getInt());
        sh_info = new Elf32_Word(elfSectionHeader.getInt());
        sh_addralign = new Elf32_Word(elfSectionHeader.getInt());
        sh_entsize = new Elf32_Word(elfSectionHeader.getInt());
    }

    @Override
    public String toString() {
        return "Name: " + sh_name + '\n' +
                "Type: " + sh_type + '\n' +
                "Flags: " + sh_flags + '\n' +
                "Entry point address: " + sh_addr + '\n' +
                "Offset: " + sh_offset + '\n' +
                "Size: " + sh_size + '\n' +
                "Link: " + sh_link + '\n' +
                "Info: " + sh_info + '\n' +
                "Address align: " + sh_addralign + '\n' +
                "Entry size: " + sh_entsize + '\n' + '\n';
    }

}
