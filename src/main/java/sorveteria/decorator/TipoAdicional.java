package sorveteria.decorator;

public enum TipoAdicional {
    GRANULADO("Granulado", 0.50),
    AMENDOIN("Amendoin", 0.75),

    ;

    private final String nome;
    private final double preco;

    TipoAdicional(String nome, double preco) {
        this.nome = nome;
        this.preco = preco;
    }

    public String getNome() {
        return nome;
    }

    public double getPreco() {
        return preco;
    }
}
