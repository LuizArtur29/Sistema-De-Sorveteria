package sorveteria.command;

import sorveteria.model.Pedido;
import sorveteria.state.CanceladoState;
import sorveteria.state.EstadoPedido;

public class CancelarPedidoCommand implements Command {
    private Pedido pedido;
    private EstadoPedido estadoAnterior;

    public CancelarPedidoCommand(Pedido pedido) {
        this.pedido = pedido;
    }

    public void executar() {
        this.estadoAnterior = pedido.getEstado();
        pedido.cancelar();
        if (pedido.getEstado() instanceof CanceladoState) {
            System.out.println("Comando: Pedido #" + pedido.getId() + " foi cancelado.");
        } else {
            System.out.println("Comando: Pedido #" + pedido.getId() + " não pôde ser cancelado no estado atual.");
        }
    }

    public void desfazer() {
        if (estadoAnterior != null && !(pedido.getEstado() instanceof CanceladoState)) {
            System.out.println("Comando: Desfazendo cancelamento do Pedido #" + pedido.getId() + ". Voltando para: " + estadoAnterior.getDescricao());
            pedido.setEstado(estadoAnterior);
        } else if (estadoAnterior != null && pedido.getEstado() instanceof CanceladoState) {
            System.out.println("Comando: Desfazendo cancelamento do Pedido #" + pedido.getId() + ". Revertendo de Cancelado para: " + estadoAnterior.getDescricao());
            pedido.setEstado(estadoAnterior);
        } else {
            System.out.println("Comando: Não é possível desfazer o cancelamento do Pedido #" + pedido.getId() + ". Estado anterior não registrado ou pedido não foi cancelado.");
        }
    }
}
