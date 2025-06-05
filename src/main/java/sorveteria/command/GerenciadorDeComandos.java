package sorveteria.command;

import java.util.Stack;

public class GerenciadorDeComandos {
    private Stack<Command> historicoComandos = new Stack<>();
    private Stack<Command> refazerComandos = new Stack<>();

    public void executarComando(Command comando) {
        comando.executar();
        historicoComandos.push(comando);
        refazerComandos.clear();
    }

    public void desfazerUltimoComando() {
        if (!historicoComandos.empty()) {
            Command ultimoComando = historicoComandos.pop();
            ultimoComando.desfazer();
            refazerComandos.push(ultimoComando);
        } else {
            System.out.println("Não há comandos para desfazer.");
        }
    }

    public void refazerUltimoComando() {
        if (!refazerComandos.empty()) {
            Command comandoParaRefazer = refazerComandos.pop();
            comandoParaRefazer.executar();
            historicoComandos.push(comandoParaRefazer);
        } else {
            System.out.println("Não há comandos para refazer.");
        }
    }
}