
package sqlpackage;

import java.util.ArrayList;

public class SQLCompiler {
    public static void main(String[] args) {
        String sqlFilePath = "SQL_FILE.sql";
        String sqlCode = SQLFileReader.readFile(sqlFilePath).toUpperCase();
        System.out.println(sqlCode);
        
        ArrayList<Token> tokens = Tokens.lex(sqlCode);
        
        System.out.println(tokens.toString());
        
    }   
}
