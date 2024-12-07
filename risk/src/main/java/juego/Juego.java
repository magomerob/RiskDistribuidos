package juego;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Juego {
    private boolean acabado;
    private int njugadores;
    private String[] ips;
    private ServerSocket ss;
    private InterfazJuego interfaz;
    private int nturno;
    private boolean metoca;
    final private CountDownLatch sincro;

    public Juego(String[] ips, boolean empieza, InterfazJuego interfaz, CountDownLatch sincrop){
        this.acabado = false;
        this.njugadores = ips.length;
        this.ips = ips;
        this.interfaz= interfaz;
        this.sincro = sincrop;
        this.metoca = empieza;

        turno(metoca);

        while(!acabado){
            
        }
    }

    private void turno(boolean metoca){
        if(metoca){
            List<ServidorJugador> jugadores = unirJugadores();
            for (ServidorJugador jugador : jugadores) {
                jugador.broadcast("yow!");
            }
            cerrarServerSocket();
        }if(!metoca){
            escucharTurno(ips[nturno%njugadores]);
        }
    }

    private List<ServidorJugador> unirJugadores(){
        this.ss = null;
        List<ServidorJugador> jugadores = new ArrayList<>();
            try{
                this.ss = new ServerSocket(55555);
                ExecutorService pool = Executors.newCachedThreadPool();
                sincro.countDown();
                System.out.println("sincro: "+sincro.getCount());
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
        Socket s = null;
        try  {
            System.out.println(ip);
            if(ip.equals("127.0.0.1")){
                ip = "localhost";
            }
            System.out.println(ip);
            sincro.await();
            System.out.println("sincronizado");
            s = new Socket("localhost", 55555);
            BufferedReader inp = new BufferedReader(new InputStreamReader(s.getInputStream(), StandardCharsets.UTF_8));
            System.out.println(inp.readLine()); 
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }catch(InterruptedException e2){
            e2.printStackTrace();
        }finally{
            if(s!=null){
                try{
                    s.close();
                }catch(IOException e){
                    e.printStackTrace();
                }
            }
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
}
