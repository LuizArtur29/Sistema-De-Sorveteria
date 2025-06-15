package sorveteria.decorator;

public enum SaborBase {
    CHOCOLATE("Chocolate", 8.50),
    MORANGO("Morango", 7.90),
    BAUNILHA("Baunilha", 7.50);

    private final String nome;
    private final double precoBase;

    SaborBase(String nome, double precoBase) {
        this.nome = nome;
        this.precoBase = precoBase;
    }


    public String getNome() {
        return nome;
    }

    public double getPrecoBase() {
        return precoBase;
    }
}
