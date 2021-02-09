/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chatserver;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ChatServerThread extends Thread {

    private Socket servidor;
    private int id;
    private DataInputStream flujoE;
    private DataOutputStream flujoS;
    private boolean alive = true;
    private ChatServer server;
    private String nombre = "";

    public ChatServerThread(Socket servidor, ChatServer server) {
        this.servidor = servidor;
        this.server = server;

        if (server.getServerThreads().size() == 0) {
            server.getUsuariosChat().setText("No hay nadie conectado");
        } else {
            server.getUsuariosChat().setText(server.getServerThreads().toString());
        }

        try {
            flujoE = new DataInputStream(servidor.getInputStream());
            flujoS = new DataOutputStream(servidor.getOutputStream());

            flujoS.writeUTF("Esta es la lista de usuarios conectados" + server.getServerThreads().toString());

        } catch (IOException ex) {
            System.out.println("Fallo catch ChatServerThread Constructor : " + ex.getLocalizedMessage());
            alive = false;
            server.getUsuariosChat().setText("");
            server.getUsuariosChat().setText(server.getServerThreads().toString());
        }

    }

    public void send(String text) {
        try {

            flujoS.writeUTF(text);
            flujoS.flush();

        } catch (IOException ex) {
            System.out.println("Fallo catch ChatServerThread SEND : " + ex.getLocalizedMessage());
            alive = false;
            server.getUsuariosChat().setText("");
            server.getUsuariosChat().setText(server.getServerThreads().toString());
        }
    }

    public void sendTo(String text, String name) {
        try {

            flujoS.writeUTF(name + " > " + text + " (Mensaje Privado) ");
            flujoS.flush();

        } catch (IOException ex) {
            System.out.println("Fallo catch ChatServerThread SEND : " + ex.getLocalizedMessage());
            alive = false;
            server.getUsuariosChat().setText("");
            server.getUsuariosChat().setText(server.getServerThreads().toString());
        }
    }

    @Override
    public void run() {
        String text;
        while (alive) {
            try {

                text = flujoE.readUTF();
                if (text.length() != 0) {

                    if (this.nombre.length() == 0) {

                        this.setNombre(text);
                        server.getUsuariosChat().setText("");
                        server.getUsuariosChat().setText(server.getServerThreads().toString());
                        flujoS.writeUTF("/clear");
                        flujoS.writeUTF("Bienvenido/a al chat " + this.nombre);

                    } else {
                        server.getUsuariosChat().setText("");
                        server.getUsuariosChat().setText(server.getServerThreads().toString());

                        if (text.charAt(0) == '-' && text.charAt(1) == '>') {
                            String destinatario = "";
                            for (int i = 2; i < text.length(); i++) {
                                if (text.charAt(i) != ' ') {
                                    destinatario += text.charAt(i);
                                } else {
                                    break;
                                }
                            }
                            String textFiltrado = "";
                            for (int i = destinatario.trim().length() + 2; i < text.length(); i++) {
                                textFiltrado += text.charAt(i);
                            }

                            server.broadcastTo(destinatario.trim(), textFiltrado.trim(), this.nombre);
                        } else {
                            server.broadcast(this.nombre + " > " + text);
                        }

                    }

                }

            } catch (IOException ex) {
                System.out.println("Fallo catch ChatServerThread Run : " + ex.getLocalizedMessage());
                alive = false;
                server.getUsuariosChat().setText("");
                server.getUsuariosChat().setText(server.getServerThreads().toString());
            }

        }

    }

    public boolean getAlive() {
        return alive;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    @Override
    public String toString() {
        return nombre;
    }

}
