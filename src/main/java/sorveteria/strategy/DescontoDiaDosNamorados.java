package sorveteria.strategy;

import sorveteria.model.Pedido;
import java.time.LocalDate; // Importa a classe para trabalhar com datas
import java.time.Month;   // Importa a enumeração para meses

public class DescontoDiaDosNamorados implements DescontoStrategy {
    private static final double DESCONTO = 0.15; // 15% de Desconto

    @Override
    public double aplicarDesconto(double valorOriginal) {
        return valorOriginal * (1 - DESCONTO);
    }

    public static boolean isPedidoElegivel(Pedido pedido) {
        // Obtém a data atual

        LocalDate hoje = LocalDate.of(LocalDate.now().getYear(), Month.JUNE, 12); // Teste para o dia dos namorados
        //LocalDate hoje = LocalDate.now();

        boolean isDiaDosNamoradosParaTeste = true; // True == dia 12/06

        // Define a data do Dia dos Namorados para o ano atual (12 de Junho)
        LocalDate diaDosNamorados = LocalDate.of(hoje.getYear(), Month.JUNE, 12);

        // Verifica se a data atual é igual ao Dia dos Namorados
        boolean isDiaDosNamorados = hoje.isEqual(diaDosNamorados);

        // Retorna true se o pedido tiver 2 itens E for o Dia dos Namorados
        return pedido.getItens().size() == 2 && isDiaDosNamorados;
    }
}