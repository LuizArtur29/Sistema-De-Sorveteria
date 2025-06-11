// src/main/java/sorveteria/controller/SorveteriaController.java
package sorveteria.controller;

import sorveteria.decorator.SaborBase;
import sorveteria.decorator.TipoAdicional;
import sorveteria.facade.SistemaSorveteriaFacade;
import sorveteria.factory.Produto;
import sorveteria.model.Pedido;

import java.util.List;
import java.util.Scanner;

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
            exibirMenu();
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
                    aplicarDescontoEmPedido();
                    break;
                case 6:
                    desfazerUltimoComando();
                    break;
                case 7:
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

    private void exibirMenu() {
        System.out.println("Escolha uma opção:");
        System.out.println("1. Fazer novo pedido");
        System.out.println("2. Processar próximo pedido da fila");
        System.out.println("3. Avançar estado de um pedido");
        System.out.println("4. Cancelar um pedido");
        System.out.println("5. Aplicar desconto em um valor");
        System.out.println("6. Desfazer último comando");
        System.out.println("7. Refazer último comando");
        System.out.println("0. Sair");
        System.out.print("Sua opção: ");
    }

    private void fazerNovoPedido() {
        System.out.print("Digite o ID do pedido: ");
        String idPedido = scanner.nextLine();
        System.out.print("Digite o nome do cliente: ");
        String nomeCliente = scanner.nextLine();

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
            Pedido novoPedido = facade.registrarNovoPedido(idPedido, nomeCliente);
            if (novoPedido != null) {
                novoPedido.adicionarItem(produto);
                System.out.println("Produto " + produto.getNome() + " adicionado ao pedido #" + idPedido + " com preço R$" + produto.getPreco());
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
        facade.processarProximoPedidoDaFila();
    }

    private void avancarEstadoDoPedido() {
        System.out.print("Digite o ID do pedido para avançar o estado: ");
        String idPedido = scanner.nextLine();
        // Para avançar o estado, precisamos do objeto Pedido.
        // Em um sistema real, você buscaria o pedido pelo ID no repositório.
        // Para simplificar a simulação aqui, vamos criar um pedido mock ou assumir que ele já existe na fila.
        // Idealmente, a fachada deveria ter um método para buscar um pedido.

        // Simulação: Criamos um pedido temporário para o comando,
        // mas o correto seria buscar o pedido real da fila ou do repositório.
        // Como a fila é um singleton, o pedido na fila teria seu estado atualizado.
        // Para um cenário mais robusto, o facade.processarProximoPedidoDaFila()
        // já avança o estado e o facade precisaria de um método para "pegar" um pedido específico.
        Pedido pedidoParaAvancar = new Pedido(idPedido);
        System.out.println("Avançando estado do pedido #" + idPedido + " (assumindo que ele existe).");
        facade.avancarEstadoPedido(pedidoParaAvancar);
    }

    private void cancelarPedido() {
        System.out.print("Digite o ID do pedido para cancelar: ");
        String idPedido = scanner.nextLine();
        // Similar ao avancarEstadoDoPedido, precisamos do objeto Pedido real.
        Pedido pedidoParaCancelar = new Pedido(idPedido);
        System.out.println("Cancelando pedido #" + idPedido + " (assumindo que ele existe).");
        facade.cancelarPedido(pedidoParaCancelar);
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