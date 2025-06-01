package sorveteria.decorator;

import sorveteria.model.Produto;

public class DecoradorProduto extends Produto {
    private Produto produtoDecorado;

    public DecoradorProduto(Produto produtoDecorado, String nome, double preco) {
        super(nome,preco);
        this.produtoDecorado = produtoDecorado;
    }


    public String getName() {
        return produtoDecorado.getNome() + " + " + super.getNome();
    }

    public double getPreco() {
        return produtoDecorado.getPreco() + super.getPreco();
    }
}
