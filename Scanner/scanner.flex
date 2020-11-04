%%
%public
%class Lexer
%unicode
%type Symbol
%function scanToken

%{
    StringBuilder string;
    private String addPlus(String str)
    {
        int i = str.indexOf('e');
        return str.substring(0, i + 1) + "+" + str.substring(i + 1);
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
    "void"              { return new Symbol(Token.VOID , yytext()); }
    "int"               { return new Symbol(Token.INT , yytext()); }
    "double"            { return new Symbol(Token.DOUBLE , yytext()); }
    "bool"              { return new Symbol(Token.BOOL , yytext()); }
    "string"            { return new Symbol(Token.STRING , yytext()); }
    "class"             { return new Symbol(Token.CLASS , yytext()); }
    "interface"         { return new Symbol(Token.INTERFACE , yytext()); }
    "null"              { return new Symbol(Token.NULL , yytext()); }
    "this"              { return new Symbol(Token.THIS , yytext()); }
    "extends"           { return new Symbol(Token.EXTENDS , yytext()); }
    "implements"        { return new Symbol(Token.IMPLEMENTS , yytext()); }
    "for"               { return new Symbol(Token.FOR , yytext()); }
    "while"             { return new Symbol(Token.WHILE , yytext()); }
    "if"                { return new Symbol(Token.IF , yytext()); }
    "else"              { return new Symbol(Token.ELSE , yytext()); }
    "return"            { return new Symbol(Token.RETURN , yytext()); }
    "break"             { return new Symbol(Token.BREAK , yytext()); }
    "continue"          { return new Symbol(Token.CONTINUE , yytext()); }
    "new"               { return new Symbol(Token.NEW , yytext()); }
    "NewArray"          { return new Symbol(Token.NEWARRAY , yytext()); }
    "Print"             { return new Symbol(Token.PRINT , yytext()); }
    "ReadInteger"       { return new Symbol(Token.READINTEGER , yytext()); }
    "ReadLine"          { return new Symbol(Token.READLINE , yytext()); }
    "dtoi"              { return new Symbol(Token.DTOI , yytext()); }
    "itod"              { return new Symbol(Token.ITOD , yytext()); }
    "btoi"              { return new Symbol(Token.BTOI , yytext()); }
    "itob"              { return new Symbol(Token.ITOB , yytext()); }
    "private"           { return new Symbol(Token.PRIVATE , yytext()); }
    "protected"         { return new Symbol(Token.PROTECTED , yytext()); }
    "public"            { return new Symbol(Token.PUBLIC , yytext()); }
    "true"              { return new Symbol(Token.TRUE , yytext()); }
    "false"             { return new Symbol(Token.FALSE , yytext()); }


    // Identifier
    {ID}                { return new Symbol(Token.ID, yytext()); }

    // Decimal Integer
    {DecInt}            { return new Symbol(Token.INTLITERAL, yytext()); }

    // Hexadecimal
    {HexInt}            { return new Symbol(Token.INTLITERAL, yytext());}

    // Double
    {Double}            { return new Symbol(Token.DOUBLELITERAL , yytext()); }

    // Scientific Double
    {SciDouble}         { return new Symbol(Token.SCIDOUBLELITERAL , yytext()); }
    {SciDouble2}         { return new Symbol(Token.SCIDOUBLELITERAL , addPlus(yytext())); }

    // Strings
    \"                  { string = new StringBuilder("\""); yybegin(STRING); }

    // Punctuations
    "+"					{ return new Symbol(Token.ADD , yytext()); }
    "-"					{ return new Symbol(Token.MINUS , yytext()); }
    "*"					{ return new Symbol(Token.PROD , yytext()); }
    "/"					{ return new Symbol(Token.DIV , yytext()); }
    "%"					{ return new Symbol(Token.MOD , yytext()); }
    "<"					{ return new Symbol(Token.LESS , yytext()); }
    "<="				{ return new Symbol(Token.LESSEQ , yytext()); }
    ">"					{ return new Symbol(Token.GR , yytext()); }
    ">="				{ return new Symbol(Token.GREQ , yytext()); }
    "="					{ return new Symbol(Token.ASSIGN , yytext()); }
    "=="				{ return new Symbol(Token.EQ , yytext()); }
    "!="				{ return new Symbol(Token.NOTEQ , yytext()); }
    "&&"			    { return new Symbol(Token.AND , yytext()); }
    "||"				{ return new Symbol(Token.OR , yytext()); }
    "!"					{ return new Symbol(Token.NOT , yytext()); }
    ";"					{ return new Symbol(Token.SEMICOLON , yytext()); }
    ","					{ return new Symbol(Token.COMMA , yytext()); }
    "."					{ return new Symbol(Token.DOT , yytext()); }
    "["					{ return new Symbol(Token.LBRACK , yytext()); }
    "]"					{ return new Symbol(Token.RBRACK , yytext()); }
    "("					{ return new Symbol(Token.LPAREN , yytext()); }
    ")"					{ return new Symbol(Token.RPAREN , yytext()); }
    "{"					{ return new Symbol(Token.LCURLY , yytext()); }
    "}"					{ return new Symbol(Token.RCURLY , yytext()); }


    {Comment}           { }

    {WhiteSpace}        { }

    EOF                 { return new Symbol(Token.EOF , ""); }

    //error
    [^]                 { return new Symbol(Token.ERROR , "ERROR");}
}

<STRING> {
    \" {
        string.append("\"");
	    yybegin(YYINITIAL);
	    return new Symbol(Token.STRINGLITERAL, string);
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
    [^]             { return new Symbol(Token.ERROR , "STRING NOT FINISHED");}
}
