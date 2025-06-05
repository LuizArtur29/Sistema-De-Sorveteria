package sorveteria.facade;

import sorveteria.decorator.DecoradorProduto;
import sorveteria.decorator.SaborBase;
import sorveteria.decorator.TipoAdicional;
import sorveteria.factory.Factory;
import sorveteria.factory.Produto;
import sorveteria.model.Pedido;
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
            for (tipoAdicional adicional : adicionais) {
                produtoFinal = new DecoradorProduto(produtoFinal, adicional);
            }
        }
        return produtoFinal;
    }

    public Pedido fazerNovoPedido(String idPedido, String tipoProduto, SaborBase sabor, List<TipoAdicional> adicionais) {
        Produto produtoPedido = criarEPErsonalizarProduto(tipoProduto, sabor, adicionais);
        if (produtoPedido == null) {
            return null;
        }

        Pedido novoPedido = new Pedido(idPedido);
        List<Produto> itensParaCalculo = new ArrayList<>();
        itensParaCalculo.add(produtoPedido);

        System.out.println("Pedido " + idPedido + " criado com " + produtoPedido.getNome() + " (R$" + produtoPedido.getPreco() + ")");
        filaPedidos.adicionarPedido(novoPedido);
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
