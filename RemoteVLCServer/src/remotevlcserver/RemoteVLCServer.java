/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package remotevlcserver;

import java.awt.AWTException;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Robot;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author vagisha
 */
public class RemoteVLCServer {
    
    private static ServerSocket server;
    private static Socket client;
    private static BufferedReader in = null;
    private static final int SERVER_PORT = 8999;
    private static boolean isConnected;
    private static String line;
    private static Robot robot;
    

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {
        try {
            // TODO code application logic here
            server = new ServerSocket(SERVER_PORT);
            client = server.accept();
            isConnected = true;
            
            robot = new Robot();
            
            in = new BufferedReader(new InputStreamReader(client.getInputStream()));
            
            
            
        } catch (IOException ex) {
            Logger.getLogger(RemoteVLCServer.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("Isconnected is false now");
            isConnected = false;
        } catch (AWTException ex) {
            Logger.getLogger(RemoteVLCServer.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("Robot creation error");
        }
        
        while(isConnected){
            
            System.out.println("Is Connected");
            line = in.readLine();
            System.out.println(line);
           
            if(line.equalsIgnoreCase("next")){
               robot.keyPress(KeyEvent.VK_N);
               robot.keyRelease(KeyEvent.VK_N);
            }
            
            else if(line.equalsIgnoreCase("play")){
            
                robot.keyPress(KeyEvent.VK_SPACE);
		robot.keyRelease(KeyEvent.VK_SPACE);
            }
            else if(line.equalsIgnoreCase("previous")){
              robot.keyPress(KeyEvent.VK_P);
	      robot.keyRelease(KeyEvent.VK_P);		
            
            }
            else if(line.contains(",")){
                float distx = Float.parseFloat(line.split(",")[0]);
                float disty = Float.parseFloat(line.split(",")[1]);
                Point point = MouseInfo.getPointerInfo().getLocation();
                float curx = point.x;
                float cury = point.y;
                
                robot.mouseMove((int)(curx+distx), (int)(cury+disty));
            }
            
            else if(line.contains("left_click")){
                 robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
		robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
            }
            else if(line.equalsIgnoreCase("exit")){
            
                             isConnected=false;
				
				server.close();
				client.close();
            }
               
            
         
        }
        
    }
    
}
