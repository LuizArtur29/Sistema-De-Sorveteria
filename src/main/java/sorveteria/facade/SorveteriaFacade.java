package sorveteria.facade;

import sorveteria.decorator.DecoradorProduto;
import sorveteria.decorator.SaborBase;
import sorveteria.decorator.TipoAdicional;
import sorveteria.factory.Factory;
import sorveteria.factory.Produto;
import sorveteria.model.Pedido; // Adicionado para a classe Pedido
import sorveteria.pagamento.ProcessadorPagamento;
import sorveteria.singleton.FilaPedidos;
import sorveteria.strategy.DescontoStrategy;

import java.util.ArrayList;
import java.util.List;

public class SorveteriaFacade {
    private Factory produtoFactory;
    private FilaPedidos filaPedidos;
    private ProcessadorPagamento processadorPagamento;

    public SorveteriaFacade() {
        this.produtoFactory = new Factory();
        this.filaPedidos = FilaPedidos.getInstance();
        this.processadorPagamento = new ProcessadorPagamento();
    }

    public Produto criarEPErsonalizarProduto(String tipoProduto, SaborBase sabor, List<TipoAdicional> adicionais) {
        Produto produtoBase = produtoFactory.criarSorvete(tipoProduto, sabor);
        if (produtoBase == null) {
            System.out.println("Tipo de produto inválido.");
            return null;
        }

        Produto produtoFinal = produtoBase;
        if (adicionais != null) {
            for (TipoAdicional adicional : adicionais) {
                produtoFinal = new DecoradorProduto(produtoFinal, adicional);
            }
        }
        return produtoFinal;
    }

    // Método corrigido: não recebe mais 'idPedido'
    public Pedido fazerNovoPedido(String tipoProduto, SaborBase sabor, List<TipoAdicional> adicionais) {
        Produto produtoPedido = criarEPErsonalizarProduto(tipoProduto, sabor, adicionais);
        if (produtoPedido == null) {
            return null;
        }

        // NOVO Pedido criado sem ID. O ID será gerado pelo banco ao salvar.
        Pedido novoPedido = new Pedido();
        novoPedido.adicionarItem(produtoPedido); // Adiciona o item ao pedido

        // O ID do pedido só estará disponível após o PedidoRepository.salvar() ser chamado
        // e ele recuperar o ID gerado pelo banco de dados.
        // A mensagem abaixo imprimirá um ID 0 ou o valor inicial, se o objeto não for salvo antes.
        // A FilaPedidos geralmente trabalha com o objeto Pedido antes da persistência final.
        System.out.println("Pedido criado com " + produtoPedido.getNome() + " (R$" + produtoPedido.getPreco() + ")");
        filaPedidos.adicionarPedido(novoPedido); // Adiciona o pedido à fila
        return novoPedido;
    }

    public boolean processarEPagarPedido(Pedido pedido, DescontoStrategy descontoStrategy) {
        if (pedido == null) {
            System.out.println("Erro: Pedido inválido para processamento.");
            return false;
        }

        double valorTotal = 25.0;
        System.out.println("Valor base do pedido #" + pedido.getId() + ": R$" + valorTotal);

        if (descontoStrategy != null) {
            valorTotal = descontoStrategy.aplicarDesconto(valorTotal);
            System.out.println("Valor com desconto aplicado: R$" + valorTotal);
        }

        if (pedido.getEstado().getDescricao().equals("Pedido recebido")) {
            pedido.avancarEstado();
        } else {
            System.out.println("Pedido #" + pedido.getId() + " já está em preparo ou estado avançado. Prosseguindo com pagamento.");
        }

        boolean pagamentoAprovado = processadorPagamento.processarPagamento(valorTotal);

        if (pagamentoAprovado) {
            System.out.println("Pedido #" + pedido.getId() + " pago com sucesso!");
            return true;
        } else {
            System.out.println("Falha no pagamento do Pedido #" + pedido.getId());
            return false;
        }
    }

    public Pedido processarProximaPedidoFila() {
        Pedido proximo = filaPedidos.processarProximoPedido();
        if (proximo != null) {
            System.out.println("Processando próximo pedido da fila: #" + proximo.getId());
        } else {
            System.out.println("Fila de pedidos vazia.");
        }
        return proximo;
    }
}