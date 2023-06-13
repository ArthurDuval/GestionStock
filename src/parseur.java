import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import org.jopendocument.dom.spreadsheet.Sheet;
import org.jopendocument.dom.spreadsheet.SpreadSheet;
import org.jopendocument.model.OpenDocument;
import org.jopendocument.panel.ODSViewerPanel;
import javax.swing.JFrame;

public class parseur {
    private Statement stmt;
    private OpenDocument doc;
    private final String locOds = "/home/arthur/Desktop/stock.ods";
    private final String locCsv = "/home/arthur/Desktop/stock.csv";
    parseur() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver").getDeclaredConstructor().newInstance();
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/Store?user=admin&password=admin1234");
            this.stmt = conn.createStatement();
            this.doc = new OpenDocument();
            this.doc.loadFrom(this.locOds);
        } catch (InstantiationException | ClassNotFoundException | NoSuchMethodException | InvocationTargetException | IllegalAccessException | SQLException e) {
            System.out.println("Exception : " + e.getMessage());
        }
    }
    public void afficherContenuBDD() {
        try {
            ResultSet rs = stmt.executeQuery("SELECT * FROM Stock;");
            ResultSetMetaData rsmd = rs.getMetaData();
            int columnsNumber = rsmd.getColumnCount();
            while (rs.next()) {
                for (int i = 1; i <= columnsNumber; i++) {
                    System.out.print(rsmd.getColumnName(i) + " : " + rs.getString(i));
                    if (i < columnsNumber)
                        System.out.print(", ");
                }
                System.out.print('\n');
            }
            System.out.print('\n');
            rs.close();
        } catch (SQLException e) {
            System.out.println("Exception : " + e.getMessage());
        }
    }
    public void afficherOpenCalc() {
        JFrame mainFrame = new JFrame("OpenCalc Viewer");
        mainFrame.setContentPane(new ODSViewerPanel(doc));
        mainFrame.pack();
        mainFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        mainFrame.setVisible(true);
    }
    public void convertirOdsEnCsv() {
        try {
            System.out.println("OpenCalc importé dans la BDD!\n");
            Sheet sheet = SpreadSheet.createFromFile(new File(this.locOds)).getSheet(0);
            int rCount = sheet.getRowCount();
            int cCount = sheet.getColumnCount();
            Writer writer = new FileWriter(this.locCsv);
            for(int i = 0; i < rCount; i++) {
                for(int j = 0; j < cCount; j++) {
                    writer.write(sheet.getImmutableCellAt(j,i).getValue().toString());
                    if (j < cCount - 1)
                        writer.write(',');
                }
                writer.write('\n');
            }
            writer.close();
        } catch (IOException e) {
            System.out.println("Exception : " + e.getMessage());
        }
    }
    public void viderBDD() {
        try {
            this.stmt.execute("DELETE FROM Stock;");
        } catch (SQLException e) {
            System.out.println("Exception : " + e.getMessage());
        }
    }
    public void importerCsvDansBDD() {
        try {
            BufferedReader br = new BufferedReader(new FileReader(this.locCsv));
            String line; String[] words;
            viderBDD();
            while (true){
                try {
                    if ((line = br.readLine()) == null) break;
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                words = line.split(",");
                if (!words[1].equals("nombre")){
                    try {
                        this.stmt.execute("INSERT INTO Stock (type, nombre, hauteur, largeur, decoupe)" +
                                "VALUES ('" + words[0] + "'," + words[1] + "," + words[2] + "," + words[3] + "," + words[4] + ");");
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
            br.close();
        } catch (IOException e) {
            System.out.println("Exception : " + e.getMessage());
        }
    }
}