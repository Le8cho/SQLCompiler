
package sqlpackage;

public class SQLCompiler {
    public static void main(String[] args) {
        String sqlFilePath = "SQL_FILE.sql";
        String sqlCode = SQLFileReader.readFile(sqlFilePath);
        System.out.println(sqlCode);
        
    }   
}
