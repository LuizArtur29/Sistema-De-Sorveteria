package sorveteria.model;

import sorveteria.factory.Produto;
import sorveteria.observer.PedidoObserver;
import sorveteria.state.*;

import java.util.ArrayList;
import java.util.List;

public class Pedido{
    private EstadoPedido estado;
    private String id;
    private List<PedidoObserver> observers;
    private List<Produto> itens;
    private double valorTotal;

    public Pedido(String id) {
        this.id = id;
        this.estado = new RecebidoState(); // Estado inicial
        this.observers = new ArrayList<>();
        this.itens = new ArrayList<>();
        this.setValorTotal(0.0);
    }

    // Construtor para ser usado pelo Repository ao carregar dados do DB
    public Pedido(String id, double valorTotal) {
        this.id = id;
        this.observers = new ArrayList<>();
        this.itens = new ArrayList<>();
        this.setValorTotal(valorTotal);
        // O estado inicial não é definido aqui, será carregado pelo PedidoRepository
    }

    public void avancarEstado() {
        estado.avancar(this);
        System.out.println("Pedido #" + id + " - Estado: " + estado.getDescricao());
        notificarObservers();
    }

    public void cancelar() {
        estado.cancelar(this);
        notificarObservers();
    }

    public void adicionarItem(Produto produto) {
        this.itens.add(produto);
        calcularValorTotal();
    }

    public void removerItem(Produto produto) {
        this.itens.remove(produto);
        calcularValorTotal();
    }

    public List<Produto> getItens() {
        return itens;
    }

    private void calcularValorTotal() {
        this.setValorTotal(0.0);
        for (Produto item : itens) {
            this.setValorTotal(this.getValorTotal() + item.getPreco());
        }
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

    public double getValorTotal() {
        return valorTotal;
    }

    public void setValorTotal(double valorTotal) {
        this.valorTotal = valorTotal;
    }
}
