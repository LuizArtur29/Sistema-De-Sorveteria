package sorveteria.state;

import sorveteria.model.Pedido;

public class ProntoParaEntregaState implements EstadoPedido {

    public void avancar(Pedido pedido) {
        pedido.setEstado(new EntregueState());
    }

    public void cancelar(Pedido pedido) {
        System.out.println("Pedido pronto não pode ser cancelado!");
    }

    public String getDescricao() {
        return "Pronto para entrega";
    }
}