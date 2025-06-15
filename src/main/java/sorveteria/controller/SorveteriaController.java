package sorveteria.controller;

import sorveteria.decorator.SaborBase;
import sorveteria.decorator.TipoAdicional;
import sorveteria.facade.SistemaSorveteriaFacade;
import sorveteria.factory.Produto;
import sorveteria.model.Cliente;
import sorveteria.model.Pedido;

import java.util.List;
import java.util.Scanner;
import java.util.Optional;

public class SorveteriaController {
    private SistemaSorveteriaFacade facade;
    private Scanner scanner;

    public SorveteriaController() {
        this.facade = new SistemaSorveteriaFacade();
        this.scanner = new Scanner(System.in);
    }

    public void iniciarSistema() {
        System.out.println("Bem-vindo à Sorveteria!");
        int opcao;
        do {
            exibirMenuPrincipal();
            opcao = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (opcao) {
                case 1:
                    menuClientes();
                    break;
                case 2:
                    menuPedidos();
                    break;
                case 3:
                    aplicarDescontoEmPedido();
                    break;
                case 4:
                    desfazerUltimoComando();
                    break;
                case 5:
                    refazerUltimoComando();
                    break;
                case 0:
                    System.out.println("Saindo do sistema. Obrigado!");
                    break;
                default:
                    System.out.println("Opção inválida. Tente novamente.");
            }
            System.out.println("\n-----------------------------------\n");
        } while (opcao != 0);
    }

    private void exibirMenuPrincipal() {
        System.out.println("Escolha uma opção:");
        System.out.println("1. Gerenciar Clientes");
        System.out.println("2. Gerenciar Pedidos");
        System.out.println("3. Aplicar desconto em um valor");
        System.out.println("4. Desfazer último comando");
        System.out.println("5. Refazer último comando");
        System.out.println("0. Sair");
        System.out.print("Sua opção: ");
    }

    private void menuClientes() {
        int opcao;
        do {
            exibirMenuClientes();
            opcao = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (opcao) {
                case 1:
                    cadastrarNovoCliente();
                    break;
                case 2:
                    listarTodosClientes();
                    break;
                case 3:
                    buscarClientePorId();
                    break;
                case 4:
                    atualizarDadosCliente();
                    break;
                case 5:
                    deletarCliente();
                    break;
                case 0:
                    System.out.println("Retornando ao menu principal...");
                    break;
                default:
                    System.out.println("Opção inválida. Tente novamente.");
            }
            System.out.println("\n-----------------------------------\n");
        } while (opcao != 0);
    }

    private void exibirMenuClientes() {
        System.out.println("\n--- Gerenciamento de Clientes ---");
        System.out.println("1. Cadastrar novo cliente");
        System.out.println("2. Listar todos os clientes");
        System.out.println("3. Buscar cliente por ID");
        System.out.println("4. Atualizar dados do cliente");
        System.out.println("5. Deletar cliente");
        System.out.println("0. Voltar ao menu principal");
        System.out.print("Sua opção: ");
    }

    private void cadastrarNovoCliente() {
        // ID do cliente NÃO É MAIS SOLICITADO, será gerado pelo banco de dados.

        System.out.print("Digite o nome do cliente: ");
        String nome = scanner.nextLine();
        System.out.print("Digite o email do cliente: ");
        String email = scanner.nextLine();

        Cliente novoCliente = new Cliente(); // Usa o construtor sem argumentos
        novoCliente.setNome(nome);
        novoCliente.setEmail(email);

        facade.cadastrarCliente(novoCliente); // Passa o objeto Cliente sem ID, que será gerado no repositório
    }

    private void listarTodosClientes() {
        List<Cliente> clientes = facade.listarClientes();
        if (clientes.isEmpty()) {
            System.out.println("Nenhum cliente cadastrado.");
        } else {
            System.out.println("\n--- Clientes Cadastrados ---");
            clientes.forEach(System.out::println);
        }
    }

    private void buscarClientePorId() {
        int id;
        try {
            System.out.print("Digite o ID do cliente para buscar (número inteiro): ");
            id = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("ID inválido. Por favor, digite um número inteiro.");
            return;
        }

        Optional<Cliente> cliente = facade.buscarClientePorId(id);
        cliente.ifPresentOrElse(
                System.out::println,
                () -> System.out.println("Cliente com ID " + id + " não encontrado.")
        );
    }

    private void atualizarDadosCliente() {
        int id;
        try {
            System.out.print("Digite o ID do cliente para atualizar (número inteiro): ");
            id = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("ID inválido. Por favor, digite um número inteiro.");
            return;
        }

        Optional<Cliente> clienteExistente = facade.buscarClientePorId(id);

        if (clienteExistente.isPresent()) {
            Cliente cliente = clienteExistente.get();
            System.out.print("Digite o novo nome do cliente (deixe em branco para manter '" + cliente.getNome() + "'): ");
            String novoNome = scanner.nextLine();
            if (!novoNome.isEmpty()) {
                cliente.setNome(novoNome);
            }

            System.out.print("Digite o novo email do cliente (deixe em branco para manter '" + cliente.getEmail() + "'): ");
            String novoEmail = scanner.nextLine();
            if (!novoEmail.isEmpty()) {
                cliente.setEmail(novoEmail);
            }
            facade.atualizarCliente(cliente);
            System.out.println("Cliente com ID " + id + " atualizado com sucesso.");
        } else {
            System.out.println("Cliente com ID " + id + " não encontrado.");
        }
    }

    private void deletarCliente() {
        int id;
        try {
            System.out.print("Digite o ID do cliente para deletar (número inteiro): ");
            id = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("ID inválido. Por favor, digite um número inteiro.");
            return;
        }
        facade.deletarCliente(id);
    }


    private void menuPedidos() {
        int opcao;
        do {
            exibirMenuPedidos();
            opcao = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (opcao) {
                case 1:
                    fazerNovoPedido();
                    break;
                case 2:
                    processarProximoPedido();
                    break;
                case 3:
                    avancarEstadoDoPedido();
                    break;
                case 4:
                    cancelarPedido();
                    break;
                case 5:
                    listarTodosPedidos();
                    break;
                case 6:
                    buscarPedidoPorId();
                    break;
                case 7:
                    deletarPedido();
                    break;
                case 0:
                    System.out.println("Retornando ao menu principal...");
                    break;
                default:
                    System.out.println("Opção inválida. Tente novamente.");
            }
            System.out.println("\n-----------------------------------\n");
        } while (opcao != 0);
    }

    private void exibirMenuPedidos() {
        System.out.println("\n--- Gerenciamento de Pedidos ---");
        System.out.println("1. Fazer novo pedido");
        System.out.println("2. Processar próximo pedido da fila");
        System.out.println("3. Avançar estado de um pedido");
        System.out.println("4. Cancelar um pedido");
        System.out.println("5. Listar todos os pedidos");
        System.out.println("6. Buscar pedido por ID");
        System.out.println("7. Deletar pedido");
        System.out.println("0. Voltar ao menu principal");
        System.out.print("Sua opção: ");
    }

    private void fazerNovoPedido() {
        // ID do pedido NÃO É MAIS SOLICITADO, será gerado pelo banco de dados.

        int idCliente;
        try {
            System.out.print("Digite o ID do cliente associado ao pedido (número inteiro): ");
            idCliente = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("ID de Cliente inválido. Por favor, digite um número inteiro.");
            return;
        }

        Optional<Cliente> cliente = facade.buscarClientePorId(idCliente);
        if (cliente.isEmpty()) {
            System.out.println("Cliente com ID " + idCliente + " não encontrado. Por favor, cadastre o cliente primeiro.");
            return;
        }

        System.out.print("Digite o tipo de produto (sorvete, milkshake, picole): ");
        String tipoProduto = scanner.nextLine();

        SaborBase saborEscolhido = null;
        boolean saborValido = false;
        while (!saborValido) {
            System.out.print("Escolha o sabor base (CHOCOLATE, MORANGO, BAUNILHA): ");
            try {
                saborEscolhido = SaborBase.valueOf(scanner.nextLine().toUpperCase());
                saborValido = true;
            } catch (IllegalArgumentException e) {
                System.out.println("Sabor inválido. Por favor, escolha entre CHOCOLATE, MORANGO, BAUNILHA.");
            }
        }

        List<TipoAdicional> adicionais = solicitarAdicionais();

        Produto produto = facade.criarProduto(tipoProduto, saborEscolhido, adicionais);
        if (produto != null) {
            Pedido novoPedido = facade.registrarNovoPedido(cliente.get().getNome());
            if (novoPedido != null) {
                novoPedido.adicionarItem(produto);
                facade.salvarPedido(novoPedido);
                System.out.println("Produto " + produto.getNome() + " adicionado ao pedido #" + novoPedido.getId() + " com preço R$" + String.format("%.2f", produto.getPreco()));
                System.out.println("Pedido #" + novoPedido.getId() + " salvo no banco de dados com ID gerado.");
            }
        }
    }

    private List<TipoAdicional> solicitarAdicionais() {
        List<TipoAdicional> adicionais = new java.util.ArrayList<>();
        String adicionarMais;
        do {
            System.out.print("Deseja adicionar adicionais? (sim/não): ");
            adicionarMais = scanner.nextLine().toLowerCase();
            if ("sim".equals(adicionarMais)) {
                TipoAdicional adicionalEscolhido = null;
                boolean adicionalValido = false;
                while (!adicionalValido) {
                    System.out.print("Escolha um adicional (GRANULADO, AMENDOIN): ");
                    try {
                        adicionalEscolhido = TipoAdicional.valueOf(scanner.nextLine().toUpperCase());
                        adicionais.add(adicionalEscolhido);
                        adicionalValido = true;
                    } catch (IllegalArgumentException e) {
                        System.out.println("Adicional inválido. Por favor, escolha entre GRANULADO, AMENDOIN.");
                    }
                }
            }
        } while ("sim".equals(adicionarMais));
        return adicionais;
    }

    private void processarProximoPedido() {
        System.out.println("Tentando processar o próximo pedido da fila...");
        Pedido pedidoProcessado = facade.processarProximoPedidoDaFila();
        if (pedidoProcessado != null) {
            facade.salvarPedido(pedidoProcessado);
        }
    }

    private void avancarEstadoDoPedido() {
        int idPedido;
        try {
            System.out.print("Digite o ID do pedido para avançar o estado (número inteiro): ");
            idPedido = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("ID de Pedido inválido. Por favor, digite um número inteiro.");
            return;
        }

        Optional<Pedido> pedidoOptional = facade.buscarPedidoPorId(idPedido);
        if (pedidoOptional.isPresent()) {
            Pedido pedidoParaAvancar = pedidoOptional.get();
            System.out.println("Avançando estado do pedido #" + idPedido + "...");
            facade.avancarEstadoPedido(pedidoParaAvancar);
            facade.salvarPedido(pedidoParaAvancar);
        } else {
            System.out.println("Pedido com ID " + idPedido + " não encontrado.");
        }
    }

    private void cancelarPedido() {
        int idPedido;
        try {
            System.out.print("Digite o ID do pedido para cancelar (número inteiro): ");
            idPedido = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("ID de Pedido inválido. Por favor, digite um número inteiro.");
            return;
        }

        Optional<Pedido> pedidoOptional = facade.buscarPedidoPorId(idPedido);
        if (pedidoOptional.isPresent()) {
            Pedido pedidoParaCancelar = pedidoOptional.get();
            System.out.println("Cancelando pedido #" + idPedido + "...");
            facade.cancelarPedido(pedidoParaCancelar);
            facade.salvarPedido(pedidoParaCancelar);
        } else {
            System.out.println("Pedido com ID " + idPedido + " não encontrado.");
        }
    }

    private void listarTodosPedidos() {
        List<Pedido> pedidos = facade.listarPedidos();
        if (pedidos.isEmpty()) {
            System.out.println("Nenhum pedido cadastrado.");
        } else {
            System.out.println("\n--- Pedidos Cadastrados ---");
            pedidos.forEach(pedido -> {
                System.out.println("ID: " + pedido.getId() +
                        ", Estado: " + pedido.getEstado().getDescricao() +
                        ", Valor Total: R$" + String.format("%.2f", pedido.getValorTotal()));
                if (!pedido.getItens().isEmpty()) {
                    System.out.println("  Itens:");
                    pedido.getItens().forEach(item ->
                            System.out.println("    - " + item.getNome() + " (R$" + String.format("%.2f", item.getPreco()) + ")")
                    );
                }
            });
        }
    }

    private void buscarPedidoPorId() {
        int id;
        try {
            System.out.print("Digite o ID do pedido para buscar (número inteiro): ");
            id = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("ID de Pedido inválido. Por favor, digite um número inteiro.");
            return;
        }

        Optional<Pedido> pedido = facade.buscarPedidoPorId(id);
        pedido.ifPresentOrElse(
                p -> {
                    System.out.println("ID: " + p.getId() +
                            ", Estado: " + p.getEstado().getDescricao() +
                            ", Valor Total: R$" + String.format("%.2f", p.getValorTotal()));
                    if (!p.getItens().isEmpty()) {
                        System.out.println("  Itens:");
                        p.getItens().forEach(item ->
                                System.out.println("    - " + item.getNome() + " (R$" + String.format("%.2f", item.getPreco()) + ")")
                        );
                    }
                },
                () -> System.out.println("Pedido com ID " + id + " não encontrado.")
        );
    }

    private void deletarPedido() {
        int id;
        try {
            System.out.print("Digite o ID do pedido para deletar (número inteiro): ");
            id = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("ID de Pedido inválido. Por favor, digite um número inteiro.");
            return;
        }
        facade.deletarPedido(id);
    }

    private void aplicarDescontoEmPedido() {
        System.out.print("Digite o valor original para aplicar o desconto: ");
        double valorOriginal = scanner.nextDouble();
        scanner.nextLine(); // Consume newline

        System.out.print("Digite o tipo de desconto (fidelidade/diadosnamorados): ");
        String tipoDesconto = scanner.nextLine();

        double valorComDesconto = facade.aplicarDesconto(valorOriginal, tipoDesconto);
        System.out.println("Valor original: R$" + String.format("%.2f", valorOriginal));
        System.out.println("Valor com desconto (" + tipoDesconto + "): R$" + String.format("%.2f", valorComDesconto));
    }

    private void desfazerUltimoComando() {
        System.out.println("Desfazendo último comando...");
        facade.desfazerUltimoComando();
    }

    private void refazerUltimoComando() {
        System.out.println("Refazendo último comando...");
        facade.refazerUltimoComando();
    }
}