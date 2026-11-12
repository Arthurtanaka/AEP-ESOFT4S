package controller;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

/**
 * Entrega ao navegador os arquivos da interface (HTML, CSS e JavaScript) que ficam na pasta src/web.
 * Usa Path/Paths do Java, que cuidam das diferenças de separador de pastas entre Windows ("\")
 * e Linux/macOS ("/") - compatibilidade entre sistemas operacionais.
 */
public class ArquivosEstaticosController implements HttpHandler {

    private static final Map<String, String> TIPOS = new HashMap<>();

    static {
        TIPOS.put("html", "text/html; charset=utf-8");
        TIPOS.put("css", "text/css; charset=utf-8");
        TIPOS.put("js", "application/javascript; charset=utf-8");
        TIPOS.put("json", "application/json; charset=utf-8");
        TIPOS.put("svg", "image/svg+xml");
        TIPOS.put("png", "image/png");
        TIPOS.put("ico", "image/x-icon");
    }

    private final Path pastaBase;

    public ArquivosEstaticosController(String pasta) {
        this.pastaBase = Paths.get(pasta).toAbsolutePath().normalize();
    }

    @Override
    public void handle(HttpExchange troca) throws IOException {
        try {
            if (!troca.getRequestMethod().equalsIgnoreCase("GET")) {
                enviar(troca, 405, "text/plain; charset=utf-8", "Método não permitido.".getBytes(StandardCharsets.UTF_8));
                return;
            }
            String caminho = URLDecoder.decode(troca.getRequestURI().getPath(), StandardCharsets.UTF_8);
            if (caminho.equals("/") || caminho.isEmpty()) {
                caminho = "/index.html";
            }
            Path arquivo = pastaBase.resolve(caminho.substring(1)).normalize();

            // Segurança: impede acessar arquivos fora da pasta web (ex.: /../../senha.txt)
            if (!arquivo.startsWith(pastaBase) || !Files.isRegularFile(arquivo)) {
                enviar(troca, 404, "text/plain; charset=utf-8", "Página não encontrada.".getBytes(StandardCharsets.UTF_8));
                return;
            }
            String nome = arquivo.getFileName().toString();
            String extensao = nome.contains(".") ? nome.substring(nome.lastIndexOf('.') + 1).toLowerCase() : "";
            String tipo = TIPOS.getOrDefault(extensao, "application/octet-stream");
            enviar(troca, 200, tipo, Files.readAllBytes(arquivo));
        } finally {
            troca.close();
        }
    }

    private void enviar(HttpExchange troca, int status, String tipo, byte[] bytes) throws IOException {
        troca.getResponseHeaders().set("Content-Type", tipo);
        troca.sendResponseHeaders(status, bytes.length);
        try (OutputStream saida = troca.getResponseBody()) {
            saida.write(bytes);
        }
    }
}
