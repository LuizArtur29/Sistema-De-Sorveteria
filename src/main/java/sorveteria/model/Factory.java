package sorveteria.model;

public class Factory {
    public Produto criarSorvete(String tipo) {
        switch (tipo.toLowerCase()) {
            case "sorvete":
                return new Sorvete();
            case "milkshake":
                return new Milkshake();
            case "picole":
                return new Picole();

        }
        return null;
    }
}
