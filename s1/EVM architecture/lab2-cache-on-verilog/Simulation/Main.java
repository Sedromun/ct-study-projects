public class Main {
    public static void main(String[] args) {
        int M = 64;
        int N = 60;
        int K = 32;
        Cache cache = new Cache();

        int time = 0;
        int cacheUsages = 0;
        int cacheHits = 0;
        int pa = 0;
        int pc = 0;
        time += 2;
//        int x = 0;
//        int pb = 0;
        for (int y = 0; y < M; y++)
        {
            for (int x = 0; x < N; x++)
            {
                int pb = 0;
                time += 2;

                for (int k = 0; k < K; k++)
                {
                    //обращение к pa[k]: a[pa][k]
                    //a - 8bit => addr = pa + k
                    int addr = pa + k;
                    int addTime = cache.cacheRequest(addr, 'r');
                    if (addTime == 9){
                        cacheHits++;
                       // System.out.println("cache_hit");
                    } //else {
                       // System.out.println("cache miss");
                   // }
                    time += addTime;
                    cacheUsages++;

                    //обращение к pb[x]: b[pb][x]
                    //a - 16bit => addr = (pb + x)*2
                    addr = M*K + (pb + x)*2;
                    addTime = cache.cacheRequest(addr, 'r');
                    if (addTime == 9){
                        cacheHits++;
                       // System.out.println("cache_hit");
                    } // else {
                       // System.out.println("cache miss");
                  //  }
                    time += addTime;
                    cacheUsages++;

                    //s += pa[k] * pb[x];
                    time += 8; //add, mult, mem x2

                    pb+=N;
                    time += 2; //add, for
                }
                //pc[x] = s;
                time += 1;
                //обращение к c[pc][x] для записи
                int addr = M*K + 2*K*N + (pc + x)*4;
                int addTime = cache.cacheRequest(addr, 'w');
                if (addTime == 9){
                    cacheHits++;
                }
                time += addTime;
                cacheUsages++;

                time++;
            }
            pa+=K;
            pc+=N;
            time += 3;
        }

        time++; //exit mmul
        double percentOfCacheHits = cacheHits * 100.0 / cacheUsages;
        System.out.println("Value of cache hits: " + cacheHits);
        System.out.println("Value of cache misses: " + (cacheUsages - cacheHits));
        System.out.println("Value of cache usages: " + cacheUsages);
        System.out.println("Percent of cache hits: " + percentOfCacheHits + "%");
        System.out.println("Percent of cache misses: " + (100 - percentOfCacheHits) + "%");
        System.out.println("Estimated time " + time + " (in processor tact's)");
    }
}
