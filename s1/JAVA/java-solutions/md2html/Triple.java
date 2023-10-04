package md2html;

public class Triple {
    public String type;
    public int start;
    public int finish;

    Triple(String type, int start, int finish) {
        this.type = type;
        this.start = start;
        this.finish = finish;
    }

    @Override
    public String toString() {
        return type + " " + start + " " + finish;
    }
}
