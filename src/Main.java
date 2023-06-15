import java.util.Scanner;

/*
downloads :
    https://dev.mysql.com/downloads/connector/j/ -> mysql-connector-j-8.0.33.jar
    https://www.jopendocument.org/download.html -> jOpenDocument-1.5.jar
 */

public class Main {
    public static void menu() {
        System.out.print(
                "1. Afficher OpenCalc\n" +
                "2. Importer OpenCalc dans la BDD\n" +
                "3. Afficher BDD\n" +
                "4. Vérifier stock BDD\n" +
                "5. Vider BDD\n" +
                "Q. Quitter\n");
    }
    public static void main(String[] args) {
        parseur p = new parseur("/home/arthur/Desktop/stock.ods", "/home/arthur/Desktop/stock.csv");
        clientNotifications cN = new clientNotifications("/home/arthur/Desktop/alert.csv");
        Scanner sc = new Scanner(System.in);
        char c;
        do {
            menu();
            c = sc.next().charAt(0);
            c = Character.toUpperCase(c);
            switch (c) {
                case '1':
                    p.afficherOpenCalc();
                    break;
                case '2':
                    p.convertirOdsEnCsv();
                    p.importerCsvDansBDD();
                    break;
                case '3':
                    p.afficherContenuBDD();
                    break;
                case '4':
                    System.out.println("En cours de vérification...\n");
                    cN.verifierStock();
                    break;
                case '5':
                    p.viderBDD();
                    System.out.println("BDD vidé!\n");
                    break;
                case 'Q':
                    System.out.println("Arrêt de l'application...\n");
                    sc.close();
                    break;
                default:
                    System.out.println("Touche invalide.\n");
                    break;
            }
        } while (c != 'Q');
    }
}