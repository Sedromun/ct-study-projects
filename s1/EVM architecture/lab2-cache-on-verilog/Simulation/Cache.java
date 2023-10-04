public class Cache {
    private static final int CACHE_LINE_SIZE = 16;
    private static final int CACHE_SETS_COUNT = 64;
    private static final int CACHE_WAY = 2;
    CacheLine[][] cacheLines;

    public Cache() {
        cacheLines = new CacheLine[CACHE_SETS_COUNT][CACHE_WAY];
        for (int i = 0; i < CACHE_SETS_COUNT; i++) {
            for (int j = 0; j < CACHE_WAY; j++) {
                cacheLines[i][j] = new CacheLine();
            }
        }
    }

    public int cacheRequest(int addr, char type) {
        int set = (addr / CACHE_LINE_SIZE) % CACHE_SETS_COUNT;
        int tag = (addr / CACHE_LINE_SIZE) / CACHE_SETS_COUNT;
        Address address = new Address(set, tag);
        if(this.containsAddress(address, type)) {
            return 9;
        } else {
            return this.add(address, type);
        }
    }

    public boolean containsAddress(Address address, char type) {
        for(int i = 0; i < 2; i++) {
            CacheLine cacheLine = cacheLines[address.getSet()][i];
            if (cacheLine.valid && cacheLine.tag == address.getTag()) {
                // cache hit
                if (type == 'w') {
                    cacheLine.dirty = true;
                }
                cacheLines[address.getSet()][1-i].lru = false;
                cacheLines[address.getSet()][i].lru = true;
                return true;
            }
        }
        return false;
    }

    //only in case of cache miss
    public int add(Address address, char type) {
        for(int i = 0; i < 2; i++) {
            CacheLine cacheLine = cacheLines[address.getSet()][i];
            if (!cacheLine.valid) {
                cacheLines[address.getSet()][i].lru = true;
                cacheLines[address.getSet()][1-i].lru = false;
                cacheLines[address.getSet()][i].valid = true;
                cacheLines[address.getSet()][i].tag = address.getTag();
                if (type == 'r')
                    cacheLines[address.getSet()][i].dirty = false;
                else
                    cacheLines[address.getSet()][i].dirty = true;
                //data from memory
                return 116;
            }
        }

        if (!cacheLines[address.getSet()][1].lru) {
            cacheLines[address.getSet()][1].lru = true;
            cacheLines[address.getSet()][0].lru = false;
            cacheLines[address.getSet()][1].tag = address.getTag();

            if (cacheLines[address.getSet()][1].dirty) {
                if (type == 'r') {
                    cacheLines[address.getSet()][1].dirty = false;
                } //else if (type == 'w')
                  //  cacheLines[address.getSet()][1].dirty = true;
                  // dirty = true в этом случае
                return 225;
            }

            if (type == 'w')
                cacheLines[address.getSet()][1].dirty = true;
            return 116;
            //data from memory
        } else {
            cacheLines[address.getSet()][0].lru = true;
            cacheLines[address.getSet()][1].lru = false;
            cacheLines[address.getSet()][0].tag = address.getTag();
            if (cacheLines[address.getSet()][0].dirty) {
                if (type == 'r') {
                    cacheLines[address.getSet()][0].dirty = false;
                }
                return 225;
            }

            if (type == 'w') {
                cacheLines[address.getSet()][0].dirty = true;
            }
            return 116;
        }
    }

}
