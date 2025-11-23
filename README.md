# Projeto Final - Sistema de Gerenciamento de Pedidos (Pizzaria)

Este projeto implementa melhorias no sistema de pedidos para uma pizzaria: métodos para **alterar pedidos**, **gerar relatórios (com grafo de sabores)** e **calcular frete**.

## Arquivos
- `Pizza.java` — modelo de pizza
- `Order.java` — modelo de pedido
- `OrderManager.java` — lógica de negócios (criar/alterar/pesquisar/relatórios/frete)
- `Main.java` — aplicação console para demonstração interativa

## Funcionalidades principais
1. **Alterar Pedido**
   - Localizar por `orderId` ou `customerName`
   - Adicionar pizzas ao pedido
   - Remover pizzas por `pizzaId`
   - Alterar sabor de pizza por `pizzaId`

2. **Gerar Relatório**
   - Total de faturamento (soma de todos pedidos)
   - Top N sabores mais pedidos
   - Grafo de conexões entre sabores (co-ocorrência dentro do mesmo pedido)

3. **Calcular Frete**
   - Fórmula básica: `frete = base + perKm * distance + pesoTotal * precoPorKg`
   - Peso por pizza = 0.5 kg (ajustável)
   - Retorna valor com frete mínimo

## Como compilar e executar
1. Coloque os arquivos `.java` em `src/`
2. No terminal (na raiz do projeto):
```bash
javac src/*.java
java -cp src Main
