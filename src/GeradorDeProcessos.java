public class GeradorDeProcessos {

    private static int idCont = 0;

    public static Processo getNovoProcesso() {
        return new Processo(idCont++);
    }

    public static int getIdCont() {
        return idCont;
    }

}
