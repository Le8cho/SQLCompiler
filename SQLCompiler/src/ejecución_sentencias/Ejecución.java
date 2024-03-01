/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ejecución_sentencias;

import EDD.Cola;
import Lector_Datos.Atributo;
import Lector_Datos.Tabla;
import analizador_lexico.Token;
import analizador_lexico.Tokenizer;
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

        //Downcasting
        Cola<Token> colaColumnasSelect = (Cola<Token>) parametroListaColumnas;
        
        System.out.println(verificarColumnasEnTabla(colaColumnasSelect, tabla_resultado));

//        // SI EL PARÁMETRO DE COLUMNA ES "*", SE SELECCIONAN TODAS LAS COLUMNAS
//        if (columna.equals("*")) {            
//            String[] nombreColumnas = new String[tabla_resultado.getListaAtributos().size()];
//            
//            for (int i = 0 ; i < tabla_resultado.getListaAtributos().size() ; i++) {
//                nombreColumnas[i] = tabla_resultado.getListaAtributos().get(i).getNombre();
//            }
//        
//            modelo.setColumnIdentifiers(nombreColumnas);  
//            
//            Object[] fila = new Object[modelo.getColumnCount()];
//            
//            //Itera el numero de registros que tenemos de las tablas == numero de filas de la tabla
//            for (int i = 0 ; i < tabla_resultado.getListaAtributos().get(0).getListaValores().size() ; i++) { 
//                
//                for (int j = 0 ; j < modelo.getColumnCount() ; j++) {
//                    
//                    fila[j] = tabla_resultado.getListaAtributos().get(j).getListaValores().get(i);                    
//                }
//                modelo.addRow(fila);              
//            } 
//            return modelo;
//        }
//        // TODO: Establecer todos los demás casos con if's
//        // EN ESTE CASO: SOLO 1 COLUMNA
//        else {
//            String[] nombreColumnas = new String[1];
//            int indAtributo = 0;
//            
//            //Recorremos la lista de atributos
//            for (int i = 0 ; i < tabla_resultado.getListaAtributos().size() ; i++) {
//                //si la columna que buscamos (mayusculas) es igual al nombre del atributo de la iteracion
//                if (columna.toUpperCase().equals(tabla_resultado.getListaAtributos().get(i).getNombre().toUpperCase())) {
//                    //asiganmos la columna al array nombreColumnas
//                    nombreColumnas[0] = columna;
//                    //guardamos el indice del atributo requerido
//                    indAtributo = i;
//                }               
//            }
//            
//            //A nuestra tabla colocamos el identificador de cada columna (En este caso solo 1)
//            modelo.setColumnIdentifiers(nombreColumnas); 
//            
//            Object[] fila = new Object[modelo.getColumnCount()];
//            
//            //Iteramos la cantidad de valores que tiene el atributo
//            for (int i = 0 ; i < tabla_resultado.getListaAtributos().get(indAtributo).getListaValores().size() ; i++) { 
//                
//                // FILAS DE SOLO 1 COLUMNA
//                fila[0] = tabla_resultado.getListaAtributos().get(indAtributo).getListaValores().get(i);                    
//                
//                modelo.addRow(fila);               
//            }  
//            return modelo;
//        }
        return modelo;
    }

    public boolean verificarColumnasEnTabla(Cola<Token> colaColumnasSelect, Tabla tabla_resultado) {

        //Obtenemos la lista de atributos de la tabla
        ArrayList<Atributo> listaAtributos = tabla_resultado.getListaAtributos();

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

}
