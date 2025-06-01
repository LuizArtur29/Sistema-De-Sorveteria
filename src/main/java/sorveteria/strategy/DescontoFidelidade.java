package sorveteria.strategy;

public class DescontoFidelidade implements DescontoStrategy{

    public double aplicarDesconto(double valorOriginal) {
        return valorOriginal * 0.90; // 10% de desconto
    }
}