package sorveteria.decorator;

public enum TipoAdicional {
    GRANULADO("Granulado", 1.00),
    AMENDOIN("Amendoin", 1.50),

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
