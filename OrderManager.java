import java.util.*;
import java.util.stream.Collectors;

public class OrderManager {
    private final Map<Integer, Order> ordersById = new HashMap<>();
    private final Map<String, List<Order>> ordersByCustomer = new HashMap<>();

    // criar pedido
    public Order createOrder(String customerName, String address) {
        Order o = new Order(customerName, address);
        ordersById.put(o.getId(), o);
        ordersByCustomer.computeIfAbsent(customerName.toLowerCase(), k -> new ArrayList<>()).add(o);
        return o;
    }

    public Optional<Order> findOrderById(int id) {
        return Optional.ofNullable(ordersById.get(id));
    }

    public List<Order> findOrdersByCustomer(String customerName) {
        return ordersByCustomer.getOrDefault(customerName.toLowerCase(), Collections.emptyList());
    }

    public List<Order> listAllOrders() {
        return new ArrayList<>(ordersById.values())
                .stream()
                .sorted(Comparator.comparing(Order::getCreatedAt))
                .collect(Collectors.toList());
    }

    // ********** M E T O D O  A L T E R A R  P E D I D O **********
    // localizar por id ou nome, e aplicar mudanças: adicionar/remover pizza e alterar sabor
    public boolean alterarPedidoById(int orderId, List<Pizza> pizzasToAdd, List<Integer> pizzaIdsToRemove, Map<Integer, String> changeFlavorMap) {
        Order o = ordersById.get(orderId);
        if (o == null) return false;

        // remover pizzas por id
        if (pizzaIdsToRemove != null) {
            for (int pid : pizzaIdsToRemove) {
                o.removePizzaById(pid);
            }
        }
        // adicionar pizzas
        if (pizzasToAdd != null) {
            for (Pizza p : pizzasToAdd) o.addPizza(p);
        }
        // alterar sabor de pizzas existentes (map pizzaId -> novoSabor)
        if (changeFlavorMap != null) {
            for (Map.Entry<Integer, String> e : changeFlavorMap.entrySet()) {
                int pid = e.getKey();
                String novo = e.getValue();
                for (Pizza p : o.getPizzas()) {
                    if (p.getId() == pid) {
                        p.setFlavor(novo);
                    }
                }
            }
        }
        return true;
    }

    public boolean alterarPedidoByCustomer(String customerName, int orderIndexZeroBased,
                                           List<Pizza> pizzasToAdd, List<Integer> pizzaIdsToRemove, Map<Integer, String> changeFlavorMap) {
        List<Order> list = findOrdersByCustomer(customerName);
        if (orderIndexZeroBased < 0 || orderIndexZeroBased >= list.size()) return false;
        Order o = list.get(orderIndexZeroBased);
        return alterarPedidoById(o.getId(), pizzasToAdd, pizzaIdsToRemove, changeFlavorMap);
    }

    // ********** M E T O D O  G E R A R  R E L A T Ó R I O **********
    // gera: faturamento total, sabores mais pedidos (top N) e grafo de conexões (co-ocorrência entre sabores)
    public Report gerarRelatorio(int topNFlavors) {
        double totalRevenue = 0.0;
        Map<String, Integer> flavorCounts = new HashMap<>();
        Map<String, Map<String,Integer>> adjacency = new HashMap<>(); // graph: flavor -> (neighborFlavor -> count)

        for (Order o : ordersById.values()) {
            totalRevenue += o.totalPrice();

            // contar sabores
            List<String> sabores = o.getPizzas().stream()
                    .map(Pizza::getFlavor)
                    .filter(Objects::nonNull)
                    .map(String::toLowerCase)
                    .collect(Collectors.toList());

            for (String s : sabores) flavorCounts.put(s, flavorCounts.getOrDefault(s, 0) + 1);

            // para cada par de sabores distintos no pedido, incrementar aresta (co-ocorrência)
            Set<String> uniqueSabores = new HashSet<>(sabores);
            List<String> listS = new ArrayList<>(uniqueSabores);
            Collections.sort(listS);
            for (int i = 0; i < listS.size(); i++) {
                for (int j = i + 1; j < listS.size(); j++) {
                    String a = listS.get(i);
                    String b = listS.get(j);
                    adjacency.computeIfAbsent(a, k -> new HashMap<>()).put(b,
                            adjacency.getOrDefault(a, Collections.emptyMap()).getOrDefault(b, 0) + 1);
                    adjacency.computeIfAbsent(b, k -> new HashMap<>()).put(a,
                            adjacency.getOrDefault(b, Collections.emptyMap()).getOrDefault(a, 0) + 1);
                }
            }
        }

        // obter top N sabores
        List<Map.Entry<String,Integer>> sortedFlavors = new ArrayList<>(flavorCounts.entrySet());
        sortedFlavors.sort((e1,e2) -> Integer.compare(e2.getValue(), e1.getValue()));
        List<FlavorCount> top = new ArrayList<>();
        for (int i = 0; i < Math.min(topNFlavors, sortedFlavors.size()); i++) {
            Map.Entry<String,Integer> e = sortedFlavors.get(i);
            top.add(new FlavorCount(e.getKey(), e.getValue()));
        }

        return new Report(totalRevenue, top, adjacency);
    }

    // ********** M E T O D O  C A L C U L A R  F R E T E **********
    // Fórmula exemplo:
    // frete = base + distance * perKm + numPizzas * perPizzaWeight * perKgPrice
    // assumimos peso por pizza (kg)
    public double calcularFrete(double distanceKm, int numPizzas) {
        final double base = 5.0; // R$ base
        final double perKm = 1.2; // R$ por km
        final double pesoPorPizza = 0.5; // kg
        final double precoPorKg = 2.0; // R$ por kg

        double pesoTotal = numPizzas * pesoPorPizza;
        double frete = base + (distanceKm * perKm) + (pesoTotal * precoPorKg);
        // política: frete mínimo
        return Math.max(frete, 7.0);
    }

    // data classes for report
    public static class Report {
        public final double totalRevenue;
        public final List<FlavorCount> topFlavors;
        // adjacency: flavor -> (neighborFlavor -> count)
        public final Map<String, Map<String,Integer>> graph;

        public Report(double totalRevenue, List<FlavorCount> topFlavors, Map<String, Map<String,Integer>> graph) {
            this.totalRevenue = totalRevenue;
            this.topFlavors = topFlavors;
            this.graph = graph;
        }
    }

    public static class FlavorCount {
        public final String flavor;
        public final int count;
        public FlavorCount(String flavor, int count) { this.flavor = flavor; this.count = count; }
    }
}
