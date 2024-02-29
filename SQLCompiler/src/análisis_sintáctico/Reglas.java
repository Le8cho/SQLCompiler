/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package análisis_sintáctico;

/**
 *
 * @author utente
 */
public class Reglas {
    
    private Object[] parametros = new Object[6];
    
    // Si es una regla secuencial, sumar al contador antes de buscar otro token
    public Object[] select() {   
        if (!lista_columnas()) return null;
        Sintáctico.contador_cola++;
        //Si el siguiente token es diferente de FROM retornar null
        if (!Sintáctico.tipo_actual().equals("FROM")) return null;
        Sintáctico.contador_cola++;
        if (!lista_tablas()) return null;
        return parametros;
    }
    
    public boolean lista_columnas() {
        return columna();
    }
    
    public boolean lista_tablas() {
        return nombre_tabla();
    }
    
    // Falta ID.ID y <expresión_aritmética>
    // TODO: Array para agregar más columnas
    public boolean columna() {
        if (!Sintáctico.tipo_actual().equals("ID")) {
            if (!Sintáctico.tipo_actual().equals("*")) return false;           
            parametros[0] = Sintáctico.valor_actual();//agregar el asterisco como primer parametro
            return true;
        }
        parametros[0] = Sintáctico.valor_actual();//agregar el identificador como segundo parametro    
        return true;
    }
    
    // FALTA ID AS ID
    public boolean nombre_tabla() {
        if (!Sintáctico.tipo_actual().equals("ID")) return false;
        parametros[1] = Sintáctico.valor_actual();
        return true;
    }

}
