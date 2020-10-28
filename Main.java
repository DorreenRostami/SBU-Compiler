package compiler;

import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

public class Main
{
    public static void main(String[] args) throws IOException
    {
        //String inputFile = new Scanner(System.in).next();
        //Lexer lexer = new Lexer(new FileReader(inputFile));
        Lexer lexer = new Lexer(new FileReader("F:\\uni\\Sem 5\\Compiler\\Scanner\\testsANDscripts\\java\\tests\\t12-simple2.in"));
        Symbol symbol;
        boolean noError = true;
        while (noError)
        {
            symbol = lexer.scanToken();
            if(symbol == null)
                break;
            switch (symbol.token)
            {
                case INTLITERAL:
                case ID:
                case STRINGLITERAL:
                    System.out.println("T_" + symbol.token + " " + symbol.content);
                    break;
                case SCIDOUBLELITERAL:
                case DOUBLELITERAL:
                    System.out.println("T_DOUBLELITERAL " + symbol.content);
                    break;
                case TRUE:
                case FALSE:
                    System.out.println("T_BOOLEANLITERAL " + symbol.content);
                    break;
                case ERROR:
                    System.out.println(symbol.content);
                    noError = false;
                    break;
                default:
                    System.out.println(symbol.content);
                    break;
            }
        }

    }
}
