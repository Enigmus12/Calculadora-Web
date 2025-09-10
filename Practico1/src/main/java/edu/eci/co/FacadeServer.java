package edu.eci.co;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.*;

public class FacadeServer {

    private static final String BACKEND_HOST = "localhost";
    private static final int BACKEND_PORT = 9001;

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(9000);
        System.out.println("Fachada lista en puerto 9000");

        while (true) {
            Socket clientSocket = serverSocket.accept();
            new Thread(() -> handleClient(clientSocket)).start();
        }
    }

    private static void handleClient(Socket clientSocket) {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)) {

            String inputLine = in.readLine();
            if (inputLine == null) return;

            System.out.println("Fachada recibió: " + inputLine);

            String[] tokens = inputLine.split(" ");
            if (tokens.length < 2) return;
            String path = tokens[1];

            String outputLine;

            if (path.equals("/cliente")) {
                outputLine = getClientHtml();
            } else {
                outputLine = forwardToBackend(path);
            }

            out.println("HTTP/1.1 200 OK");
            out.println("Content-Type: application/json");
            out.println();
            out.println(outputLine);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try { clientSocket.close(); } catch (IOException e) {}
        }
    }

    private static String forwardToBackend(String path) {
        try {
            Socket backendSocket = new Socket(BACKEND_HOST, BACKEND_PORT);
            PrintWriter out = new PrintWriter(backendSocket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(backendSocket.getInputStream()));

            out.println("GET " + path + " HTTP/1.1");
            out.println("Host: " + BACKEND_HOST);
            out.println();

            String line;
            boolean jsonStarted = false;
            StringBuilder sb = new StringBuilder();
            while ((line = in.readLine()) != null) {
                if (line.startsWith("{")) jsonStarted = true;
                if (jsonStarted) sb.append(line);
            }

            backendSocket.close();
            return sb.toString();

        } catch (Exception e) {
            return "{\"status\":\"ERR\",\"error\":\"backend_unreachable\"}";
        }
    }

    private static String getClientHtml() {
        // Retorna un JSON con HTML dentro
        String html = "<!DOCTYPE html><html><head><meta charset='UTF-8'><title>Calculadora</title></head><body>" +
                "<h1>Calculadora Web</h1>" +
                "<form onsubmit='addNumber(); return false;'><input type='number' id='numInput' step='any' required><input type='submit' value='Agregar'></form>" +
                "<form onsubmit='listNumbers(); return false;'><input type='submit' value='Listar'></form>" +
                "<form onsubmit='clearNumbers(); return false;'><input type='submit' value='Borrar'></form>" +
                "<form onsubmit='getStats(); return false;'><input type='submit' value='Estadísticas'></form>" +
                "<pre id='output'></pre>" +
                "<script>const output=document.getElementById('output');function sendGet(path,callback){const xhttp=new XMLHttpRequest();xhttp.onload=function(){callback(this.responseText);};xhttp.open('GET',path);xhttp.send();}function addNumber(){const num=document.getElementById('numInput').value;sendGet('/add?x='+num,res=>output.textContent=res);}function listNumbers(){sendGet('/list',res=>output.textContent=res);}function clearNumbers(){sendGet('/clear',res=>output.textContent=res);}function getStats(){sendGet('/stats',res=>output.textContent=res);}</script>" +
                "</body></html>";
        return "{\"status\":\"OK\",\"html\":\"" + html.replace("\"", "\\\"") + "\"}";
    }

    
}
