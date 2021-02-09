
package chatserver;

import java.awt.List;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.logging.Level;
import java.util.logging.Logger;


public class ChatServer {

    private ServerSocket servicio;
    private ArrayList<ChatServerThread> serverThreads = new ArrayList<>();
    private boolean run = true;
    private UsuariosConectadosChat usuariosChat;
    private ArrayList<String>strings=new ArrayList<>();
    
    public ChatServer(int port) {
        try {
            servicio = new ServerSocket(port);
            strings.add("hola");
            strings.add("adios");
            usuariosChat = new UsuariosConectadosChat();
            usuariosChat.setVisible(true);
            usuariosChat.setText("No hay nadie conectado");
            
            
            
        } catch (IOException ex) {
            System.out.println("Fallo catch ChatServer Constructor : "+ex.getLocalizedMessage());
            usuariosChat.setText("");
            usuariosChat.setText(serverThreads.toString());
        }
        
    }

public void startService(){
    
    Thread hebraPrincipal = new Thread(){
        public void run(){
            Socket servidor;
            ChatServerThread serverThread;
         
            while(run){
                compruebaVidaCliente();
                try {
                    
                    servidor = servicio.accept();
                    serverThread = new ChatServerThread(servidor,ChatServer.this);
                    serverThreads.add(serverThread);
                    serverThread.setId(serverThreads.indexOf(serverThread));
                    
                    serverThread.start();
                    
                    
                } catch (IOException ex) {
                    System.out.println("Fallo catch ChatServer StartService : "+ex.getLocalizedMessage());
                    usuariosChat.setText("");
                    usuariosChat.setText(serverThreads.toString());
                }
            }
        }
    };
    hebraPrincipal.start();
    
}

public void compruebaVidaCliente(){
    
    try{
        
        for(ChatServerThread cliente: serverThreads){
            if(!cliente.getAlive()){
                System.out.println(cliente.getAlive()+"boll");
                serverThreads.remove(cliente);
            }
        }
        
    }catch(ConcurrentModificationException ex){
        usuariosChat.setText("");
                usuariosChat.setText(serverThreads.toString());
    }
    
}
public void broadcast(String text){
    try{
        
        for(ChatServerThread cliente: serverThreads){
            if(cliente.getAlive()){
               cliente.send(text); 
            }else{
                serverThreads.remove(cliente);
                usuariosChat.setText("");
                usuariosChat.setText(serverThreads.toString());

            }
        }
        
    }catch(ConcurrentModificationException ex){
        usuariosChat.setText("");
                usuariosChat.setText(serverThreads.toString());
    }

}
public void broadcastTo(String to,String text,String from){
    for(ChatServerThread cliente: serverThreads){
        if(cliente.getNombre().compareTo(to)== 0){
            if(cliente.getAlive()){
               cliente.sendTo(text, from);
            }else{
                serverThreads.remove(cliente);

            }
            
        }
    }
}
    
    public static void main(String[] args) {
        ChatServer chatServer = new ChatServer(5000);
        chatServer.startService();
    }

    public ArrayList<ChatServerThread> getServerThreads() {
        return serverThreads;
    }

    public void setServerThreads(ArrayList<ChatServerThread> serverThreads) {
        this.serverThreads = serverThreads;
    }

    public UsuariosConectadosChat getUsuariosChat() {
        return usuariosChat;
    }

    public void setUsuariosChat(UsuariosConectadosChat usuariosChat) {
        this.usuariosChat = usuariosChat;
    }
    
    
    
    
}
