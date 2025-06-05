package sorveteria.facade;

import sorveteria.command.AvancarEstadoPedidoCommand;
import sorveteria.command.CancelarPedidoCommand;
import sorveteria.command.GerenciadorDeComandos;
import sorveteria.decorator.DecoradorProduto;
import sorveteria.decorator.SaborBase;
import sorveteria.decorator.TipoAdicional;
import sorveteria.factory.Factory;
import sorveteria.factory.Produto;
import sorveteria.model.Pedido;
import sorveteria.observer.ClienteObserver;
import sorveteria.observer.PedidoObserver;
import sorveteria.singleton.FilaPedidos;
import sorveteria.strategy.DescontoDiaDosNamorados;
import sorveteria.strategy.DescontoFidelidade;
import sorveteria.strategy.DescontoStrategy;

import java.util.List;

public class SistemaSorveteriaFacade {
    private Factory produtoFactory;
    private FilaPedidos filaPedidos;
    private GerenciadorDeComandos gerenciadorComandos;

    public SistemaSorveteriaFacade() {
        this.produtoFactory = new Factory();
        this.filaPedidos = FilaPedidos.getInstance();
        this.gerenciadorComandos = new GerenciadorDeComandos();
    }

    public Produto criarProduto(String tipo, SaborBase sabor, List<TipoAdicional> adicionais) {
        Produto produto = produtoFactory.criarSorvete(tipo, sabor);
        if (produto == null) {
            System.out.println("Tipo de produto inválido.");
            return null;
        }
        for (TipoAdicional adicional : adicionais) {
            produto = new DecoradorProduto(produto, adicional);
        }
        return produto;
    }

    public Pedido registrarNovoPedido(String idPedido, String nomeCliente) {
        Pedido pedido = new Pedido(idPedido);
        pedido.adicionarObserver(new ClienteObserver(nomeCliente));
        filaPedidos.adicionarPedido(pedido);
        System.out.println("Pedido #" + idPedido + " registrado com sucesso para " + nomeCliente + ".");
        return pedido;
    }

    public Pedido processarProximoPedidoDaFila() {
        Pedido pedido = filaPedidos.processarProximoPedido();
        if (pedido != null) {
            gerenciadorComandos.executarComando(new AvancarEstadoPedidoCommand(pedido));
            System.out.println("Processando próximo pedido da fila: #" + pedido.getId());
        } else {
            System.out.println("Fila de pedidos vazia.");
        }
        return pedido;
    }

    public double aplicarDesconto(double valorOriginal, String tipoDesconto) {
        DescontoStrategy strategy;
        switch (tipoDesconto.toLowerCase()) {
            case "fidelidade":
                strategy = new DescontoFidelidade();
                break;
            case "diadosnamorados":
                strategy = new DescontoDiaDosNamorados();
                break;
            default:
                System.out.println("Tipo de desconto inválido. Nenhum desconto aplicado.");
                return valorOriginal;
        }
        return strategy.aplicarDesconto(valorOriginal);
    }

    public void avancarEstadoPedido(Pedido pedido) {
        gerenciadorComandos.executarComando(new AvancarEstadoPedidoCommand(pedido));
    }

    public void cancelarPedido(Pedido pedido) {
        gerenciadorComandos.executarComando(new CancelarPedidoCommand(pedido));
    }

    public void desfazerUltimoComando() {
        gerenciadorComandos.desfazerUltimoComando();
    }

    public void refazerUltimoComando() {
        gerenciadorComandos.refazerUltimoComando();
    }
}