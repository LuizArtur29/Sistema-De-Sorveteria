package sorveteria.decorator;

public enum SaborBase {
    CHOCOLATE("Chocolate", 0.50),
    MORANGO("Morango", 0.50),
    BAUNILHA("Baunilha", 0.50);

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
