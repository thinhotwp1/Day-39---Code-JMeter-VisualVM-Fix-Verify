import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;

public class ServerBottleneckDemo {

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
            // --- ĐIỂM NÓNG (BOTTLENECK) Ở ĐÂY ---
            String response = processBigData(); 
            
            t.sendResponseHeaders(200, response.length());
            OutputStream os = t.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }

        // Phương thức này CỐ TÌNH viết kém hiệu năng
        private String processBigData() {
            String result = "";
            // Cộng chuỗi bằng dấu '+' trong vòng lặp lớn
            // Điều này tạo ra hàng ngàn đối tượng String rác
            for (int i = 0; i < 5000; i++) {
                result += "Data" + i; 
            }
            return "Done"; // Trả về dummy thôi, quan trọng là quá trình cộng ở trên
        }
    }
}

