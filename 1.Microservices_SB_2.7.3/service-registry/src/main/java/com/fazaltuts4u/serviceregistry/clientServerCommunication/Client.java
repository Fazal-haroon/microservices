package com.fazaltuts4u.serviceregistry.clientServerCommunication;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class Client {

    public static void main(String[] args) {
        try {

//create Instance of socket and pass and pass url of the server in IO.server method argument
            Socket socket = IO.socket("http://localhost:3000");

            /*n event handler is added to handle the Socket.EVENT_CONNECT event,
             * which is triggered when the socket successfully connects to the server.
             * Inside the event handler, a message is printed to the console to indicate that the client has connected to the server.
             */
            socket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    System.out.println("Connected to the server");
                }
            });


            socket.on(Socket.EVENT_MESSAGE, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    String data = (String) args[0];
                    System.out.println("Received message: " + data);
                }
            });

            socket.connect();

// Sending a message to the server
            socket.emit("message", "Hello, Server!");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}