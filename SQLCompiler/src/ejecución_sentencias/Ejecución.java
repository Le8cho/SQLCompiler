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
                        System.out.println("o no hay atribut columna o no hay lista de valores");
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

}
