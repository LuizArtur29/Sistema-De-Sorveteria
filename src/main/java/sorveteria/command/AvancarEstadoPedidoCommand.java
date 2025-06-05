package sorveteria.command;

import sorveteria.model.Pedido;
import sorveteria.state.EstadoPedido;

public class AvancarEstadoPedidoCommand implements Command {
    private Pedido pedido;
    private EstadoPedido estadoAnterior;

    public AvancarEstadoPedidoCommand(Pedido pedido) {
        this.pedido = pedido;
    }

    public void executar() {
        this.estadoAnterior = pedido.getEstado();
        pedido.avancarEstado();
        System.out.println("Comando: Pedido #" + pedido.getId() + " avançou para o estado: " + pedido.getEstado().getDescricao());
    }

    public void desfazer() {
        if (estadoAnterior != null) {
            System.out.println("Comando: Desfazendo avanço do Pedido #" + pedido.getId() + ". Voltando para: " + estadoAnterior.getDescricao());
            pedido.setEstado(estadoAnterior);
        } else {
            System.out.println("Comando: Não é possível desfazer o avanço do Pedido #" + pedido.getId() + ". Estado anterior não registrado.");
        }
    }
}
