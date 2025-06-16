package sorveteria.model;

import sorveteria.factory.Produto;
import sorveteria.observer.PedidoObserver;
import sorveteria.state.*;

import java.util.ArrayList;
import java.util.List;

public class Pedido{
    private EstadoPedido estado;
    private int id;
    private int idCliente;
    private List<PedidoObserver> observers;
    private List<Produto> itens;
    private double valorTotal;

    // NOVO CONSTRUTOR: Para criar um novo pedido cujo ID será gerado pelo BD
    public Pedido() {
        this.estado = new RecebidoState(); // Estado inicial
        this.observers = new ArrayList<>();
        this.itens = new ArrayList<>();
        this.valorTotal = 0.0;
    }

    // Construtor para carregar pedidos existentes do DB (com ID já existente)
    public Pedido(int id, int idCliente, double valorTotal, EstadoPedido estadoInicial) {
        this.id = id;
        this.setIdCliente(idCliente);
        this.observers = new ArrayList<>();
        this.itens = new ArrayList<>();
        this.valorTotal = valorTotal;
        this.estado = estadoInicial;
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
        this.valorTotal = 0.0;
        for (Produto item : itens) {
            this.valorTotal += item.getPreco();
        }
    }

    public EstadoPedido getEstado() {
        return estado;
    }

    public void setEstado(EstadoPedido estado) {
        this.estado = estado;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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


    public int getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(int idCliente) {
        this.idCliente = idCliente;
    }
}