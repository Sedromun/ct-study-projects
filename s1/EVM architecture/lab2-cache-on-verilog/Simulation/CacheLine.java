public class CacheLine {
    public int tag;
    public boolean valid;
    public boolean dirty;
    public boolean lru; //true => later used

    public CacheLine() {
        this.tag = 0;
        valid = false;
        dirty = false;
        lru = false;
    }
}
