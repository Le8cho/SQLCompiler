/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Lector_Datos;

import java.util.ArrayList;

/**
 *
 * @author utente
 */
public class Tabla {
    
    private int codTabla;
    private String nombreTabla;
    private final ArrayList<Atributo> listaAtributos = new ArrayList<>();

    public ArrayList<Atributo> getListaAtributos() {
        return listaAtributos;
    }

    public void agregarAtributo(Atributo at) {
        this.listaAtributos.add(at);
    }

    public int getCodTabla() {
        return codTabla;
    }

    public void setCodTabla(int codTabla) {
        this.codTabla = codTabla;   
    }    

    public String getNombreTabla() {
        return nombreTabla;
    }

    public void setNombreTabla(String nombreTabla) {
        this.nombreTabla = nombreTabla;
    }
    
    public void imprimirTabla() {
        System.out.println("Código de Tabla: " + this.codTabla);
        System.out.println("Nombre de Tabla: " + this.nombreTabla);
        
        
        for (int i = 0 ; i < listaAtributos.get(0).getListaValores().size() ; i++) {
            for (Atributo at : listaAtributos) {
                System.out.print(at.getNombre() + " " + at.getListaValores().get(i) + "\t");
            }
            System.out.println("");
        }
    } 
    
}
