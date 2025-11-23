import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Order {
    private static int nextId = 1000;
    private final int id;
    private String customerName;
    private String address;
    private final List<Pizza> pizzas;
    private boolean delivered;
    private final LocalDateTime createdAt;

    public Order(String customerName, String address) {
        this.id = nextId++;
        this.customerName = customerName;
        this.address = address;
        this.pizzas = new ArrayList<>();
        this.delivered = false;
        this.createdAt = LocalDateTime.now();
    }

    public int getId() { return id; }
    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public List<Pizza> getPizzas() { return pizzas; }
    public boolean isDelivered() { return delivered; }
    public void setDelivered(boolean delivered) { this.delivered = delivered; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    public void addPizza(Pizza p) { pizzas.add(p); }
    public boolean removePizzaById(int pizzaId) {
        return pizzas.removeIf(p -> p.getId() == pizzaId);
    }
    public double totalPrice() {
        return pizzas.stream().mapToDouble(Pizza::getPrice).sum();
    }
    public int totalPizzas() { return pizzas.size(); }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("Order{id=%d, customer='%s', address='%s', pizzas=%d, total=R$%.2f, delivered=%s}\n",
                id, customerName, address, pizzas.size(), totalPrice(), delivered));
        for (Pizza p : pizzas) sb.append("  - ").append(p).append("\n");
        return sb.toString();
    }
}
