package analisepedidos.service;

import analisepedidos.exception.ApiException;
import analisepedidos.model.RespostaCarrinhos;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

/**
 * Parte 2: faz o GET na API DummyJSON e desserializa o JSON em objetos Java.
 */
public class CarrinhoApiClient {

    private static final String URL = "https://dummyjson.com/carts?limit=0";

    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    private final ObjectMapper mapper = new ObjectMapper();

    public RespostaCarrinhos buscarCarrinhos() throws ApiException {
        HttpRequest requisicao = HttpRequest.newBuilder()
                .uri(URI.create(URL))
                .timeout(Duration.ofSeconds(20))
                .header("Accept", "application/json")
                .GET()
                .build();

        HttpResponse<String> resposta;
        try {
            resposta = httpClient.send(requisicao, HttpResponse.BodyHandlers.ofString());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new ApiException("A requisição foi interrompida.", e);
        } catch (IOException e) {
            throw new ApiException("Falha de conexão com a API (" + URL + "): " + e.getMessage(), e);
        }

        if (resposta.statusCode() != 200) {
            throw new ApiException("A API respondeu com status HTTP " + resposta.statusCode()
                    + " (esperado: 200).");
        }

        return desserializar(resposta.body());
    }

    /**
     * Converte o corpo JSON em RespostaCarrinhos.
     * JSON malformado vira ApiException com mensagem clara.
     */
    public RespostaCarrinhos desserializar(String json) throws ApiException {
        try {
            RespostaCarrinhos dados = mapper.readValue(json, RespostaCarrinhos.class);
            if (dados == null) {
                throw new ApiException("A API retornou uma resposta vazia.");
            }
            return dados;
        } catch (JsonProcessingException e) {
            throw new ApiException("A resposta da API não é um JSON válido: "
                    + e.getOriginalMessage(), e);
        }
    }
}
