/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client;


/**
 *
 * @author Elie
 */
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class Client extends JFrame {
    
    private static int port=8000;
    //text field for receiving data
    private JTextField jtextfield = new JTextField();
    
    //text area to display contents
    private JTextArea jtextarea = new JTextArea();
    //input output variables
    private OutputStream output;
    private OutputStreamWriter outputwriter;
    private BufferedWriter bufwriter;
    private InputStream input;
    private InputStreamReader inputreader;
    private BufferedReader bufreader;
   
    public static void main(String[] zero) throws IOException {
         new Client(port);     
    }
     
    public Client(int port)  {
        
        //put text area on frame
        JPanel p = new JPanel();
        p.setLayout(new BorderLayout());
        p.add(new JLabel("Message"), BorderLayout.WEST);
        p.add(jtextfield, BorderLayout.CENTER);
        jtextfield.setHorizontalAlignment(JTextField.RIGHT);
        setLayout(new BorderLayout());
        add(p, BorderLayout.SOUTH);
        add( new JScrollPane(jtextarea), BorderLayout.CENTER);  
        jtextfield.addActionListener(new TextFieldListener()); 
        setTitle("Client");
        setSize(500,500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
        
        try {
            //create socket to connect to server
            Socket socket = new Socket(InetAddress.getLocalHost(), port);
            
            //input stream to get data from server
            input = socket.getInputStream();
            inputreader = new InputStreamReader(input);
            bufreader = new BufferedReader(inputreader);
            
            //create output stream to send data to server
            output = socket.getOutputStream();
            outputwriter = new OutputStreamWriter(output);
            bufwriter = new BufferedWriter(outputwriter);
            
            while(true){
                //get message from server
                String message = bufreader.readLine();
                jtextarea.append("\n" + message);
            }
            

        //socket.close();
        } catch (UnknownHostException exception){
            exception.printStackTrace();
        } catch (IOException exception){
            exception.printStackTrace();
        }

    }
    
    private class TextFieldListener implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent e){
            try{
                //get the radius from text field
                String text=jtextfield.getText().trim();       
                //send text to server
                bufwriter.write(text + "\n");
                bufwriter.flush();
                jtextfield.setText("");
            }
            catch(IOException exception){
                System.err.println(exception);
            }
        }
        
    }

}

