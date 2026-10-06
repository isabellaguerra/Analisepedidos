package analisepedidos.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Carrinho {

    private int id;
    private int userId;
    private double total;
    private double discountedTotal;
    private int totalProducts;
    private int totalQuantity;
    private List<ProdutoCarrinho> products = new ArrayList<>();

    public Carrinho() {
    }

    public int getId() {
        return id;
    }

    public int getUserId() {
        return userId;
    }

    public double getTotal() {
        return total;
    }

    public double getDiscountedTotal() {
        return discountedTotal;
    }

    public int getTotalProducts() {
        return totalProducts;
    }

    public int getTotalQuantity() {
        return totalQuantity;
    }

    public List<ProdutoCarrinho> getProducts() {
        return products == null ? List.of() : products;
    }

    /**
     * Quanto o cliente economizou: total - discountedTotal.
     * É um valor calculado (não vem do JSON), por isso o @JsonIgnore.
     */
    @JsonIgnore
    public double getEconomia() {
        return total - discountedTotal;
    }

    @Override
    public String toString() {
        return "Carrinho{" +
                "id=" + id +
                ", userId=" + userId +
                ", total=" + total +
                ", discountedTotal=" + discountedTotal +
                ", totalProducts=" + totalProducts +
                ", totalQuantity=" + totalQuantity +
                ", products=" + products +
                '}';
    }
}
