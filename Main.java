package analisepedidos;

import analisepedidos.exception.ApiException;
import analisepedidos.model.RespostaCarrinhos;
import analisepedidos.service.AnalisePedidos;
import analisepedidos.service.CarrinhoApiClient;

public class Main {

    public static void main(String[] args) {
        CarrinhoApiClient cliente = new CarrinhoApiClient();

        RespostaCarrinhos resposta;
        try {
            resposta = cliente.buscarCarrinhos();
        } catch (ApiException e) {
            // Mensagem clara, sem derrubar o programa com stack trace
            System.err.println("Não foi possível obter os carrinhos: " + e.getMessage());
            return;
        }

        if (resposta.getCarts().isEmpty()) {
            System.out.println("A API não retornou nenhum carrinho.");
            return;
        }

        System.out.println("Carrinhos recebidos: " + resposta.getCarts().size());
        new AnalisePedidos().gerarRelatorio(resposta.getCarts());
    }
}
