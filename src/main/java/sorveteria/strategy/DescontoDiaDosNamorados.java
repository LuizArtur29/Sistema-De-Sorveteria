package sorveteria.strategy;

public class DescontoDiaDosNamorados implements DescontoStrategy {
    private static final double DESCONTO = 0.15; // 15% de Desconto

    public double aplicarDesconto(double valorOriginal) {
        return valorOriginal * (1 - DESCONTO);
    }

    public static boolean isPedidoElegivel(Pedido pedido) {
        return pedido.getItens().size() == 2;
    }
}
