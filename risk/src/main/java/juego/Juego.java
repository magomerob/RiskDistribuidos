package juego;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.print.PrintColor;
import protocolo.Mensaje;
import protocolo.Protocolo;

public class Juego {
    private boolean acabado;
    private int njugadores;
    private String[] ips;
    private ServerSocket ss;
    private InterfazJuego interfaz;
    private int nturno;
    private int miturno;
    private List<ServidorJugador> jugadores;
    private Socket s;
    private HashMap<String,Pais> tablero;

    public Juego(String[] ips, int miturno, InterfazJuego interfaz){
        this.acabado = false;
        this.njugadores = ips.length;
        this.ips = ips;
        this.interfaz= interfaz;
        this.tablero = interfaz.getMap().getPaises();
        this.miturno = miturno;
        this.interfaz.setJugador(this.miturno);
    }

    public void iniciarPartida(){
        turno();
    }

    private void turno(){
        System.out.println("Turno: "+nturno);
        if((nturno%njugadores) == miturno){
            Platform.runLater(() -> interfaz.getButton().setText("Acabar turno"));
            jugadores = unirJugadores();
            if(nturno == 0){
                crearymandartablero();
            }
        }else{
            Platform.runLater(() -> interfaz.getButton().setText("Turno de jugador: "+ (nturno%njugadores)));
            escucharTurno(ips[nturno%njugadores]);
        }
    }

    private List<ServidorJugador> unirJugadores(){
        this.ss = null;
        List<ServidorJugador> jugadores = new ArrayList<>();
            try{
                this.ss = new ServerSocket(55555);
                ExecutorService pool = Executors.newCachedThreadPool();
                while(jugadores.size() < this.njugadores-1) {
                    Socket s = ss.accept();
                    System.out.println("aceptado");
                    ServidorJugador sj = new ServidorJugador(s);
                    jugadores.add(sj);
                    pool.submit(sj);
                }
                System.out.println("aceptados");
            } catch (IOException e) {
                e.printStackTrace();
            }
            return jugadores;
    }

    private void escucharTurno(String ip){
        s = null;
        if(ip.equals("127.0.0.1")){
            ip = "localhost";
        }
        
        while (s == null) {
            //cochinada para que se conecte
            try {
                s = new Socket("localhost", 55555);
            } catch (IOException e) {
                try {
                    Thread.sleep(10);
                } catch (InterruptedException ie) {
                    ie.printStackTrace();
                    Thread.currentThread().interrupt();
                }
            }
        }
        Task<Void> task = new Task<>() {
            protected Void call() throws Exception {
                escucha();
                return null;
            }};
        new Thread(task).start();

    }

    private void broadcast(String msg){
        for (ServidorJugador jugador : jugadores) {
            jugador.broadcast(msg);
        }
    }

    private void cerrarServerSocket(){
        if(this.ss != null){
            try{
                ss.close();
            }catch(IOException e){
                e.printStackTrace();
            }
        }
    }

    private void cerrarSocket(){
        if(this.s != null){
            try{
                s.close();
            }catch(IOException e){
                e.printStackTrace();
            }
        }
    }

    private void crearymandartablero(){
        Random r = new Random();
        for (String npais :tablero.keySet()) {
            int jugador = r.nextInt(njugadores);
            int tropas = r.nextInt(5)+1;
            
            interfaz.actualizarPais(npais, tropas, jugador);
            broadcast(Mensaje.actualizarPais(npais, tropas, jugador));
        }
    }

    public void atacar(String np1, String np2){
        Pais p1 = this.tablero.get(np1);
        Pais p2 = this.tablero.get(np2);
        if(p1.getJugadorControl() != p2.getJugadorControl()){
            if(p1.getNumtropas() > p2.getNumtropas()){
                p2.setNumtropas(p1.getNumtropas()-1-p2.getNumtropas());
                p2.setJugadorControl(p1.getJugadorControl());
                p1.setNumtropas(1);
            }else{
                p2.setNumtropas(p2.getNumtropas()-p1.getNumtropas()+1);
                p1.setNumtropas(1);
            }
        }else{
            p2.setNumtropas(p2.getNumtropas()+p1.getNumtropas()-1);
            p1.setNumtropas(1);
        }
        Platform.runLater(() -> interfaz.actualizarPais(np1, p1.getNumtropas(), p1.getJugadorControl()));
        Platform.runLater(() -> interfaz.actualizarPais(np2, p2.getNumtropas(), p2.getJugadorControl()));
        broadcast(Mensaje.actualizarPais(np1, p1.getNumtropas(), p1.getJugadorControl()));
        broadcast(Mensaje.actualizarPais(np2, p2.getNumtropas(), p2.getJugadorControl()));
        cerrarServerSocket();

        //this.nturno++;
        //Thread(() -> turno(nturno)).run();
    }

    public boolean isTurno(){
        return ((nturno%njugadores) == miturno);
    }

    private void escucha(){
        try{
            BufferedReader inp = new BufferedReader(new InputStreamReader(s.getInputStream(), StandardCharsets.UTF_8));
            System.out.println("leyendo2");
            String line = inp.readLine();
            while(line != null && !line.equals(Protocolo.ACABAR_TURNO)){
                System.out.println(line);
                String[] partido = line.split(Protocolo.DEL);
                if(partido[0].equals(Protocolo.ACTUALIZAR_PAIS)){
                    String pais = partido[1];
                    int tropas = Integer.parseInt(partido[2]);
                    int jugador = Integer.parseInt(partido[3]);
                    //No se actualiza en tiempo real?
                    //Tiene que esperar a que 
                    Platform.runLater(() -> interfaz.actualizarPais(pais, tropas, jugador));
                }
                line = inp.readLine();
                
            }
            this.nturno++;

            Task<Void> task = new Task<>() {
                protected Void call() throws Exception {
                    turno();
                    return null;
                }};
            new Thread(task).start();
            
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    public void acabarTurno(){
        broadcast(Protocolo.ACABAR_TURNO);
        this.nturno++;
        Task<Void> task = new Task<>() {
            protected Void call() throws Exception {
                turno();
                return null;
            }};
        new Thread(task).start();
    }

}
