package sorveteria.state;

import sorveteria.model.Pedido;

public class EntregueState implements EstadoPedido {

    public void avancar(Pedido pedido) {
        System.out.println("Pedido já entregue!");
    }

    public String getDescricao() {
        return "Entregue";
    }
}

