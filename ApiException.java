package analisepedidos.exception;

/**
 * Erro amigável lançado quando algo dá errado ao consultar a API
 * (conexão, status HTTP diferente de 200 ou JSON malformado).
 */
public class ApiException extends Exception {

    public ApiException(String mensagem) {
        super(mensagem);
    }

    public ApiException(String mensagem, Throwable causa) {
        super(mensagem, causa);
    }
}
