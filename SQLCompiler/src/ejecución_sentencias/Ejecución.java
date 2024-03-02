/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ejecución_sentencias;

import EDD.Cola;
import EDD.Pila;
import Lector_Datos.Atributo;
import Lector_Datos.Tabla;
import analizador_lexico.Token;
import analizador_lexico.Tokenizer;
import análisis_sintáctico.Reglas;
import java.util.ArrayList;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author utente
 */
public class Ejecución {

    ArrayList<Tabla> base = new ArrayList<>();

    public Ejecución(ArrayList baseTablas) {
        this.base = baseTablas;
    }

    public DefaultTableModel crear_modelo_tabla(Object[] parametros, int num_elementos) {
        DefaultTableModel modelo = null;
        if (num_elementos == 2) {
            //Lista de columnas parametros[0] y parametros[1] nombre tabla 
            modelo = ejec_2_parametros(parametros[0], parametros[1].toString());
        }
        if (num_elementos == 3) {
            modelo = ejec_3_parametros(parametros[0], parametros[2], parametros[1].toString());
        }
        return modelo;
    }

    public DefaultTableModel ejec_2_parametros(Object parametroListaColumnas, String nombreTabla) {
        DefaultTableModel modelo = new DefaultTableModel();

        Tabla tabla_resultado = null;

        for (Tabla tabla : base) {
            if (nombreTabla.equals(tabla.getNombreTabla())) {
                tabla_resultado = tabla;
            }
        }

        //No se encontró la tabla
        if (tabla_resultado == null) {
            return null;
        }

        if (parametroListaColumnas == null) {
            System.out.println("No se identifico al lista de columnas para procesar");
            return null;
        }

        // Numero de registros sin where
        int numRegistros = tabla_resultado.getListaAtributos().size();
        
        // Aquí el where (fijar número de registros a mostrar)
        
        
        //Downcasting
        Cola<Token> colaColumnasSelect = (Cola<Token>) parametroListaColumnas;
        
        // Verificar si existen expresiones aritméticas
        if (buscarAritméticas(parametroListaColumnas)) {
            String operacion = crearStringOperacion(parametroListaColumnas);
            InfijasAPosfijas op = new InfijasAPosfijas();
            int resultado = op.operarExpresion(operacion);
            colaColumnasSelect = modificarColaColumnas(colaColumnasSelect, resultado);
            
            // DEBUG
            colaColumnasSelect.imprimirCola();
            
            // Se crea un nuevo Atributo para tratar la constante como uno y mantener uniformidad del modelo
            // Atributo atConstante = new Atributo(resultado, numRegistros);
            
        }
        
        
       

        System.out.println(verificarColumnasEnTabla(colaColumnasSelect, tabla_resultado));

        //Si comprobamos que todas las tablas estan OK procedemos a mostrar la tabla Query
        if (verificarColumnasEnTabla(colaColumnasSelect, tabla_resultado)) {

            
            insertarColumnas(colaColumnasSelect, tabla_resultado, modelo);

        }

        return modelo;
    }

    public boolean verificarColumnasEnTabla(Cola<Token> colaColumnasSelect, Tabla tabla_resultado) {

        //Obtenemos la lista de atributos de la tabla
        ArrayList<Atributo> listaAtributos = tabla_resultado.getListaAtributos();

        //Un valor de bandera para poder determinar si hay una columna ajena a la tabla
        boolean columnaEncontradaTabla = false;

        for (int i = 0; i < colaColumnasSelect.getSize(); i++) {

            Token columna = colaColumnasSelect.buscar_por_orden(i);
            String valorColumna = columna.getTokenValor().toUpperCase();

            if (columna.getTipo().equals(Tokenizer.ID)) {
                //Si es ID hacemos la busqueda
                for (Atributo atributo : listaAtributos) {
                    //Si encontramos la columna en la lista de atributos
                    String nombreAtributo = atributo.getNombre().toUpperCase();
                    if (valorColumna.equals(nombreAtributo)) {
                        columnaEncontradaTabla = true;
                        break;
                    }
                }
                if (!columnaEncontradaTabla) {
                    System.out.println("Error semántico: columna [" + valorColumna + "] no fue encontrada en la Tabla");
                    return false;
                }
                //Si se encontro la columna buscar por la otra
                columnaEncontradaTabla = false;

            }

        }
        return true;
    }

    private void insertarColumnas(Cola<Token> colaColumnasSelect, Tabla tabla_resultado, DefaultTableModel modelo) {

        //Obtenemos la lista de atributos de la Tabla requerida
        ArrayList<Atributo> listaAtributos = tabla_resultado.getListaAtributos();

        //Iteramos sobre cada columna obtenida de la sentencia SELECT
        for (int i = 0; i < colaColumnasSelect.getSize(); i++) {

            //Obtenemos la columna y lo que nos convenga
            Token columna = colaColumnasSelect.buscar_por_orden(i);
            String valorColumna = columna.getTokenValor().toUpperCase();
            String tipoColumna = columna.getTipo().toUpperCase();

            switch (tipoColumna) {
                case Tokenizer.ID:
                    Atributo atributoColumna = null;
                    //Buscaremos el atributo que representa la columna
                    for (Atributo atributo : listaAtributos) {
                        String nombreAtributo = atributo.getNombre().toUpperCase();
                        if (valorColumna.equals(nombreAtributo)) {
                            atributoColumna = atributo;
                            //salimos del for puesto que ya encontramos el atributo relacionado
                            break;
                        }
                    }
                    //insertamos la columna en la tabla con la lista de valores del atributo
                    if (atributoColumna != null) {
                        int sizeAtributoColumna = atributoColumna.getListaValores().size();
                        //Creo un objeto que represente una columna
                        Object[] columnaModelo = new Object[sizeAtributoColumna];

                        //paso los valores de la lista de valores en la columna
                        for (int fila = 0; fila < sizeAtributoColumna; fila++) {
                            columnaModelo[fila] = atributoColumna.getListaValores().get(fila);
                        }
                        //añadimos la columna a la Jtable
                        modelo.addColumn(valorColumna, columnaModelo);

                    } else {
                        System.out.println("o no hay atributo columna o no hay lista de valores");
                    }

                    break;

                    
                    
                    
                    
                case Tokenizer.NUMBER:
                case Tokenizer.STRING:
                    insertarConstante(valorColumna, modelo, listaAtributos);
                    break;
                    
                    
                    
                    
                case Tokenizer.ASTERISK:
                    //insertamos todas las columnas
                    for (Atributo atributo : listaAtributos) {
                        //obtenemos la cantidad de filas
                        int sizeAtributoColumna = atributo.getListaValores().size();
                        Object[] columnaModelo = new Object[sizeAtributoColumna];

                        //paso los valores de la lista de valores en la columna
                        for (int fila = 0; fila < sizeAtributoColumna; fila++) {
                            columnaModelo[fila] = atributo.getListaValores().get(fila);
                        }
                        //añadimos la columna a la Jtable
                        modelo.addColumn(atributo.getNombre(), columnaModelo);
                    }
                    break;

            }

        }

    }

    private void insertarConstante(String valorColumna, DefaultTableModel modelo, ArrayList<Atributo> listaAtributos) {

        //Obtenemos la cantidad de filas o registros de un atributo cualquiera
        int cantRegistrosTabla = listaAtributos.get(0).getListaValores().size();

        //Creamos un atributo temporal falso
        Atributo atributoColumna = new Atributo();
        //Rellenamos la lista de valores con el valor constante
        for (int i = 0; i < cantRegistrosTabla; i++) {
            atributoColumna.agregarValor(valorColumna);
        }

        //Creo un objeto que represente una columna
        Object[] columnaModelo = new Object[cantRegistrosTabla];

        //paso los valores de la lista de valores en la columna
        for (int i = 0; i < cantRegistrosTabla; i++) {
            columnaModelo[i] = atributoColumna.getListaValores().get(i);
        }
        //añadimos la columna a la Jtable
        modelo.addColumn(valorColumna, columnaModelo);

    }


    private boolean buscarAritméticas(Object parametroListaColumnas) {
        Cola <Token> colaSelect = (Cola <Token>) parametroListaColumnas;
        boolean tieneArit = false;
        
        for (int i = 0 ; i < colaSelect.getSize() ; i++) {
            if (colaSelect.buscar_por_orden(i).getTipo().equals("ARIT")) {
                tieneArit = true;
            }
        }    
        return tieneArit;
    }
    
    private String crearStringOperacion(Object parametroListaColumnas) {
        Cola <Token> colaSelect = (Cola <Token>) parametroListaColumnas;
        String operacion = "";
        
        for (int i = 0 ; i < colaSelect.getSize() ; i++) {
            if (colaSelect.buscar_por_orden(i).getTipo().equals("ARIT")) {
                operacion = operacion + colaSelect.buscar_por_orden(i).getTokenValor();
            }
        }    
        
        // DEBUG
        System.out.println("String: " + operacion);
        
        return operacion;
    }
    
    public Cola<Token> modificarColaColumnas(Cola<Token> columnasSelect, int resultado) {
        int indiceFinal = 0;
        
        for (int i = 0 ; i < columnasSelect.getSize() ; i++) {
            if (columnasSelect.buscar_por_orden(i).getTipo().equals("ARIT")) {
                indiceFinal = i;
            }
        }
        
        columnasSelect.buscar_por_orden(indiceFinal).setTipo("NUMBER");
        columnasSelect.buscar_por_orden(indiceFinal).setTokenValor(Integer.toString(resultado));
 
        return columnasSelect;
    } 
    
    

    public DefaultTableModel ejec_3_parametros(Object parametroListaColumnas, Object parametroListaLogica, String nombreTabla) {
        DefaultTableModel modelo = new DefaultTableModel();

        Tabla tabla_resultado = null;

        for (Tabla tabla : base) {
            if (nombreTabla.equals(tabla.getNombreTabla())) {
                tabla_resultado = tabla;
            }
        }

        //No se encontró la tabla
        if (tabla_resultado == null) {
            System.out.println("Tabla no encontrada " + nombreTabla);
            return null;
        }

        if (parametroListaColumnas == null) {
            System.out.println("No se identifico al lista de columnas para procesar");
            return null;
        }

        //Downcasting
        //traemos el token de 'columnas' identificadas
        Cola<Token> colaColumnasSelect = (Cola<Token>) parametroListaColumnas;
        colaColumnasSelect.imprimirCola();

        //traemos el token de columnas logicas
        Cola<Token> tokensLogicos = (Cola<Token>) parametroListaLogica;
        tokensLogicos.imprimirCola();

        if (!verificarColumnasEnTabla(colaColumnasSelect, tabla_resultado)) {
            return null;
        }

        if (!verificarColumnasEnTabla(tokensLogicos, tabla_resultado)) {
            return null;
        }

        //ahora si, el trabajo duro
        //Hemos obtenido la cola de elemntos posfijos
        Cola<Object> colaElementosPosfijos = convertirPosfija(tokensLogicos, tabla_resultado);
        colaElementosPosfijos.imprimirCola();

        //Ahora a obtener los indices que no cumplen con la expresion booleana 
        ArrayList<Integer> indicesCumplenWhere = obtenerIndicesWhere(colaElementosPosfijos, tabla_resultado);
        //indices que cumplen con la condicion
        System.out.println(indicesCumplenWhere.toString());

        //Si comprobamos que todas las tablas estan OK procedemos a mostrar la tabla Query
        if (verificarColumnasEnTabla(colaColumnasSelect, tabla_resultado) && verificarColumnasEnTabla(tokensLogicos, tabla_resultado) ) {

            insertarColumnasWhere(colaColumnasSelect, tabla_resultado, modelo, indicesCumplenWhere);

        }

        return modelo;

    }

    private void insertarColumnasWhere(Cola<Token> colaColumnasSelect, Tabla tabla_resultado, DefaultTableModel modelo, ArrayList<Integer> indicesCumplenWhere) {

        //Obtenemos la lista de atributos de la Tabla requerida
        ArrayList<Atributo> listaAtributos = tabla_resultado.getListaAtributos();

        //Iteramos sobre cada columna obtenida de la sentencia SELECT
        for (int i = 0; i < colaColumnasSelect.getSize(); i++) {
            //Obtenemos la columna y lo que nos convenga
            Token columna = colaColumnasSelect.buscar_por_orden(i);
            String valorColumna = columna.getTokenValor().toUpperCase();
            String tipoColumna = columna.getTipo().toUpperCase();

            switch (tipoColumna) {
                case Tokenizer.ID:
                    Atributo atributoColumna = null;
                    //Buscaremos el atributo que representa la columna
                    for (Atributo atributo : listaAtributos) {
                        String nombreAtributo = atributo.getNombre().toUpperCase();
                        if (valorColumna.equals(nombreAtributo)) {
                            atributoColumna = atributo;
                            //salimos del for puesto que ya encontramos el atributo relacionado
                            break;
                        }
                    }
                    //insertamos la columna en la tabla con la lista de valores del atributo
                    if (atributoColumna != null) {
                        int sizeAtributoColumna = indicesCumplenWhere.size();
                        //Creo un objeto que represente una columna
                        Object[] columnaModelo = new Object[sizeAtributoColumna];

                        //paso los valores de la lista de valores en la columna que cumplen el where
                        int index = 0;
                        for (Integer fila : indicesCumplenWhere) {
                            columnaModelo[index] = atributoColumna.getListaValores().get(fila);
                            index++;
                        }
                        //añadimos la columna a la Jtable
                        modelo.addColumn(valorColumna, columnaModelo);

                    } else {
                        System.out.println("o no hay atributo columna o no hay lista de valores");
                    }

                    break;

                case Tokenizer.NUMBER:
                case Tokenizer.STRING:
                    insertarConstanteWhere(valorColumna, modelo, listaAtributos, indicesCumplenWhere);
                    break;
                case Tokenizer.ASTERISK:
                    //insertamos todas las columnas
                    for (Atributo atributo : listaAtributos) {
                        //obtenemos la cantidad de filas
                        int sizeAtributoColumna = indicesCumplenWhere.size();
                        Object[] columnaModelo = new Object[sizeAtributoColumna];

                        //paso los valores de la lista de valores en la columna
                        int index = 0;
                        for (Integer fila : indicesCumplenWhere) {
                            columnaModelo[index] = atributo.getListaValores().get(fila);
                            index++;
                        }
                        //añadimos la columna a la Jtable
                        modelo.addColumn(atributo.getNombre(), columnaModelo);
                    }
                    break;

            }

        }

    }

    private void insertarConstanteWhere(String valorColumna, DefaultTableModel modelo, ArrayList<Atributo> listaAtributos, ArrayList<Integer> indicesCumplenWhere) {

        //Obtenemos la cantidad de filas o registros de un atributo cualquiera
        int cantRegistrosTabla = indicesCumplenWhere.size();

        //Creamos un atributo temporal falso
        Atributo atributoColumna = new Atributo();
        //Rellenamos la lista de valores con el valor constante
        for (int i = 0; i < cantRegistrosTabla; i++) {
            atributoColumna.agregarValor(valorColumna);
        }

        //Creo un objeto que represente una columna
        Object[] columnaModelo = new Object[cantRegistrosTabla];

        //paso los valores de la lista de valores en la columna
        for (int i = 0; i < cantRegistrosTabla; i++) {
            columnaModelo[i] = atributoColumna.getListaValores().get(i);
        }
        //añadimos la columna a la Jtable
        modelo.addColumn(valorColumna, columnaModelo);

    }

    //Con este metodo planeamos primero obtener la cola TokensLogicos en forma posfija
    //Luego reconocer los atributos que estan en esa cola posfija 
    private Cola<Object> convertirPosfija(Cola<Token> tokensLogicos, Tabla tabla_resultado) {

        //obtenemos el orden posfijo de los Tokens
        Cola<Token> tokensPosfijo = obtenerColaTokenPosfija(tokensLogicos);
        tokensPosfijo.imprimirCola();

        //ahora obtenemos la cola posfija de Atributos y Tokens Operadores
        Cola<Object> elementosPosfijos = obtenerColaElementosPosfijos(tokensPosfijo, tabla_resultado);

        return elementosPosfijos;
    }

    private Cola<Token> obtenerColaTokenPosfija(Cola<Token> tokensLogicos) {

        //Mi pila de operadores
        Pila<Token> pilaOperadores = new Pila<>();
        //Nueva cola posfija
        Cola<Token> tokensPosfijo = new Cola<>();

        int sizeCola = tokensLogicos.getSize();
        int index = 0;

        while (index < sizeCola) {
            Token tokenActual = tokensLogicos.buscar_por_orden(index);
            String tipoTokenActual = tokenActual.getTipo();
            switch (tipoTokenActual) {
                case Tokenizer.ID:
                case Tokenizer.NUMBER:
                case Tokenizer.STRING:
                    tokensPosfijo.agregar(tokenActual);
                    break;
                //Si es un operador revisar las precedencias antes de hacer un movimiento
                case Tokenizer.EQUAL:
                case "!=":
                case "<>":
                case Tokenizer.GREATER_EQUAL:
                case Tokenizer.LESS_EQUAL:
                case Tokenizer.GREATER:
                case Tokenizer.LESS:
                case Tokenizer.NOT:
                case Tokenizer.AND:
                case Tokenizer.OR:
                    while (!pilaOperadores.isEmpty() && precedencia(tipoTokenActual) <= precedencia(pilaOperadores.getTope().getDato().getTipo())) {
                        tokensPosfijo.agregar((Token) pilaOperadores.pop().getDato());
                    }
                    pilaOperadores.push(tokenActual);
                    break;

                default:
                    System.out.println("Algo salio mal: operador no reconocido " + tokenActual.getTokenValor());
            }
            index++;
        }

        //Agregamos los operadores que quedaron en la pila a la Cola Posfija
        while (!pilaOperadores.isEmpty()) {
            Token tokenOperadorCima = (Token) pilaOperadores.pop().getDato();
            tokensPosfijo.agregar(tokenOperadorCima);
        }

        return tokensPosfijo;
    }

    private Cola<Object> obtenerColaElementosPosfijos(Cola<Token> tokensPosfijo, Tabla tabla_resultado) {

        //lista atributos de la tabla
        ArrayList<Atributo> listaAtributosTabla = tabla_resultado.getListaAtributos();
        //Cola de elementos de la expresion Atributos y tokens
        Cola<Object> colaElementosPosfijos = new Cola<>();

        for (int i = 0; i < tokensPosfijo.getSize(); i++) {
            boolean encontroAtributo = false;
            Token tokenActual = tokensPosfijo.buscar_por_orden(i);
            //Guardamos el nombre del token que puede corresponder a una columna
            String nombreToken = tokenActual.getTokenValor().toUpperCase();
            //Buscamos la columna en la lista de atributos
            for (int j = 0; j < listaAtributosTabla.size(); j++) {
                String nombreAtributoActual = listaAtributosTabla.get(j).getNombre().toUpperCase();
                if (nombreToken.equals(nombreAtributoActual)) {
                    //Agregamos el atributo coincidente
                    colaElementosPosfijos.agregar(listaAtributosTabla.get(j));
                    encontroAtributo = true; //Se encontro el atributo
                    break;
                }
            }
            //Entonces si no se encontro atributo debe ser un operador u otro token
            if (!encontroAtributo) {
                colaElementosPosfijos.agregar(tokenActual);
            }
        }

        //Retorna la cola de atributos y operadores
        return colaElementosPosfijos;
    }

    //Metodo para resolver expresiones posfijas
    private ArrayList<Integer> obtenerIndicesWhere(Cola<Object> colaElementosPosfijos, Tabla tabla_resultado) {
        ArrayList<Integer> listaIndicesWhere = new ArrayList<>();
        int numeroSize = tabla_resultado.getListaAtributos().get(0).getListaValores().size();
        for (int indexAtributo = 0; indexAtributo < numeroSize; indexAtributo++) {
            Pila<Object> pilaExpresion = new Pila<>();
            for (int i = 0; i < colaElementosPosfijos.getSize(); i++) {
                Object elemento = colaElementosPosfijos.buscar_por_orden(i);
                if (elemento instanceof Atributo) {
                    if (elemento instanceof Atributo) {
                        // Suponiendo que getValor devuelve un Integer
                        pilaExpresion.push(Integer.valueOf((String) ((Atributo) elemento).getListaValores().get(indexAtributo)));
                    }

                } else if (elemento instanceof Token) {
                    if (((Token) elemento).getTipo().equals(Tokenizer.NUMBER)) {
                        pilaExpresion.push(Integer.valueOf(((Token) elemento).getTokenValor()));
                    } else {
                        Token operador = (Token) elemento;
                        switch (operador.getTokenValor()) {
                            case Tokenizer.EQUAL:
                                // Ejemplo para EQUAL
                                if (operador.getTokenValor().equals(Tokenizer.EQUAL)) {
                                    Integer valorDer = (Integer) pilaExpresion.pop().getDato();
                                    Integer valorIzq = (Integer) pilaExpresion.pop().getDato();
                                    Boolean resultado = valorIzq.equals(valorDer);
                                    pilaExpresion.push(resultado);
                                }
                                break;
                            case "!=":
                            case "<>":
                            case Tokenizer.GREATER_EQUAL:
                            case Tokenizer.LESS_EQUAL:
                            case Tokenizer.GREATER:
                            case Tokenizer.LESS:
                            case Tokenizer.AND:
                            case Tokenizer.OR:
                            case Tokenizer.NOT:

                                // Implementar otros casos...
                                break;
                            default:
                                // Manejo de operadores desconocidos o errores
                                System.out.println("Operador desconocido: " + operador.getTokenValor());
                        }
                    }

                }
            }
            // Evaluación final para este índice
            if (!pilaExpresion.isEmpty() && (Boolean) pilaExpresion.pop().getDato()) {
                listaIndicesWhere.add(indexAtributo);
            }
        }
        return listaIndicesWhere;
    }

    int precedencia(String operador) {
        switch (operador) {
            case Tokenizer.EQUAL:
            case "!=":
            case "<>":
            case Tokenizer.GREATER_EQUAL:
            case Tokenizer.LESS_EQUAL:
            case Tokenizer.GREATER:
            case Tokenizer.LESS:
                return 3;
            case Tokenizer.NOT:
                return 2;
            case Tokenizer.AND:
                return 1;
            case Tokenizer.OR:
                return 0;
            default:
                //Operador desconocido
                return -1;

        }
    }


}

    