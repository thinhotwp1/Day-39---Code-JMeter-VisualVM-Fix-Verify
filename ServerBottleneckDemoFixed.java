import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;

public class ServerBottleneckDemoFixed {

    public static void main(String[] args) throws IOException {
        // Tạo server tại port 8000
        HttpServer server = HttpServer.create(new InetSocketAddress(8000), 0);
        
        // Tạo endpoint /test
        server.createContext("/test", new MyHandler());
        
        // Sử dụng Executor mặc định (đơn giản hóa cho demo)
        server.setExecutor(java.util.concurrent.Executors.newFixedThreadPool(10)); 
        
        server.start();
        System.out.println("Server đang chạy tại http://localhost:8000/test");
        System.out.println("PID: " + ProcessHandle.current().pid());
    }

    static class MyHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange t) throws IOException {
            String response = processBigData(); 
            
            t.sendResponseHeaders(200, response.length());
            OutputStream os = t.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }

        // Performance Fixed 
        private String processBigData() {
            StringBuilder sb = new StringBuilder(); 
            for (int i = 0; i < 5000; i++) {
                sb.append("Data").append(i); 
            }
            return "Done";
        }
    }
}

