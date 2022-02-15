package com.Hzz.Misc;

import java.net.*;
import java.io.*;

public class LaboratoriumUjiCoba {
    private ServerSocket serverSocket;
    
    public static void main(String[] args) {
        LaboratoriumUjiCoba server = new LaboratoriumUjiCoba();
        server.start(12312);
        System.out.println("Done");
    }
    
    public void start(int port) {
        try {
            System.out.println("Starting server on port " + port);
            serverSocket = new ServerSocket(port);
            while (true) {
                new EchoClientHandler(serverSocket.accept()).start();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
    public void stop() {
        try {
            if (serverSocket != null) {
                serverSocket.close();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
    private static class EchoClientHandler extends Thread {
        
        private Socket clientSocket;
        private PrintWriter out;
        private BufferedReader in;
        
        public EchoClientHandler(Socket socket) {
            this.clientSocket = socket;
            System.out.println("Request from " + socket.getInetAddress().getHostAddress());
        }
        
        public void run() {
            try {
                out = new PrintWriter(clientSocket.getOutputStream(), true);
                in = new BufferedReader(
                        new InputStreamReader(clientSocket.getInputStream()));
                
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    if (".".equals(inputLine)) {
                        out.println("shh ^_^");
                        break;
                    }
                    out.println(inputLine);
                }
                
                in.close();
                out.close();
                clientSocket.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
}
