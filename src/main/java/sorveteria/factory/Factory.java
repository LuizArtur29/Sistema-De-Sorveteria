package sorveteria.factory;

import sorveteria.decorator.SaborBase;

public class Factory {
    public Produto criarSorvete(String tipo, SaborBase sabor) {
        switch (tipo.toLowerCase()) {
            case "sorvete":
                return new Sorvete(sabor);
            case "milkshake":
                return new Milkshake(sabor);
            case "picole":
                return new Picole(sabor);

        }
        return null;
    }
}
