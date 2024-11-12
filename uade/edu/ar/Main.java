package uade.edu.ar;

import uade.edu.ar.util.TransaccionUtils;
import uade.edu.progra3.AlgoritmoDeBlockchain;
import uade.edu.progra3.model.Transaccion;

import java.util.List;

public class Main {
    public static void main(String[] args){
        AlgoritmoDeBlockchain algoritmoDeBlockchain = new AlgoritmoDeBlockchainImpl();
        List<Transaccion> transacciones = TransaccionUtils.crearTransaccionesSimples(10, 100, 1000, 5);
        algoritmoDeBlockchain.construirBlockchain(transacciones,1000,10,5,5);
    }
}
