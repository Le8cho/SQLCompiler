package hola;

import EDD.Cola;
import analizador_lexico.Token;
import analizador_lexico.Tokenizer;

public class pruebasLexer {
    public static void main(String[] args) {
        String sql = "Select hola, tula, rodriguez from tablaEstoESGuerra Where (codigo>5) AnD (codigo<5) Or (apellido='Pacotaype')";
        Cola<Token> resultado = Tokenizer.lex(sql.toUpperCase());
        resultado.imprimirCola();
    }
}
