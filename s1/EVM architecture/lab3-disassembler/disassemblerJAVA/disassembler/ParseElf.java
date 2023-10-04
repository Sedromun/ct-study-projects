package disassembler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

public class ParseElf {
    private final ElfFile elfFile;
    protected final SymbolTable symbolTable;

    public ParseElf(String fileName) throws IOException {
        this.elfFile = new ElfFile(fileName);
        symbolTable = parseSymbolTable();
        elfFile.setSymbolTable(symbolTable);
        elfFile.setMapOfFunctions();
    }

    public SymbolTable parseSymbolTable() {
        ArrayList<SymbolTableEntry> symbolTableEntries = new ArrayList<>();
        SectionHeader symTab = elfFile.symTab;
        int offset = symTab.sh_offset.getIntData();
        int startOffset = offset;
        int index = 0;
        while(offset - startOffset < symTab.sh_size.getIntData()) {
            symbolTableEntries.add(parseSymbolTableEntry(offset, index));
            offset += 16;
            index++;
        }
        return new SymbolTable(symbolTableEntries);
    }

    private SymbolTableEntry parseSymbolTableEntry(int offset, int index) {
        Elf32_Word st_name = new Elf32_Word(elfFile.elfFileGetInt(offset));
        offset += 4;
        Elf32_Addr st_value = new Elf32_Addr(elfFile.elfFileGetInt(offset));
        offset += 4;
        Elf32_Word st_size = new Elf32_Word(elfFile.elfFileGetInt(offset));
        offset += 4;
        byte st_info = elfFile.elfFileGet(offset);
        offset += 1;
        byte st_other = elfFile.elfFileGet(offset);
        offset += 1;
        Elf32_Half st_shndx = new Elf32_Half(elfFile.elfFileGetShort(offset));
        return new SymbolTableEntry(elfFile, index, st_name, st_value, st_size, st_info, st_other, st_shndx);
    }

    public Text parseText() {
        Text text = elfFile.parseTextSection();
        int cnt = 0;
        for(Map.Entry<Integer, String> entry : elfFile.functions.entrySet()) {
            text.addCommand(
                    (entry.getKey() - elfFile.text.sh_addr.getIntData()) / 4 + cnt,
                    new Command(entry.getKey(), entry.getValue())
            );
            cnt++;
        }
        return text;
    }
}
