package juego;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class ServidorJugador implements Runnable{

    private Socket s;
    private InputStream inp;
    private OutputStream out;

    public ServidorJugador(Socket socket) {
        this.s = socket;
    }

    @Override
    public void run() {
        try{
            this.inp = this.s.getInputStream();
            this.out = this.s.getOutputStream();

            PrintWriter pw = new PrintWriter(new OutputStreamWriter(this.out, StandardCharsets.UTF_8));

            pw.println("HOLA");
            pw.flush();
            
            }catch(IOException e){
                e.printStackTrace();
            }
        }

    public void broadcast(String msg){
        PrintWriter pw = new PrintWriter(new OutputStreamWriter(this.out, StandardCharsets.UTF_8));

        pw.println(msg);
        pw.flush();
    }
}
