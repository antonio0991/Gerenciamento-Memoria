import javax.management.InstanceNotFoundException;
import java.util.*;

public class Memoria {
    private static final int TAMANHO = 300;
    //False = posição livre. True = posição ocupada
    private static boolean[] memoria = new boolean[TAMANHO];

    //A chave vai ser o ID do processo
    //Os valores serão a posição e o tamanho
    private static Map<Integer, Integer[]> processosMap = new HashMap<Integer, Integer[]>();

    //O set é necessário para selecionar uma chave aleatória
    private static Set<Integer> chaves = new HashSet<Integer>();

    public static void zerarMemoria() {
        Arrays.fill(memoria, Boolean.FALSE);
    }

    public static void removerProcessoAleatorio() throws InstanceNotFoundException {
        //Pegando uma chave aleatória
        int id = chaves.stream().skip(new Random().nextInt(chaves.size())).findFirst().orElseThrow(InstanceNotFoundException::new);
        Integer[] values = processosMap.get(id);
        int posicaoInicial = values[0];
        int posicaoFinal = posicaoInicial + values[1] + 1;
        //Libera espaços referentes ao processo
        Arrays.fill(memoria, posicaoInicial, posicaoFinal, Boolean.FALSE);
        processosMap.remove(id);
        chaves.remove(id);
        System.out.println("PROCESSO REMOVIDO: \nID: " + id +"\nTAMANHO: " + values[1] + "\nPOSICAO: " + posicaoInicial);
    }

    public static String printMemoria(){
        StringBuilder buffer = new StringBuilder();
        for (int i = 0; i < TAMANHO; i++){
            if(i % 30 == 0){
                buffer.append('\n');
            }
            buffer.append(memoria[i]).append(", ");
        }
        return buffer.toString();
    }

    private static void addProcesso(Processo processo, int posicao){
        processosMap.put(processo.getId(), new Integer[]{posicao, processo.getTamAlocacao()});
        chaves.add(processo.getId());
    }

    public static void alocarProcessoFirstFit(Processo processo) {
        int ultimaPosicaoOcupada = 0;
        int numEspacosLivres = 0;
        for (int i = 0; i <= TAMANHO; i++) {
            //Se a varredura chegar até o fim do array, o processo é
            //descartado por falta de espaço
            if (i == TAMANHO) {
                System.out.println("\nPROCESSO DESCARTADO POR FALTA DE ESPAÇO!");
                break;
            }

            //Checando o número de espaços livres até o tamanho do processo
            if (!memoria[i]) {
                numEspacosLivres++;
                if (numEspacosLivres == processo.getTamAlocacao()) {
                    //Ocupar os espaços
                    Arrays.fill(memoria, ultimaPosicaoOcupada, ultimaPosicaoOcupada + numEspacosLivres + 1, Boolean.TRUE);
                    //Adicionar processo ao mapa e set com chaves
                    addProcesso(processo, ultimaPosicaoOcupada);
                    break;
                }
            }
            //Se não encontrar espaço o suficiente
            else {
                numEspacosLivres = 0;
                ultimaPosicaoOcupada = i;
            }
        }
    }

}