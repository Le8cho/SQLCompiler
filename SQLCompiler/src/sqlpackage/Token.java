package sqlpackage;

public class Token {

    private String tipo = null;
    private String tokenValor = null;
    private int index = -1; 

    public Token() {
    }

    public Token(String tipo, String tokenValor, int index) {
        this.tipo = tipo;
        this.tokenValor = tokenValor;
        this.index = index;
    }

    @Override
    public String toString() {
        return "('" + tipo + "', " + tokenValor + ", '" + index + "')";
    }
    
    
    
    
    public String getTipo() {
        return tipo;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getTokenValor() {
        return tokenValor;
    }

    public void setTokenValor(String tokenValor) {
        this.tokenValor = tokenValor;
    }

}
