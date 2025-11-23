import java.util.Objects;

public class Pizza {
    private static int nextId = 1;
    private final int id;
    private String flavor;
    private double price;
    private int slices; // opcional

    public Pizza(String flavor, double price) {
        this.id = nextId++;
        this.flavor = flavor;
        this.price = price;
        this.slices = 8;
    }

    public int getId() { return id; }
    public String getFlavor() { return flavor; }
    public void setFlavor(String flavor) { this.flavor = flavor; }
    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }
    public int getSlices() { return slices; }
    public void setSlices(int slices) { this.slices = slices; }

    @Override
    public String toString() {
        return String.format("Pizza{id=%d, flavor='%s', price=R$%.2f}", id, flavor, price);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Pizza)) return false;
        Pizza pizza = (Pizza) o;
        return id == pizza.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
