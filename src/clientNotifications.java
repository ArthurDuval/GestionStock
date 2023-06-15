import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.InvocationTargetException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class clientNotifications {
    private Statement stmt = null;
    private String locCsv;
    clientNotifications(String csv) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver").getDeclaredConstructor().newInstance();
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/Store?user=admin&password=admin1234");
            this.stmt = conn.createStatement();
            this.locCsv = csv;
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException | ClassNotFoundException | SQLException e) {
            System.out.println("Exception : " + e.getMessage());
        }
    }
    public void creerAlerte() {
        try {
            DatagramSocket socket = new DatagramSocket();
            BufferedReader br = new BufferedReader(new FileReader(this.locCsv));
            StringBuilder str = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                str.append(line).append('\n');
            }
            byte[] buffer = str.toString().getBytes();
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length, InetAddress.getByName("127.0.0.1"), 12345);
            socket.send(packet);
            socket.close();
        } catch (IOException e) {
            System.out.println("Exception : " + e.getMessage());
        }
    }
    public void verifierStock() {
        try {
            boolean isStockOk = true;
            ResultSet rs = stmt.executeQuery("SELECT type, nombre FROM Stock WHERE decoupe = 0;");
            Writer writer = new FileWriter(this.locCsv);
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
                System.out.println("Envoi demande de réapprovisionnment au service commercial...\n");
            }
            else {
                System.out.println("Le stock n'a pas besoin d'être réapprovisionné!\n");
            }
        } catch (SQLException | IOException e) {
            System.out.println("Exception : " + e.getMessage());
        }
    }
}