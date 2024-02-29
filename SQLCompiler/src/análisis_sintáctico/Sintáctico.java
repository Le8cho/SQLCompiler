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
    
    public static int contador_cola = 0;
    public static Cola cola;   
    
    public Sintáctico(Cola cola) {
        //error corregido, cola es un atributo estatico por lo tanto se referencia a la clase no a la instancia del objeto
        Sintáctico.cola=cola;
    }
    
    public static Token token_actual() {       
        Token token = (Token) cola.buscar_por_orden(contador_cola);             
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
        
        contador_cola = 0;

        Reglas re = new Reglas();
        
        //Si la cola de tokens está vacío no hay nada que analizar
        if (cola.estaVacio()) {
            return null;
        }
        
        //Verificamos que se trate de una setencia SELECT verificando el primer token
        if(!tipo_actual().equals("SELECT")){
            return null;
        }
        
        //Avanzamos al siguiente token una vez reconocido el SELECT
        contador_cola++;
        
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
