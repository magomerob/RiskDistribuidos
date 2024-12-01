package servidor;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import javafx.print.PrintColor;

public class Sala implements Serializable{
    private int numJugadores;
    private String nombre;
    private int capacidad;
    private List<Jugador> jugadores = new ArrayList<Jugador>();
    public Sala(String nombre, int capacidad){
        this.nombre = nombre;
        this.capacidad = capacidad;
    }

    public String getNombre(){
        return this.nombre;
    }
    public int getCapacidad(){
        return this.capacidad;
    }
    public List<Jugador> getJugadores(){
        return this.jugadores;
    }

    public void addJugador(Jugador j){
        this.jugadores.add(j);
    }

    public void remJugador(Socket j){
        this.jugadores.remove(j);
    }

    public void setNumJugadores(int i){
        this.numJugadores = i;
    }

    public int getNumJugadores(){
        if(this.jugadores.isEmpty()){
            return this.numJugadores;
        }
        return this.jugadores.size();
    }
    public void updateListoJugador(Jugador j){
        for (int i=0; i<jugadores.size(); i++) {
            Jugador jugador = jugadores.get(i);
            if(jugador.getNombre().equals(j.getNombre()) && jugador.getIp().equals(j.getIp())){
                jugadores.set(i, j);
            }
        }
    }

    public void remJugador(Jugador j){
        for (int i=0; i<jugadores.size(); i++) {
            Jugador jugador = jugadores.get(i);
            if(jugador.getNombre().equals(j.getNombre()) && jugador.getIp().equals(j.getIp())){
                this.jugadores.remove(i);
            }
        }
    }
}
