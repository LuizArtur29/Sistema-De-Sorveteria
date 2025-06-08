package sorveteria.repository;

import sorveteria.model.Cliente;
import sorveteria.model.Pedido;

import java.util.List;
import java.util.Optional;

public interface PedidoRepository {
    void salvar(Pedido pedido);
    Optional<Pedido> buscarPorId(String id);
    List<Pedido> buscarTodos();
    void atualizar(Pedido pedido);
    void deletar(String id);
}