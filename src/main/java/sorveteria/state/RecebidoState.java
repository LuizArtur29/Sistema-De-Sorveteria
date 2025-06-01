package sorveteria.state;

import sorveteria.model.Pedido;

public class RecebidoState implements EstadoPedido{

    public void avancar(Pedido pedido) {
        pedido.setEstado(new EmPreparoState());
    }

    public void cancelar(Pedido pedido) {
        pedido.setEstado(new CanceladoState());
    }

    public String getDescricao() {
        return "Pedido recebido";
    }
}
