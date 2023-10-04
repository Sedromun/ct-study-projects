`define MEM_SIZE 262144 - 1
`define MEM_LINE_COUNT 16384 - 1
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

module MEM(input CLK, input [`ADDR2_BUS_SIZE : 0] A2, inout [`DATA2_BUS_SIZE : 0] D2, inout [`CTR2_BUS_SIZE : 0] C2,
            input M_DUMP, input RESET);

    reg [`DATA2_BUS_SIZE : 0] retData2 = 16'bzzzzzzzzzzzzzzzz;
    reg [`CTR2_BUS_SIZE : 0] com2 = 2'bzz;

    assign D2 = retData2;
    assign C2 = com2;

    int M = 64;
    int N = 60;
    int K = 32;

    integer SEED = 225526;

    reg [7:0] data[0 : `MEM_LINE_COUNT][0 : `CACHE_LINE_SIZE];

    initial begin
        for(int i = 0; i < M*K + N*K + M*N; i+=1) begin
            data[ i / (`CACHE_LINE_SIZE + 1) ][ i % (`CACHE_LINE_SIZE + 1) ] = $random(SEED)>>16;  
        end
    end

    reg [`CACHE_TAG_SIZE : 0] cur_tag;
    reg [`CACHE_SET_SIZE : 0] cur_set;

    task leave_bus_mem_cache;
        if(log) begin
            $display("leave_bus_mem_cache");
        end
        #1;
        retData2 = 16'bzzzzzzzzzzzzzzzz;
        com2 = 2'bzz;
    endtask

    always @(posedge CLK) begin
        case (C2) 
            0: begin
                //C2_NOP
                if(log) begin
                    $display("Memory: nothing is done");
                end
            end
            2: begin
                //C2_READ_LINE
                if(log) begin
                    $display("Memory: read line");
                end
                cur_tag = A2[`CACHE_TAG_SIZE : 0];
                cur_set = A2[`ADDR2_BUS_SIZE : `CACHE_TAG_SIZE + 1];

                //TAG * set_size + SET
                #100; //memory work time
                if(log) begin
                    $display("Memory: line is read");
                end
                #1;
                com2 = 1;
                for(int i = 0; i < 8; i+=1) begin
                    retData2 [7:0] = data[cur_tag * (`CACHE_SETS_COUNT + 1) + cur_set][2*i];
                    retData2 [15:8] = data[cur_tag * (`CACHE_SETS_COUNT + 1) + cur_set][2*i + 1];
                    #2;
                end
                leave_bus_mem_cache();
            end
            3: begin
                //C2_WRITE_LINE
                if(log) begin
                    $display("Memory: read line");
                end
                cur_tag = A2[`CACHE_TAG_SIZE : 0];
                cur_set = A2[`ADDR1_BUS_SIZE : `CACHE_TAG_SIZE + 1];

                for(int i = 0; i < 8; i+=1) begin
                    data[cur_tag * (`CACHE_SET_SIZE + 1) + cur_set][2*i] = retData2 [7:0];
                    data[cur_tag * (`CACHE_SET_SIZE + 1) + cur_set][2*i + 1] = retData2 [15:8];
                    #2;
                end
                
                
                #100;
                #1;
                com2 = 1;
                if(log) begin
                    $display("Memory: line is written");
                end
                #2;
                leave_bus_mem_cache();
            end
        endcase

        if(M_DUMP) begin
            if(log) $display("M_DUMP");

            $dumpfile("MEM_DUMP.vcd");
    		$dumpvars(1, MEM);
        end

        if(RESET) begin
            if(log) $display("RESET");

            for(int i = 0; i < M*K + N*K + M*N; i+=1) begin
                data[ i / (`CACHE_LINE_SIZE + 1) ][ i % (`CACHE_LINE_SIZE + 1) ] = $random(SEED)>>16;  
            end
            
        end
    end

endmodule