package sorveteria.model;

import sorveteria.state.*;

public class Pedido {
    private EstadoPedido estado;
    private String id;

    public Pedido(String id) {
        this.id = id;
        this.estado = new RecebidoState(); // Estado inicial
    }

    public void avancarEstado() {
        estado.avancar(this);
        System.out.println("Pedido #" + id + " - Estado: " + estado.getDescricao());
    }

    public void cancelar() {
        estado.cancelar(this);
    }

    // Getters e Setters
    public EstadoPedido getEstado() {
        return estado;
    }

    public void setEstado(EstadoPedido estado) {
        this.estado = estado;
    }
}
