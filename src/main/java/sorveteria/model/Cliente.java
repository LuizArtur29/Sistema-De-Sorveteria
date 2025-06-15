package sorveteria.model;

public class Cliente {
    private int id;
    private String nome;
    private String email;

    // NOVO CONSTRUTOR: Para criar um novo cliente cujo ID será gerado pelo BD
    public Cliente() {
        // O ID será definido pelo repositório após a inserção no banco de dados.
        // Outros campos podem ser definidos via setters.
    }

    // Construtor para carregar clientes existentes do BD (com ID já existente)
    public Cliente(int id, String nome, String email) {
        this.setId(id);
        this.setNome(nome);
        this.setEmail(email);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return "Cliente{" +
                "id=" + id +
                ", nome='" + nome + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}