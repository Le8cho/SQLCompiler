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
import análisis_sintáctico.Inst_Select;
import análisis_sintáctico.Lista_Instrucciones;
import java.util.ArrayList;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author utente
 */
public class Ejecución {

    ArrayList<Integer> indicesFinal = new ArrayList <>();
    ArrayList<Tabla> base = new ArrayList<>();
    Tabla tablaFinal;
    boolean detectoFuncion;

    public Ejecución(ArrayList baseTablas) {
        this.base = baseTablas;
        this.tablaFinal = new Tabla();
        this.indicesFinal = new ArrayList<>();
        this.detectoFuncion = false;
    }

    public DefaultTableModel crear_tabla_resultado(Lista_Instrucciones instrucciones) {
        DefaultTableModel modelo = new DefaultTableModel();

        Tabla tablaRes = buscarTabla(instrucciones.getTabla());

        // No se encontró la tabla
        if (tablaRes == null) {
            JOptionPane.showMessageDialog(null, "No se encontró la tabla buscada", "ERROR", JOptionPane.ERROR_MESSAGE);
            // return null;
        }

        aplicarSelect(instrucciones.getListaSelect(), tablaRes);
        
        if (!instrucciones.getInstrOrder().estaVacio()) {
            aplicarOrder(instrucciones.getInstrOrder(), tablaRes);
        }       
        
        if (!instrucciones.getListaWhere().isEmpty()) {
            aplicarWhere(instrucciones.getListaWhere(), tablaRes);
        }
 
        modelo = crearModelo(modelo);

        return modelo;
    }

    public void aplicarSelect(ArrayList<Inst_Select> listaSelect, Tabla tablaRes) {
        for (Inst_Select inst : listaSelect) {

            if (inst.getTipo().equals("ID")) {
                Atributo at = buscarAtributo(inst.getParamToken(), tablaRes);

                if (at == null) {
                    JOptionPane.showMessageDialog(null, "No se encontró el atributo buscado", "ERROR",
                            JOptionPane.ERROR_MESSAGE);
                }

                tablaFinal.agregarAtributo(at);
            }
            
            if (inst.getTipo().equals("*")) {
                for (Atributo atributo : tablaRes.getListaAtributos()) {
                    tablaFinal.agregarAtributo(atributo);
                }
            }

            if (inst.getTipo().equals("STRING")) {
                Atributo atributoConst = new Atributo(inst.getParamToken(),
                        tablaRes.getListaAtributos().get(0).getListaValores().size());
                tablaFinal.agregarAtributo(atributoConst);
            }

            if (inst.getTipo().equals("NUMBER")) {
                Atributo atributoConst = new Atributo(Integer.parseInt(inst.getParamToken()),
                        tablaRes.getListaAtributos().get(0).getListaValores().size());
                tablaFinal.agregarAtributo(atributoConst);
            }

            if (inst.getTipo().equals("OPERACION")) {
                ArrayList<Object> listaValores = realizarOperacion(inst.getParamCola(), tablaRes);
                Atributo atributoOperacion = new Atributo(listaValores);
                atributoOperacion.setNombre(nombrarOperacion(inst));
                tablaFinal.agregarAtributo(atributoOperacion);
            }

            if (inst.getTipo().equals("FUNCION")) {
                Atributo atributoFuncion = realizarFuncion(inst.getParamCola(), tablaRes);
                tablaFinal.agregarAtributo(atributoFuncion);
            }
        }
    }

    public void aplicarWhere(ArrayList<Cola<Token>> listaWhere, Tabla tablaRes) {
               
        Atributo atributoRes = buscarAtributo(listaWhere.get(0).buscar_por_orden(0).getTokenValor(), tablaRes);

        if (atributoRes == null) {
            JOptionPane.showMessageDialog(null, "No se encontró el atributo buscado", "ERROR",
                    JOptionPane.ERROR_MESSAGE);
        }

        if (indicesFinal.isEmpty()) {
            indicesFinal = filtrarFilas(listaWhere, tablaRes, atributoRes);
            
        } else {
            // SE OBTIENE LA FILA DE ELEMENTOS QUE CUMPLEN
            ArrayList<Integer> indicesFiltrados = filtrarFilas(listaWhere, tablaRes, atributoRes);
            
            for (int i = 0; i < indicesFinal.size(); i++) {
                if (!indicesFiltrados.contains(indicesFinal.get(i))) {
                    indicesFinal.remove(i);
                    i--; // Decrementar el índice para evitar saltar elementos
                }
            }
            
        }       
    }

    public void aplicarOrder(Cola<Token> instrOrder, Tabla tablaRes) {
        Atributo atributoRes = buscarAtributo(instrOrder.buscar_por_orden(2).getTokenValor(), tablaRes);
        ArrayList<Integer> indicesTemp;
        
        if (atributoRes.getTipo() != 'N') {
            JOptionPane.showMessageDialog(null, "No se puede ordenar una cadena", "ERROR",
                        JOptionPane.ERROR_MESSAGE);
        } else {            
            if (instrOrder.getSize() == 4) {
                if (instrOrder.buscar_por_orden(3).getTokenValor().equals("DESC")) {
                    
                    indicesTemp = ordenarDescendente(atributoRes.getListaValores());
                    
                } else {
                    
                    indicesTemp = ordenarAscendente(atributoRes.getListaValores());
                }
                
            } else {               
                indicesTemp = ordenarAscendente(atributoRes.getListaValores());                
            }        
            // AGREGAR ÍNDICES A LA LISTA FINAL
            indicesFinal = indicesTemp;    
        }
    }   
    
    public Tabla buscarTabla(String tablaBuscada) {
        Tabla tabla_resultado = null;

        for (Tabla tabla : base) {
            if (tabla.getNombreTabla().equals(tablaBuscada)) {
                tabla_resultado = tabla;
            }
        }
        return tabla_resultado;
    }

    public Atributo buscarAtributo(String atributoBuscado, Tabla tablaRes) {
        Atributo atributo_resultado = null;

        for (Atributo atributo : tablaRes.getListaAtributos()) {
 
            if (atributo.getNombre().toUpperCase().equals(atributoBuscado)) {
                atributo_resultado = atributo;
            }
        }
        // System.out.println("Atributo buscado: " + atributo_resultado.getNombre());
        return atributo_resultado;
    }

    public ArrayList<Integer> filtrarFilas(ArrayList<Cola<Token>> listaWhere, Tabla tablaRes, Atributo atributoRes) {
        String operador = listaWhere.get(0).buscar_por_orden(1).getTokenValor();
        Token tokenCondicion = listaWhere.get(0).buscar_por_orden(2);
        
        ArrayList<Integer> indices = new ArrayList<>();

        if (operador.equals("<")) {
            if (tokenCondicion.getTipo().equals("NUMBER")) {
                // Recorre todos los valores del atributo
                for (int i = 0; i < atributoRes.getListaValores().size(); i++) {
                    if (Integer.parseInt(String.valueOf(atributoRes.getListaValores().get(i))) < Integer.parseInt(tokenCondicion.getTokenValor())) {
                        indices.add(i);
                        return indices;
                    }
                }
            } else {
                JOptionPane.showMessageDialog(null, "Se esperaba un number para esta operación WHERE", "ERROR",
                        JOptionPane.ERROR_MESSAGE);
            }

        } else
        if (operador.equals("<=")) {
            if (tokenCondicion.getTipo().equals("NUMBER")) {
                // Recorre todos los valores del atributo
                for (int i = 0; i < atributoRes.getListaValores().size(); i++) {
                    if (Integer.parseInt(String.valueOf(atributoRes.getListaValores().get(i))) <= Integer.parseInt(tokenCondicion.getTokenValor())) {
                        indices.add(i);
                        return indices;
                    }
                }
            } else {
                JOptionPane.showMessageDialog(null, "Se esperaba un number para esta operación WHERE", "ERROR",
                        JOptionPane.ERROR_MESSAGE);
            }

        } else
        if (operador.equals(">")) {
            if (tokenCondicion.getTipo().equals("NUMBER")) {
                // Recorre todos los valores del atributo
                for (int i = 0; i < atributoRes.getListaValores().size(); i++) {
                    if (Integer.parseInt(String.valueOf(atributoRes.getListaValores().get(i))) > Integer.parseInt(tokenCondicion.getTokenValor())) {
                        indices.add(i);
                        return indices;
                    }
                }
            } else {
                JOptionPane.showMessageDialog(null, "Se esperaba un number para esta operación WHERE", "ERROR",
                        JOptionPane.ERROR_MESSAGE);
            }

        } else
        if (operador.equals(">=")) {
            if (tokenCondicion.getTipo().equals("NUMBER")) {
                // Recorre todos los valores del atributo
                for (int i = 0; i < atributoRes.getListaValores().size(); i++) {
                    if (Integer.parseInt(String.valueOf(atributoRes.getListaValores().get(i))) >= Integer.parseInt(tokenCondicion.getTokenValor())) {
                        indices.add(i);
                        return indices;
                    }
                }
            } else {
                JOptionPane.showMessageDialog(null, "Se esperaba un number para esta operación WHERE", "ERROR",
                        JOptionPane.ERROR_MESSAGE);
            }

        } else
        if (operador.equals("=")) {
            if (tokenCondicion.getTipo().equals("NUMBER")) {
                // Recorre todos los valores del atributo
                for (int i = 0; i < atributoRes.getListaValores().size(); i++) {
                    if ( Integer.parseInt(String.valueOf(atributoRes.getListaValores().get(i))) == Integer.parseInt(tokenCondicion.getTokenValor())) {
                        indices.add(i);   
                    }       
                }
                return indices;
                
            } else if (tokenCondicion.getTipo().equals("STRING")) {
                // Recorre todos los valores del atributo
                for (int i = 0; i < atributoRes.getListaValores().size(); i++) {
                        
                    String valorActual = '\'' + atributoRes.getListaValores().get(i).toString() + '\'';
                    if (valorActual.equals(tokenCondicion.getTokenValor())) {
                        indices.add(i);
                        
                        System.out.println(indices); 
                    }
                }     
                return indices;
                
            } else {
                JOptionPane.showMessageDialog(null, "Se esperaba un number o string para esta operación WHERE", "ERROR",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
        return indices;
    }

    public DefaultTableModel crearModelo(DefaultTableModel modelo) {
        
        String[] nombreColumnas = new String[tablaFinal.getListaAtributos().size()];

        for (int i = 0; i < tablaFinal.getListaAtributos().size(); i++) {
            nombreColumnas[i] = tablaFinal.getListaAtributos().get(i).getNombre();
        }       

        modelo.setColumnIdentifiers(nombreColumnas);
        
        if (!indicesFinal.isEmpty()) {
            try {

                Object[] fila = new Object[modelo.getColumnCount()];

                // Recorre las filas
                for (int ind : indicesFinal) {
                    
                    // Recorre los atributos
                    for (int j = 0; j < modelo.getColumnCount(); j++) {

                        fila[j] = tablaFinal.getListaAtributos().get(j).getListaValores().get(ind);
                    }

                    modelo.addRow(fila);
                    
                    // ESCRIBE SOLO 1 FILA SI SE DETECTÓ FUNCIÓN DE AGREGACIÓN
                    if (indicesFinal.indexOf(ind) == 0 && detectoFuncion == true) {
                        break;
                    }
                }
                return modelo;
            } catch (Exception ex) {
                System.err.println("Ha ocurrido un error al leer la tabla");
            }
   
        } else {
            try {

                Object[] fila = new Object[modelo.getColumnCount()];

                for (int i = 0; i < tablaFinal.getListaAtributos().get(0).getListaValores().size(); i++) {

                    for (int j = 0; j < modelo.getColumnCount(); j++) {

                        fila[j] = tablaFinal.getListaAtributos().get(j).getListaValores().get(i);
                    }
                    modelo.addRow(fila);
                    
                    // ESCRIBE SOLO 1 FILA SI SE DETECTÓ FUNCIÓN DE AGREGACIÓN
                    if (i == 0 && detectoFuncion == true) {
                        break;
                    }
                }
                return modelo;
            } catch (Exception ex) {
                System.err.println("Ha ocurrido un error al leer la tabla");
            }
        }

        return null;
    }

    
    
    
    
    public ArrayList realizarOperacion (Cola<Token> operacion, Tabla tablaRes) {
        ArrayList<Integer> valoresResultado = new ArrayList<>();        
        Cola<Token> operacionNumerica;
        String operacionString;
        int resultado;
        
        if (existeID(operacion, tablaRes)) {
            // i ES EL NÚMERO DE REGISTRO ACTUAL
            for (int i = 0 ; i < tablaRes.getListaAtributos().get(0).getListaValores().size() ; i++) {
                
                // CAMBIA TODOS LOS IDS POR SU VALOR RESPECTIVO DEL REGISTRO ACTUAL
                operacionNumerica = cambiar_ID_por_numero(operacion, tablaRes, i);        
                
                operacionString = crearStringOperacion(operacionNumerica);         
            
                InfijasAPosfijas op = new InfijasAPosfijas();
                resultado = op.operarExpresion(operacionString);
                valoresResultado.add(resultado); 
            }  
        } else {
            for (int i = 0 ; i < tablaRes.getListaAtributos().get(0).getListaValores().size() ; i++) {
                operacionString = crearStringOperacion(operacion);
                InfijasAPosfijas op = new InfijasAPosfijas();
                resultado = op.operarExpresion(operacionString);
                valoresResultado.add(resultado);
            }
        }
        return valoresResultado;
    }

    public boolean existeID (Cola<Token> operacion, Tabla tablaRes) {
        for (int i = 0 ; i < operacion.getSize() ; i++) {
            if (operacion.buscar_por_orden(i).getTipo().equals("ID")) {
                // VERIFICA QUE NO SEA TIPO 'N'
                Atributo at = buscarAtributo(operacion.buscar_por_orden(i).getTokenValor(), tablaRes);
                if (at.getTipo() != 'N') {
                    JOptionPane.showMessageDialog(null, "Se intentó operar con columna no numérica", "ERROR",
                        JOptionPane.ERROR_MESSAGE);
                }               
                return true;
            }
        }
        return false;
    }
    
    public Cola<Token> cambiar_ID_por_numero (Cola<Token> operacion, Tabla tablaRes, int numRegistro) {
        int valorActual;
        Cola<Token> operacionCopia = copiarCola(operacion);
        
        // RECORRE COLA PARA BUSCAR TOKENS QUE SEAN DE TIPO ID
        for (int i = 0 ; i < operacionCopia.getSize() ; i++) {
            Token to = operacionCopia.buscar_por_orden(i);
            
            if (to.getTipo().equals("ID")) {
                
                valorActual = Integer.parseInt(buscarAtributo(to.getTokenValor(), tablaRes).getListaValores().get(numRegistro).toString());
                operacionCopia.buscar_por_orden(i).setTipo("NUMBER");
                operacionCopia.buscar_por_orden(i).setTokenValor(String.valueOf(valorActual));
            }            
        }  
        return operacionCopia;
    }
    
    // COPIADO NORMAL SIGUE REFIRIÉNDOSE A ESA COLA
    public Cola<Token> copiarCola (Cola<Token> cola) {
        Cola<Token> colaCopia = new Cola();
        
        for (int i = 0 ; i < cola.getSize() ; i++) {
            Token to  = new Token();
            to.setTipo(cola.buscar_por_orden(i).getTipo());
            to.setTokenValor(cola.buscar_por_orden(i).getTokenValor());
            to.setIndex(cola.buscar_por_orden(i).getIndex());
            colaCopia.agregar(to);
        }
        
        return colaCopia;
    }
    
    
    
    
    
    public Atributo realizarFuncion (Cola<Token> funcion, Tabla tablaRes) {
        
        String identFuncion = funcion.buscar_por_orden(0).getTipo();
        Atributo atributoRes;
        
        if (identFuncion.equals(Tokenizer.MAX)) {
            // CAMBIA DETECTOFUNCION PARA QUE SOLO MUESTRE 1 CAMPO
            this.detectoFuncion = true;
            
            if (funcion.getSize() != 4) {
                JOptionPane.showMessageDialog(null, "Se brindó un número erróneo de parámetros para la función", "ERROR",
                            JOptionPane.ERROR_MESSAGE);
                return null;
            }
            atributoRes = AplicarMax(funcion.buscar_por_orden(2), tablaRes);
            return atributoRes;
        }
        
        if (identFuncion.equals(Tokenizer.MIN)) {
            // CAMBIA DETECTOFUNCION PARA QUE SOLO MUESTRE 1 CAMPO
            this.detectoFuncion = true;
            
            if (funcion.getSize() != 4) {
                JOptionPane.showMessageDialog(null, "Se brindó un número erróneo de parámetros para la función", "ERROR",
                            JOptionPane.ERROR_MESSAGE);
                return null;
            }
            atributoRes = AplicarMin(funcion.buscar_por_orden(2), tablaRes);
            return atributoRes;
        }
        
        if (identFuncion.equals(Tokenizer.AVG)) {
            // CAMBIA DETECTOFUNCION PARA QUE SOLO MUESTRE 1 CAMPO
            this.detectoFuncion = true;
            
            if (funcion.getSize() != 4) {
                JOptionPane.showMessageDialog(null, "Se brindó un número erróneo de parámetros para la función", "ERROR",
                            JOptionPane.ERROR_MESSAGE);
                return null;
            }
            atributoRes = AplicarAvg(funcion.buscar_por_orden(2), tablaRes);
            return atributoRes;
        }        
        return null;
    }
    
    public Atributo AplicarMax(Token tok, Tabla tablaRes) {
        if (tok.getTipo().equals(Tokenizer.ID)) {
        Atributo atributo = buscarAtributo(tok.getTokenValor(), tablaRes);

            if (atributo != null) {
                if (atributo.getTipo() == 'N') {
                    Object maximo = encontrarMaximo(atributo.getListaValores());

                    Atributo atributoMaximo = new Atributo(Integer.parseInt((String)maximo),tablaRes.getListaAtributos().get(0).getListaValores().size());
                    atributoMaximo.setNombre("MAX(" + atributo.getNombre().toUpperCase() +")");
                
                    return atributoMaximo;
//                    tablaFinal.agregarAtributo(atributoMaximo);
                } else {
                    JOptionPane.showMessageDialog(null, "El atributo debe ser de tipo numérico para aplicar MAX", "ERROR",
                        JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(null, "No se encontró el atributo buscado", "ERROR",
                    JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(null, "Se esperaba un ID para aplicar MAX", "ERROR",
                JOptionPane.ERROR_MESSAGE);
        }
        return null;
    }

    // Método auxiliar para encontrar el máximo valor en una lista de objetos comparables
    private Object encontrarMaximo(ArrayList<Object> listaValores) {
        if (!listaValores.isEmpty()) {
            Object maximo = listaValores.get(0);
            for (Object valor : listaValores) {
                if (((Comparable) valor).compareTo(maximo) > 0) {
                    maximo = valor;
                }
            }
            return maximo;
        } else {
            // Manejar el caso cuando la lista está vacía
            return null;
        }
    }

    // Método para aplicar la función MIN a un atributo
    public Atributo AplicarMin(Token tok, Tabla tablaRes) {
        if (tok.getTipo().equals(Tokenizer.ID)) {
            Atributo atributo = buscarAtributo(tok.getTokenValor(), tablaRes);

            if (atributo != null) {
                if (atributo.getTipo() == 'N') {
                    Object minimo = encontrarMinimo(atributo.getListaValores());

                    Atributo atributoMinimo = new Atributo(Integer.parseInt((String) minimo), tablaRes.getListaAtributos().get(0).getListaValores().size());
                    atributoMinimo.setNombre("MIN(" + atributo.getNombre().toUpperCase() + ")");

                    return atributoMinimo;
                    //tablaFinal.agregarAtributo(atributoMinimo);
                } else {
                    JOptionPane.showMessageDialog(null, "El atributo debe ser de tipo numérico para aplicar MIN", "ERROR",
                            JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(null, "No se encontró el atributo buscado", "ERROR",
                        JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(null, "Se esperaba un ID para aplicar MIN", "ERROR",
                    JOptionPane.ERROR_MESSAGE);
        }
        return null;
    }

    // Método auxiliar para encontrar el mínimo valor en una lista de objetos comparables
    private Object encontrarMinimo(ArrayList<Object> listaValores) {
        if (!listaValores.isEmpty()) {
            Object minimo = listaValores.get(0);
            for (Object valor : listaValores) {
                if (((Comparable) valor).compareTo(minimo) < 0) {
                    minimo = valor;
                }
            }
            return minimo;
        } else {
            // Manejar el caso cuando la lista está vacía
            return null;
        }
    }
    
    // Método para aplicar la función AVG a un atributo
    public Atributo AplicarAvg(Token tok, Tabla tablaRes) {
        if (tok.getTipo().equals(Tokenizer.ID)) {
            Atributo atributo = buscarAtributo(tok.getTokenValor(), tablaRes);

            if (atributo != null) {
                if (atributo.getTipo() == 'N') {
                    double promedio = encontrarPromedio(atributo.getListaValores());

                    Atributo atributoPromedio = new Atributo(String.format("%.4f", promedio), tablaRes.getListaAtributos().get(0).getListaValores().size());
                    atributoPromedio.setNombre("AVG(" + atributo.getNombre().toUpperCase() + ")");

                    return atributoPromedio;
                    //tablaFinal.agregarAtributo(atributoPromedio);
                } else {
                    JOptionPane.showMessageDialog(null, "El atributo debe ser de tipo numérico para aplicar AVG", "ERROR",
                            JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(null, "No se encontró el atributo buscado", "ERROR",
                        JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(null, "Se esperaba un ID para aplicar AVG", "ERROR",
                    JOptionPane.ERROR_MESSAGE);
        }
        return null;
    }

    // Método auxiliar para encontrar el promedio en una lista de objetos numericos
    private double encontrarPromedio(ArrayList<Object> listaValores) {
        if (!listaValores.isEmpty()) {
            double suma = 0;
            for (Object valor : listaValores) {
                suma += Double.parseDouble(valor.toString());
            }
            return suma / listaValores.size();
        } else {
            // Manejar el caso cuando la lista está vacía
            return 0;
        }
    }
    
    public ArrayList<Integer> ordenarAscendente(ArrayList<Object> lista) {
        int n = lista.size();
        ArrayList<Integer> indices = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            indices.add(i); // Inicializar índices
        }

        for (int i = 0; i < n - 1; i++) {
            for (int j = 0; j < n - i - 1; j++) {
                if ( Integer.parseInt(String.valueOf(lista.get(j))) >  Integer.parseInt(String.valueOf(lista.get(j + 1)))) {
                    // Intercambiar valores
//                    int temp = Integer.parseInt(String.valueOf(lista.get(j)));
//                    lista.set(j, lista.get(j + 1));
//                    lista.set(j + 1, temp);

                    // Intercambiar índices
                    int tempIndex = indices.get(j);
                    indices.set(j, indices.get(j + 1));
                    indices.set(j + 1, tempIndex);
                }
            }
        }
        
        // DEBUG
        System.out.println("ORDEN ASC: "+ indices);
        
        return indices;
    }
    
    public ArrayList<Integer> ordenarDescendente(ArrayList<Object> lista) {
        int n = lista.size();
        ArrayList<Integer> indices = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            indices.add(i); // Inicializar índices
        }

        for (int i = 0; i < n - 1; i++) {
            for (int j = 0; j < n - i - 1; j++) {
                if (Integer.parseInt(String.valueOf(lista.get(j))) < Integer.parseInt(String.valueOf(lista.get(j + 1)))) {
                    // Intercambiar valores
//                    int temp = Integer.parseInt(String.valueOf(lista.get(j)));
//                    lista.set(j, lista.get(j + 1));
//                    lista.set(j + 1, temp);

                    // Intercambiar índices
                    int tempIndex = indices.get(j);
                    indices.set(j, indices.get(j + 1));
                    indices.set(j + 1, tempIndex);
                }
            }
        }
        // DEBUG
        System.out.println("ORDEN DESC: "+ indices);
        
        return indices;
    }
    
    public String nombrarOperacion(Inst_Select operacion) { 
        String nombre = "";
        
        for (int i = 0 ; i < operacion.getParamCola().getSize() ; i++) {
            nombre = nombre + operacion.getParamCola().buscar_por_orden(i).getTokenValor();          
        }
        
        return nombre;
    }
    
    
    
    
    
    
    
    /*
     * public DefaultTableModel crear_modelo_tabla(Object[] parametros, int
     * num_elementos) {
     * DefaultTableModel modelo = null;
     * if (num_elementos == 2) {
     * //Lista de columnas parametros[0] y parametros[1] nombre tabla
     * modelo = ejec_2_parametros(parametros[0], parametros[1].toString());
     * }
     * if (num_elementos == 3) {
     * modelo = ejec_3_parametros(parametros[0], parametros[2],
     * parametros[1].toString());
     * }
     * return modelo;
     * }
     */

   

    public boolean verificarColumnasEnTabla(Cola<Token> colaColumnasSelect, Tabla tabla_resultado) {

        // Obtenemos la lista de atributos de la tabla
        ArrayList<Atributo> listaAtributos = tabla_resultado.getListaAtributos();

        // Un valor de bandera para poder determinar si hay una columna ajena a la tabla
        boolean columnaEncontradaTabla = false;

        for (int i = 0; i < colaColumnasSelect.getSize(); i++) {

            Token columna = colaColumnasSelect.buscar_por_orden(i);
            String valorColumna = columna.getTokenValor().toUpperCase();

            if (columna.getTipo().equals(Tokenizer.ID)) {
                // Si es ID hacemos la busqueda
                for (Atributo atributo : listaAtributos) {
                    // Si encontramos la columna en la lista de atributos
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
                // Si se encontro la columna buscar por la otra
                columnaEncontradaTabla = false;

            }

        }
        return true;
    }

    private void insertarColumnas(Cola<Token> colaColumnasSelect, Tabla tabla_resultado, DefaultTableModel modelo) {

        // Obtenemos la lista de atributos de la Tabla requerida
        ArrayList<Atributo> listaAtributos = tabla_resultado.getListaAtributos();

        // Iteramos sobre cada columna obtenida de la sentencia SELECT
        for (int i = 0; i < colaColumnasSelect.getSize(); i++) {

            // Obtenemos la columna y lo que nos convenga
            Token columna = colaColumnasSelect.buscar_por_orden(i);
            String valorColumna = columna.getTokenValor().toUpperCase();
            String tipoColumna = columna.getTipo().toUpperCase();

            switch (tipoColumna) {
                case Tokenizer.ID:
                    Atributo atributoColumna = null;
                    // Buscaremos el atributo que representa la columna
                    for (Atributo atributo : listaAtributos) {
                        String nombreAtributo = atributo.getNombre().toUpperCase();
                        if (valorColumna.equals(nombreAtributo)) {
                            atributoColumna = atributo;
                            // salimos del for puesto que ya encontramos el atributo relacionado
                            break;
                        }
                    }
                    // insertamos la columna en la tabla con la lista de valores del atributo
                    if (atributoColumna != null) {
                        int sizeAtributoColumna = atributoColumna.getListaValores().size();
                        // Creo un objeto que represente una columna
                        Object[] columnaModelo = new Object[sizeAtributoColumna];

                        // paso los valores de la lista de valores en la columna
                        for (int fila = 0; fila < sizeAtributoColumna; fila++) {
                            columnaModelo[fila] = atributoColumna.getListaValores().get(fila);
                        }
                        // añadimos la columna a la Jtable
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
                    // insertamos todas las columnas
                    for (Atributo atributo : listaAtributos) {
                        // obtenemos la cantidad de filas
                        int sizeAtributoColumna = atributo.getListaValores().size();
                        Object[] columnaModelo = new Object[sizeAtributoColumna];

                        // paso los valores de la lista de valores en la columna
                        for (int fila = 0; fila < sizeAtributoColumna; fila++) {
                            columnaModelo[fila] = atributo.getListaValores().get(fila);
                        }
                        // añadimos la columna a la Jtable
                        modelo.addColumn(atributo.getNombre(), columnaModelo);
                    }
                    break;

            }

        }

    }

    private void insertarConstante(String valorColumna, DefaultTableModel modelo, ArrayList<Atributo> listaAtributos) {

        // Obtenemos la cantidad de filas o registros de un atributo cualquiera
        int cantRegistrosTabla = listaAtributos.get(0).getListaValores().size();

        // Creamos un atributo temporal falso
        Atributo atributoColumna = new Atributo();
        // Rellenamos la lista de valores con el valor constante
        for (int i = 0; i < cantRegistrosTabla; i++) {
            atributoColumna.agregarValor(valorColumna);
        }

        // Creo un objeto que represente una columna
        Object[] columnaModelo = new Object[cantRegistrosTabla];

        // paso los valores de la lista de valores en la columna
        for (int i = 0; i < cantRegistrosTabla; i++) {
            columnaModelo[i] = atributoColumna.getListaValores().get(i);
        }
        // añadimos la columna a la Jtable
        modelo.addColumn(valorColumna, columnaModelo);

    }

    private boolean buscarAritméticas(Object parametroListaColumnas) {
        Cola<Token> colaSelect = (Cola<Token>) parametroListaColumnas;
        boolean tieneArit = false;

        for (int i = 0; i < colaSelect.getSize(); i++) {
            if (colaSelect.buscar_por_orden(i).getTipo().equals("ARIT")) {
                tieneArit = true;
            }
        }
        return tieneArit;
    }

    private String crearStringOperacion(Object parametroListaColumnas) {
        Cola<Token> colaSelect = (Cola<Token>) parametroListaColumnas;
        String operacion = "";

        for (int i = 0; i < colaSelect.getSize(); i++) {           
            operacion = operacion + colaSelect.buscar_por_orden(i).getTokenValor();           
        }

        // DEBUG
        System.out.println("String: " + operacion);

        return operacion;
    }

    public Cola<Token> modificarColaColumnas(Cola<Token> columnasSelect, int resultado) {
        int indiceFinal = 0;

        for (int i = 0; i < columnasSelect.getSize(); i++) {
            if (columnasSelect.buscar_por_orden(i).getTipo().equals("ARIT")) {
                indiceFinal = i;
            }
        }

        columnasSelect.buscar_por_orden(indiceFinal).setTipo("NUMBER");
        columnasSelect.buscar_por_orden(indiceFinal).setTokenValor(Integer.toString(resultado));

        return columnasSelect;
    }

    public DefaultTableModel ejec_3_parametros(Object parametroListaColumnas, Object parametroListaLogica,
            String nombreTabla) {
        DefaultTableModel modelo = new DefaultTableModel();

        Tabla tabla_resultado = null;

        for (Tabla tabla : base) {
            if (nombreTabla.equals(tabla.getNombreTabla())) {
                tabla_resultado = tabla;
            }
        }

        // No se encontró la tabla
        if (tabla_resultado == null) {
            System.out.println("Tabla no encontrada " + nombreTabla);
            return null;
        }

        if (parametroListaColumnas == null) {
            System.out.println("No se identifico al lista de columnas para procesar");
            return null;
        }

        // Downcasting
        // traemos el token de 'columnas' identificadas
        Cola<Token> colaColumnasSelect = (Cola<Token>) parametroListaColumnas;
        colaColumnasSelect.imprimirCola();

        // traemos el token de columnas logicas
        Cola<Token> tokensLogicos = (Cola<Token>) parametroListaLogica;
        tokensLogicos.imprimirCola();

        if (!verificarColumnasEnTabla(colaColumnasSelect, tabla_resultado)) {
            return null;
        }

        if (!verificarColumnasEnTabla(tokensLogicos, tabla_resultado)) {
            return null;
        }

        // ahora si, el trabajo duro
        // Hemos obtenido la cola de elemntos posfijos
        Cola<Object> colaElementosPosfijos = convertirPosfija(tokensLogicos, tabla_resultado);
        colaElementosPosfijos.imprimirCola();

        // Ahora a obtener los indices que no cumplen con la expresion booleana
        ArrayList<Integer> indicesCumplenWhere = obtenerIndicesWhere(colaElementosPosfijos, tabla_resultado);
        // indices que cumplen con la condicion
        System.out.println(indicesCumplenWhere.toString());

        // Si comprobamos que todas las tablas estan OK procedemos a mostrar la tabla
        // Query
        if (verificarColumnasEnTabla(colaColumnasSelect, tabla_resultado)
                && verificarColumnasEnTabla(tokensLogicos, tabla_resultado)) {

            insertarColumnasWhere(colaColumnasSelect, tabla_resultado, modelo, indicesCumplenWhere);

        }

        return modelo;

    }

    private void insertarColumnasWhere(Cola<Token> colaColumnasSelect, Tabla tabla_resultado, DefaultTableModel modelo,
            ArrayList<Integer> indicesCumplenWhere) {

        // Obtenemos la lista de atributos de la Tabla requerida
        ArrayList<Atributo> listaAtributos = tabla_resultado.getListaAtributos();

        // Iteramos sobre cada columna obtenida de la sentencia SELECT
        for (int i = 0; i < colaColumnasSelect.getSize(); i++) {
            // Obtenemos la columna y lo que nos convenga
            Token columna = colaColumnasSelect.buscar_por_orden(i);
            String valorColumna = columna.getTokenValor().toUpperCase();
            String tipoColumna = columna.getTipo().toUpperCase();

            switch (tipoColumna) {
                case Tokenizer.ID:
                    Atributo atributoColumna = null;
                    // Buscaremos el atributo que representa la columna
                    for (Atributo atributo : listaAtributos) {
                        String nombreAtributo = atributo.getNombre().toUpperCase();
                        if (valorColumna.equals(nombreAtributo)) {
                            atributoColumna = atributo;
                            // salimos del for puesto que ya encontramos el atributo relacionado
                            break;
                        }
                    }
                    // insertamos la columna en la tabla con la lista de valores del atributo
                    if (atributoColumna != null) {
                        int sizeAtributoColumna = indicesCumplenWhere.size();
                        // Creo un objeto que represente una columna
                        Object[] columnaModelo = new Object[sizeAtributoColumna];

                        // paso los valores de la lista de valores en la columna que cumplen el where
                        int index = 0;
                        for (Integer fila : indicesCumplenWhere) {
                            columnaModelo[index] = atributoColumna.getListaValores().get(fila);
                            index++;
                        }
                        // añadimos la columna a la Jtable
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
                    // insertamos todas las columnas
                    for (Atributo atributo : listaAtributos) {
                        // obtenemos la cantidad de filas
                        int sizeAtributoColumna = indicesCumplenWhere.size();
                        Object[] columnaModelo = new Object[sizeAtributoColumna];

                        // paso los valores de la lista de valores en la columna
                        int index = 0;
                        for (Integer fila : indicesCumplenWhere) {
                            columnaModelo[index] = atributo.getListaValores().get(fila);
                            index++;
                        }
                        // añadimos la columna a la Jtable
                        modelo.addColumn(atributo.getNombre(), columnaModelo);
                    }
                    break;

            }

        }

    }

    private void insertarConstanteWhere(String valorColumna, DefaultTableModel modelo,
            ArrayList<Atributo> listaAtributos, ArrayList<Integer> indicesCumplenWhere) {

        // Obtenemos la cantidad de filas o registros de un atributo cualquiera
        int cantRegistrosTabla = indicesCumplenWhere.size();

        // Creamos un atributo temporal falso
        Atributo atributoColumna = new Atributo();
        // Rellenamos la lista de valores con el valor constante
        for (int i = 0; i < cantRegistrosTabla; i++) {
            atributoColumna.agregarValor(valorColumna);
        }

        // Creo un objeto que represente una columna
        Object[] columnaModelo = new Object[cantRegistrosTabla];

        // paso los valores de la lista de valores en la columna
        for (int i = 0; i < cantRegistrosTabla; i++) {
            columnaModelo[i] = atributoColumna.getListaValores().get(i);
        }
        // añadimos la columna a la Jtable
        modelo.addColumn(valorColumna, columnaModelo);

    }

    // Con este metodo planeamos primero obtener la cola TokensLogicos en forma
    // posfija
    // Luego reconocer los atributos que estan en esa cola posfija
    private Cola<Object> convertirPosfija(Cola<Token> tokensLogicos, Tabla tabla_resultado) {

        // obtenemos el orden posfijo de los Tokens
        Cola<Token> tokensPosfijo = obtenerColaTokenPosfija(tokensLogicos);
        tokensPosfijo.imprimirCola();

        // ahora obtenemos la cola posfija de Atributos y Tokens Operadores
        Cola<Object> elementosPosfijos = obtenerColaElementosPosfijos(tokensPosfijo, tabla_resultado);

        return elementosPosfijos;
    }

    private Cola<Token> obtenerColaTokenPosfija(Cola<Token> tokensLogicos) {

        // Mi pila de operadores
        Pila<Token> pilaOperadores = new Pila<>();
        // Nueva cola posfija
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
                // Si es un operador revisar las precedencias antes de hacer un movimiento
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
                    while (!pilaOperadores.isEmpty() && precedencia(tipoTokenActual) <= precedencia(
                            pilaOperadores.getTope().getDato().getTipo())) {
                        tokensPosfijo.agregar((Token) pilaOperadores.pop().getDato());
                    }
                    pilaOperadores.push(tokenActual);
                    break;

                default:
                    System.out.println("Algo salio mal: operador no reconocido " + tokenActual.getTokenValor());
            }
            index++;
        }

        // Agregamos los operadores que quedaron en la pila a la Cola Posfija
        while (!pilaOperadores.isEmpty()) {
            Token tokenOperadorCima = (Token) pilaOperadores.pop().getDato();
            tokensPosfijo.agregar(tokenOperadorCima);
        }

        return tokensPosfijo;
    }

    private Cola<Object> obtenerColaElementosPosfijos(Cola<Token> tokensPosfijo, Tabla tabla_resultado) {

        // lista atributos de la tabla
        ArrayList<Atributo> listaAtributosTabla = tabla_resultado.getListaAtributos();
        // Cola de elementos de la expresion Atributos y tokens
        Cola<Object> colaElementosPosfijos = new Cola<>();

        for (int i = 0; i < tokensPosfijo.getSize(); i++) {
            boolean encontroAtributo = false;
            Token tokenActual = tokensPosfijo.buscar_por_orden(i);
            // Guardamos el nombre del token que puede corresponder a una columna
            String nombreToken = tokenActual.getTokenValor().toUpperCase();
            // Buscamos la columna en la lista de atributos
            for (int j = 0; j < listaAtributosTabla.size(); j++) {
                String nombreAtributoActual = listaAtributosTabla.get(j).getNombre().toUpperCase();
                if (nombreToken.equals(nombreAtributoActual)) {
                    // Agregamos el atributo coincidente
                    colaElementosPosfijos.agregar(listaAtributosTabla.get(j));
                    encontroAtributo = true; // Se encontro el atributo
                    break;
                }
            }
            // Entonces si no se encontro atributo debe ser un operador u otro token
            if (!encontroAtributo) {
                colaElementosPosfijos.agregar(tokenActual);
            }
        }

        // Retorna la cola de atributos y operadores
        return colaElementosPosfijos;
    }

    // Metodo para resolver expresiones posfijas
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
                        pilaExpresion.push(
                                Integer.valueOf((String) ((Atributo) elemento).getListaValores().get(indexAtributo)));
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
                // Operador desconocido
                return -1;

        }
    }

}
