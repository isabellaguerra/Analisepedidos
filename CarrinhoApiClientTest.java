package analisepedidos.service;

import analisepedidos.exception.ApiException;
import analisepedidos.model.Carrinho;
import analisepedidos.model.ProdutoCarrinho;
import analisepedidos.model.RespostaCarrinhos;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

class CarrinhoApiClientTest {

    private CarrinhoApiClient cliente;
    private String jsonExemplo;

    @BeforeEach
    void preparar() throws IOException {
        cliente = new CarrinhoApiClient();
        try (InputStream in = getClass().getResourceAsStream("/carrinhos-exemplo.json")) {
            assertNotNull(in, "arquivo de exemplo não encontrado");
            jsonExemplo = new String(in.readAllBytes(), StandardCharsets.UTF_8);
        }
    }

    @Test
    void desserializaRespostaCompleta() throws ApiException {
        RespostaCarrinhos resposta = cliente.desserializar(jsonExemplo);

        assertEquals(2, resposta.getCarts().size());
        assertEquals(50, resposta.getTotal());
        assertEquals(0, resposta.getSkip());
        assertEquals(50, resposta.getLimit());

        Carrinho primeiro = resposta.getCarts().get(0);
        assertEquals(1, primeiro.getId());
        assertEquals(33, primeiro.getUserId());
        assertEquals(1200.50, primeiro.getTotal(), 0.001);
        assertEquals(2, primeiro.getProducts().size());

        ProdutoCarrinho notebook = primeiro.getProducts().get(0);
        assertEquals("Notebook", notebook.getTitle());
        assertEquals(12.5, notebook.getDiscountPercentage(), 0.001);
    }

    @Test
    void ignoraCamposDesconhecidos() {
        assertDoesNotThrow(() -> cliente.desserializar(jsonExemplo));
    }

    @Test
    void economiaEhTotalMenosDiscountedTotal() throws ApiException {
        Carrinho primeiro = cliente.desserializar(jsonExemplo).getCarts().get(0);
        assertEquals(150.50, primeiro.getEconomia(), 0.001);
    }

    @Test
    void jsonMalformadoGeraApiExceptionComMensagemClara() {
        ApiException erro = assertThrows(ApiException.class,
                () -> cliente.desserializar("{ isso nao eh json"));
        assertTrue(erro.getMessage().contains("JSON"));
    }
}
