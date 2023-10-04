public class Address {
    private final int tag;
    private final int set;

    public Address(int set, int tag) {
        this.tag = tag;
        this.set = set;
    }

    public int getTag() {
        return tag;
    }

    public int getSet() {
        return set;
    }
}
