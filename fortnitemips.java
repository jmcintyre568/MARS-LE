package mars.mips.instructions.customlangs;
import mars.simulator.*;
import mars.mips.hardware.*;
import mars.mips.instructions.syscalls.*;
import mars.*;
import mars.util.*;
import java.util.*;
import java.io.*;
import mars.mips.instructions.*;
import java.util.Random;

public class fortnitemips extends CustomAssembly{
    @Override
    public String getName(){
        return "Fortnite Mips";
    }

    @Override
    public String getDescription(){
        return "Fortnite themed mips language that lets you code in mips like you play fortnite";
    }

    @Override
    protected void populate(){
        instructionList.add(
                //add
                new BasicInstruction("matfarm $t0,$t1,$t2",
                        "matfarm: adds value of source registers t1 and t2 and stores the sum in t0",
                        BasicInstructionFormat.R_FORMAT,
                        "000000 sssss ttttt fffff 00000 100000",
                        new SimulationCode()
                        {
                            public void simulate(ProgramStatement statement) throws ProcessingException
                            {
                                int[] operands = statement.getOperands();
                                int rs = RegisterFile.getValue(operands[1]);
                                int rt = RegisterFile.getValue(operands[2]);

                                int sum = rs + rt;
                                RegisterFile.updateRegister(operands[0], sum);
                            }
                        }));
        //addi
        instructionList.add(

                new BasicInstruction("farmandcollect $t0,$t1,100",
                        "farmandcollect: adds immediate value imm to source register t1 and stores the sum in t0",
                        BasicInstructionFormat.I_FORMAT,
                        "001000 sssss fffff tttttttttttttttt",
                        new SimulationCode()
                        {
                            public void simulate(ProgramStatement statement) throws ProcessingException
                            {
                                int[] operands = statement.getOperands();
                                int rt = operands[0];
                                int rs = RegisterFile.getValue(operands[1]);
                                int immed = (short)operands[2];
                                int sum = rs + immed;

                                RegisterFile.updateRegister(rt, sum);
                            }
                        }));
        //sub
        instructionList.add(
                new BasicInstruction("dropguns $t0,$t1,$t2",
                        "dropguns: subtracts t2 from t1 and stores the result in t0",
                        BasicInstructionFormat.R_FORMAT,
                        "000000 sssss ttttt fffff 00000 000010",
                        new SimulationCode()
                        {
                            public void simulate(ProgramStatement statement) throws ProcessingException
                            {
                                int[] operands = statement.getOperands();
                                int rs = RegisterFile.getValue(operands[1]);
                                int rt = RegisterFile.getValue(operands[2]);
                                // check for overflow
                                if((rs<0 && rt >=0 && rs-rt >=0) || (rs>=0 && rt < 0 && rs-rt <0)){
                                    throw new ProcessingException(statement, "overflow", Exceptions.ARITHMETIC_OVERFLOW_EXCEPTION);
                                }

                                int sub = rs-rt;
                                RegisterFile.updateRegister(operands[0], sub);
                            }
                        }));
        //bitwise and
        instructionList.add(
                new BasicInstruction("purpletac $t0,$t1,$t2",
                        "purpletac: performs the bitwise and on rs and rt and stores the result in rd",
                        BasicInstructionFormat.R_FORMAT,
                        "000000 sssss ttttt fffff 00000 100100",
                        new SimulationCode()
                        {
                            public void simulate(ProgramStatement statement) throws ProcessingException
                            {
                                int[] operands = statement.getOperands();
                                int rs = RegisterFile.getValue(operands[1]);
                                int rt = RegisterFile.getValue(operands[2]);
                                int out = rt & rs;
                                RegisterFile.updateRegister(operands[0], out);
                            }
                        }));
        //bitwise or
        instructionList.add(
                new BasicInstruction("zoneorres $t0,$t1,$t2",
                        "zoneorres:  bitwise or on rs and rt and stores result in rd",
                        BasicInstructionFormat.R_FORMAT,
                        "000000 sssss ttttt fffff 00000 100101",
                        new SimulationCode()
                        {
                            public void simulate(ProgramStatement statement) throws ProcessingException
                            {
                                int[] operands = statement.getOperands();
                                int rs = RegisterFile.getValue(operands[1]);
                                int rt = RegisterFile.getValue(operands[2]);
                                int out = rt | rs;
                                RegisterFile.updateRegister(operands[0], out);
                            }
                        }));
        //slt
        instructionList.add(
                new BasicInstruction("popshield $t0,$t1,$t2",
                        "popshield:  sets rd to 1 if rs is less then rt and 0 if greater",
                        BasicInstructionFormat.R_FORMAT,
                        "000000 sssss ttttt fffff 00000 101010",
                        new SimulationCode()
                        {
                            public void simulate(ProgramStatement statement) throws ProcessingException
                            {
                                int[] operands = statement.getOperands();
                                int s1 = RegisterFile.getValue(operands[1]);
                                int s2 = RegisterFile.getValue(operands[2]);
                                int out = (s1 < s2) ? 1 : 0;
                                RegisterFile.updateRegister(operands[0], out);
                            }
                        }));
        //lw
        instructionList.add(
                new BasicInstruction("boardbattlebus $t0,$t1,4",
                        "boardbattlebus:  loads a word from data memory at rs + offset and stores it in rt ",
                        BasicInstructionFormat.I_FORMAT,
                        "100011 ttttt fffff ssssssssssssssss",
                        new SimulationCode()
                        {
                            public void simulate(ProgramStatement statement) throws ProcessingException
                            {
                                int[] operands = statement.getOperands();
                                int rt = operands[0];
                                int rs = RegisterFile.getValue(operands[1]);
                                int offset = operands[2] << 16 >> 16;
                                int addy = rs + offset;
                                try {
                                    int loaded = Memory.getInstance().getWord(addy);
                                    RegisterFile.updateRegister(rt, loaded);
                                } catch(AddressErrorException e){
                                    throw new ProcessingException(statement, e);
                                }
                            }
                        }));
        //sw
        instructionList.add(
                new BasicInstruction("droptilted $t0,$t1,4",
                        "droptilted:  stores a word from rs into data memory address rb+offset",
                        BasicInstructionFormat.I_FORMAT,
                        "101011 ttttt fffff ssssssssssssssss",
                        new SimulationCode()
                        {
                            public void simulate(ProgramStatement statement) throws ProcessingException
                            {
                                int[] operands = statement.getOperands();
                                int rs = operands[0];
                                int rb = operands[1];
                                int offset = (short)operands[2];
                                int basead = RegisterFile.getValue(rb);
                                int address = basead + offset;
                                int va = RegisterFile.getValue(rs);
                                try {
                                    Memory.getInstance().setWord(address, va);
                                } catch (AddressErrorException e){
                                    throw new ProcessingException(statement, e);
                                }
                            }
                        }));
        //beq
        instructionList.add(
                new BasicInstruction("impulse $t0,$t1,label",
                        "impulse:  if t0 is equal to t1, branch to label instruction ",
                        BasicInstructionFormat.I_BRANCH_FORMAT,
                        "000100 fffff sssss tttttttttttttttt",
                        new SimulationCode()
                        {
                            public void simulate(ProgramStatement statement) throws ProcessingException
                            {
                                int[] operands = statement.getOperands();
                                int s1 = RegisterFile.getValue(operands[0]);
                                int s2 = RegisterFile.getValue(operands[1]);
                                //calculate new program counter
                                if(s1==s2){

                                    int offset = operands[2] << 16 >> 16;
                                    RegisterFile.setProgramCounter(statement.getAddress()+4 + (offset << 2));
                                }


                            }
                        }));
        //bne
        instructionList.add(
                new BasicInstruction("bootifscrub $t0,$t1,label",
                        "bootifscrub:  if t0 is not equal to t1, branch to label instruction ",
                        BasicInstructionFormat.I_BRANCH_FORMAT,
                        "000101 sssss ttttt ffffffffffffffff",
                        new SimulationCode()
                        {
                            public void simulate(ProgramStatement statement) throws ProcessingException
                            {
                                int[] operands = statement.getOperands();
                                int rs = RegisterFile.getValue(operands[0]);
                                int rt = RegisterFile.getValue(operands[1]);
                                //calculate new program counter
                                if(rs != rt){

                                    int offset = operands[2] << 16 >> 16;
                                    RegisterFile.setProgramCounter(statement.getAddress()+4 + (offset << 2));
                                }


                            }
                        }));
        //jump
        instructionList.add(
                new BasicInstruction("crank label",
                        "crank: jumps to instruction at memory address label ",
                        BasicInstructionFormat.J_FORMAT,
                        "000010 ffffffffffffffffffffffffff",
                        new SimulationCode()
                        {
                            public void simulate(ProgramStatement statement) throws ProcessingException
                            {
                                int[] operands = statement.getOperands();
                                int address = (operands[0] <<2) | (statement.getAddress()+4 & 0xF0000000);
                                RegisterFile.setProgramCounter(address);
                            }
                        }));
        //max
        instructionList.add(
                new BasicInstruction("pickupbettergun $t0,$t1,$t2",
                        "pickupbettergun:  compares t2 and t1 (gun rarity) and stores the larger value (better gun) in t0 (inventory)",
                        BasicInstructionFormat.R_FORMAT,
                        "000000 sssss ttttt fffff 00000 010000",
                        new SimulationCode()
                        {
                            public void simulate(ProgramStatement statement) throws ProcessingException
                            {
                                int[] operands = statement.getOperands();
                                int rs = RegisterFile.getValue(operands[1]);
                                int rt = RegisterFile.getValue(operands[2]);

                                int betterrarity = (rs >rt) ? rs : rt;
                                RegisterFile.updateRegister(operands[0], betterrarity);
                            }
                        }));
//swap registers
        instructionList.add(
                new BasicInstruction("swapinventory $t0,$t1",
                        "swapinventory: swaps the contents of t0 and t1",
                        BasicInstructionFormat.R_FORMAT,
                        "000000 sssss ttttt 00000 00000 110110",
                        new SimulationCode()
                        {
                            public void simulate(ProgramStatement statement) throws ProcessingException
                            {
                                int[] operands = statement.getOperands();
                                int rs = RegisterFile.getValue(operands[0]);
                                int rt = RegisterFile.getValue(operands[1]);


                                RegisterFile.updateRegister(operands[0], rt);
                                RegisterFile.updateRegister(operands[1], rs);


                            }
                        }));



//mul
        instructionList.add(
                new BasicInstruction("chugjug $t0,$t1,$t2",
                        "chugjug:multiples rs and rt (healing is proportional to shield) and stores the product in rd",
                        BasicInstructionFormat.R_FORMAT,
                        "000000 sssss ttttt fffff 00000 010110",
                        new SimulationCode()
                        {
                            public void simulate(ProgramStatement statement) throws ProcessingException
                            {
                                int[] operands = statement.getOperands();
                                int rs = RegisterFile.getValue(operands[1]);
                                int rt = RegisterFile.getValue(operands[2]);

                                long product = (long) rs *rt;
                                //check if higher 32 bits arent 0
                                if(product <Integer.MIN_VALUE || product > Integer.MAX_VALUE){
                                    throw new ProcessingException(statement, "overflowed 32 bit range", Exceptions.ARITHMETIC_OVERFLOW_EXCEPTION);
                                }
                                int result = (int) product;
                                RegisterFile.updateRegister(operands[0], result);


                            }
                        }));
        //mod
        instructionList.add(
                new BasicInstruction("squadsleft $t0,$t1,$t2",
                        "squadsleft:finds t1 mod t2 (random location of loot drop in circle) and stores remainder in t0",
                        BasicInstructionFormat.R_FORMAT,
                        "000000 sssss ttttt fffff 00000 010111",
                        new SimulationCode()
                        {
                            public void simulate(ProgramStatement statement) throws ProcessingException
                            {
                                int[] operands = statement.getOperands();
                                int rs = RegisterFile.getValue(operands[1]);
                                int rt = RegisterFile.getValue(operands[2]);

                                if(rt == 0){
                                    throw new ProcessingException(statement, "cant mod by 0");
                                }
                                int rem = rs %rt;

                                RegisterFile.updateRegister(operands[0], rem);


                            }
                        }));
        //random
        instructionList.add(
                new BasicInstruction("lootroll $t0,$t1",
                        "lootroll: generates random number between 0 and t1-1  and stores in t0",
                        BasicInstructionFormat.R_FORMAT,
                        "000000 sssss 00000 fffff 00000 011110",
                        new SimulationCode()
                        {
                            public void simulate(ProgramStatement statement) throws ProcessingException
                            {
                                int[] operands = statement.getOperands();
                                int max = RegisterFile.getValue(operands[1]);

                                if (max<=0){
                                    RegisterFile.updateRegister(operands[0], 0);
                                    return;
                                }
                                Random rand = new Random();
                                int rannum = rand.nextInt(max);
                                RegisterFile.updateRegister(operands[0], rannum);


                            }
                        }));
        //print integers
        instructionList.add(
                new BasicInstruction("location $t0",
                        "location: prints the value in register $t0",
                        BasicInstructionFormat.R_FORMAT,
                        "000000 00000 fffff 00000 00000 111100",
                        new SimulationCode()
                        {
                            public void simulate(ProgramStatement statement) throws ProcessingException
                            {
                                int[] operands = statement.getOperands();
                                int regnum = operands[0];
                                int addy = RegisterFile.getValue(regnum);
                                SystemIO.printString(  addy +  "\n");
                                if (addy >=10){
                                    SystemIO.printString("Victory Royale! \n");
                                } else{
                                    SystemIO.printString("Better luck next game! \n ");
                                }

                            }

                        }));

//load address
        instructionList.add(
                new BasicInstruction("toisland $t0,label",
                        "toisland: loads label's address into t0",
                        BasicInstructionFormat.I_FORMAT,
                        "001111 00000 fffff ssssssssssssssss",
                        new SimulationCode()
                        {
                            public void simulate(ProgramStatement statement) throws ProcessingException
                            {
                                int[] operands = statement.getOperands();
                                int stringat = operands[1];
                                RegisterFile.updateRegister(operands[0], stringat);



                            }
                        }));
        //print string
        instructionList.add(
                new BasicInstruction("placement $t0",
                        "placement: prints string at memory address $t0",
                        BasicInstructionFormat.R_FORMAT,
                        "000000 00000 fffff 00000 00000 111001",
                        new SimulationCode()
                        {
                            public void simulate(ProgramStatement statement) throws ProcessingException
                            {
                                int[] operands = statement.getOperands();
                                int addy = RegisterFile.getValue(operands[0]);
                                try{
                                    int mini = Memory.getInstance().getByte(addy);
                                    while (mini!=0){
                                        //SystemIO.printString(mini + "\n");
                                        addy++;
                                        mini = Memory.getInstance().getByte(addy);

                                    }
                                } catch(AddressErrorException e){
                                    throw new ProcessingException(statement,e);
                                }






                            }
                        }));






    }
}
