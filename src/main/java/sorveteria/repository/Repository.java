package sorveteria.repository;

import java.util.List;
import java.util.Optional;

public interface Repository<T, ID> {
    T salvar(T entity);
    Optional<T> buscarPorId(ID id);
    List<T> buscarTodos();
    void deletar(ID id);
}