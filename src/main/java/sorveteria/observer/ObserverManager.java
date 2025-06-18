package sorveteria.observer;

import sorveteria.model.Pedido;
import java.util.ArrayList;
import java.util.List;

public class ObserverManager implements Observadores {
    private final List<PedidoObserver> observers;
    private final Pedido observableSubject;

    public ObserverManager(Pedido observableSubject) {
        this.observers = new ArrayList<>();
        this.observableSubject = observableSubject;
    }

    @Override
    public void adicionarObserver(PedidoObserver observer) {
        this.observers.add(observer);
    }

    @Override
    public void removerObserver(PedidoObserver observer) {
        this.observers.remove(observer);
    }

    @Override
    public void notificarObservers() {
        for (PedidoObserver observer : observers) {
            observer.atualizar(observableSubject);
        }
    }
}