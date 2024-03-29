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
public class Inst_Select {

    private String tipo;
    private Cola <Token> paramCola = null;
    private String paramToken = null;
    
    public Inst_Select(Cola<Token> cola) {      
        this.tipo = determinarTipo(cola);
        
        if (tipo.equals("ID") || tipo.equals("*") || tipo.equals("STRING") || tipo.equals("NUMBER")){
            this.paramToken = cola.buscar_por_orden(0).getTokenValor();
        }
        else {
            this.paramCola = cola;
        }
    }    

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public Cola<Token> getParamCola() {
        return paramCola;
    }

    public void setParamCola(Cola<Token> paramCola) {
        this.paramCola = paramCola;
    }

    public String getParamToken() {
        return paramToken;
    }

    public void setParamToken(String paramToken) {
        this.paramToken = paramToken;
    }

    public final String determinarTipo(Cola<Token> cola) {
        
        if (cola.getSize() == 1) {
            if (cola.buscar_por_orden(0).getTipo().equals("ID")) {
                return "ID";
            }
            if (cola.buscar_por_orden(0).getTipo().equals("STRING")) {
                return  "STRING";
            }
            if (cola.buscar_por_orden(0).getTipo().equals("*")) {
                return "*";
            }  
            if (cola.buscar_por_orden(0).getTipo().equals("NUMBER")) {
                return "NUMBER";
            } 
        }
        
        else {
            String tipoActual;
            
            for (int i = 0 ; i < cola.getSize() ; i++) {
                
                tipoActual = cola.buscar_por_orden(i).getTipo();
                
                if (tipoActual.equals("+") || tipoActual.equals("-") || tipoActual.equals("*") || tipoActual.equals("/")) {
                    return "OPERACION";                   
                }   
            }   
        } 
        
        // Si es cola y no encuentra operador
        return "FUNCION";
    }
    
    
}
