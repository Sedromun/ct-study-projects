package disassembler;

public abstract class AbstractElf32DataTypes {
    protected final Number data;

    public AbstractElf32DataTypes(Number data) {
        this.data = data;
    }

    public int getIntData() {
        return data.intValue();
    }

    public int getShortData() {
        return data.shortValue();
    }

    @Override
    public String toString() {
        return String.valueOf(this.getIntData());
    }

}
