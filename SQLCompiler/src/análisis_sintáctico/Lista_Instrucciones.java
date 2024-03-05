/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package análisis_sintáctico;

import EDD.Cola;
import analizador_lexico.Token;
import java.util.ArrayList;

/**
 *
 * @author utente
 */
public class Lista_Instrucciones {
 
    private String tabla;
    private ArrayList<Inst_Select> listaSelect;
    private ArrayList<Cola<Token>> listaWhere;
    private Cola<Token> instrOrder;

    public Lista_Instrucciones() {
        this.listaSelect = new ArrayList();
        this.listaWhere = new ArrayList();
        this.tabla = "";
        this.instrOrder = new Cola<Token>();
    }
      
    public void insertarSelect(Cola<Token> cola) {
        Inst_Select inst = new Inst_Select(cola);
        this.listaSelect.add(inst);
    }
    
    public void insertarWhere(Cola<Token> cola) {
        this.listaWhere.add(cola);
    }

    public String getTabla() {
        return tabla;
    }
    public void setTabla(String tabla) {
        this.tabla = tabla;
    }

    public ArrayList<Inst_Select> getListaSelect() {
        return listaSelect;
    }

    public void setListaSelect(ArrayList<Inst_Select> listaSelect) {
        this.listaSelect = listaSelect;
    }

    public ArrayList<Cola<Token>> getListaWhere() {
        return listaWhere;
    }

    public void setListaWhere(ArrayList<Cola<Token>> listaWhere) {
        this.listaWhere = listaWhere;
    }   

    public Cola<Token> getInstrOrder() {
        return instrOrder;
    }

    public void setInstrOrder(Cola<Token> instrOrder) {
        this.instrOrder = instrOrder;
    }
    
}
