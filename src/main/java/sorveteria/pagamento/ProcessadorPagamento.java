package sorveteria.pagamento;

public class ProcessadorPagamento {

    public boolean processarPagamento(double valor) {
        System.out.println("Processando pagamento de R$" + valor + "...");
        if (valor > 0) {
            System.out.println("Pagamento de R$" + valor + " aprovado.");
            return true;
        } else {
            System.out.println("Erro: Valor de pagamento inválido.");
            return false;
        }
    }
}
