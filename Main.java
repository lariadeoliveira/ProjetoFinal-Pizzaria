import java.util.*;

public class Main {
    private static final OrderManager manager = new OrderManager();
    private static final Scanner sc = new Scanner(System.in);

    private static void seedSampleData() {
        Order o1 = manager.createOrder("Maria", "Rua A, 100");
        o1.addPizza(new Pizza("Margherita", 30.0));
        o1.addPizza(new Pizza("Pepperoni", 35.0));

        Order o2 = manager.createOrder("João", "Av B, 200");
        o2.addPizza(new Pizza("Calabresa", 33.0));

        Order o3 = manager.createOrder("Ana", "Rua C, 50");
        o3.addPizza(new Pizza("Margherita", 30.0));
        o3.addPizza(new Pizza("Calabresa", 33.0));
        o3.addPizza(new Pizza("Pepperoni", 35.0));
    }

    public static void main(String[] args) {
        seedSampleData();
        boolean running = true;
        while (running) {
            System.out.println("\n=== Sistema Pizzaria - Projeto Final ===");
            System.out.println("1 - Criar pedido");
            System.out.println("2 - Listar pedidos");
            System.out.println("3 - Alterar pedido");
            System.out.println("4 - Gerar relatório");
            System.out.println("5 - Calcular frete");
            System.out.println("0 - Sair");
            System.out.print("Opção: ");
            String opt = sc.nextLine().trim();
            switch (opt) {
                case "1": cmdCreateOrder(); break;
                case "2": cmdListOrders(); break;
                case "3": cmdAlterOrder(); break;
                case "4": cmdGenerateReport(); break;
                case "5": cmdCalculateFreight(); break;
                case "0": running = false; break;
                default: System.out.println("Opção inválida."); break;
            }
        }
        System.out.println("Encerrando...");
    }

    private static void cmdCreateOrder() {
        System.out.print("Nome do cliente: "); String name = sc.nextLine().trim();
        System.out.print("Endereço: "); String addr = sc.nextLine().trim();
        Order o = manager.createOrder(name, addr);
        System.out.println("Pedido criado com id: " + o.getId());
        boolean add = true;
        while (add) {
            System.out.print("Adicionar pizza (s/n)? ");
            String r = sc.nextLine().trim().toLowerCase();
            if (!r.equals("s")) break;
            System.out.print("Sabor: "); String sabor = sc.nextLine().trim();
            System.out.print("Preço (ex: 30.0): "); double price = Double.parseDouble(sc.nextLine().trim());
            o.addPizza(new Pizza(sabor, price));
        }
    }

    private static void cmdListOrders() {
        List<Order> all = manager.listAllOrders();
        if (all.isEmpty()) System.out.println("Nenhum pedido.");
        for (Order o : all) System.out.println(o);
    }

    private static void cmdAlterOrder() {
        System.out.println("Localizar por (1) ID ou (2) nome do cliente?");
        String r = sc.nextLine().trim();
        if (r.equals("1")) {
            System.out.print("Digite ID: ");
            int id = Integer.parseInt(sc.nextLine().trim());
            Optional<Order> oo = manager.findOrderById(id);
            if (!oo.isPresent()) { System.out.println("Pedido não encontrado."); return; }
            Order o = oo.get();
            System.out.println("Pedido encontrado:\n" + o);
            interactiveAlterForOrder(o.getId());
        } else if (r.equals("2")) {
            System.out.print("Digite nome do cliente: ");
            String name = sc.nextLine().trim();
            List<Order> list = manager.findOrdersByCustomer(name);
            if (list.isEmpty()) { System.out.println("Nenhum pedido desse cliente."); return; }
            for (int i = 0; i < list.size(); i++) System.out.println(i + ": " + list.get(i));
            System.out.print("Escolha índice do pedido a alterar (0-based): ");
            int idx = Integer.parseInt(sc.nextLine().trim());
            if (idx < 0 || idx >= list.size()) { System.out.println("Índice inválido."); return; }
            Order o = list.get(idx);
            interactiveAlterForOrder(o.getId());
        } else {
            System.out.println("Opção inválida.");
        }
    }

    // interface simples para alterar um pedido: adicionar, remover, alterar sabor
    private static void interactiveAlterForOrder(int orderId) {
        boolean done = false;
        while (!done) {
            System.out.println("\nAlterar pedido id " + orderId + " - opções:");
            System.out.println("1 - Adicionar pizza");
            System.out.println("2 - Remover pizza (por pizzaId)");
            System.out.println("3 - Alterar sabor (por pizzaId)");
            System.out.println("4 - Mostrar pedido");
            System.out.println("0 - Voltar");
            System.out.print("Opção: ");
            String op = sc.nextLine().trim();
            switch (op) {
                case "1":
                    System.out.print("Sabor: "); String s = sc.nextLine().trim();
                    System.out.print("Preço: "); double p = Double.parseDouble(sc.nextLine().trim());
                    manager.alterarPedidoById(orderId, Arrays.asList(new Pizza(s,p)), null, null);
                    System.out.println("Pizza adicionada.");
                    break;
                case "2":
                    System.out.print("Digite pizzaId a remover: ");
                    int pid = Integer.parseInt(sc.nextLine().trim());
                    manager.alterarPedidoById(orderId, null, Arrays.asList(pid), null);
                    System.out.println("Remoção solicitada.");
                    break;
                case "3":
                    System.out.print("PizzaId: "); int pp = Integer.parseInt(sc.nextLine().trim());
                    System.out.print("Novo sabor: "); String novo = sc.nextLine().trim();
                    Map<Integer,String> mv = new HashMap<>(); mv.put(pp, novo);
                    manager.alterarPedidoById(orderId, null, null, mv);
                    System.out.println("Sabor alterado (se pizzaId existir).");
                    break;
                case "4":
                    manager.findOrderById(orderId).ifPresent(System.out::println);
                    break;
                case "0": done = true; break;
                default: System.out.println("Opção inválida."); break;
            }
        }
    }

    private static void cmdGenerateReport() {
        System.out.print("Quantos top sabores deseja listar? (ex: 3): ");
        int topN = Integer.parseInt(sc.nextLine().trim());
        OrderManager.Report report = manager.gerarRelatorio(topN);
        System.out.println("\n--- Relatório ---");
        System.out.printf("Faturamento total: R$ %.2f\n", report.totalRevenue);
        System.out.println("Top sabores:");
        for (OrderManager.FlavorCount fc : report.topFlavors) {
            System.out.printf("  %s — %d pedidos\n", fc.flavor, fc.count);
        }
        System.out.println("\nConexões entre sabores (co-ocorrência no mesmo pedido):");
        for (Map.Entry<String, Map<String,Integer>> e : report.graph.entrySet()) {
            System.out.println("  " + e.getKey() + " -> " + e.getValue());
        }
    }

    private static void cmdCalculateFreight() {
        System.out.print("Distância (km): ");
        double km = Double.parseDouble(sc.nextLine().trim());
        System.out.print("Quantidade de pizzas: ");
        int qtd = Integer.parseInt(sc.nextLine().trim());
        double frete = manager.calcularFrete(km, qtd);
        System.out.printf("Frete calculado: R$ %.2f\n", frete);
    }
}
