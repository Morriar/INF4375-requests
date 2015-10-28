/*
 * Copyright 2015 Alexandre Terrasa <alexandre@moz-code.org>.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package inf4375.exercices;

import inf4375.server.Request;
import inf4375.server.Response;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * An HTTP Client used to test the server with different HTTP requests and
 * methods.
 */
public class Client {

    public static void main(String[] args) throws IOException, Exception {
        String method = "GET";
        if (args.length == 1) {
            method = args[0];
        }

        System.out.println("Connect to server localhost:3000...");
        // Open a new socket connection to "localhost:3000"
        Socket socket = new Socket("localhost", 8080);
        // Open a reader used to read messages sent by the server
        BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        // Open a print writer on the output stream to send message to the server
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

        Request request;
        switch (method) {
            case "POST":
                request = makePOST(out);
                break;
            case "PUT":
                request = makePUT(out);
                break;
            case "DELETE":
                request = makeDELETE(out);
                break;
            default:
                request = makeGET(out);
        }
        request.send(out);

        Response response = new Response(input);
        System.out.println(response.toString());

        socket.close();
    }

    // Make a GET request
    public static Request makeGET(PrintWriter out) {
        String method = "GET";
        String uri = "/albums/1";
        String version = "HTTP/1.1";
        String body = null;
        return new Request(method, uri, version, body);
    }

    // Make a POST request
    public static Request makePOST(PrintWriter out) {
        String method = "POST";
        String uri = "/albums/10";
        String version = "HTTP/1.1";
        StringBuilder body = new StringBuilder();
        body.append("\r\n");
        body.append("{"
                + "\"id\": \"10\","
                + "\"title\": \"My New Album\","
                + "\"artist\": \"By this amazing artist, me!\","
                + "\"instock\": true,"
                + "\"price\": 0.1,"
                + "\"year\": 2015"
                + "}");
        Request request = new Request(method, uri, version, body.toString());
        request.headers.put("Content-Type", "text/json");
        return request;
    }

    // Make a PUT request
    public static Request makePUT(PrintWriter out) {
        String method = "PUT";
        String uri = "/albums/1";
        String version = "HTTP/1.1";
        StringBuilder body = new StringBuilder();
        body.append("\r\n");
        body.append("{"
                + "\"instock\": true"
                + "}");
        Request request = new Request(method, uri, version, body.toString());
        request.headers.put("Content-Type", "text/json");
        return request;
    }

    // Make a DELETE request
    public static Request makeDELETE(PrintWriter out) {
        String method = "DELETE";
        String uri = "/albums/1";
        String version = "HTTP/1.1";
        Request request = new Request(method, uri, version, null);
        request.headers.put("Content-Type", "text/json");
        return request;
    }
}
