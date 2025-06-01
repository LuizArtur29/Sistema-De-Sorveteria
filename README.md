### 🎯 Objetivo do projeto:  
Criar um sorveteria.sorveteria.model de pedidos para uma sorveteria, permitindo gerenciar sabores, personalizar pedidos, aplicar descontos, acompanhar atualizações e otimizar o fluxo de pedidos.

### 🔹 Funcionalidades e padrões de design aplicados:
1. Strategy → Aplicação de diferentes estratégias de desconto (ex: desconto para clientes frequentes, desconto sazonal).  
2. Decorator → Personalização do sorvete (ex: adicionar cobertura, calda, chantilly ao pedido).  
3. Observer → Notificação automática ao cliente sobre o status do pedido.  
4. Singleton → Gerenciamento único da fila de pedidos na sorveteria.  
5. Factory → Criação de objetos de diferentes tipos de sorvetes no menu (ex: picolé, massa, milkshake).  
6. Command → Encapsular ações dos pedidos, permitindo desfazer/reexecutar comandos (ex: cancelar ou refazer pedido).  
7. State → Controlar estados dos pedidos (ex: PedidoRecebido, PedidoPreparando, PedidoEntregue).  
8. Facade → Interface simplificada para gerenciar pedidos e pagamentos.  
9. Repository → Camada de persistência para salvar pedidos e histórico de clientes.  

### 🚀 Passos para implementação:
1. Crie um projeto Java com pacotes organizados (ex: `sorveteria.sorveteria.model.sorveteria.sorveteria.model`, `service`, `repository`, `observer`).  
2. Implemente os padrões começando pelos mais básicos (Singleton, Factory, Strategy).  
3. Simule a execução do sorveteria.sorveteria.model com diferentes pedidos e estados.
