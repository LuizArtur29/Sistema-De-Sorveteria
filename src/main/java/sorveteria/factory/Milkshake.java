package sorveteria.factory;

import sorveteria.decorator.SaborBase;

public class Milkshake extends Produto {
    private final SaborBase sabor;

    public Milkshake(SaborBase sabor) {
        super("Milkshake de " + sabor.getNome(), sabor.getPrecoBase());
        this.sabor = sabor;
    }
}
