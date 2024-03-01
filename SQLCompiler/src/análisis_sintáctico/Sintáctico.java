/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package análisis_sintáctico;

import EDD.Cola;
import analizador_lexico.Token;

/**
 *
 * @author utente
 */
public class Sintáctico {
    
    public static int indexColaTokens = 0;
    public static Cola colaTokens;   
    
    public Sintáctico(Cola colaTokens) {
        Sintáctico.colaTokens=colaTokens;
    }
    
    public static Token token_actual() {       
        Token token = (Token) colaTokens.buscar_por_orden(indexColaTokens);             
        return token;     
    }
    
    public static String valor_actual () {        
        Token token = token_actual();             
        return token.getTokenValor();
    }
    
    public static String tipo_actual () {
        Token token = token_actual();              
        return token.getTipo();
    }
    
    //TODO: Excepción diciendo que no cumple la gramática cuando el array es nulo
    public Object[] parser() {
        
        indexColaTokens = 0;

        Reglas re = new Reglas();
        
        //Si la cola de tokens está vacío no hay nada que analizar
        if (colaTokens.estaVacio()) {
            return null;
        }
        
        //Verificamos que se trate de una setencia SELECT verificando el primer token
        if(!tipo_actual().equals("SELECT")){
            System.out.println("No es una sentencia SELECT SQL");
            return null;
        }
        
        //Avanzamos al siguiente token una vez reconocido el SELECT
        indexColaTokens++;
        
        //Retornamos los parametros de la sentencia select: columnas, tabla...
        return re.select();
    }
    
    public int contar_elementos(Object[] parametros) {
        int contador = 0;
        
        for (int i = 0 ; i < parametros.length ; i++) {
            if (parametros[i]!=null) {
                contador++;
            }
        }
                
        return contador;
    }
    
}
