/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import java.io.PrintWriter;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.awt.*;
import javax.swing.*;

/**
 *
 * @author Elie
 */
public class Server extends JFrame{
    private JTextArea jta = new JTextArea();
    private static int port=8000;
    private int i = 1; //numero attibué aux clients
    private HashSet<String> names = new HashSet<String>();
    private HashSet<PrintWriter> writers = new HashSet<PrintWriter>();
    
public static void main(String[] zero) throws IOException {
         new Server(port);
    }

    public Server(int port) throws IOException {
        
        //put text area on frame
        setLayout(new BorderLayout());
        add(new JScrollPane(jta),BorderLayout.CENTER);
        setTitle("Server");
        setSize(500,500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
        try{
            //create server socket
            ServerSocket server = new ServerSocket(port);
            jta.append("Sever started at: "+ new Date()+'\n');
            
            while(true){
                    //listen for connection request
                    Socket socket = server.accept();
                    //client host name and ip 
                    InetAddress inetAddress = socket.getInetAddress();
                    jta.append("Client"+i+" with ip " + inetAddress.getHostAddress()+" and with name "+inetAddress.getHostName()+ " is connected.\n");
                    //create a new thread for the connection
                    HandleClient task = new HandleClient(socket,i);
                    //start new thread
                    new Thread(task).start();
                    i++;
            }
        }
        catch(IOException ex){
            System.out.println(ex);
        }

      
    }

    class HandleClient implements Runnable{
        private Socket socket; 
        private int numclient;
        private Boolean pseudo=false;
        private PrintWriter writer;
        private String name;
        
        //construct a thread
        public HandleClient(Socket socket, int i){
            this.socket = socket;
            numclient = i;   
        }
        
        @Override
        public void run(){
            try{
                //create data input and output streams
                //reading in/out stream from client
                InputStream is = socket.getInputStream();
                InputStreamReader isr = new InputStreamReader(is);
                BufferedReader br = new BufferedReader(isr);

                //Sending the response back to the client.
                OutputStream os = socket.getOutputStream();
                OutputStreamWriter osw = new OutputStreamWriter(os);
                BufferedWriter bw = new BufferedWriter(osw);
                bw.write("Welcome in the chat!\n");
                bw.write("You can choose a nickname by writting : '/nick nickname' \n\n");
                bw.flush();
                writer = new PrintWriter(socket.getOutputStream(), true);
                writers.add(writer);

                while (true) {
                                        
                    //receive
                    String text = br.readLine();
                    
                    //if the client wants to change his nickname
                    if(text.startsWith("/nick ")){
                        String arrayString[] = text.split("\\s+");
                        name=arrayString[1];
                        names.add(name);
                        pseudo = true;
                        for (PrintWriter writer : writers) {
                            writer.println("Client"+numclient+ " becomes " + name);
                        }
                        jta.append("Client"+numclient+ " becomes " + name + "\n");
                    }
                    //if the nickname is not set
                    else if(pseudo==false){
                        jta.append("Client"+numclient+ " : " + text + "\n");
                        //broadcast the text to all writers
                        for (PrintWriter writer : writers) {
                            writer.println("Client"+numclient+ " : " + text);
                        }
                    //if the nickname is set
                    }else{
                        jta.append(name + " : " + text + "\n");
                        //broadcast the text to all writers
                        for (PrintWriter writer : writers) {
                            writer.println(name + " : " + text);
                        }
                    }
                }
            }
            catch(IOException ex){
                System.err.println(ex);
            }finally {
                //When the client quite the chat
                //Remove the name of the client
                if (name != null) {
                    names.remove(name);
                }
                //Remove the writer
                if (writer != null) {
                    jta.append("Client"+numclient+ " has gone");
                    writers.remove(writer);
                }
                //Close the socket
                try {
                    socket.close();
                } catch (IOException e) {
                }
            }
        }
       
    }

}


