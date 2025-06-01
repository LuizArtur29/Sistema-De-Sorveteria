package sorveteria.factory;

import sorveteria.decorator.SaborBase;

public class Picole extends Produto {
    private final SaborBase sabor;

    public Picole(SaborBase sabor) {
        super("Picolé de " + sabor.getNome(), sabor.getPrecoBase());
        this.sabor = sabor;
    }
}