package disassembler;

import java.nio.ByteBuffer;

public class ElfFileHeader {
    private final ByteBuffer elfHeader;
    protected Elf32_Half e_type; // type of file
    protected Elf32_Half e_machine; // required architecture
    protected Elf32_Word e_version; // version is valid or not
    protected Elf32_Addr e_entry; // entry point address
    protected Elf32_Off e_phoff; // start of program headers
    protected Elf32_Off e_shoff; // start of section headers
    protected Elf32_Word e_flags; // flag
    protected Elf32_Half e_ehsize; // size of this header
    protected Elf32_Half e_phentsize; // size of program header
    protected Elf32_Half e_phnum; // number of program headers
    protected Elf32_Half e_shentsize; // size of section header
    protected Elf32_Half e_shnum; // number of section headers
    protected Elf32_Half e_shstrndx; // section header string table index

    public ElfFileHeader(ByteBuffer elfFile) {
        elfHeader = elfFile;
        setElfHeaderData();
    }

    public void setElfHeaderData() {
        //skip first 16 bytes for e_ident, it's not interesting in this task
        byte[] bytes = new byte[16];
        elfHeader.get(bytes, 0, 16);
        e_type = new Elf32_Half(elfHeader.getShort());
        e_machine = new Elf32_Half(elfHeader.getShort());
        e_version = new Elf32_Word(elfHeader.getInt());
        e_entry = new Elf32_Addr(elfHeader.getInt());
        e_phoff = new Elf32_Off(elfHeader.getInt());
        e_shoff = new Elf32_Off(elfHeader.getInt());
        e_flags = new Elf32_Word(elfHeader.getInt());
        e_ehsize = new Elf32_Half(elfHeader.getShort());
        e_phentsize = new Elf32_Half(elfHeader.getShort());
        e_phnum = new Elf32_Half(elfHeader.getShort());
        e_shentsize = new Elf32_Half(elfHeader.getShort());
        e_shnum = new Elf32_Half(elfHeader.getShort());
        e_shstrndx = new Elf32_Half(elfHeader.getShort());
    }


    //toString for debugging out
    @Override
    public String toString() {
        return "Type: " + e_type + '\n' +
                "Architecture: " + e_machine + '\n' +
                "Version: " + e_version + '\n' +
                "Entry point address: " + e_entry + '\n' +
                "Start of program headers: " + e_phoff + '\n' +
                "Start of section header: " + e_shoff + '\n' +
                "Flag: " + e_flags + '\n' +
                "Size of this header: " + e_ehsize + '\n' +
                "Size of program header: " + e_phentsize + '\n' +
                "Number of program headers: " + e_phnum + '\n' +
                "Size of section header: " + e_shentsize + '\n' +
                "Number of section headers: " + e_shnum + '\n' +
                "Section header string table index: " + e_shstrndx;
    }
}
