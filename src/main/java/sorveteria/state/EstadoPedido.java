package sorveteria.state;

import sorveteria.model.Pedido;

public interface EstadoPedido {
    void avancar(Pedido pedido);
    void cancelar(Pedido pedido);
    String getDescricao();
}
