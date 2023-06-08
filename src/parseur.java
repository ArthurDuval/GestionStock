// gérer la connexion et la manipulation à la BDD
import java.io.*;
import java.sql.*;
// importer l'OpenCalc
import org.jopendocument.dom.spreadsheet.Sheet;
import org.jopendocument.dom.spreadsheet.SpreadSheet;
import org.jopendocument.model.OpenDocument;
// gérer la fenêtre d'affichage de l'OpenCalc
import org.jopendocument.panel.ODSViewerPanel;
import javax.swing.*;

public class parseur {
    private Statement stmt;
    private OpenDocument doc;
    private File file;
    parseur() {
        try {
            // spécifier à l'objet DriverManager quel driver JDBC on va utiliser (dans notre cas le Connector/J)
            Class.forName("com.mysql.cj.jdbc.Driver").getDeclaredConstructor().newInstance();
            // créer une connexion avec la BDD spécifiée
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/Store?user=admin&password=admin1234");
            // créer un objet Statement à partir de la connexion afin de réaliser des requêtes SQL
            this.stmt = conn.createStatement();
        }
        catch (Exception ex) {
            System.out.println("Exception générée : " + ex.getMessage());
        }
    }
    void afficherContenuBDD() {
        // TODO : améliorer l'affichage (type : ...)
        try {
            // créer un objet ResultSet qui va contenir les résultats des requêtes faites par l'objet Statement
            ResultSet rs = stmt.executeQuery("SELECT * FROM Stock;");
            ResultSetMetaData rsmd = rs.getMetaData();
            int columnsNumber = rsmd.getColumnCount();
            while (rs.next()) {
                for (int i = 1; i <= columnsNumber; i++) {
                    if (i > 1) System.out.print(",  ");
                    String columnValue = rs.getString(i);
                    System.out.print(columnValue + " " + rsmd.getColumnName(i));
                }
                System.out.print('\n');
            }
        }
        catch (Exception ex) {
            System.out.println("Exception générée : " + ex.getMessage());
        }
    }
    void obtenirOpenCalc() {
        this.doc = new OpenDocument();
        // TODO : pop-up path
        String locOds = "/home/arthur/Desktop/stock.ods";
        // TODO : essayer de remove les commentaires console (hauteur et largeur en cm)
        doc.loadFrom(locOds);
        this.file = new File(locOds);
    }
    void afficherOpenCalc() {
        if(doc != null) {
            JFrame mainFrame = new JFrame("OpenCalc Viewer");
            ODSViewerPanel viewerPanel = new ODSViewerPanel(doc);
            mainFrame.setContentPane(viewerPanel);
            mainFrame.pack();
            mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            mainFrame.setLocation(10, 10);
            mainFrame.setVisible(true);
        }
        else {
            System.out.println("Vous devez la récupérer l'OpenCalc d'abord !");
        }
    }
    void convertirOdsEnCsv() {
        if(doc != null) {
            try {
                // créer un objet Sheet à partir de l'OpenDocument afin de le manipuler
                Sheet sheet = SpreadSheet.createFromFile(file).getSheet(0);
                int rCount = sheet.getRowCount();
                int cCount = sheet.getColumnCount();
                // TODO : pop-up path
                String locCsv = "/home/arthur/Desktop/stock.csv";
                Writer writer = new FileWriter(locCsv);
                // boucle de création du .csv à partir du .ods
                for(int i = 0; i < rCount; i++) {
                    for(int j = 0; j < cCount; j++) {
                        writer.write(sheet.getImmutableCellAt(j,i).getValue().toString());
                        if (j < cCount - 1)
                            writer.write(',');
                    }
                    writer.write('\n');
                }
                writer.close();
            }
            catch (Exception ex) {
                System.out.println("Exception générée : " + ex.getMessage());
            }
        }
        else {
            System.out.println("Vous devez la récupérer l'OpenCalc d'abord !");
        }
    }
    void viderBDD() {
        try {
            this.stmt.execute("DELETE FROM Stock;");
        }
        catch (Exception ex) {
            System.out.println("Exception générée : " + ex.getMessage());
        }
    }
    void importerCsvDansBDD() {
        try {
            // TODO : pop-up path
            String locCsv = "/home/arthur/Desktop/stock.csv";
            Reader reader = new FileReader(locCsv);
            // création d'un objet BufferedReader afin d'accéder à la méthode "readLine()" pour lire entièrement une ligne et pas "char par char"
            BufferedReader br = new BufferedReader(reader);
            String line; String[] words;
            viderBDD();
            while ((line = br.readLine()) != null){
                words = line.split(",");
                // TODO : problème de header
                if (!words[1].equals("nombre")){
                    this.stmt.execute("INSERT INTO Stock (type, nombre, hauteur, largeur, decoupe)" +
                            "VALUES ('" + words[0] + "'," + words[1] + "," + words[2] + "," + words[3] + "," + words[4] + ");");
                }
            }
            br.close();
            reader.close();
        }
        catch (Exception ex) {
            System.out.println("Exception générée : " + ex.getMessage());
        }
    }
}