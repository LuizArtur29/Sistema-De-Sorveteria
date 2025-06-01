package sorveteria.state;

import sorveteria.model.Pedido;

public class EmPreparoState implements EstadoPedido {

    public void avancar(Pedido pedido) {
        pedido.setEstado(new ProntoParaEntregaState());
    }

    public void cancelar(Pedido pedido) {
        System.out.println("Pedido em preparo não pode ser cancelado!");
    }

    public String getDescricao() {
        return "Em preparo";
    }
}
