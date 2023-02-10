package Chatagainagain;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.InetAddress;
import java.io.*;
import java.net.*;

public class Server{
   
   private ServerSocket serverSocket;

   public Server(ServerSocket serverSocket) {
   
      this.serverSocket = serverSocket;
   
   }
   public void startServer() {
   
      try{
         while(!serverSocket.isClosed()) {
         
            Socket socket = serverSocket.accept();
            System.out.println("A new client has connected!");
            ClientHandler clientHandler = new ClientHandler(socket);
         
            Thread thread = new Thread(clientHandler);
            thread.start();
         }
      }catch (IOException e){
      
      
      }
   
   
   }
   public void closeServerSocket(){
   
      try{
         if (serverSocket != null){
         
            serverSocket.close();
         
         }
      }catch (IOException e){
      
         e.printStackTrace();
      
      }
   
   
   }
   public static void main(String[] args) throws IOException {
     //InetAddress ip = InetAddress.getLocalHost();
   //System.out.println(ip.getHostAddress());
         ServerSocket serverSocket = new ServerSocket(6667);
      Server server = new Server(serverSocket);
      server.startServer();
   }

}