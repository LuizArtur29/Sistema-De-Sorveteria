package sorveteria.observer;

import sorveteria.model.Pedido;

public class ClienteObserver implements PedidoObserver {
    private String nomeCliente;

    public ClienteObserver(String nomeCliente) {
        this.nomeCliente = nomeCliente;
    }

    public void atualizar(Pedido pedido) {
        System.out.println("Olá, " + nomeCliente + "! O status do seu Pedido #" + pedido.getId() +
                " mudou para: " + pedido.getEstado().getDescricao() + ".");
    }
}