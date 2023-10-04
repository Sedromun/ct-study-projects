package disassembler;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class ElfFile {
    private SymbolTable symbolTable;
    private final ByteBuffer elfFile;
    protected ElfFileHeader elfFileHeader;
    protected final ArrayList<SectionHeader> sectionsHeaders = new ArrayList<>();
    protected final SectionHeader shStrTab;
    protected final SectionHeader strTab;
    protected final SectionHeader symTab;
    protected final SectionHeader text;
    protected int textIndex;
    private int labelNumber = -1;
    protected final Map<Integer, String> functions = new TreeMap<>();
    public ElfFile(String fileName) throws IOException {
        elfFile = ByteBuffer.wrap(Files.readAllBytes(Path.of(fileName)));
        elfFile.order(ByteOrder.LITTLE_ENDIAN);
        setHeader();
        setSectionsHeaders();
        shStrTab = sectionsHeaders.get((elfFileHeader.e_shstrndx).getIntData());
        symTab = findSectionWithName(".symtab");
        text = findSectionWithName(".text");
        strTab = findSectionWithName(".strtab");
    }
    protected void setMapOfFunctions() {
        for(SymbolTableEntry symbolTableEntry : symbolTable.symbolTable) {
            if(symbolTableEntry.getType().equals("FUNC") && symbolTableEntry.getSt_shndx().getIntData() == textIndex) {
                functions.put(symbolTableEntry.getSt_value().getIntData(), symbolTableEntry.getName());
            }
        }
    }
    protected String getFunctionName(int address) {
        if (functions.containsKey(address)) {
            return functions.get(address);
        } else {
            labelNumber++;
            functions.put(address, "L" + labelNumber);
            return "L" + labelNumber;
        }
    }
    public void setSymbolTable(SymbolTable symbolTable) {
        this.symbolTable = symbolTable;
    }
    private void setHeader() {
        ByteBuffer elfFileHeader = elfFile.duplicate();
        elfFileHeader.order(ByteOrder.LITTLE_ENDIAN);
        this.elfFileHeader = new ElfFileHeader(elfFileHeader);
        //System.out.println(this.elfFileHeader);
    }
    private void setSectionsHeaders() {
        int i = elfFileHeader.e_shoff.getIntData();
        //System.out.println(i);
        for(int j = 0; j < elfFileHeader.e_shnum.getShortData(); j++) {
            SectionHeader header = getNextSectionHeader(i);
            //System.out.println(header);
            sectionsHeaders.add(header);
            i += elfFileHeader.e_shentsize.getShortData();
        }

    }
    private SectionHeader getNextSectionHeader(int i) {
        SectionHeader header;
        ByteBuffer sectionHeader = elfFile.duplicate();
        sectionHeader.order(ByteOrder.LITTLE_ENDIAN);
        sectionHeader.position(i);
        header = new SectionHeader(sectionHeader);
        //System.out.println(header);
        return header;
    }
    private SectionHeader findSectionWithName(String name) {
        for(int i = 0; i < sectionsHeaders.size(); i++) {
            SectionHeader header = sectionsHeaders.get(i);
            if(getNameByOffset(shStrTab.sh_offset.getIntData() + header.sh_name.getIntData()).equals(name)) {
                if (name.equals(".text")) {
                    textIndex = i;
                }
                return header;
            }
        }
        throw new RuntimeException("header with name '" + name + "' not found");
    }

    protected String getNameByOffset(int offset) {
        char c = (char)elfFile.get(offset);
        StringBuilder ans = new StringBuilder();
        while(c != '\0') {
            ans.append(c);
            offset++;
            c = (char)elfFile.get(offset);
        }
        return ans.toString();
    }

    protected Text parseTextSection() {
        Text parsedText = new Text();
        int offset = text.sh_offset.getIntData();
        int size = text.sh_size.getIntData();
        int end = offset + size;
        while(offset < end) {
            Command command = parseCommand(offset);
            offset += 4;
            parsedText.addCommand(command);
        }
        return parsedText;
    }

    private Command parseCommand(int offset) {
        int address = text.sh_addr.getIntData() + offset - text.sh_offset.getIntData();
        return new Command(this, address, elfFile.getInt(offset));
    }

    protected int elfFileGetInt(int offset) {
        return elfFile.getInt(offset);
    }

    protected short elfFileGetShort(int offset) {
        return elfFile.getShort(offset);
    }

    protected byte elfFileGet(int offset) {
        return elfFile.get(offset);
    }


}
