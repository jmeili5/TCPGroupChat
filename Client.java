package Chatagainagain;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.Socket;
import java.util.Scanner;

import javax.swing.*;

public class Client {

   public Socket socket;
   static BufferedReader bufferedReader;
   static BufferedWriter bufferedWriter;
   static String username;

   public Client(String host, int port, String username) {
      try {
         this.socket = new Socket(host, port);
         this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
         this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
         this.username = username;
      } catch (IOException e) {
         closeEverything(socket, bufferedReader, bufferedWriter);
      }
   }

   public void sendMessage() {
      try {
         bufferedWriter.write(username);
         bufferedWriter.newLine();
         bufferedWriter.flush();
      } catch (IOException e) {
         closeEverything(socket, bufferedReader, bufferedWriter);
      }
   }

   public void listenForMessage() {
      new Thread(
                new Runnable() {
                   @Override
                    public void run() {
                      String msgFromGroupChat;
                   
                      while (socket.isConnected()) {
                         try {
                            msgFromGroupChat = bufferedReader.readLine();
                            if (msgFromGroupChat != null) {
                               chatTextArea.append(msgFromGroupChat + "\n");
                            }
                         } catch (IOException e) {
                            closeEverything(socket, bufferedReader, bufferedWriter);
                         }
                      }
                   }
                }
         ).start();
   }

   public void closeEverything(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter) {
      try {
         if (bufferedReader != null) {
            bufferedReader.close();
         }
         if (bufferedWriter != null) {
            bufferedWriter.close();
         }
         if (socket != null) {
            socket.close();
         }
      } catch (IOException e) {
         e.printStackTrace();
      }
   }

   static JTextArea chatTextArea;
   static JTextField messageField;
    
   public static void main(String[] args) throws IOException {
      JFrame frame = new JFrame("Group Chat Client");

      frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
   
      String host = JOptionPane.showInputDialog(frame, "Enter the hostname of the chat server:");
      int port = Integer.parseInt(JOptionPane.showInputDialog(frame, "Enter the port number of the chat server:"));
      String username = JOptionPane.showInputDialog(frame, "Enter your username for the group chat:");
   
      Client client = new Client(host, port, username);
   
      JPanel mainPanel = new JPanel(new BorderLayout());
   
      JPanel messagePanel = new JPanel(new BorderLayout());
      JPanel messageSubPanel = new JPanel(new BorderLayout());
      messageField = new JTextField();
      messageField.addActionListener(
         new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
               String message = username + ": " + messageField.getText() + "\n";
               try {
                  bufferedWriter.write(message);
                  bufferedWriter.flush();
               } catch (IOException ex) {
                  ex.printStackTrace();
               }
               messageField.setText("");
            }
         });
      messageSubPanel.add(messageField, BorderLayout.CENTER);
      JButton sendButton = new JButton("Send");
      sendButton.addActionListener(
         new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
               String message = username + ": " + messageField.getText() + "\n";
               try {
                  bufferedWriter.write(message);
                  bufferedWriter.flush();
               } catch (IOException ex) {
                  ex.printStackTrace();
               }
               messageField.setText("");
            }
         });
      messageSubPanel.add(sendButton, BorderLayout.EAST);
      messagePanel.add(messageSubPanel, BorderLayout.SOUTH);
   
      chatTextArea = new JTextArea();

      chatTextArea.setEditable(false);
      messagePanel.add(new JScrollPane(chatTextArea), BorderLayout.CENTER);
   
      mainPanel.add(messagePanel, BorderLayout.CENTER);
   
      frame.getContentPane().add(mainPanel);
      frame.pack();

      frame.setVisible(true);
   
      client.sendMessage();
      client.listenForMessage();
      frame.setSize(500, 300);
   }
}