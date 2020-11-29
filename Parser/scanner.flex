import java_cup.runtime.*;

%%
%public
%class Lexer
%unicode
%cup
%type Symbol
%char

%{
    StringBuilder string;
//    private String addPlus(String str)
//    {
//        int i = str.indexOf('e');
//        return str.substring(0, i + 1) + "+" + str.substring(i + 1);
//    }

    private Symbol token(int tokenType) {
        return new Symbol(tokenType);
    }

    private Symbol token(int tokenType, Object value) {
        return new Symbol(tokenType, value);
    }
%}

Alphabet = [A-Za-z]
Digit = [0-9]

DecInt = {Digit}+
HexAlphabet = [A-Fa-f]
HexInt = 0 ("x" | "X") ({Digit} | {HexAlphabet})+
SciDouble = {Double}("e" | "E")("-" | "+"){DecInt}
SciDouble2 = {Double}("e" | "E"){DecInt}
Double = {DecInt}[.]{DecInt}?

ID = ({Alphabet}) ({Alphabet} | {Digit} | _)*



LineTerminator = \r|\n|\r\n
InputCharacter = [^\r\n]
WhiteSpace = {LineTerminator} | [ \t\f]

/* comments */
EndOfLineComment = "//" [^\r\n]*
TraditionalComment = "/*" ~ "*/"
Comment = {EndOfLineComment} | {TraditionalComment}


%state STRING

%%

<YYINITIAL>{
    // Reserved keywors
    "void"              { return token(sym.VOID); }
    "int"               { return token(sym.INT); }
    "double"            { return token(sym.DOUBLE); }
    "bool"              { return token(sym.BOOL); }
    "string"            { return token(sym.STRING); }
    "class"             { return token(sym.CLASS); }
    "interface"         { return token(sym.INTERFACE); }
    "null"              { return token(sym.NULL); }
    "this"              { return token(sym.THIS); }
    "extends"           { return token(sym.EXTENDS); }
    "implements"        { return token(sym.IMPLEMENTS); }
    "for"               { return token(sym.FOR); }
    "while"             { return token(sym.WHILE); }
    "if"                { return token(sym.IF); }
    "else"              { return token(sym.ELSE); }
    "return"            { return token(sym.RETURN); }
    "break"             { return token(sym.BREAK); }
    "continue"          { return token(sym.CONTINUE); }
    "new"               { return token(sym.NEW); }
    "NewArray"          { return token(sym.NEWARRAY); }
    "Print"             { return token(sym.PRINT); }
    "ReadInteger"       { return token(sym.READINTEGER); }
    "ReadLine"          { return token(sym.READLINE); }
    "dtoi"              { return token(sym.DTOI); }
    "itod"              { return token(sym.ITOD); }
    "btoi"              { return token(sym.BTOI); }
    "itob"              { return token(sym.ITOB); }
    "private"           { return token(sym.PRIVATE); }
    "protected"         { return token(sym.PROTECTED); }
    "public"            { return token(sym.PUBLIC); }
    "true"              { return token(sym.TRUE); }
    "false"             { return token(sym.FALSE); }

    // Identifier
    {ID}                { return token(sym.IDENT); }

    // Decimal Integer
    {DecInt}            { return token(sym.INTLITERAL); }

    // Hexadecimal
    {HexInt}            { return token(sym.INTLITERAL);}

    // Double
    {Double}            { return token(sym.DOUBLELITERAL); }

    // Scientific Double
    {SciDouble}         { return token(sym.DOUBLELITERAL); }
    {SciDouble2}        { return token(sym.DOUBLELITERAL); }

    // Strings
    \"                  { string = new StringBuilder("\""); yybegin(STRING); }

    // Punctuations
    "+"					{ return token(sym.PLUS); }
    "-"					{ return token(sym.MINUS); }
    "*"					{ return token(sym.PROD); }
    "/"					{ return token(sym.DIV); }
    "%"					{ return token(sym.MOD); }
    "<"					{ return token(sym.LT); }
    "<="				{ return token(sym.LE); }
    ">"					{ return token(sym.GT); }
    ">="				{ return token(sym.GE); }
    "="					{ return token(sym.ASSIGN); }
    "=="				{ return token(sym.EQ); }
    "!="				{ return token(sym.NE); }
    "&&"			    { return token(sym.AND); }
    "||"				{ return token(sym.OR); }
    "!"					{ return token(sym.NOT); }
    ";"					{ return token(sym.SEMICOLON); }
    ","					{ return token(sym.COMMA); }
    "."					{ return token(sym.DOT); }
    "["					{ return token(sym.LBRACK); }
    "]"					{ return token(sym.RBRACK); }
    "("					{ return token(sym.LPAREN); }
    ")"					{ return token(sym.RPAREN); }
    "{"					{ return token(sym.LCURLY); }
    "}"					{ return token(sym.RCURLY); }


    {Comment}           { }

    {WhiteSpace}        { }

    //EOF                 { return token(sym.EOF , ""); }

    //error
    //[^]                 { return token(sym.ERROR , "ERROR");}
}

<STRING> {
    \" {
        string.append("\"");
	    yybegin(YYINITIAL);
	    return token(sym.STRINGLITERAL, string);
    }

    [^\n\r\"\\]*    { string.append(yytext()); }
    \\t             { string.append('\t'); }
    \\              { string.append('\\'); }
    \\\'            { string.append('\''); }
    \\\"            { string.append('\"'); }
    \\b             { string.append('\b'); }
    \\f             { string.append('\f'); }
    \\0             { string.append('\0'); }

    //error
    //[^]             { return token(sym.ERROR , "STRING NOT FINISHED");}
}