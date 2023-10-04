package disassembler;

import java.util.ArrayList;

public class SymbolTable {
    protected final ArrayList<SymbolTableEntry> symbolTable;
    public SymbolTable(ArrayList<SymbolTableEntry> symbolTable) {
        this.symbolTable = symbolTable;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(".symtab\nSymbol Value              Size Type 	Bind 	 Vis       Index Name\n");
        for (SymbolTableEntry symbolTableEntry : symbolTable) {
            sb.append(symbolTableEntry.toString());
        }
        return sb.toString();
    }
}
