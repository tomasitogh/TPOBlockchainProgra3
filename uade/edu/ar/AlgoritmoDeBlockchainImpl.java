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
        System.out.println("Iniciando construcción de Blockchain...");
        List<List<Bloque>> solucion = new ArrayList<>();
        List<Bloque> blockchain = new ArrayList<>();
        backtrackingBlockchain(transacciones, 0, solucion, blockchain, maxTamanioBloque, maxValorBloque, maxTransacciones);
        System.out.println("Construcción completada. Soluciones encontradas: " + solucion.size());
        return solucion;
    }

    private void backtrackingBlockchain(List<Transaccion> transacciones,
                                        int indice,
                                        List<List<Bloque>> solucion,
                                        List<Bloque> blockchain,
                                        int maxTamanioBloque,
                                        int maxValorBloque,
                                        int maxTransacciones) {
        System.out.println("Llamada recursiva con índice: " + indice + ", Blockchain actual: " + blockchain);
        for (Bloque bloque : blockchain) {
            System.out.println("  Bloque:" + bloque);
            for (Transaccion transaccion : bloque.getTransacciones()) {
                System.out.println("    Transacción: " + transaccion);
            }
        }
        if (indice == transacciones.size()) {
            if (validarBlockchain(blockchain, transacciones)) {

                System.out.println("Fin de las transacciones. Solución válida encontrada: " + blockchain);
                solucion.add(new ArrayList<>(copiarBlockchain(blockchain)));
                return;
            } else {
                return;
            }
        }

        if (!blockchain.isEmpty()) {
            for (int j = 0; j < blockchain.size(); j++) {
                System.out.println("Índice j: " + j);
                Bloque bloqueBlockchain = blockchain.get(j);
                for (int i = indice; i < transacciones.size(); i++) {
                    System.out.println("Índice i: " + i);
                    Transaccion transaccion = transacciones.get(i);
                    if (transaccionValidaEnBloque(blockchain, bloqueBlockchain, transaccion, maxTamanioBloque, maxValorBloque, maxTransacciones)) {
                        System.out.println("Agregando transacción " + transaccion + " al bloque existente " + bloqueBlockchain);
                        List<Transaccion> transaccionesDeBloque = bloqueBlockchain.getTransacciones();
                        transaccionesDeBloque.add(transaccion);
                        System.out.println("Cambiando tamaño y valor del bloque " + bloqueBlockchain);
                        bloqueBlockchain.setTamanioTotal(bloqueBlockchain.getTamanioTotal() + transaccion.getTamanio());
                        bloqueBlockchain.setValorTotal(bloqueBlockchain.getValorTotal() + transaccion.getValor());
                        bloqueBlockchain.setTransacciones(transaccionesDeBloque);

                        backtrackingBlockchain(transacciones, i + 1, solucion, blockchain, maxTamanioBloque, maxValorBloque, maxTransacciones);

                        System.out.println("Deshaciendo cambios en el bloque " + bloqueBlockchain);
                        bloqueBlockchain.setTamanioTotal(bloqueBlockchain.getTamanioTotal() - transaccion.getTamanio());
                        bloqueBlockchain.setValorTotal(bloqueBlockchain.getValorTotal() - transaccion.getValor());
                        transaccionesDeBloque.remove(transaccion);
                        bloqueBlockchain.setTransacciones(transaccionesDeBloque);
                    }
                }
            }
        }
        Bloque nuevoBloque = new Bloque();

        Transaccion transaccion2 = transacciones.get(indice);
        System.out.println("Creando nuevo bloque para transacción: " + transaccion2);
        if (transaccionValidaEnBloque(blockchain, nuevoBloque, transaccion2, maxTamanioBloque, maxValorBloque, maxTransacciones)) {
            System.out.println("Creando nuevo bloque para transacción: " + transaccion2);
            List<Transaccion> transaccionesDeBloqueVacia = new ArrayList<>(nuevoBloque.getTransacciones());
            transaccionesDeBloqueVacia.add(transaccion2);
            nuevoBloque.setTamanioTotal(transaccion2.getTamanio());
            nuevoBloque.setValorTotal(transaccion2.getValor());
            nuevoBloque.setTransacciones(transaccionesDeBloqueVacia);
            blockchain.add(nuevoBloque);

            backtrackingBlockchain(transacciones, indice + 1, solucion, blockchain, maxTamanioBloque, maxValorBloque, maxTransacciones);

            nuevoBloque.setTamanioTotal(nuevoBloque.getTamanioTotal() - transaccion2.getTamanio());
            nuevoBloque.setValorTotal(nuevoBloque.getValorTotal() - transaccion2.getValor());
            nuevoBloque.setTransacciones(new ArrayList<>());
            blockchain.remove(nuevoBloque);
            System.out.println("Deshaciendo bloque: " + nuevoBloque);
        }

    }

    public static boolean transaccionValidaEnBloque(List<Bloque> BC,
                                                    Bloque B,
                                                    Transaccion Tr,
                                                    int maxTamanioB,
                                                    int maxValorB,
                                                    int maxTransaccionesPorB) {
        System.out.println("Validando transacción: " + Tr + " en bloque: " + B);
        if (B.getTamanioTotal() + Tr.getTamanio() > maxTamanioB) {
            System.out.println("Transacción inválida por tamaño");
            return false;
        }

        for (Bloque bloque : BC) {
            for (Transaccion transaccion : bloque.getTransacciones()) {
                if (transaccion.equals(Tr)) {
                    System.out.println("Transacción ya existente en otro bloque");
                    return false;
                }
            }
        }
        for (Transaccion t : B.getTransacciones()) {
            if (t.equals(Tr)) {
                System.out.println("Transacción ya existente en el mismo bloque");
                return false;
            }
        }

        if (B.getValorTotal() + Tr.getValor() > maxValorB) {
            System.out.println("Transacción inválida por valor");
            return false;
        }

        if (B.getTransacciones().size() + 1 > maxTransaccionesPorB) {
            System.out.println("Transacción inválida por número de transacciones");
            return false;
        }

        if (Tr.getDependencia() != null) {
            boolean encontrado = false;
            for (Bloque bloque : BC) {
                for (Transaccion transaccion : bloque.getTransacciones()) {
                    if (transaccion.equals(Tr.getDependencia())) {
                        encontrado = true;
                        break;
                    }
                }
            }
            if (!encontrado) {
                System.out.println("Transacción inválida, dependencia no encontrada");
                return false;
            }
        }

        if (!Tr.isFirmada()) {
            System.out.println("Transacción inválida, no está firmada");
            return false;
        }

        System.out.println("Transacción válida");
        return true;
    }

    private List<Bloque> copiarBlockchain(List<Bloque> blockchain) {
        List<Bloque> copia = new ArrayList<>();
        for (Bloque bloque : blockchain) {
            Bloque nuevoBloque = new Bloque();
            nuevoBloque.setTamanioTotal(bloque.getTamanioTotal());
            nuevoBloque.setValorTotal(bloque.getValorTotal());
            List<Transaccion> transaccionesCopia = new ArrayList<>();
            for (Transaccion transaccion : bloque.getTransacciones()) {
                Transaccion transaccionCopia = new Transaccion(transaccion.getTamanio(),
                                                                transaccion.getValor(),
                                                                transaccion.getDependencia(),
                                                                transaccion.getFirmasRequeridas() );
                transaccionesCopia.add(transaccionCopia);
            }
            nuevoBloque.setTransacciones(transaccionesCopia);
            copia.add(nuevoBloque);
        }
        return copia;
    }

    private boolean validarBlockchain(List<Bloque> blockchain, List<Transaccion> transacciones) {
        boolean flag = true;
        int contTransacciones = 0;
        for (Bloque bloque : blockchain) {
            if (bloque.getValorTotal() % 10 != 0) {
                System.out.println("Blockchain no supera la prueba de trabajo");
                flag = false;
            }
            contTransacciones += bloque.getTransacciones().size();
        }
        if (contTransacciones < transacciones.size()) {
            System.out.println("Blockchain no posee tamaño correspondiente");
            flag = false;
        }
        return flag;
    }
}