package sorveteria.observer;

public interface Observadores {
    void adicionarObserver(PedidoObserver observer);
    void removerObserver(PedidoObserver observer);
    void notificarObserver();
}
