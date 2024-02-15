
package hola;

import cola.Cola;


public class pruebaCola {
    public static void main(String[] args) throws Exception {
        Cola micola = new Cola();
        micola.agregar("hola");
        micola.agregar("como");
        micola.agregar("estas");
        
        micola.imprimirCola();
        
        micola.eliminar();
        micola.eliminar();
        micola.eliminar();
        
        
        micola.imprimirCola();
        
        
    }
}
