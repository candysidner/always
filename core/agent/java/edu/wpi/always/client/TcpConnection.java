package edu.wpi.always.client;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import edu.wpi.always.Always;

public class TcpConnection implements RemoteConnection {

   private final String hostname;
   private final int port;
   private Socket skt;
   private PrintWriter out;
   private BufferedReader in;
   private final ExecutorService sendThreadService;
   private final ExecutorService recvThreadService;
   private final ArrayList<TcpConnectionObserver> observers = new ArrayList<TcpConnectionObserver>();

   public TcpConnection (String hostname, int port) {
      this.hostname = hostname;
      this.port = port;
      sendThreadService = Executors.newSingleThreadExecutor();
      recvThreadService = Executors.newSingleThreadExecutor();
   }

   @Override
   public void connect () {
      try {
         skt = new Socket(hostname, port);
         out = new PrintWriter(skt.getOutputStream(), true);
         in = new BufferedReader(new InputStreamReader(skt.getInputStream()));
         recvThreadService.execute(new Runnable() {

            @Override
            public void run () {
               char[] buf = new char[8192];
               int n;
               try {
                  while ((n = in.read(buf)) != -1) {
                     String s = new String(buf, 0, n);
                     // note can receive more than one message in buffer
                     if ( Always.TRACE ) System.out.println("Received: " + s);
                     fireMessageReceived(s);
                  }
               } catch (Exception e) { e.printStackTrace(); }
            }
         });
      } catch (ConnectException e) { 
         System.err.println(e+" to "+hostname+" "+port+" (retrying)"); 
         try { Thread.sleep(3000);  } catch (InterruptedException i) {}
         connect();
      }
        catch (Exception e) { throw new RuntimeException(e); }
   }

   protected void fireMessageReceived (String s) {
      for (TcpConnectionObserver o : observers) {
         o.notifyMessageReceive(this, s);
      }
   }

   @Override
   public boolean isConnected () {
      return skt != null && skt.isConnected();
   }

   @Override
   public void beginSend (String message) {
      enqueue(message);
   }

   private void enqueue (String message) {
      final String msg = message;
      sendThreadService.execute(new Runnable() {

         @Override
         public void run () {
            send(msg);
         }
      });
   }

   private String lastMessage = "";
   private int count;
   
   private void send (String message) {
      if ( !isConnected() )
         connect();
      if ( isConnected() ) {
         // optimization to help debugging
         if ( lastMessage.equals(message) ) count++;
         else {
            if ( count > 0 ) System.out.println("Sending... "+count+" more"); 
            System.out.println("Sending... " + message);
            count = 0;
         }
         out.print(message + "\n");
         out.flush();
         lastMessage = message;
      }
   }

   @Override
   public void addObserver (TcpConnectionObserver o) {
      observers.add(o);
   }

   @Override
   public void removeObserver (TcpConnectionObserver o) {
      observers.remove(o);
   }
}
