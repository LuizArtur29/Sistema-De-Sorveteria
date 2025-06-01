package sorveteria.singleton;

import java.time.Period;
import java.util.LinkedList;
import java.util.Queue;

public class FilaPedidos {

    private static FilaPedidos instance;
    private final Queue<Pedido> fila;

    private FilaPedidos() {
        this.fila = new LinkedList<>();
    }

    public static synchronized FilaPedidos getInstance() {
        if(instance == null) {
            instance = new FilaPedidos();
        }
        return instance;
    }

    public void adicionarPedido(Pedido pedido) {
        fila.offer(pedido);
        System.out.println("Pedido #" + pedido.getId() + " adicionado à fila.");
    }

    public Pedido processarProximoPedido() {
        return fila.poll();
    }

    public int tamanhoFila() {
        return fila.size();
    }
}
