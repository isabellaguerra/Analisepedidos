package analisepedidos.service;

import analisepedidos.model.Carrinho;
import analisepedidos.model.ProdutoCarrinho;

import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Parte 3 + desafios extras: relatório com Streams e Lambdas (sem for/while).
 */
public class AnalisePedidos {

    // Desafio extra 2: formatação feita com uma lambda
    private static final Function<Carrinho, String> FORMATADOR = c -> String.format(Locale.US,
            "Carrinho #%d | Usuário: %d | Itens: %d | Total: US$ %.2f",
            c.getId(), c.getUserId(), c.getTotalQuantity(), c.getTotal());

    public void gerarRelatorio(List<Carrinho> carrinhos) {

        // 1) Filtragem (filter)
        titulo("1) filter — Carrinhos com total acima de US$ 1.000");
        carrinhos.stream()
                .filter(c -> c.getTotal() > 1000)
                .map(FORMATADOR)
                .forEach(System.out::println);

        // 2) Mapeamento (flatMap + map)
        titulo("2) flatMap + map — Títulos dos produtos com desconto acima de 15%");
        carrinhos.stream()
                .flatMap(c -> c.getProducts().stream())
                .filter(p -> p.getDiscountPercentage() > 15)
                .map(ProdutoCarrinho::getTitle)
                .distinct()
                .forEach(t -> System.out.println("- " + t));

        // 3) Ordenação (sorted)
        titulo("3) sorted — Carrinhos pela economia, da maior para a menor");
        carrinhos.stream()
                .sorted(Comparator.comparingDouble(Carrinho::getEconomia).reversed())
                .forEach(c -> System.out.printf(Locale.US,
                        "Carrinho #%d | Economia: US$ %.2f%n", c.getId(), c.getEconomia()));

        // 4) Redução (reduce)
        titulo("4) reduce — Soma do discountedTotal de todos os carrinhos");
        double somaDescontados = carrinhos.stream()
                .map(Carrinho::getDiscountedTotal)
                .reduce(0.0, Double::sum);
        Stream.of(somaDescontados)
                .forEach(s -> System.out.printf(Locale.US, "Soma: US$ %.2f%n", s));

        // 5) Agrupamento (groupingBy)
        titulo("5) groupingBy — Quantidade de carrinhos por número de produtos (totalProducts)");
        Map<Integer, Long> carrinhosPorQtdProdutos = carrinhos.stream()
                .collect(Collectors.groupingBy(
                        Carrinho::getTotalProducts, TreeMap::new, Collectors.counting()));
        carrinhosPorQtdProdutos.forEach((produtos, qtd) ->
                System.out.printf("%d produto(s): %d carrinho(s)%n", produtos, qtd));

        // Desafio extra 1: carrinho de maior valor com max e Optional
        titulo("Extra) max + Optional — Carrinho de maior valor");
        carrinhos.stream()
                .max(Comparator.comparingDouble(Carrinho::getTotal))
                .map(FORMATADOR)
                .ifPresentOrElse(
                        System.out::println,
                        () -> System.out.println("Nenhum carrinho encontrado."));
    }

    private void titulo(String texto) {
        System.out.println();
        System.out.println("=== " + texto + " ===");
    }
}
