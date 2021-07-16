package request;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

public class Request {
    private final String method;
    private final String path;
    private final Map<String, String> headers;
    private final String body;
    private static String GET = "GET";
    private static String POST = "POST";

    @Override
    public String toString() {
        return "Request{" +
                "method='" + method + '\'' +
                ", path='" + path + '\'' +
                ", headers=" + headers +
                ", in=" + body +
                '}';
    }

    private Request(String method, String path, Map<String, String> headers, String body) {
        this.method = method;
        this.path = path;
        this.headers = headers;
        this.body = body;

    }

    public String getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public String getBody() {
        return body;
    }

    public static Request fromInputStream(InputStream inn) throws IOException {
        var in = new BufferedInputStream(inn);
        var allowedMethods = List.of(GET, POST);
        // лимит на request line + заголовки
        final var limit = 4096;

        in.mark(limit);
        final var buffer = new byte[limit];
        final var read = in.read(buffer);

        // ищем request line
        final var requestLineDelimiter = new byte[]{'\r', '\n'};
        final var requestLineEnd = indexOf(buffer, requestLineDelimiter, 0, read);
        if (requestLineEnd == -1) {
            throw new IOException("Invalid request");
//            badRequest(out);
//            continue;

        }

        // читаем request line
        final var requestLine = new String(Arrays.copyOf(buffer, requestLineEnd)).split(" ");
        if (requestLine.length != 3) {
//            badRequest(out);
//            continue;
            throw new IOException("Invalid request");
        }

        final var method = requestLine[0];
        if (!allowedMethods.contains(method)) {
//            badRequest(out);
//            continue;
            throw new IOException("Invalid request");
        }
        System.out.println(method);

        final var path = requestLine[1];
        if (!path.startsWith("/")) {
//            badRequest(out);
//            continue;
            throw new IOException("Invalid request");
        }
        System.out.println(path);

        // ищем заголовки
        final var headersDelimiter = new byte[]{'\r', '\n', '\r', '\n'};
        final var headersStart = requestLineEnd + requestLineDelimiter.length;
        final var headersEnd = indexOf(buffer, headersDelimiter, headersStart, read);
        if (headersEnd == -1) {
//            badRequest(out);
//            continue;
            throw new IOException("Invalid request");
        }

        // отматываем на начало буфера
        in.reset();
        // пропускаем requestLine
        in.skip(headersStart);
        final var headerMap = new HashMap<String, String>();
        final var headersBytes = in.readNBytes(headersEnd - headersStart);
        final var headers = Arrays.asList(new String(headersBytes).split("\r\n"));
        for (String header : headers) {
            var i = header.indexOf(":");
            String name = header.substring(0, i);
            String value = header.substring(i + 2);
            headerMap.put(name, value);
        }
        System.out.println(headers);

        // для GET тела нет
        String body = null;
        if (!method.equals(GET)) {
            in.skip(headersDelimiter.length);
            // вычитываем Content-Length, чтобы прочитать body
            final var contentLength = extractHeader(headers, "Content-Length");
            if (contentLength.isPresent()) {
                final var length = Integer.parseInt(contentLength.get());
                final var bodyBytes = in.readNBytes(length);

                body = new String(bodyBytes);
                System.out.println(body);
            }
        }
        return new Request(method, path, headerMap, body);
    }

    private static Optional<String> extractHeader(List<String> headers, String header) {
        return headers.stream()
                .filter(o -> o.startsWith(header))
                .map(o -> o.substring(o.indexOf(" ")))
                .map(String::trim)
                .findFirst();
    }

//    private static void badRequest() throws IOException {
//        var out = new BufferedOutputStream(socket.getOutputStream);
//        out.write((
//                "HTTP/1.1 400 Bad Request\r\n" +
//                        "Content-Length: 0\r\n" +
//                        "Connection: close\r\n" +
//                        "\r\n"
//        ).getBytes());
//        out.flush();
//    }

    // from google guava with modifications
    private static int indexOf(byte[] array, byte[] target, int start, int max) {
        outer:
        for (int i = start; i < max - target.length + 1; i++) {
            for (int j = 0; j < target.length; j++) {
                if (array[i + j] != target[j]) {
                    continue outer;
                }
            }
            return i;
        }
        return -1;
    }
}
