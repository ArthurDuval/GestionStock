import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.Reader;
import java.io.Writer;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class clientNotifications {
    private Statement stmt;
    private String locCsv;
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
    public void creerAlerte() {
        try {
            // création de la socket
            DatagramSocket socket = new DatagramSocket();
            // conversion .csv en string afin de le mettre dans le buffer
            Reader reader = new FileReader(locCsv);
            BufferedReader br = new BufferedReader(reader);
            StringBuilder str = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                str.append(line).append('\n');
            }
            byte[] buffer = str.toString().getBytes();
            int size = buffer.length;
            // création du packet
            DatagramPacket packet = new DatagramPacket(buffer, size, socket.getLocalAddress(), 35236);
            // envoi du packet
            socket.send(packet);
            socket.close();
        }
        catch (Exception ex) {
            System.out.println("Exception générée : " + ex.getMessage());
        }
    }
    public void verifierStock() {
        try {
            ResultSet rs = stmt.executeQuery("SELECT type, nombre FROM Stock WHERE decoupe = 0;");
            // TODO : pop-up path
            this.locCsv = "/home/arthur/Desktop/alert.csv";
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
                creerAlerte();
            }
        }
        catch (Exception ex) {
            System.out.println("Exception générée : " + ex.getMessage());
        }
    }
}