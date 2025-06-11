// src/main/java/sorveteria/state/EntregueState.java
package sorveteria.state;

import sorveteria.model.Pedido;

public class EntregueState implements EstadoPedido {

    public void avancar(Pedido pedido) {
        System.out.println("Pedido já entregue!");
    }

    @Override
    public void cancelar(Pedido pedido) {
        System.out.println("Pedido já foi entregue e não pode ser cancelado.");
    }

    public String getDescricao() {
        return "Entregue";
    }
}