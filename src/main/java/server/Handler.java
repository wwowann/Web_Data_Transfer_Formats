package server;

import request.Request;

import java.io.BufferedOutputStream;

public interface Handler {
    void handle(Request request, BufferedOutputStream out);
}
