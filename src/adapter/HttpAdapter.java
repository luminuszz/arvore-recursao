package adapter;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;
import model.TreeBuilder;
import model.RecurrenceResult;

import java.io.IOException;
import java.io.OutputStream;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.io.File;

public class HttpAdapter {

    public static void startServer(int port) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
        
        server.createContext("/", new StaticFileHandler());
        server.createContext("/api/solve", new SolveHandler());
        
        server.setExecutor(null);
        server.start();
        System.out.println("Servidor web iniciado na porta " + port);
        System.out.println("Acesse: http://localhost:" + port);
    }

    static class StaticFileHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String path = exchange.getRequestURI().getPath();
            if (path.equals("/")) {
                path = "/index.html";
            }
            
            File file = new File("src/web" + path);
            if (file.exists() && !file.isDirectory()) {
                exchange.sendResponseHeaders(200, file.length());
                OutputStream os = exchange.getResponseBody();
                Files.copy(file.toPath(), os);
                os.close();
            } else {
                String response = "404 Not Found";
                exchange.sendResponseHeaders(404, response.length());
                OutputStream os = exchange.getResponseBody();
                os.write(response.getBytes());
                os.close();
            }
        }
    }

    static class SolveHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            try {
                String query = exchange.getRequestURI().getQuery();
                Map<String, String> params = parseQuery(query);
                
                int quantidadeDeSubproblemas = Integer.parseInt(params.get("quantidadeDeSubproblemas"));
                double divisorDoTamanhoDoSubproblema = Double.parseDouble(params.get("divisorDoTamanhoDoSubproblema"));
                double constanteDeTrabalho = Double.parseDouble(params.get("constanteDeTrabalho"));
                double expoenteDoPolinomioDeTrabalho = Double.parseDouble(params.get("expoenteDoPolinomioDeTrabalho"));
                double tamanhoInicialDoProblema = Double.parseDouble(params.get("tamanhoInicialDoProblema"));
                
                RecurrenceResult resultado = TreeBuilder.construirArvore(tamanhoInicialDoProblema, quantidadeDeSubproblemas, divisorDoTamanhoDoSubproblema, constanteDeTrabalho, expoenteDoPolinomioDeTrabalho);
                String respostaJson = resultado.toJson();
                
                exchange.getResponseHeaders().set("Content-Type", "application/json");
                exchange.sendResponseHeaders(200, respostaJson.getBytes().length);
                OutputStream os = exchange.getResponseBody();
                os.write(respostaJson.getBytes());
                os.close();
            } catch (Exception e) {
                String response = "{\"error\":\"" + e.getMessage() + "\"}";
                exchange.getResponseHeaders().set("Content-Type", "application/json");
                exchange.sendResponseHeaders(400, response.getBytes().length);
                OutputStream os = exchange.getResponseBody();
                os.write(response.getBytes());
                os.close();
            }
        }

        private Map<String, String> parseQuery(String query) {
            Map<String, String> map = new HashMap<>();
            if (query != null) {
                String[] pairs = query.split("&");
                for (String pair : pairs) {
                    String[] kv = pair.split("=");
                    if (kv.length > 1) {
                        map.put(kv[0], kv[1]);
                    }
                }
            }
            return map;
        }
    }
}
