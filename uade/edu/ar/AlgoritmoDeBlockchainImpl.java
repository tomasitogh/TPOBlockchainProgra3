package uade.edu.ar;

import uade.edu.progra3.AlgoritmoDeBlockchain;
import uade.edu.progra3.model.Bloque;
import uade.edu.progra3.model.Transaccion;

import java.util.ArrayList;
import java.util.List;
/**
 * @author AlejandroFoglino
 */
public class AlgoritmoDeBlockchainImpl implements AlgoritmoDeBlockchain {
    @Override
    public List<List<Bloque>> construirBlockchain(List<Transaccion> transacciones,
                                                  int maxTamanioBloque,
                                                  int maxValorBloque,
                                                  int maxTransacciones,
                                                  int maxBloques) {
        List<List<Bloque>> solucion = new ArrayList<>();
        List<Bloque> blockchain = new ArrayList<>();
        Bloque bloque = new Bloque();
        backtrackingBlockchain(transacciones, 0, solucion, );
        return solucion;
    }

    private void backtrackingBlockchain(List<Transaccion> transacciones,
                                        int indice,
                                        List<List<Bloque>> solucion,
                                        List<Bloque> blockchain,
                                        Bloque bloque,
                                        int maxTamanioBloque,
                                        int maxValorBloque,
                                        int maxTransacciones) {
        if (indice == transacciones.size()) {
            bloque.setTransacciones();
            solucion.add(blockchain);
            return;
        } else {
            for (int i = indice; i <= transacciones.size(); i++) {
                Transaccion transaccion = transacciones.get(i);
                if (blockchain != null) {
                    for (int j = 0; j <= blockchain.size(); j++) {
                        Bloque bloque = blockchain.get(j);
                        if (transaccionValidaEnBloque(bloque, transaccion)) {
                            // Supongamos que tienes una instancia de Bloque y una instancia de Transaccio

                            List<Transaccion> transaccionesDeBloque = bloque.getTransacciones();

                            transaccionesDeBloque.add(transaccion);

                            bloque.setTamanioTotal(bloque.getTamanioTotal() + transaccion.getTamanio());
                            bloque.setValorTotal(bloque.getValorTotal() + transaccion.getValor());

                            bloque.setTransacciones(transacciones);
                        }
                    }
                }
            }
        }
    }
}