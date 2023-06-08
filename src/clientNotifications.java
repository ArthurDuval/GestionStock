import java.io.FileWriter;
import java.io.Writer;
import java.sql.*;

public class clientNotifications {
    Statement stmt;
    clientNotifications() {
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
    void créerAlerte() {
        // TODO
    }
    void vérifierStock() {
        try {
            ResultSet rs = stmt.executeQuery("SELECT type, nombre FROM Stock WHERE decoupe = 0;");
            // TODO : pop-up path
            String locCsv = "/home/arthur/Desktop/alert.csv";
            Writer writer = new FileWriter(locCsv);
            boolean isStockOk = true;
            while(rs.next()) {
                if (rs.getInt(2) < 10) {
                    isStockOk = false;
                    writer.write(rs.getString(1) + "," + rs.getString(2) + '\n');
                }
            }
            writer.close();
            rs.close();
            if (!isStockOk) {
                créerAlerte();
            }
        }
        catch (Exception ex) {
            System.out.println("Exception générée : " + ex.getMessage());
        }
    }
}