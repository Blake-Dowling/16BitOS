import java.io.IOException;
import java.io.PrintWriter;

/*
 * Author: Blake Dowling
 * Project: Virtual Machine I
 * CodeWriter.java - Translates commands from .vm language to .asm language, and writes them
 * to .asm file.
 */
public class CodeWriter {
    private PrintWriter outputFile;
    private String asmFileName;
    private int count;

    // Add .asm translation.
    private final String ADD =
            "@SP\n" +
                    "AM=M-1\n" +
                    "D=M\n" +
                    "A=A-1\n" +
                    "M=D+M\n";
    // Sub .asm translation.
    private final String SUB =
            "@SP\n" +
                    "AM=M-1\n" +
                    "D=M\n" +
                    "A=A-1\n" +
                    "M=M-D\n";
    // Neg .asm translation.
    private final String NEG =
            "@SP\n" +
                    "A=M-1\n" +
                    "M=-M\n";
    // And .asm translation.
    private final String AND =
            "@SP\n" +
                    "AM=M-1\n" +
                    "D=M\n" +
                    "A=A-1\n" +
                    "M=D&M\n";
    // Or .asm translation.
    private final String OR =
            "@SP\n" +
                    "AM=M-1\n" +
                    "D=M\n" +
                    "A=A-1\n" +
                    "M=D|M\n";
    // Not .asm translation.
    private final String NOT =
            "@SP\n" +
                    "A=M-1\n" +
                    "M=!M\n";
    // Push .asm translation.
    private final String PUSH =
            "@SP\n" +
                    "A=M\n" +
                    "M=D\n" +
                    "@SP\n" +
                    "M=M+1\n";

    // Pop .asm translation.
    private final String POP =
            "@R13\n" +
                    "M=D\n" +
                    "@SP\n" +
                    "AM=M-1\n" +
                    "D=M\n" +
                    "@R13\n" +
                    "A=M\n" +
                    "M=D\n";

    /*
     * Description: Initializes CodeWriter object by initializing PrintWriter with output file name.
     * Precondition: File Name contains .asm extension.
     * Postcondition: CodeWriter object is able to create an .asm file on which to write.
     */
    public CodeWriter(String fileName) {
        asmFileName = fileName;
        count = 0;
        try {
            outputFile = new PrintWriter(fileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*
     * Description: Writes .asm translations of arithmetic commands to .asm file.
     * Precondition: FileWriter is not null.
     * Postcondition: .vm language line is written into .asm commands representing arithmetic operation.
     */
    public void writeArithmetic(String command) {
        switch (command) { //Type of arithmetic command.
            case "add": {
                outputFile.write(ADD);
                break;
            }
            case "sub": {
                outputFile.write(SUB);
                break;
            }
            case "neg": {
                outputFile.write(NEG);
                break;
            }
            case "eq": {
                count++;
                outputFile.write(
                        "@SP\n" +
                                "AM=M-1\n" +
                                "D=M\n" +
                                "A=A-1\n" +
                                "D=M-D\n" +
                                "@EQ.true." + count + "\n" +
                                "D;JEQ\n" +
                                "@SP\n" +
                                "A=M-1\n" +
                                "M=0\n" +
                                "@EQ.after." + count + "\n" +
                                "0;JMP\n" +
                                "(EQ.true." + count + ")\n" +
                                "@SP\n" +
                                "A=M-1\n" +
                                "M=-1\n" +
                                "(EQ.after." + count + ")\n");
                break;

            }
            case "gt": {
                count++;
                outputFile.write(
                        "@SP\n" +
                                "AM=M-1\n" +
                                "D=M\n" +
                                "A=A-1\n" +
                                "D=M-D\n" +
                                "@GT.true." + count + "\n" +
                                "\nD;JGT\n" +
                                "@SP\n" +
                                "A=M-1\n" +
                                "M=0\n" +
                                "@GT.after." + count + "\n" +
                                "0;JMP\n" +
                                "(GT.true." + count + ")\n" +
                                "@SP\n" +
                                "A=M-1\n" +
                                "M=-1\n" +
                                "(GT.after." + count + ")\n");
                break;
            }
            case "lt": {
                count++;
                outputFile.write(
                        "@SP\n" +
                                "AM=M-1\n" +
                                "D=M\n" +
                                "A=A-1\n" +
                                "D=M-D\n" +
                                "@LT.true." + count + "\n" +
                                "D;JLT\n" +
                                "@SP\n" +
                                "A=M-1\n" +
                                "M=0\n" +
                                "@LT.after." + count + "\n" +
                                "0;JMP\n" +
                                "(LT.true." + count + ")\n" +
                                "@SP\n" +
                                "A=M-1\n" +
                                "M=-1\n" +
                                "(LT.after." + count + ")\n");
                break;
            }
            case "and": {
                outputFile.write(AND);
                break;
            }
            case "or": {
                outputFile.write(OR);
                break;
            }
            case "not": {
                outputFile.write(NOT);
                break;
            }
        }
    }

    /*
     * Description: Writes .asm translations of push or pop commands to .asm file.
     * Precondition: FileWriter is not null.
     * Postcondition: .vm language line is written into .asm commands representing push or pop operation.
     */
    public void writePushPop(CommandType commandType, String segment, int index) {
        switch (commandType) { // Push or Pop
            case C_PUSH: {
                switch (segment) { //If push, segment is where pushing from.
                    case "local": {
                        outputFile.write(
                                "@LCL\n" +
                                        "D=M\n" +
                                        "@" + index + "\n" +
                                        "A=D+A\n" +
                                        "D=M\n" +
                                        PUSH);
                        break;
                    }
                    case "argument": {
                        outputFile.write(
                                "@ARG\n" +
                                        "D=M\n" +
                                        "@" + index + "\n" +
                                        "A=D+A\n" +
                                        "D=M\n" +
                                        PUSH);
                        break;
                    }
                    case "this": {
                        outputFile.write(
                                "@THIS\n" +
                                        "D=M\n" +
                                        "@" + index + "\n" +
                                        "A=D+A\n" +
                                        "D=M\n" +
                                        PUSH);
                        break;
                    }
                    case "that": {
                        outputFile.write(
                                "@THAT\n" +
                                        "D=M\n" +
                                        "@" + index + "\n" +
                                        "A=D+A\n" +
                                        "D=M\n" +
                                        PUSH);
                        break;
                    }
                    case "pointer": {
                        if (index == 0) {
                            outputFile.write(
                                    "@THIS\n" +
                                            "D=M\n" +
                                            PUSH);
                        } else {
                            outputFile.write(
                                    "@THAT\n" +
                                            "D=M\n" +
                                            PUSH);
                        }
                        break;
                    }
                    case "constant": {
                        outputFile.write("@" + index + "\n" + "D=A\n" + PUSH);
                        break;
                    }
                    case "static": {
                        outputFile.write(
                                "@" + asmFileName + "." + index + "\n" +
                                        "D=M\n" +
                                        PUSH);
                        break;
                    }
                    case "temp": {
                        outputFile.write(
                                "@R5\n" +
                                        "D=A\n" +
                                        "@" + index + "\n" +
                                        "A=D+A\n" +
                                        "D=M\n" +
                                        PUSH);
                        break;
                    }
                }
                break;
            }
            case C_POP: { //If pop, segment is where popping to.
                switch (segment) {
                    case "local": {
                        outputFile.write(
                                "@LCL\n" +
                                        "D=M\n" +
                                        "@" + index + "\n" +
                                        "D=D+A\n" +
                                        POP);
                        break;
                    }
                    case "argument": {
                        outputFile.write(
                                "@ARG\n" +
                                        "D=M\n" +
                                        "@" + index + "\n" +
                                        "D=D+A\n" +
                                        POP);
                        break;
                    }
                    case "this": {
                        outputFile.write(
                                "@THIS\n" +
                                        "D=M\n" +
                                        "@" + index + "\n" +
                                        "D=D+A\n" +
                                        POP);
                        break;
                    }
                    case "that": {
                        outputFile.write(
                                "@THAT\n" +
                                        "D=M\n" +
                                        "@" + index + "\n" +
                                        "D=D+A\n" +
                                        POP);
                        break;
                    }
                    case "pointer": {
                        if (index == 0) {
                            outputFile.write(
                                    "@THIS\n" +
                                            "D=A\n" +
                                            POP);
                            break;
                        } else {
                            outputFile.write(
                                    "@THAT\n" +
                                            "D=A\n" +
                                            POP);
                            break;
                        }
                    }
                    case "static": {
                        outputFile.write(
                                "@" + asmFileName + "." + index + "\n" +
                                        "D=A\n" +
                                        POP);
                        break;
                    }
                    case "temp": {
                        outputFile.write(
                                "@R5\n" +
                                        "D=A\n" +
                                        "@" + index + "\n" +
                                        "D=D+A\n" +
                                        POP);
                        break;
                    }
                }
                break;
            }
        }
    }

    /*
     * Description: method allowing VMTranslator class to close FileWriter.
     * Precondition: outputfile is open.
     * Postcondition: outputFile is closed.
     */
    public void close() {
        outputFile.close();
    }

    public void writeLabel(String label) {
        outputFile.write("(" + label + ")\n");
    }
}
