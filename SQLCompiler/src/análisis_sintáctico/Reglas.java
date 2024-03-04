/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package análisis_sintáctico;

import EDD.Cola;
import analizador_lexico.Token;
import analizador_lexico.Tokenizer;
import javax.swing.JOptionPane;

/**
 *
 * @author utente
 */
public class Reglas {

    private final Lista_Instrucciones instruccionesFinal = new Lista_Instrucciones();

    //Parsing de la sentencia con el token Select ya consumido
    // Si es una regla secuencial, sumar al contador antes de buscar otro token
    public Lista_Instrucciones select() {
        if (!lista_columnas()) {
            return null;
        }

        //Si este token es diferente de FROM retornar null
        System.out.println(Sintáctico.tipo_actual());
        System.out.println(Sintáctico.token_actual());
        
        if (!Sintáctico.tipo_actual().equals("FROM")) {
            return null;
        }
        Sintáctico.indexColaTokens++;
        if (!lista_tablas()) {
            return null;
        }
        if (!where()) {
            //si no halla where que retorne los parametros que se han reunido hasta ahora
            return instruccionesFinal;
        }
        
        //TODO: IMPLEMENTAR ORDER BY
        
        return instruccionesFinal;
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

        while (true) {
            //col1, col2... coln FROM
            //traemos el tipo del token actual
     
            tipoToken = Sintáctico.tipo_actual();
            
            if (operacion_aritmetica()) {
                Sintáctico.indexColaTokens++;
                esperaColumna = false;
    
            } else if (tipoToken.equals(Tokenizer.ID) || tipoToken.equals(Tokenizer.ASTERISK) || tipoToken.equals(Tokenizer.NUMBER) || tipoToken.equals(Tokenizer.STRING) || tipoToken.equals(Tokenizer.OPEN_P)) {

                //Si es un token y no se esta esperando una columna
                if (!esperaColumna) {
                    System.out.println("Error gramatical, se esperaba :" + Tokenizer.COMMA);
                    return false;
                }
                                          
                //Si se se esperaba una columna agregar el token ya sea ID, * , number o string
                //Se aregar y vacía la cola porque el constructor de Inst_Select detecta colas pero un token a la vez (si no es expresión aritmética)
                colaColumnas.agregar(Sintáctico.token_actual());
                instruccionesFinal.insertarSelect(colaColumnas);
                colaColumnas.vaciar();
                //Avanzamos al siguiente token
                Sintáctico.indexColaTokens++;
                //Actualizamos el esperaColumna
                esperaColumna = false;
                
//            } else if (operacion_aritmetica()) {
//                // Al llamar a la función, avanza a expresión + 1 (se espera , o FROM)
//                esperaColumna = false;
                
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
                // Al término de reconocer las columnas, el índice general estará en el FROM
                return true;
            }
            else{
                System.out.println("Error de sintaxis: Se esperaba " + Tokenizer.FROM);

                return false;
            }           
            
        }

    }

    public boolean operacion_aritmetica() {
        Cola<Token> expresion = new Cola();
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
                    JOptionPane.showMessageDialog(null, "Error de sintaxis: Se esperaba OPERADOR", "ERROR", JOptionPane.ERROR_MESSAGE);
                    return false;
                }
                indiceCopia++;
                esperaOperando = false;
              
            // El siguiente if else verifica que el operador no se encuentre en primer lugar    
            } else if (indiceCopia != Sintáctico.indexColaTokens && (tipoActual.equals(Tokenizer.PLUS) || tipoActual.equals(Tokenizer.MINUS) || tipoActual.equals(Tokenizer.ASTERISK)  || tipoActual.equals(Tokenizer.DIV))) {
                if (esperaOperando) {
                    JOptionPane.showMessageDialog(null, "Error de sintaxis: Se esperaba OPERANDO", "ERROR", JOptionPane.ERROR_MESSAGE);
                    return false;
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
                    JOptionPane.showMessageDialog(null, "Error de sintaxis: Se cerró paréntesis innecesariamente", "ERROR", JOptionPane.ERROR_MESSAGE);
                    return false;
                }
                indiceCopia++;
                esperaOperando = false;
                esperaCierre--;
               
            } else if ((tipoActual.equals(Tokenizer.COMMA) || tipoActual.equals(Tokenizer.FROM)) && tieneOperador == true) {
                if (esperaCierre != 0) {
                    JOptionPane.showMessageDialog(null, "Error de sintaxis: Hay un paréntesis no cerrado", "ERROR", JOptionPane.ERROR_MESSAGE);
                    return false;
                }
                // En este punto el índice general estará en expresión + 1
                for (int i = Sintáctico.indexColaTokens ; i < indiceCopia ; i++) {                 
                    expresion.agregar((Token) Sintáctico.colaTokens.buscar_por_orden(i));
                }
                
                // DEBUG
                System.out.println(indiceCopia);
                expresion.imprimirCola();
                
                instruccionesFinal.insertarSelect(expresion);
                Sintáctico.indexColaTokens = indiceCopia-1;
                return true;
            }
            else {
                return false;
            }
        }
 
    }   
    
    public boolean nombre_tabla() {
        if (!Sintáctico.tipo_actual().equals("ID")) {
            return false;
        }
        // parametros[1] = Sintáctico.valor_actual();
        // Reemplazado por:
        instruccionesFinal.setTabla(Sintáctico.valor_actual());       
        //Avanzamos al siguiente token
        Sintáctico.indexColaTokens++;
        return true;
    }

    public boolean where() {
        //Implementacion del analisis sintactico de where
        Sintáctico.colaTokens.imprimirCola();
        if(Sintáctico.indexColaTokens >= Sintáctico.colaTokens.getSize()){
            return false;
        }
        if (!Sintáctico.tipo_actual().equals(Tokenizer.WHERE)) {
            //El token actual no es WHERE
            //Retornar falso
            return false;
        }
        //Where reconocido, avanzamos al siguiente token
        Sintáctico.indexColaTokens++;
        //definimos una cola de tokensLogicos
        Cola<Token> colaTokensLogicos = new Cola<>();

        //1.Recogemos los tokens
        while (true) {

            Token token = Sintáctico.token_actual();
            colaTokensLogicos.agregar(token);
            //Si estamos en el ultimo indice y agregamos el ultimo token salir del while
            if (Sintáctico.indexColaTokens == Sintáctico.colaTokens.getSize() - 1) {
                break;
            }
            //Avanzar al siguiente token
            Sintáctico.indexColaTokens++;
        }

        //2. Verificar que los tokens esten gramaticalmente correctos
        //parsing de las sentencias de cola tokens
        int indexColaLogico = 0;
        boolean esperaOperando = true;
        boolean esUltimoToken = false;
        boolean inicioTermino = true;
        //iteramos toda la cola de tokens logicos
        while (indexColaLogico < colaTokensLogicos.getSize()) {
            Token token = colaTokensLogicos.buscar_por_orden(indexColaLogico);
            String tokenTipo = token.getTipo();
            
            //Estamos en el ultimo token
            if(indexColaLogico == colaTokensLogicos.getSize() - 1){
                esUltimoToken = true;
            }
            
            if (tokenTipo.equals(Tokenizer.NOT)) {
                
                //El where no termina en NOT
                if(esUltimoToken){
                    return false;
                }
                if (inicioTermino) {
                    //Consumimos el NOT, no estamos al inicio
                    inicioTermino = false;
                    esperaOperando = true;
                    //Avanzamos token
                    indexColaLogico++;
                } else {
                    return false;
                }

            } else if (tokenTipo.equals(Tokenizer.ID) || tokenTipo.equals(Tokenizer.NUMBER) || tokenTipo.equals(Tokenizer.STRING)) {
                
                if(esUltimoToken){
                    //solo hay un token y es un ID, numero o string
                    if(indexColaLogico == 0){
                        break;
                    }
                    else{
                        Token tokenAnterior = colaTokensLogicos.buscar_por_orden(indexColaLogico - 1);
                        if(isAComparisonOperator(tokenAnterior.getTipo())){
                            break;
                        }
                    }
                }
                
                if (esperaOperando) {
                    esperaOperando = false;
                    inicioTermino = false;
                    indexColaLogico++;
                } else {
                    System.out.println("Operando no esperado: " + token.getTokenValor());
                    return false;
                }
            } //Si es un operador comparacion
            else if (isAComparisonOperator(tokenTipo)) {
                
                if(esUltimoToken){
                    System.out.println("Se esperaba una comparacion");
                    return false;
                }
                
                if (!esperaOperando) {
                    esperaOperando = true;
                    indexColaLogico++;
                } else {
                    return false;
                }
            } //Si es un operador logico
            else if (tokenTipo.equals(Tokenizer.AND) || tokenTipo.equals(Tokenizer.OR)) {
                
                if(esUltimoToken){
                    System.out.println("Se esperaba otra expresion logica");
                    return false;
                }
                
                if (!inicioTermino && !esperaOperando) {
                    inicioTermino = true;
                    esperaOperando = true;
                    indexColaLogico++;
                } else {
                    return false;
                }
            }
            else{
                //un token incongruente para el where
                System.out.println("Token no valido en esta expresion");
                return false;
            }

        }
        
        //parametros[2] = colaTokensLogicos;
        // Reemplazado por:
        instruccionesFinal.insertarWhere(colaTokensLogicos);
        
        return true;   
    }
    
    private boolean isAComparisonOperator(String tokenTipo){
        return tokenTipo.equals(Tokenizer.EQUAL) || tokenTipo.equals(Tokenizer.GREATER) || tokenTipo.equals(Tokenizer.LESS) || tokenTipo.equals(Tokenizer.LESS_EQUAL) || tokenTipo.equals(Tokenizer.GREATER_EQUAL) || tokenTipo.equals(Tokenizer.NOT_EQUAL[0]) || tokenTipo.equals(Tokenizer.NOT_EQUAL[1]);
    }
    
}
