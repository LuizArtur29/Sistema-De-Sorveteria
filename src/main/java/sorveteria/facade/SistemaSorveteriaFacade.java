package sorveteria.facade;

import sorveteria.command.AvancarEstadoPedidoCommand;
import sorveteria.command.CancelarPedidoCommand;
import sorveteria.command.GerenciadorDeComandos;
import sorveteria.decorator.DecoradorProduto;
import sorveteria.decorator.SaborBase;
import sorveteria.decorator.TipoAdicional;
import sorveteria.factory.Factory;
import sorveteria.factory.Produto;
import sorveteria.model.Cliente;
import sorveteria.model.Pedido;
import sorveteria.observer.ClienteObserver;
import sorveteria.repository.ClienteRepository;
import sorveteria.repository.PedidoRepository;
import sorveteria.repository.ClienteRepositoryImpl;
import sorveteria.singleton.FilaPedidos;
import sorveteria.strategy.DescontoDiaDosNamorados;
import sorveteria.strategy.DescontoFidelidade;
import sorveteria.strategy.DescontoStrategy;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;
import java.util.Optional;

public class SistemaSorveteriaFacade {
    private Factory produtoFactory;
    private FilaPedidos filaPedidos;
    private GerenciadorDeComandos gerenciadorComandos;
    private ClienteRepository clienteRepository;
    private PedidoRepository pedidoRepository;

    public SistemaSorveteriaFacade() {
        this.produtoFactory = new Factory();
        this.filaPedidos = FilaPedidos.getInstance();
        this.gerenciadorComandos = new GerenciadorDeComandos();
        this.clienteRepository = new ClienteRepositoryImpl();
        this.pedidoRepository = new PedidoRepository();
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

    public Pedido registrarNovoPedido(int idCliente, String nomeCliente) {
        Pedido pedido = new Pedido();
        pedido.setIdCliente(idCliente);
        pedido.adicionarObserver(new ClienteObserver(nomeCliente));
        filaPedidos.adicionarPedido(pedido);
        System.out.println("Novo pedido para " + nomeCliente + " (Cliente ID: " + idCliente + ") registrado para processamento na fila.");
        return pedido;
    }

    public Pedido processarProximoPedidoDaFila() {
        Pedido pedido = filaPedidos.processarProximoPedido();
        if (pedido != null) {
            gerenciadorComandos.executarComando(new AvancarEstadoPedidoCommand(pedido));
            pedidoRepository.salvar(pedido);
            System.out.println("Processando próximo pedido da fila: #" + pedido.getId());
        } else {
            System.out.println("Fila de pedidos vazia.");
        }
        return pedido;
    }

    public void avancarEstadoPedido(Pedido pedido) {
        gerenciadorComandos.executarComando(new AvancarEstadoPedidoCommand(pedido));
        pedidoRepository.salvar(pedido);
    }

    public void cancelarPedido(Pedido pedido) {
        gerenciadorComandos.executarComando(new CancelarPedidoCommand(pedido));
        pedidoRepository.salvar(pedido);
    }

    public Optional<Pedido> buscarPedidoPorId(int id) {
        return pedidoRepository.buscarPorId(id);
    }

    public List<Pedido> listarPedidos() {
        return pedidoRepository.buscarTodos();
    }

    public void salvarPedido(Pedido pedido) {
        pedidoRepository.salvar(pedido);
    }

    public void deletarPedido(int id) {
        pedidoRepository.deletar(id);
    }

    public void cadastrarCliente(Cliente cliente) {
        clienteRepository.salvar(cliente);
    }

    public Optional<Cliente> buscarClientePorId(int id) {
        return clienteRepository.buscarPorId(id);
    }

    public List<Cliente> listarClientes() {
        return clienteRepository.buscarTodos();
    }

    public void atualizarCliente(Cliente cliente) {
        clienteRepository.atualizar(cliente);
    }

    public void deletarCliente(int id) {
        clienteRepository.deletar(id);
    }

    public boolean aplicarDescontoAoTotalDoPedido(int idPedido, String tipoDesconto) {
        Optional<Pedido> pedidoOptional = pedidoRepository.buscarPorId(idPedido);
        if (pedidoOptional.isEmpty()) {
            System.out.println("Pedido com ID " + idPedido + " não encontrado.");
            return false;
        }

        Pedido pedido = pedidoOptional.get();
        double valorOriginalDoPedido = pedido.getValorTotal();

        DescontoStrategy strategy;
        switch (tipoDesconto.toLowerCase()) {
            case "diadosnamorados":
                // Verifica a elegibilidade do desconto do Dia dos Namorados
                if (DescontoDiaDosNamorados.isPedidoElegivel(pedido)) { // isPedidoElegivel já verifica itens E data
                    strategy = new DescontoDiaDosNamorados();
                } else {
                    // Mensagem de erro mais específica
                    String msg = "Desconto Dia dos Namorados não aplicável: ";
                    if (pedido.getItens().size() != 2) {
                        msg += "o pedido precisa ter exatamente 2 itens";
                    }
                    // Verifica a condição de data separadamente para a mensagem de erro
                    LocalDate hoje = LocalDate.now();
                    LocalDate diaDosNamoradosData = LocalDate.of(hoje.getYear(), Month.JUNE, 12);
                    if (!hoje.isEqual(diaDosNamoradosData)) {
                        if (pedido.getItens().size() != 2) { // Se já adicionou a razão do item, adiciona " E "
                            msg += " E ";
                        }
                        msg += "o desconto só é aplicável em 12/06 de cada ano";
                    }
                    System.out.println(msg + ".");
                    return false;
                }
                break;
            case "fidelidade":
                strategy = new DescontoFidelidade();
                break;
            default:
                System.out.println("Tipo de desconto inválido para aplicar ao pedido. Nenhum desconto aplicado.");
                return false;
        }

        double novoValorTotalPedido = strategy.aplicarDesconto(valorOriginalDoPedido);
        pedido.setValorTotal(novoValorTotalPedido);

        pedidoRepository.salvar(pedido);
        System.out.println("Desconto '" + tipoDesconto + "' aplicado ao pedido #" + idPedido + ".");
        System.out.println("Valor original: R$" + String.format("%.2f", valorOriginalDoPedido) + ". Novo valor total do pedido: R$" + String.format("%.2f", novoValorTotalPedido));
        return true;
    }


    public void desfazerUltimoComando() {
        gerenciadorComandos.desfazerUltimoComando();
    }

    public void refazerUltimoComando() {
        gerenciadorComandos.refazerUltimoComando();
    }
}