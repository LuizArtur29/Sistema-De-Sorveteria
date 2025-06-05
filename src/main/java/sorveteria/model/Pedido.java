package sorveteria.model;

import sorveteria.observer.Observadores;
import sorveteria.observer.PedidoObserver;
import sorveteria.state.*;

import java.security.PublicKey;
import java.util.ArrayList;
import java.util.List;

public class Pedido implements Observadores {
    private EstadoPedido estado;
    private String id;
    private List<PedidoObserver> observers;

    public Pedido(String id) {
        this.id = id;
        this.estado = new RecebidoState(); // Estado inicial
        this.observers = new ArrayList<>();
    }

    public void avancarEstado() {
        estado.avancar(this);
        System.out.println("Pedido #" + id + " - Estado: " + estado.getDescricao());
        notificarObserver();
    }

    public void cancelar() {
        estado.cancelar(this);
        notificarObserver();
    }

    // Getters e Setters
    public EstadoPedido getEstado() {
        return estado;
    }

    public void setEstado(EstadoPedido estado) {
        this.estado = estado;
    }

    public String getId() {
        return id;
    }

    public void adicionarObserver(PedidoObserver observer) {
        this.observers.add(observer);
    }

    public void removerObserver(PedidoObserver observer) {
        this.observers.remove(observer);
    }

    public void notificarObservers() {
        for (PedidoObserver observer : observers) {
            observer.atualizar(this);
        }
    }
}
