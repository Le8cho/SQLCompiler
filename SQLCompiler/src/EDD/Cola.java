package EDD;

public class Cola<E> {

    private NodoCola cabeza;
    private NodoCola cola;

    public Cola() {
        cabeza = null;
    }
    
    public Cola(E dato){
        this.agregar(dato);   
    }
    
    public void imprimirCola(){
        NodoCola nodo = this.cabeza;
        
        String colaString = "[";
        
        while(nodo != null){
            colaString = colaString.concat(nodo.toString()).concat(",");
            nodo = nodo.getSiguiente();
        }
        
        colaString = colaString.substring(0, colaString.length()-1);
        colaString = colaString.concat("]");
        
        if(this.estaVacio()){
            colaString = "[]";
        }
        
        System.out.println(colaString);
  
    }

    public void agregar(E dato) {
        NodoCola nuevoNodo = new NodoCola(dato);

        if (this.cabeza == null) {
            this.cabeza = nuevoNodo;
            this.cola = nuevoNodo;
        } else {
            NodoCola nodo = cabeza;

            while (nodo.siguiente != null) {
                nodo = nodo.siguiente;
            }
            //nodo.siguiente == null
            nodo.setSiguiente(nuevoNodo);
            this.cola = nuevoNodo;
        }
    }

    //Remueve el primer nodo en la cola
    public void eliminar() throws Exception {
        if (this.estaVacio()) {
            throw new Exception("Error, cola vacía");
        } else {
            //Si la cola no es de un solo elemento
            if (this.cabeza.getSiguiente() != null) {
                NodoCola nodo = this.cabeza;
                this.cabeza = this.cabeza.getSiguiente();
                nodo.setSiguiente(null);
            }
            else{
                this.cabeza = null;
            }

        }
    }

    public boolean buscar(E dato) {

        if (this.estaVacio()) {
            return false;
        } else {
            NodoCola nodo = cabeza;
            while (nodo != null) {
                if (nodo.getDato() == dato) {
                    return true;
                } else {
                    nodo = nodo.siguiente;
                }
            }
            //nodo igual null no se hallo el dato
            return false;
        }
    }

    public boolean estaVacio() {
        return this.cabeza == null;
    }

    private class NodoCola<E> {

        private E dato;
        private NodoCola siguiente;

        public NodoCola(E dato) {
            this.dato = dato;
        }

        public E getDato() {
            return dato;
        }

        public void setDato(E dato) {
            this.dato = dato;
        }

        public NodoCola getSiguiente() {
            return siguiente;
        }

        public void setSiguiente(NodoCola siguiente) {
            this.siguiente = siguiente;
        }

        @Override
        public String toString() {
            return "(" + dato + ")";
        }
    }
}
