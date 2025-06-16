package sorveteria.repository;

import sorveteria.model.Cliente;

import java.util.List;
import java.util.Optional;

public interface ClienteRepository {
    void salvar(Cliente cliente);
    Optional<Cliente> buscarPorId(int id);
    List<Cliente> buscarTodos();
    void atualizar(Cliente cliente);
    void deletar(int id);
}