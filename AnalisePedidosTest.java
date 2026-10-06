package analisepedidos.service;

import analisepedidos.exception.ApiException;
import analisepedidos.model.Carrinho;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AnalisePedidosTest {

    private static final String JSON = """
            {"carts":[
              {"id":1,"userId":33,"total":1200.50,"discountedTotal":1050.00,"totalProducts":2,"totalQuantity":6,
               "products":[
                 {"id":10,"title":"Notebook","price":500,"quantity":2,"total":1000,"discountPercentage":12.5},
                 {"id":11,"title":"Mouse","price":50,"quantity":4,"total":200,"discountPercentage":20}]},
              {"id":2,"userId":5,"total":300,"discountedTotal":280,"totalProducts":1,"totalQuantity":1,
               "products":[
                 {"id":12,"title":"Teclado","price":300,"quantity":1,"total":300,"discountPercentage":6.7}]}
            ],"total":2,"skip":0,"limit":0}
            """;

    private String executarRelatorio() throws ApiException {
        List<Carrinho> carrinhos = new CarrinhoApiClient().desserializar(JSON).getCarts();

        PrintStream original = System.out;
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        System.setOut(new PrintStream(buffer, true, StandardCharsets.UTF_8));
        try {
            new AnalisePedidos().gerarRelatorio(carrinhos);
        } finally {
            System.setOut(original);
        }
        return buffer.toString(StandardCharsets.UTF_8);
    }

    @Test
    void filtroMostraSoCarrinhosAcimaDeMilDolares() throws ApiException {
        String saida = executarRelatorio();
        assertTrue(saida.contains("Carrinho #1 | Usuário: 33 | Itens: 6 | Total: US$ 1200.50"));
        assertFalse(saida.contains("Carrinho #2 | Usuário: 5"));
    }

    @Test
    void mapeamentoMostraApenasProdutosComDescontoAcimaDe15() throws ApiException {
        String saida = executarRelatorio();
        assertTrue(saida.contains("- Mouse"));
        assertFalse(saida.contains("- Notebook"));
        assertFalse(saida.contains("- Teclado"));
    }

    @Test
    void reduceSomaOsDiscountedTotal() throws ApiException {
        assertTrue(executarRelatorio().contains("Soma: US$ 1330.00"));
    }

    @Test
    void groupingByContaCarrinhosPorQuantidadeDeProdutos() throws ApiException {
        String saida = executarRelatorio();
        assertTrue(saida.contains("1 produto(s): 1 carrinho(s)"));
        assertTrue(saida.contains("2 produto(s): 1 carrinho(s)"));
    }
}
