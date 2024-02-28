package analizador_lexico;

import EDD.Cola;
import java.util.ArrayList;

public class SQLCompiler {

    public static void main(String[] args) {

        String sqlFilePath = "SQL_FILE.sql";
        String sqlCode = SQLFileReader.readFile(sqlFilePath).toUpperCase();
        System.out.println(sqlCode);

        Cola<Token> tokenList = Tokens.lex(sqlCode);

        tokenList.imprimirCola();

    }
}
