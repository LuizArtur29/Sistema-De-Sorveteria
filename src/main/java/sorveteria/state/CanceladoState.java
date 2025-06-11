// src/main/java/sorveteria/state/CanceladoState.java
package sorveteria.state;

import sorveteria.model.Pedido;

public class CanceladoState implements EstadoPedido {

    public void avancar(Pedido pedido) {
        System.out.println("Pedido cancelado não pode avançar!");
    }

    @Override
    public void cancelar(Pedido pedido) {
        System.out.println("Pedido já está cancelado e não pode ser cancelado novamente.");
    }

    public String getDescricao() {
        return "Cancelado";
    }
}