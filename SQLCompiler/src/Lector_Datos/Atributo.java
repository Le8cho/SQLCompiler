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
    private final ArrayList<Object> listaValores = new ArrayList<>();

    public void agregarValor(Object val) {
        this.listaValores.add(val);
    }

    public ArrayList<Object> getListaValores() {
        return listaValores;
    }
    
    public Atributo(int codigo, String nombre, char tipo, int longitud, int decimales) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.tipo = tipo;
        this.longitud = longitud;
        this.decimales = decimales;
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

