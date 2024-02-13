
package sqlpackage;

public class Token {
    private TipoToken tipo;
    private String tokenValor;

    public TipoToken getTipo() {
        return tipo;
    }

    public void setTipo(TipoToken tipo) {
        this.tipo = tipo;
    }

    public String getTokenValor() {
        return tokenValor;
    }

    public void setTokenValor(String tokenValor) {
        this.tokenValor = tokenValor;
    }
    
    public Token (TipoToken tipo, String tokenValor){
        this.tipo = tipo;
        this.tokenValor = tokenValor;
    }
}
