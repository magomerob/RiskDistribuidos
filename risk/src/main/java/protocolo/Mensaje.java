package protocolo;

import servidor.Jugador;
import servidor.Sala;
import java.util.List;

public class Mensaje {
    public static String listaSalas(List<Sala> salas){
        String mensaje = Protocolo.LISTA_SALAS;

        for (Sala sala : salas) {
            mensaje += Protocolo.DEL;
            mensaje += sala.getNombre();
            mensaje += Protocolo.DEL;
            mensaje += sala.getNumJugadores();
            mensaje += Protocolo.DEL;
            mensaje +=sala.getCapacidad();
        }

        return mensaje;
    }

    public static String nuevaSala(String nombre, int capacidad){
        String mensaje = Protocolo.CREAR_SALA;
        mensaje+=Protocolo.DEL;
        mensaje+=nombre;
        mensaje+=Protocolo.DEL;
        mensaje+=capacidad;
        return mensaje;
    }

    public static String salaCreada(boolean creada){
        if(creada){
            return Protocolo.CREAR_SALA+Protocolo.DEL+Protocolo.OK;
        }else{
            return Protocolo.CREAR_SALA+Protocolo.DEL+Protocolo.ERROR;
        }
    }

    public static String actualizarSala(Sala s){
        String mensaje = Protocolo.ACTUALIZAR_SALA;
        for (Jugador j : s.getJugadores()) {
            mensaje+=Protocolo.DEL;
            mensaje+=j.getNombre();
            mensaje+=Protocolo.DEL;
            //Para facilitar las cosas usaré solo localhost
            //mensaje+=j.getIp().getHostName();
            mensaje+="localhost";
            mensaje+=Protocolo.DEL;
            mensaje+=j.isListo();
        }
        return mensaje;
    }
    
}
