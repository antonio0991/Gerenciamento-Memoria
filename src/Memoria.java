import javax.management.InstanceNotFoundException;
import java.util.*;

public class Memoria {
    private static final int TAMANHO = 1000;
    //False = posição livre. True = posição ocupada
    private static Boolean[] memoria = new Boolean[TAMANHO];

    //A chave vai ser o ID do processo
    //Os valores serão a posição e o tamanho
    private static Map<Integer, Integer[]> processosMap = new HashMap<>();

    //O set é necessário para selecionar uma chave aleatória
    //(dá pra fazer com uma lista, mas com o set a operação é O(1) ao invés de O(n))
    private static Set<Integer> chaves = new HashSet<>();

    private static int posicaoUltimaAlocacao = 0;

    private static double totalProcessosDescartados = 0;
    private static List<Double> taxasOcupacao = new ArrayList<>();

    public static void zerarMemoria() {
        Arrays.fill(memoria, Boolean.FALSE);
        totalProcessosDescartados = 0;
        taxasOcupacao = new ArrayList<>();
        posicaoUltimaAlocacao = 0;
        processosMap = new HashMap<>();
        chaves = new HashSet<>();
    }

    public static void removerProcessoAleatorio() throws InstanceNotFoundException {
        //Pegando uma chave aleatória
        if(!chaves.isEmpty()){
            int id = chaves.stream().skip(new Random().nextInt(chaves.size())).findFirst().orElseThrow(InstanceNotFoundException::new);
            Integer[] values = processosMap.get(id);
            int posicaoInicial = values[0];
            int posicaoFinal = posicaoInicial + values[1] + 1;
            //Libera espaços referentes ao processo
            Arrays.fill(memoria, posicaoInicial, posicaoFinal - 1, Boolean.FALSE);
            processosMap.remove(id);
            chaves.remove(id);
            System.out.println("PROCESSO REMOVIDO: \nID: " + id + "\nTAMANHO: " + values[1] + "\nPOSICAO: " + posicaoInicial);
        }
    }

    public static String printMemoria() {
        StringBuilder buffer = new StringBuilder();
        for (int i = 0; i < TAMANHO; i++) {
            if (i % 30 == 0) {
                buffer.append('\n');
            }
            buffer.append(memoria[i]).append(", ");
        }
        return buffer.toString();
    }

    private static void alocarProcesso(Processo processo, int posicao, int tamanho) {
        Arrays.fill(memoria, posicao, posicao + tamanho, Boolean.TRUE);
        processosMap.put(processo.getId(), new Integer[]{posicao, processo.getTamAlocacao()});
        chaves.add(processo.getId());
        //Aqui ele filtra o array por valores true e divide o length dele pelo tamanho da memória
        //Uso de array para poder calcular a média ao final do processo
        taxasOcupacao.add((double) ((Arrays.stream(memoria).filter(m -> m).toArray().length))/TAMANHO);
    }

    private static void descartarProcesso() {
        System.out.println("\nPROCESSO DESCARTADO POR FALTA DE ESPAÇO!");
        totalProcessosDescartados++;
    }

    public static void alocarProcessoFirstFit(Processo processo) {
        int ultimaPosicaoOcupada = 0;
        int numEspacosLivres = 0;
        for (int i = 0; i <= TAMANHO; i++) {
            //Se a varredura chegar até o fim do array, o processo é
            //descartado por falta de espaço
            if (i == TAMANHO) {
                descartarProcesso();
                break;
            }

            //Checando o número de espaços livres até o tamanho do processo
            if (!memoria[i]) {
                numEspacosLivres++;
                if (numEspacosLivres == processo.getTamAlocacao()) {
                    //Adicionar processo ao array, mapa e set com chaves
                    alocarProcesso(processo, ultimaPosicaoOcupada, processo.getTamAlocacao());
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

    public static void alocarProcessoNextFit(Processo processo) {
        int numEspacosLivres = 0;
        int ultimaPosicaoOcupada = 0;
        boolean chegouAoFim = false;
        for (int i = posicaoUltimaAlocacao; i <= TAMANHO; i++) {
            //Se a varredura chegar até o fim do array, o iterador volta ao início.
            if (i == TAMANHO) {
                i = 0;
                chegouAoFim = true;
            }
            //Se o array inteiro foi varrido a partir da última posição
            if (i == posicaoUltimaAlocacao && chegouAoFim) {
                descartarProcesso();
                break;
            }

            //Checando o número de espaços livres até o tamanho do processo
            if (!memoria[i]) {
                numEspacosLivres++;
                if (numEspacosLivres == processo.getTamAlocacao()) {
                    //Adicionar processo ao array, mapa e set com chaves
                    alocarProcesso(processo, ultimaPosicaoOcupada, processo.getTamAlocacao());
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

    public static void alocarProcessoWorstFit(Processo processo) {
        int numEspacosLivres = 0;
        //O espaço mínimo vai ser o tamanho do processo
        int maiorNumEspacosLivres = processo.getTamAlocacao();
        int posicaoMaiorNumEspacosLivres = 0;
        boolean encontrouEspaco = false;
        for (int i = 0; i < TAMANHO; i++) {
            //Se a varredura chegar até o fim do array, o processo é
            //descartado por falta de espaço
            if (i == TAMANHO - 1 && !encontrouEspaco) {
                descartarProcesso();
                break;
            }

            //Checando o número de espaços livres até o tamanho do processo
            if (!memoria[i]) {
                numEspacosLivres++;
                if (numEspacosLivres >= maiorNumEspacosLivres) {
                    maiorNumEspacosLivres = numEspacosLivres;
                    //Posição inicial do trecho da memória com o maior espaço livre
                    posicaoMaiorNumEspacosLivres = i - numEspacosLivres + 1;
                    encontrouEspaco = true;
                }
            }
            //Se não encontrar espaço o suficiente
            else {
                numEspacosLivres = 0;
            }
        }
        if (encontrouEspaco) {
            alocarProcesso(processo, posicaoMaiorNumEspacosLivres, processo.getTamAlocacao());
        }
    }

    public static void alocarProcessoBestFit(Processo processo) {
        int numEspacosLivres = 0;
        //O espaço mínimo vai ser o tamanho do processo
        int maiorNumEspacosLivres = 0;
        //Partindo do pressuposto que o best fit de um array vazio é o tamanho
        //Como vamos pegar números cada vez menores, esse método funciona
        int bestFit = TAMANHO;
        int posicaOtima = 0;
        boolean encontrouEspaco = false;
        for (int i = 0; i < TAMANHO; i++) {
            //Se a varredura chegar até o fim do array e não foi encontrado
            // um slot, o processo descartado por falta de espaço
            if (i == TAMANHO - 1 && !encontrouEspaco) {
                descartarProcesso();
                break;
            }

            //Checando o número de espaços livres até o tamanho do processo
            if (!memoria[i]) {
                numEspacosLivres++;
            }
            //Se não encontrar espaço o suficiente
            else {
                numEspacosLivres = 0;
            }
            if (numEspacosLivres >= processo.getTamAlocacao() && numEspacosLivres < bestFit) {
                bestFit = numEspacosLivres;
                System.out.println("--------------BEST FIT-------------\n" + bestFit);
                //Posição inicial do trecho da memória com o maior espaço livre
                posicaOtima = i - numEspacosLivres + 1;
                System.out.println("--------------POSICAO-------------\n" + posicaOtima);
                encontrouEspaco = true;
            }
        }
        if (encontrouEspaco) {
            alocarProcesso(processo, posicaOtima, processo.getTamAlocacao());
        }

    }

    public static double getTaxaDescarte() {
        return totalProcessosDescartados / GeradorDeProcessos.getIdCont() * 100;
    }

    public static double getTaxaOcupacao() {
        double total = 0;
        for (double taxa : taxasOcupacao) {
            total += taxa;
        }
        return total/taxasOcupacao.size() * 100;
    }
}