`include "CPU.sv"
`include "MEM.sv"
`include "CACHE.sv"
`define ADDR1_BUS_SIZE 13
`define ADDR2_BUS_SIZE 14 - 1
`define DATA1_BUS_SIZE 16 - 1
`define DATA2_BUS_SIZE 16 - 1
`define CTR1_BUS_SIZE 3 - 1
`define CTR2_BUS_SIZE 2 - 1


module main;
    wire[`ADDR1_BUS_SIZE : 0] A1 = 14'bzzzzzzzzzzzzzz;
    wire[`ADDR2_BUS_SIZE : 0] A2 = 14'bzzzzzzzzzzzzzz; 
    wire[`DATA1_BUS_SIZE : 0] D1 = 16'bzzzzzzzzzzzzzzzz;
    wire[`DATA2_BUS_SIZE : 0] D2 = 16'bzzzzzzzzzzzzzzzz; 
    wire[`CTR1_BUS_SIZE : 0] C1 = 3'bzzz;
    wire[`CTR2_BUS_SIZE : 0] C2 = 2'bzz;

    reg CLK = 1'b0;
    reg C_DUMP = 1'b0;
    reg M_DUMP = 1'b0;
    reg RESET = 1'b0;

    CPU cpu(.CLK(CLK), .A1(A1), .D1(D1), .C1(C1));

    CACHE cache(.CLK(CLK), .A1(A1), .A2(A2), .D1(D1),
                .D2(D2), .C1(C1), .C2(C2),
                .C_DUMP(C_DUMP), .RESET(RESET));

    MEM mem(.CLK(CLK), .A2(A2), .D2(D2), .C2(C2),
                .M_DUMP(M_DUMP), .RESET(RESET));

    always begin
        #1 CLK = ~CLK;
    end

endmodule