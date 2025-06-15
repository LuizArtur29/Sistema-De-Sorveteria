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

    public Pedido registrarNovoPedido(String nomeCliente) {
        Pedido pedido = new Pedido(); // Cria um novo Pedido sem ID (o ID será gerado no repositório)
        pedido.adicionarObserver(new ClienteObserver(nomeCliente));
        filaPedidos.adicionarPedido(pedido);
        System.out.println("Novo pedido para " + nomeCliente + " registrado para processamento na fila.");
        return pedido;
    }

    public Pedido processarProximoPedidoDaFila() {
        Pedido pedido = filaPedidos.processarProximoPedido();
        if (pedido != null) {
            gerenciadorComandos.executarComando(new AvancarEstadoPedidoCommand(pedido));
            pedidoRepository.salvar(pedido); // Salva o estado atualizado no BD
            System.out.println("Processando próximo pedido da fila: #" + pedido.getId());
        } else {
            System.out.println("Fila de pedidos vazia.");
        }
        return pedido;
    }

    public void avancarEstadoPedido(Pedido pedido) {
        gerenciadorComandos.executarComando(new AvancarEstadoPedidoCommand(pedido));
        pedidoRepository.salvar(pedido); // Salva o estado atualizado no BD
    }

    public void cancelarPedido(Pedido pedido) {
        gerenciadorComandos.executarComando(new CancelarPedidoCommand(pedido));
        pedidoRepository.salvar(pedido); // Salva o estado atualizado no BD
    }

    public Optional<Pedido> buscarPedidoPorId(int id) { // ID é int
        return pedidoRepository.buscarPorId(id);
    }

    public List<Pedido> listarPedidos() {
        return pedidoRepository.buscarTodos();
    }

    public void salvarPedido(Pedido pedido) {
        pedidoRepository.salvar(pedido); // O ID será gerado aqui para novos pedidos
    }

    public void deletarPedido(int id) { // ID é int
        pedidoRepository.deletar(id);
    }

    public void cadastrarCliente(Cliente cliente) {
        // Se o ID do cliente for 0, é um novo cliente. O ID será gerado no repositório.
        // Se o ID não for 0, é uma atualização ou tentativa de inserir ID específico.
        // A lógica de salvar no ClienteRepositoryImpl lida com ambos os casos.
        clienteRepository.salvar(cliente);
    }

    public Optional<Cliente> buscarClientePorId(int id) { // ID é int
        return clienteRepository.buscarPorId(id);
    }

    public List<Cliente> listarClientes() {
        return clienteRepository.buscarTodos();
    }

    public void atualizarCliente(Cliente cliente) {
        clienteRepository.atualizar(cliente);
    }

    public void deletarCliente(int id) { // ID é int
        clienteRepository.deletar(id);
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

    public void desfazerUltimoComando() {
        gerenciadorComandos.desfazerUltimoComando();
    }

    public void refazerUltimoComando() {
        gerenciadorComandos.refazerUltimoComando();
    }
}