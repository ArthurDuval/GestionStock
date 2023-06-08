public class Main {
    public static void main(String[] args) {
        parseur p = new parseur();
        p.obtenirOpenCalc();
        p.convertirOdsEnCsv();
        p.importerCsvDansBDD();
        p.afficherContenuBDD();
    }
}