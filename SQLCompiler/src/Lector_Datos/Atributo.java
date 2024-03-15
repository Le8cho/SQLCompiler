/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Lector_Datos;

import java.util.ArrayList;

/**
 *
 * @author USER
 */

public class Atributo  {
    private int codigo;
    private String nombre;
    private char tipo;
    private int longitud;
    private int decimales;
    private ArrayList<Object> listaValores = new ArrayList<>();

    public void agregarValor(Object val) {
        this.listaValores.add(val);
    }

    public ArrayList<Object> getListaValores() {
        return listaValores;
    }
    
    public Atributo(){
    }
    
    // Para crear atributos RESULTADOS DE OPERACIÓN
    public Atributo(ArrayList<Object> listaValores){
        this.codigo = 0;
        this.nombre = "OPERACION";
        this.tipo = 'N';
        this.longitud = this.nombre.length();
        this.decimales = 0;
        this.listaValores = listaValores;
    }

    // Para crear atributos CONSTANTES (numerales)
    public Atributo(int constanteInt, int numRegistros){
        this.codigo = 0;
        this.nombre = Integer.toString(constanteInt);
        this.tipo = 'N';
        this.longitud = this.nombre.length();
        this.decimales = 0;
        this.listaValores = crearListaConstante(Integer.toString(constanteInt), numRegistros);
    }
    
    // Para crear atributos CONSTANTES (strings)
    public Atributo(String constanteStr, int numRegistros){
        this.codigo = 0;
        this.nombre = constanteStr;
        this.tipo = 'V';
        this.longitud = this.nombre.length();
        this.decimales = 0;
        this.listaValores = crearListaConstante(constanteStr, numRegistros);
    }   
    
    public Atributo(int codigo, String nombre, char tipo, int longitud, int decimales) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.tipo = tipo;
        this.longitud = longitud;
        this.decimales = decimales;
    }

    private ArrayList<Object> crearListaConstante (String constante, int numRegistros) {
        ArrayList<Object> listaResultado = new ArrayList<>();
        
        for (int i = 0 ; i < numRegistros ; i++) {           
            listaResultado.add(constante);
        }    
        return listaResultado;
    } 
    
   
    public int getCodigo() {
        return codigo;
    }

    public void setCodigo(int codigo) {
        this.codigo = codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public char getTipo() {
        return tipo;
    }

    public void setTipo(char tipo) {
        this.tipo = tipo;
    }

    public int getLongitud() {
        return longitud;
    }

    public void setLongitud(int longitud) {
        this.longitud = longitud;
    }

    public int getDecimales() {
        return decimales;
    }

    public void setDecimales(int decimales) {
        this.decimales = decimales;
    }
}

