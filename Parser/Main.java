import java.io.*;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws FileNotFoundException {
        String inFile;
        Lexer lexer;
        parser parser;
        for (int i = 1; i <= 9; i++) {
            inFile = "F:\\uni\\Sem 5\\Compiler\\Parser\\src\\test\\t0" + i + ".in";
            lexer = new Lexer(new FileReader(inFile));
            parser = new parser(lexer);
            try {
                parser.parse();
                System.out.println(i + " Ok");
            } catch (Exception e) {
                System.out.println(i + " Syntax Error");
            }
        }

        for (int i = 0; i <= 2; i++) {
            inFile = "F:\\uni\\Sem 5\\Compiler\\Parser\\src\\test\\t1" + i + ".in";
            lexer = new Lexer(new FileReader(inFile));
            parser = new parser(lexer);
            try {
                parser.parse();
                System.out.println("1" + i + " Ok");
            } catch (Exception e) {
                System.out.println("1" + i + " Syntax Error");
            }
        }
    }
}