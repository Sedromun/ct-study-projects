`define CACHE_SIZE 2048 - 1
`define CACHE_LINE_SIZE 16 - 1
`define CACHE_LINE_COUNT 128 - 1
`define CACHE_SETS_COUNT 64 - 1
`define CACHE_TAG_SIZE 8 - 1
`define CACHE_SET_SIZE 6 - 1
`define CACHE_OFFSET_SIZE 4 - 1
`define CACHE_ADDR_SIZE 18 - 1

`define ADDR1_BUS_SIZE 14 - 1
`define ADDR2_BUS_SIZE 14 - 1
`define DATA1_BUS_SIZE 16 - 1
`define DATA2_BUS_SIZE 16 - 1
`define CTR1_BUS_SIZE 3 - 1
`define CTR2_BUS_SIZE 2 - 1

int cache_hit_count = 0;
int cache_miss_count = 0;
int cache_usage_count = 0;

module CPU(input CLK, output [`ADDR1_BUS_SIZE : 0] A1, inout [`DATA1_BUS_SIZE : 0] D1, inout [`CTR1_BUS_SIZE : 0] C1);
    
    reg[`DATA1_BUS_SIZE : 0] data1 = 16'bzzzzzzzzzzzzzzzz;
    assign D1 = data1;

    reg[`ADDR1_BUS_SIZE : 0] addr1 = 14'bzzzzzzzzzzzzzz;
    assign A1 = addr1;

    reg[`CTR1_BUS_SIZE : 0] com1 = 3'bzzz;
    assign C1 = com1;
 
    int M = 64;
    int N = 60;
    int K = 32;
    

    reg[`CACHE_SET_SIZE : 0] set = 0;
    reg[`CACHE_TAG_SIZE : 0] tag = 0;
    reg[`CACHE_OFFSET_SIZE : 0] offset = 0;

    task leave_bus_cpu_cache;
        com1 = 3'bzzz;
        data1 = 16'bzzzzzzzzzzzzzzzz;
        addr1 = 14'bzzzzzzzzzzzzzz;
    endtask

    int pa = 0;
    int pc = 0;
    int pb = 0;
    int s = 0;
    int addr;
    int pak;
    int pbx;

    initial begin 
        #4;
        for (int y = 0; y < M; y+=1) begin
            for (int x = 0; x < N; x+=1) begin
                pb = 0;
                s = 0;
                #4;
                for (int k = 0; k < K; k+=1) begin
                    //обращение к pa[k]: a[pa][k]
                    //a - 8bit => addr = pa + k
                    //$write(y);
                    //$write(x);
                    //$display(k);
                    #2;
                    addr = pa + k;
                    set = addr >> (`CACHE_OFFSET_SIZE + 1);
                    tag = addr >> (`CACHE_OFFSET_SIZE + `CACHE_SET_SIZE + 2);
                    offset = addr;
                    #1;
                    com1 = 1;
                    addr1[`CACHE_TAG_SIZE : 0] = tag;
                    addr1[`ADDR1_BUS_SIZE : `CACHE_TAG_SIZE + 1] = set;
                    #2;
                    addr1[`CACHE_OFFSET_SIZE : 0] = offset;
                    #2;
                    leave_bus_cpu_cache();
                    
                    wait(C1 == 7);
                    cache_usage_count++;
                    

                    pak = D1[7:0];

                    //обращение к pb[x]: b[pb][x]
                    //a - 16bit => addr = (pb + x)*2
                    #2;
                    addr = M*K + (pb + x)*2;
                    set = addr >> (`CACHE_OFFSET_SIZE + 1);
                    tag = addr >> (`CACHE_OFFSET_SIZE + `CACHE_SET_SIZE + 2);
                    offset = addr;
                    #1;
                    com1 = 2;
                    addr1[`CACHE_TAG_SIZE : 0] = tag;
                    addr1[`ADDR1_BUS_SIZE : `CACHE_TAG_SIZE + 1] = set;
                    #2;
                    addr1[`CACHE_OFFSET_SIZE : 0] = offset;
                    #2;
                    leave_bus_cpu_cache();
                    
                    wait(C1 == 7);
                    cache_usage_count++;

                    pbx = D1;

                    #20 s += pak * pbx; //время работы +, *

                    pb+=N;
                end
                //pc[x] = s;
                //обращение к c[pc][x] для записи
                #2;
                addr = M*K + 2*K*N + (pc + x)*4;
                set = addr >> (`CACHE_OFFSET_SIZE + 1);
                tag = addr >> (`CACHE_OFFSET_SIZE + `CACHE_SET_SIZE + 2);
                offset = addr;
                #1;
                com1 = 7;
                addr1[`CACHE_TAG_SIZE : 0] = tag;
                addr1[`ADDR1_BUS_SIZE : `CACHE_TAG_SIZE + 1] = set;
                data1 = s % 65536;
                #2;
                addr1[`CACHE_OFFSET_SIZE : 0] = offset;
                data1 = s / 65536;
                #2;
                leave_bus_cpu_cache();
                
                wait(C1 == 7);
                cache_usage_count++;
                #4;
            end
            pa+=K;
            pc+=N;
            #6;
        end
        #2;
        $display("Value of cache hits: %d", cache_hit_count);
        $display("Value of cache misses: %d", cache_miss_count);
        $display("Value of cache usages: %d", cache_usage_count);
        $display("Percent of cache hits: %f", cache_hit_count * 100.0/cache_usage_count);
        $display("Percent of cache misses: %f", (100 - cache_hit_count * 100.0/cache_usage_count));
        $display("Estimated time %d (in processor tact's)", $time / 2);
        $finish;
    end
endmodule