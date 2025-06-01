package sorveteria.factory;

import sorveteria.decorator.SaborBase;

public class Sorvete extends Produto {
    private final SaborBase sabor;

    public Sorvete(SaborBase sabor) {
        super("Sorvete de " + sabor.getNome(), sabor.getPrecoBase());
        this.sabor = sabor;
    }
}
