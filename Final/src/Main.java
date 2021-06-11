import Model.*;
import Model.cg.CodeGen;

import java.io.*;

public class Main {
//    public static void main(String[] args) throws Exception {
//        FileReader fr = new FileReader( new File("F:\\uni\\Sem 5\\Compiler\\FinalProj\\src2\\input.txt") );
//        Lexer lexer = new Lexer(fr);
//        parser parser = new parser(lexer);
//        /*try{
//            parser.parse();
//            CodeGen.compile();
//        } catch (Exception e) {
//            System.out.println("Semantic Error");
//        }*/
//        parser.parse();
//        CodeGen.compile();
//
//    }

    public static Writer writer = null;
    public static void main(String[] args) throws IOException {
        try {
            String inputFile = null;
            String outputFile = null;
            if (args != null) {
                for (int i = 0; i < args.length; i++) {
                    if (args[i].equals("-i")) {
                        inputFile = args[i + 1];
                    }
                    if (args[i].equals("-o")) {
                        outputFile = args[i + 1];
                    }
                }
            }
            FileReader read = null;
            if (inputFile != null) {
                read = new FileReader(inputFile);
            }

            if (outputFile != null) {
                writer = new FileWriter(outputFile);
            }

            // todo
            Lexer lexer = new Lexer(read);
            parser parser = new parser(lexer);

            parser.parse();
            CodeGen.compile(writer);

            writer.flush();
            writer.close();
        } catch (Exception e) {
            //e.printStackTrace();
            String outputFile = null;
            if (args != null) {
                for (int i = 0; i < args.length; i++) {
                    if (args[i].equals("-o")) {
                        outputFile = args[i + 1];
                    }
                }
            }
            if (outputFile != null) {
                writer = new FileWriter("out/" + outputFile);
            } else {
                writer = new OutputStreamWriter(System.out);
            }

            writer.write("Semantic Error");
            writer.flush();
            writer.close();
        }
    }
}