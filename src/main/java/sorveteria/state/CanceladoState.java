package sorveteria.state;

import sorveteria.model.Pedido;

public class CanceladoState implements EstadoPedido {

    public void avancar(Pedido pedido) {
        System.out.println("Pedido cancelado não pode avançar!");
    }

    public String getDescricao() {
        return "Cancelado";
    }
}
