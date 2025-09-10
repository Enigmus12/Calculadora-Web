package edu.eci.co;

import java.io.*;
import java.net.*;
import java.nio.*;
import java.text.DecimalFormat;
import java.util.*;

public class BackendServer {

    private static LinkedList<Double> numbers = new LinkedList<>();

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(9001);
        System.out.println("Backend listo en puerto 9001");

        while (true) {
            Socket clientSocket = serverSocket.accept();
            handleClient(clientSocket);
        }
    }

    private static void handleClient(Socket clientSocket) {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)) {

            String inputLine = in.readLine();
            if (inputLine == null) return;

            System.out.println("Recibí: " + inputLine);

            String[] tokens = inputLine.split(" ");
            if (tokens.length < 2) return;
            String path = tokens[1];

            String outputLine;

            if (path.startsWith("/add")) {
                outputLine = handleAdd(path);
            } else if (path.equals("/list")) {
                outputLine = handleList();
            } else if (path.equals("/clear")) {
                outputLine = handleClear();
            } else if (path.equals("/stats")) {
                outputLine = handleStats();
            } else {
                outputLine = "{\"status\":\"ERR\",\"error\":\"unknown_endpoint\"}";
            }

            // Respuesta HTTP
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

    private static String handleAdd(String path) {
        try {
            String query = path.split("\\?")[1]; 
            double x = Double.parseDouble(query.split("=")[1]);
            numbers.add(x);
            return "{\"status\":\"OK\",\"added\":" + x + ",\"count\":" + numbers.size() + "}";
        } catch (Exception e) {
            return "{\"status\":\"ERR\",\"error\":\"invalid_number\"}";
        }
    }

    private static String handleList() {
        return "{\"status\":\"OK\",\"values\":" + numbers + "}";
    }

    private static String handleClear() {
        numbers.clear();
        return "{\"status\":\"OK\",\"message\":\"list_cleared\"}";
    }

    private static String handleStats() {
        if (numbers.isEmpty())
            return "{\"status\":\"ERR\",\"error\":\"empty_list\"}";

        double sum = 0;
        for (double n : numbers) sum += n;
        double mean = sum / numbers.size();

        double sqSum = 0;
        for (double n : numbers) sqSum += Math.pow(n - mean, 2);
        double stddev = Math.sqrt(sqSum / numbers.size()); 

        DecimalFormat df = new DecimalFormat("#.##########");
        return "{\"status\":\"OK\",\"mean\":" + df.format(mean) +
            ",\"stddev\":" + df.format(stddev) +
            ",\"count\":" + numbers.size() + "}";
    }

}

