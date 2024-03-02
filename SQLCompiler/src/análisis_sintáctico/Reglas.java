/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package análisis_sintáctico;

import EDD.Cola;
import analizador_lexico.Token;
import analizador_lexico.Tokenizer;

/**
 *
 * @author utente
 */
public class Reglas {

    private Object[] parametros = new Object[6];

    //Parsing de la sentencia con el token Select ya consumido
    // Si es una regla secuencial, sumar al contador antes de buscar otro token
    public Object[] select() {
        if (!lista_columnas()) {
            return null;
        }
        //Si el siguiente token es diferente de FROM retornar null
        System.out.println(Sintáctico.tipo_actual());
        System.out.println(Sintáctico.token_actual());
        
        if (!Sintáctico.tipo_actual().equals("FROM")) {
            return null;
        }
        Sintáctico.indexColaTokens++;
        if (!lista_tablas()) {
            return null;
        }
        return parametros;
    }

    public boolean lista_columnas() {
        return columna();
    }

    public boolean lista_tablas() {
        return nombre_tabla();
    }

    // <expresión_aritmética>
    // TODO: Array para agregar más columnas... tal vez solo necesitamos recorrer Tokens en lugar de usar un Array
    //Nueva implementacion, ahora columnas puede recoger un arreglo de columnas
    public boolean columna() {
        //Array donde guardaremos las columnas que encontremos en la cola de tokens
        Cola<Token> colaColumnas = new Cola<>();
        boolean esperaColumna = true; //despues del select esperamos una columna
        String tipoToken; 
        
        int esOperacion;
        
        while (true) {
            //col1, col2... coln FROM
            //traemos el tipo del token actual
            tipoToken = Sintáctico.tipo_actual();
            
            if (tipoToken.equals(Tokenizer.ID) || tipoToken.equals(Tokenizer.ASTERISK) || tipoToken.equals(Tokenizer.NUMBER) || tipoToken.equals(Tokenizer.STRING) || tipoToken.equals(Tokenizer.OPEN_P)) {
                //Si es un token y no se esta esperando una columna
                if (!esperaColumna) {
                    System.out.println("Error gramatical, se esperaba :" + Tokenizer.COMMA);
                    return false;
                }
               
                esOperacion = operacion_aritmetica();              
                if (esOperacion != 0) {
                    
                    // DEBUG
                    System.out.println(esOperacion);
                    
                    for (int i = Sintáctico.indexColaTokens ; i < esOperacion ; i ++) {
                        Token to = (Token) Sintáctico.colaTokens.buscar_por_orden(i);
                        
                        // DEBUG
                        System.out.println(to.getTokenValor());
                        
                        to.setTipo("ARIT");
                        colaColumnas.agregar(to);
                    }        
                    Sintáctico.indexColaTokens = esOperacion;
                    
                    // DEBUG
                    System.out.println("Indice después de OA: " + Sintáctico.indexColaTokens);
                    
                    esperaColumna = false;
                    continue;
                }             
                
                //Si se se esperaba una columna agregar el token ya sea ID, * , number o string
                colaColumnas.agregar(Sintáctico.token_actual());
                //Avanzamos al siguiente token
                Sintáctico.indexColaTokens++;
                //Actualizamos el esperaColumna
                esperaColumna = false;

            } else if (tipoToken.equals(Tokenizer.COMMA)) {
                //Si el tipotoken es comma pero se esperaba una columna
                if (esperaColumna) {
                    System.out.println("Error de sintaxis: Se esperaba columna" + Tokenizer.ID);
                    return false;
                }
                //Si no es esperaba una columna estamos aptos para recibir al coma
                //Avanzamos al siguiente token
                Sintáctico.indexColaTokens++;
                //Actualizamos el esperaColumna
                esperaColumna = true;
            } else if (tipoToken.equals(Tokenizer.FROM)) {
                
                if (esperaColumna) {
                    System.out.println("Error de sintaxis: Se esperaba" + Tokenizer.ID);
                    return false;
                }
                // DEBUG
                colaColumnas.imprimirCola();
                
               //Hemos llegado al final del reconocimiento lista columnas
                parametros[0] = colaColumnas;
                
                //Analisis gramatical de lista columnas completado (faltaria reconocer expresiones aritmeticas)
                //actualmente solo se reconoce columnas ID y se guardan en una colaDeColumnas
                return true;
            }
            else{
                System.out.println("Error de sintaxis: Se esperaba " + Tokenizer.FROM);
                return false;
            }           
            
        }

//        if (!Sintáctico.tipo_actual().equals("ID")) {
//            if (!Sintáctico.tipo_actual().equals("*")) return false;           
//            parametros[0] = Sintáctico.valor_actual();//agregar el asterisco como primer parametro
//            return true;
//        }
//        parametros[0] = Sintáctico.valor_actual();//agregar el identificador como segundo parametro    
    }

    public int operacion_aritmetica() {
        int indiceCopia = Sintáctico.indexColaTokens;
        String tipoActual;
        
        boolean tieneOperador = false;
        boolean esperaOperando = true;
        int esperaCierre = 0;
        
        while (true) {
            
            Token to = (Token) Sintáctico.colaTokens.buscar_por_orden(indiceCopia);
            tipoActual = to.getTipo();
            
            if (tipoActual.equals(Tokenizer.ID) || tipoActual.equals(Tokenizer.STRING) || tipoActual.equals(Tokenizer.NUMBER)) {
                if (!esperaOperando) {
                    System.out.println("Error de sintaxis: Se esperaba OPERADOR");
                    return 0;
                }
                indiceCopia++;
                esperaOperando = false;
                
            } else if (tipoActual.equals(Tokenizer.PLUS) || tipoActual.equals(Tokenizer.MINUS) || tipoActual.equals(Tokenizer.ASTERISK) || tipoActual.equals(Tokenizer.DIV)) {
                if (esperaOperando) {
                    System.out.println("Error de sintaxis: Se esperaba OPERANDO");
                    return 0;
                }
                indiceCopia++;
                esperaOperando = true;
                tieneOperador = true;
                
            } else if (tipoActual.equals(Tokenizer.OPEN_P)) {
                indiceCopia++;
                esperaOperando = true;
                esperaCierre++;
                
            } else if (tipoActual.equals(Tokenizer.CLOSE_P)) {
                if (esperaCierre == 0) {
                    System.out.println("Error de sintaxis: Se cerró paréntesis innecesariamente");
                    return 0;
                }
                indiceCopia++;
                esperaOperando = false;
                esperaCierre--;
               
            } else if ((tipoActual.equals(Tokenizer.COMMA) || tipoActual.equals(Tokenizer.FROM)) && tieneOperador == true) {
                if (esperaCierre != 0) {
                    System.out.println("Error de sintaxis: Hay un paréntesis no cerrado");
                    return 0;
                }
                return indiceCopia;
            }
            else {
                System.out.println("No es expresión");
                return 0;
            }
        }
 
    }
    
    
    public boolean nombre_tabla() {
        if (!Sintáctico.tipo_actual().equals("ID")) {
            return false;
        }
        parametros[1] = Sintáctico.valor_actual();
        return true;
    }

}
