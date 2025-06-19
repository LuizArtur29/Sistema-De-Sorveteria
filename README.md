# Sistema de Gerenciamento de Pedidos de Sorveteria

Este projeto é um sistema de gerenciamento de pedidos para uma sorveteria, desenvolvido em Java. Ele foi projetado para demonstrar a aplicação de diversos padrões de design de software, visando modularidade, flexibilidade e manutenibilidade.

## 🎯 Objetivo do Projeto

Criar um sistema de pedidos completo para uma sorveteria, com as seguintes capacidades:
* Gerenciar sabores e adicionais para produtos.
* Personalizar pedidos.
* Aplicar diferentes estratégias de desconto.
* Acompanhar o status dos pedidos em tempo real.
* Otimizar o fluxo de processamento de pedidos.
* Gerenciar o histórico de clientes e pedidos.

## 🔹 Funcionalidades e Padrões de Design Aplicados

O sistema incorpora os seguintes padrões de design para atingir seus objetivos:

1.  **Strategy**: Permite a aplicação de diferentes estratégias de desconto.
    * **Exemplos**: Desconto para clientes frequentes (`DescontoFidelidade`) ou descontos sazonais (`DescontoDiaDosNamorados`).
2.  **Decorator**: Habilita a personalização dinâmica de produtos adicionando funcionalidades a objetos existentes.
    * **Exemplo**: Adicionar coberturas, caldas ou chantilly a um sorvete.
3.  **Observer**: Estabelece um mecanismo de notificação automática entre objetos.
    * **Exemplo**: Notificação automática ao cliente sobre as atualizações do status do seu pedido.
4.  **Singleton**: Garante que uma classe tenha apenas uma instância e fornece um ponto de acesso global a ela.
    * **Exemplo**: Gerenciamento único da fila de pedidos da sorveteria (`FilaPedidos`).
5.  **Factory**: Define uma interface para criar objetos, mas permite que as subclasses decidam qual classe instanciar.
    * **Exemplo**: Criação de diferentes tipos de produtos de sorveteria, como picolés, sorvetes de massa e milkshakes.
6.  **Command**: Encapsula uma solicitação como um objeto, permitindo parametrizar clientes com diferentes solicitações, enfileirar ou registrar solicitações e suportar operações de desfazer/reexecutar.
    * **Exemplo**: Comandos para avançar o estado de um pedido (`AvancarEstadoPedidoCommand`) ou cancelar um pedido (`CancelarPedidoCommand`).
7.  **State**: Permite que um objeto altere seu comportamento quando seu estado interno muda.
    * **Exemplo**: Controle dos diferentes estados de um pedido (e.g., `RecebidoState`, `EmPreparoState`, `ProntoParaEntregaState`, `EntregueState`, `CanceladoState`).
8.  **Facade**: Oferece uma interface simplificada para um subsistema complexo.
    * **Exemplo**: `SistemaSorveteriaFacade` simplifica as interações para gerenciar pedidos e pagamentos, além de outras operações.
9.  **Repository**: Abstrai a camada de persistência de dados, fornecendo métodos para operações CRUD (Create, Read, Update, Delete).
    * **Exemplo**: Gerenciamento e persistência de dados de pedidos (`PedidoRepository`) e clientes (`ClienteRepositoryImpl`) em um banco de dados.

## 🚀 Passos para Implementação e Execução

Para configurar e executar o projeto, siga os passos abaixo:

1.  **Crie um projeto Java com Maven**:
    * Certifique-se de que o Java Development Kit (JDK) 11 ou superior e o Maven estejam instalados em sua máquina.
    * O `pom.xml` já está configurado com as dependências e plugins necessários, incluindo o driver PostgreSQL.

2.  **Configuração do Banco de Dados PostgreSQL**:
    * O projeto utiliza PostgreSQL para persistência. Você precisará de uma instância do PostgreSQL em execução.
    * A string de conexão do banco de dados está definida em `src/main/java/sorveteria/repository/DatabaseConnection.java`. **Atualize-a se necessário** com as credenciais do seu banco de dados.

    ```java
    // Exemplo de configuração no DatabaseConnection.java
    private static final String DB_URL = "jdbc:postgresql://your_host:your_port/your_database?user=your_user&password=your_password&sslmode=require";
    private static final String USER = "your_user";
    private static final String PASS = "your_password";
    ```
    * Você precisará criar as tabelas `clientes`, `pedidos` e `pedido_itens` no seu banco de dados. Um script SQL básico seria algo como:

    ```sql
    CREATE TABLE IF NOT EXISTS clientes (
        id SERIAL PRIMARY KEY,
        nome VARCHAR(255) NOT NULL,
        email VARCHAR(255) UNIQUE NOT NULL
    );

    CREATE TABLE IF NOT EXISTS pedidos (
        id SERIAL PRIMARY KEY,
        estado_atual VARCHAR(50) NOT NULL,
        valor_total DECIMAL(10, 2) NOT NULL,
        data_criacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
        id_cliente INT,
        FOREIGN KEY (id_cliente) REFERENCES clientes(id)
    );

    CREATE TABLE IF NOT EXISTS pedido_itens (
        id SERIAL PRIMARY KEY,
        pedido_id INT NOT NULL,
        nome_produto VARCHAR(255) NOT NULL,
        preco_unitario DECIMAL(10, 2) NOT NULL,
        FOREIGN KEY (pedido_id) REFERENCES pedidos(id) ON DELETE CASCADE
    );
    ```

3.  **Compilar e Executar**:
    * Navegue até o diretório raiz do projeto no seu terminal (onde o `pom.xml` está localizado).
    * Compile o projeto usando Maven:
        ```bash
        mvn clean install
        ```
    * Execute a aplicação a partir da classe `Main`:
        ```bash
        mvn exec:java -Dexec.mainClass="sorveteria.Main"
        ```
    * Alternativamente, você pode executar o JAR com dependências que será gerado em `target/sistema-sorveteria-1.0-SNAPSHOT-jar-with-dependencies.jar`:
        ```bash
        java -jar target/sistema-sorveteria-1.0-SNAPSHOT-jar-with-dependencies.jar
        ```

O sistema exibirá um menu interativo no console, permitindo que você gerencie clientes e pedidos.
