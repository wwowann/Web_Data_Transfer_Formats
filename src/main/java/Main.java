import request.Request;
import server.Server;

import java.io.*;
import java.net.ServerSocket;
import java.nio.file.Files;

import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        Server server = new Server(64);
        server.addHandler("GET", "/classic.html", ((request, out) -> {
            try {
                final var filePath = Path.of(".", "public", request.getPath());//считываем ресурс из папки public
                final var mimeType = Files.probeContentType(filePath);
                final var template = Files.readString(filePath);
                final var content = template.replace(
                        "{time}",
                        LocalDateTime.now().toString()
                ).getBytes();
                out.write((
                        "HTTP/1.1 200 OK\r\n" +
                                "Content-Type: " + mimeType + "\r\n" +
                                "Content-Length: " + content.length + "\r\n" +
                                "Connection: close\r\n" +
                                "\r\n"
                ).getBytes());
                out.write(content);
                out.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }));

        server.addHandler("GET", "/index.html", ((request, out) -> {
            final var filePath = Path.of(".", "public/messages", request.getPath());
            final String mimeType;
            try {
                mimeType = Files.probeContentType(filePath);
                final long length = Files.size(filePath);
                out.write((
                        "HTTP/1.1 200 OK\r\n" +
                                "Content-Type: " + mimeType + "\r\n" +
                                "Content-Length: " + length + "\r\n" +
                                "Connection: close\r\n" +
                                "\r\n"
                ).getBytes());
                Files.copy(filePath, out);
                out.flush();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }));

        server.addHandler("GET", "/page.html", ((request, out) -> {
            final var filePath = Path.of(".", "public/messages", request.getPath());
            final String mimeType;
            try {
                mimeType = Files.probeContentType(filePath);
                final long length = Files.size(filePath);
                out.write((
                        "HTTP/1.1 200 OK\r\n" +
                                "Content-Type: " + mimeType + "\r\n" +
                                "Content-Length: " + length + "\r\n" +
                                "Connection: close\r\n" +
                                "\r\n"
                ).getBytes());
                Files.copy(filePath, out);
                out.flush();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }));
        server.addHandler("POST", "/page.html", ((request, out) -> {
            final var filePath = Path.of(".", "public/messages", request.getPath());
            final String mimeType;
            try {
                mimeType = Files.probeContentType(filePath);
                final long length = Files.size(filePath);
                out.write((
                        "HTTP/1.1 200 OK\r\n" +
                                "Content-Type: " + mimeType + "\r\n" +
                                "Content-Length: " + length + "\r\n" +
                                "Connection: close\r\n" +
                                "\r\n" + request.getBody()
                ).getBytes());
                Files.copy(filePath, out);
                out.flush();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }));
        server.listen(9999);
    }
}