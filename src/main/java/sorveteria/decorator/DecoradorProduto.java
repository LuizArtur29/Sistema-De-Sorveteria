package sorveteria.decorator;

import sorveteria.factory.Produto;

public class DecoradorProduto extends Produto {
    private final Produto produtoDecorado;
    private final TipoAdicional adicional;

    public DecoradorProduto(Produto produtoDecorado, TipoAdicional adicional) {
        super(produtoDecorado.getNome() + " + " + adicional.getNome(),
                produtoDecorado.getPreco() + adicional.getPreco());
        this.produtoDecorado = produtoDecorado;
        this.adicional = adicional;
    }

    public TipoAdicional adicional() {
        return adicional;
    }
}
