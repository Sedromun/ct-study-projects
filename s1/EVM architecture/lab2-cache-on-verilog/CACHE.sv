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

int log = 0;

module CACHE (
        input CLK, input [`ADDR1_BUS_SIZE : 0] A1, output [`ADDR2_BUS_SIZE : 0] A2, 
        inout [`DATA1_BUS_SIZE : 0] D1, inout [`DATA2_BUS_SIZE : 0] D2, 
        inout [`CTR1_BUS_SIZE : 0] C1, inout [`CTR2_BUS_SIZE : 0] C2,
        input C_DUMP, input RESET
    );  

    reg [1:0] valid[0 : `CACHE_SETS_COUNT][0:1];
    reg [1:0] dirty[0 : `CACHE_SETS_COUNT][0:1];
    reg [1:0] lru[0 : `CACHE_SETS_COUNT][0:1];
    reg [`CACHE_TAG_SIZE : 0] cache_line_tag[0 : `CACHE_SETS_COUNT][0:1];
    reg [7:0] data[0 : `CACHE_SETS_COUNT][0 : `CACHE_LINE_SIZE][0:1];

    initial begin
        for(int i = 0; i <= `CACHE_SETS_COUNT; i += 1) begin
            for(int j = 0; j < 2; j += 1) begin
                valid[i][j] = 0;
            end
        end
    end

    reg[`CACHE_TAG_SIZE : 0] cur_tag;
    reg[`CACHE_SET_SIZE : 0] cur_set;
    reg[`CACHE_OFFSET_SIZE : 0] cur_offset;
    reg[2 * `DATA1_BUS_SIZE + 1: 0] cur_data1;
    reg[`DATA2_BUS_SIZE : 0] cur_data2;
    reg[`CTR1_BUS_SIZE : 0] cur_com;



    reg [`ADDR2_BUS_SIZE : 0] addr2 = 14'bzzzzzzzzzzzzzz; 
    reg [`DATA1_BUS_SIZE : 0] retData1 = 16'bzzzzzzzzzzzzzzzz;
    reg [`DATA2_BUS_SIZE : 0] retData2 = 16'bzzzzzzzzzzzzzzzz; 
    reg [`CTR1_BUS_SIZE : 0] com1 = 3'bzzz; 
    reg [`CTR2_BUS_SIZE : 0] com2 = 2'bzz;

    reg[1:0] cache_hit;
    int done = 0;

    assign A2 = addr2;
    assign D1 = retData1;
    assign D2 = retData2;
    assign C1 = com1;
    assign C2 = com2;

    task leave_bus_cache_mem;
        if(log) begin
            $display("leave_bus_cache_mem");
        end
        #1;
        addr2 = 14'bzzzzzzzzzzzzzz;
        retData2 = 16'bzzzzzzzzzzzzzzzz;
        com2 = 2'bzz;
    endtask

    task leave_bus_cache_cpu;
        if(log) begin
            $display("leave_bus_cache_cpu");
        end
        #1;
        retData1 = 16'bzzzzzzzzzzzzzzzz;
        com1 = 3'bzzz;
    endtask

    task get_input;
        if(log) begin
            $display("get_input");
        end
        cur_tag = A1[`CACHE_TAG_SIZE : 0];
        cur_set = A1[`ADDR1_BUS_SIZE : `CACHE_TAG_SIZE + 1];
        cur_data1[15 : 0] = D1;
        cur_com = C1;
        #2; //wait new part of address and data
        cur_data1[31 : 16] = D1;
        cur_offset = A1;
        #1;
    endtask

    task check_cache_hit;
        if(log) begin
            $display("check_cache_hit");
        end
        cache_hit = 2'b11;
        
        for(int j = 0; j < 2; j += 1) begin
            if (cache_line_tag[cur_set][j] != cur_tag) begin
                cache_hit[j] = 0;
            end
        end 
    endtask

    task return_read(int i);
        if(log) begin
            $display("return_read");
        end
        #1;
        com1 = 7;
        retData1[7:0] = data[cur_set][cur_offset][i];
        retData1[15:8] = data[cur_set][cur_offset + 1][i];

        if(cur_com == 3) begin
            #2;
            retData1[7:0] = data[cur_set][cur_offset + 2][i];
            retData1[15:8] = data[cur_set][cur_offset + 3][i];
        end


        #2;
        leave_bus_cache_cpu();

    endtask

    task return_response;
        if(log) begin
            $display("return_response");
        end
        #1;
        com1 = 7;

        #2;
        leave_bus_cache_cpu();

    endtask

    task write(int i);
        if(log) begin
            $display("write");
        end
        data[cur_set][cur_offset][i] = cur_data1[7:0]; //C1_WRITE8
        if(cur_com >= 6) begin  //C1_WRITE16
            data[cur_set][cur_offset + 1][i] = cur_data1[15:8];
        end

        if(cur_com == 7) begin //C1_WRITE32
            data[cur_set][cur_offset + 2][i] = cur_data1[23:16];
            data[cur_set][cur_offset + 3][i] = cur_data1[31:24];
        end
    endtask
        
    task set_lru(int i);
        if(log) begin
            $display("set_lru");
        end
        lru[cur_set][i] = 1;
        lru[cur_set][1 - i] = 0;
    endtask

    task write_procedure(int i);
        if(log) begin
            $display("write_procedure");
        end
        write(i);
        dirty[cur_set][i] = 1;
        return_response();
    endtask;

    task do_if_cache_hit;
        if(log) begin
            $display("do_if_cache_hit");
        end
        for(int i = 0; i < 2; i += 1) begin
            if (valid[cur_set][i] == 1 && cache_hit[i] == 1 && done == 0) begin
                // CACHE попадание
                cache_hit_count += 1;

                set_lru(i);
                if(cur_com <= 3) begin
                    return_read(i);
                end else begin
                    write_procedure(i);
                end

                done = 1;
            end
        end
    endtask

    task make_memory_read_request;
        if(log) begin
            $display("make_memory_read_request");
        end

        #1;
        com2 = 2;
        addr2 [`CACHE_TAG_SIZE : 0] = cur_tag;
        addr2 [`ADDR2_BUS_SIZE : `CACHE_TAG_SIZE + 1] = cur_set;
        #2;
        leave_bus_cache_mem();
        #1;

        wait(C2 == 1); //wait for response
    endtask

    task make_memory_write_request(int i);
        if(log) begin
            $display("make_memory_write_request");
        end
        #2;
        com2 = 3;
        addr2 [`CACHE_TAG_SIZE : 0] = cache_line_tag[cur_set][i];
        addr2 [`ADDR2_BUS_SIZE : `CACHE_TAG_SIZE + 1] = cur_set;

        for(int j = 0; j < 8; j+=1) begin
            retData2[7:0] = data[cur_set][2*j][i];
            retData2[15:8] = data[cur_set][2*j+1][i];
            #2; //wait to send new portion of data
        end

        leave_bus_cache_mem();
        #1;

        //wait for response(to be sure data is written)
        wait(C2 == 1);
        #2;
    endtask

    task set_info(int i);
        if(log) begin
            $display("set_info");
        end
        valid[cur_set][i] = 1;
        dirty[cur_set][i] = 0;
        set_lru(i);
        cache_line_tag[cur_set][i] = cur_tag;
    endtask

    task get_data_from_memory(int i);
        if(log) begin
            $display("get_data_from_memory");
        end
        data[cur_set][0][i] = D2[7:0];
        data[cur_set][1][i] = D2[15:8];
        for(int j = 1; j < 8; j += 1) begin 
            #2; //wait new data;
            data[cur_set][2*j][i] = D2[7:0];
            data[cur_set][2*j + 1][i] = D2[15:8];
        end
        #2;
    endtask

    task cache_miss_tail_tasks(int i);
        if(log) begin
            $display("cache_miss_tail_tasks");
        end
        if(cur_com <= 3) begin

            make_memory_read_request();

            get_data_from_memory(i);

            set_info(i);
            
            return_read(i);

        end else begin

            write_procedure(i);

        end
    endtask

    task check_valid;
        if(log) begin
            $display("check_valid");
        end
        for(int i = 0; i < 2; i += 1) begin
            if (valid[cur_set][i] == 0 && done == 0) begin
                
                cache_miss_tail_tasks(i);

                done = 1;
            end
        end
    endtask

    task cache_miss_lru;
        if(log) begin
            $display("cache_miss_lru");
        end
        for(int i = 0; i < 2; i += 1) begin

            if (lru[cur_set][i] == 0 && done == 0) begin

                if(dirty[cur_set][i] == 1) begin
                    make_memory_write_request(i);
                end
                
                cache_miss_tail_tasks(i);
                done = 1;
            end

        end
    endtask

    task do_if_cache_miss;
        if(log) begin
            $display("do_if_cache_miss");
        end

        cache_miss_count++;

        check_valid();

        if(done == 1) begin
            done = 0;
        end else begin
            cache_miss_lru();
            done = 0;
        end

    endtask

    
    always @(posedge CLK) begin
        case (C1) 
            0: begin
                //C1_NOP
                if(log) begin
                    $display("Сache: command C1_NOP. Nothing is done");
                end
            end
            1, 2, 3, 5, 6, 7: begin

                get_input();

                //C1_READ8 C1_READ16 C1_READ32
                if(log == 1 && cur_com <= 3) begin
                    $display("Cache: command C1_READ");
                end

                //C1_WRITE8 C1_WRITE16 C1_WRITE32
                if(log == 1 && cur_com >= 5) begin
                    $display("Cache: command C1_WRITE");
                end

                #8; //cache hit or cache miss wait

                check_cache_hit();

                do_if_cache_hit();

                if(done == 1) begin
                    done = 0;
                end else begin
                    do_if_cache_miss();
                end
                    
            end
            4: begin
                //C1_INVALIDATE_LINE
                if(log) begin
                    $display("Cache: command C1_INVALIDATE_LINE");
                end

                get_input();

                #2; 

                for(int i = 0; i < 2; i += 1) begin
                    if (cache_line_tag[cur_set][i] == cur_tag) begin
                        valid[cur_set][i] = 0;
                    end
                end 

                return_response();

            end
        endcase

        if(C_DUMP) begin
            $dumpfile("CACHE_DUMP.vcd");
    		$dumpvars(1, CACHE);
        end

        if(RESET) begin
            for(int i = 0; i <= `CACHE_SETS_COUNT; i++) begin
                for(int j = 0; j < 2; j++) begin
                    valid[i][j] = 0;
                end
            end
        end
    end

endmodule