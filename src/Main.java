import com.microsoft.sqlserver.jdbc.SQLServerDataSource;

import javax.sql.DataSource;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        DataSource dataSource = createDataSource();
        Scanner scan = new Scanner(System.in);

        try (Connection connection = dataSource.getConnection()) {
            System.out.println("Uspjesno ste spojeni na bazu JavaAdv!");

            while (true) {
                System.out.println("#### IZBORNIK ####");
                System.out.println("1. Unesi novog polaznika");
                System.out.println("2. Unesi novi program obrazovanja");
                System.out.println("3. Upisi polaznika na program obrazovanja");
                System.out.println("4. Prebaci polazniga iz jednog u drugi program obrazovanja");
                System.out.println("5. Ispisi informacije o polaznicima za zadani program obrazovanja");
                System.out.println("0. Izadi iz aplikacije");
                System.out.print("Odabir: ");

                int opcija = Integer.parseInt(scan.nextLine());

                switch (opcija) {
                    case 1:
                        noviPolaznik(connection, scan);
                        break;
                    case 2:
                        noviProgram(connection, scan);
                        break;
                    case 3:
                        upisiPolaznika(connection, scan);
                        break;
                    case 4:
                        prebaciPolaznika(connection, scan);
                        break;
                    case 5:
                        ispisPolaznikaPoProgramu(connection, scan);
                        break;
                    case 0:
                        System.out.println("Hvala, bok!");
                        return;
                    default:
                        System.err.println("Izabrali ste nepostojecu opciju!");
                }
            }
        } catch (SQLException e) {
            System.err.println("Greska pri spajanju na bazu!");
            e.printStackTrace();
        }
    }

    private static void noviPolaznik(Connection connection, Scanner scan) throws SQLException {
        System.out.print("Unesi ime novog polaznika: ");
        String ime = scan.nextLine();
        System.out.print("Unesi prezime novog polaznika: ");
        String prezime = scan.nextLine();

        CallableStatement stmt = connection.prepareCall("{call NoviPolaznik(?,?)}");
        stmt.setString(1, ime);
        stmt.setString(2, prezime);
        stmt.execute();
        System.out.println("Novi polaznik uspjesno unesen!");
    }

    private static void noviProgram(Connection connection, Scanner scan) throws SQLException {
        System.out.print("Unesi naziv novog programa: ");
        String naziv = scan.nextLine();
        System.out.print("Unesi broj CSVET bodova novog programa: ");
        int csvet = Integer.parseInt(scan.nextLine());

        CallableStatement stmt = connection.prepareCall("{call NoviProgram(?,?)}");
        stmt.setString(1, naziv);
        stmt.setInt(2, csvet);
        stmt.execute();
        System.out.println("Novi program uspjesno unesen!");
    }

    private static void upisiPolaznika(Connection connection, Scanner scan) throws SQLException {
        System.out.print("Unesi ID polaznika kojeg zelis upisati u odredeni program: ");
        int idPolaznik = Integer.parseInt(scan.nextLine());
        System.out.print("Unesi ID programa u koji zelis upisati odabranog polaznika: ");
        int idProgram = Integer.parseInt(scan.nextLine());

        CallableStatement stmt = connection.prepareCall("{call UpisiPolaznika(?,?)}");
        stmt.setInt(1, idPolaznik);
        stmt.setInt(2, idProgram);
        stmt.execute();
        System.out.println("Polaznik je uspjesno upisan u trazeni program obrazovanja!");
    }

    private static void prebaciPolaznika(Connection connection, Scanner scan) throws SQLException {
        System.out.print("Unesi ID polaznika kojeg zelis prebaciti: ");
        int idPolaznik = Integer.parseInt(scan.nextLine());
        System.out.print("Unesi ID trenutnog programa: ");
        int currId = Integer.parseInt(scan.nextLine());
        System.out.print("Unesi ID novog programa: ");
        int newId = Integer.parseInt(scan.nextLine());

        connection.setAutoCommit(false);
        try {
            CallableStatement stmt = connection.prepareCall("{call PrebaciPolaznika(?,?,?)}");
            stmt.setInt(1, idPolaznik);
            stmt.setInt(2, currId);
            stmt.setInt(3, newId);
            stmt.execute();
            connection.commit();
            System.out.println("Polaznik je uspjesno prebacen!");
        } catch (SQLException e) {
            connection.rollback();
            System.err.println("Transakcija neuspjesna!");
            e.printStackTrace();
        } finally {
            connection.setAutoCommit(true);
        }
    }

    private static void ispisPolaznikaPoProgramu(Connection connection, Scanner scan) throws SQLException {
        System.out.print("Unesi ID programa cije polaznike zelite ispisati: ");
        int idProgram = Integer.parseInt(scan.nextLine());

        CallableStatement stmt = connection.prepareCall("{call IspisPolaznikaPoProgramu(?)}");
        stmt.setInt(1, idProgram);
        ResultSet rs = stmt.executeQuery();
        System.out.println("Popis polaznika: ");
        while (rs.next()) {
            String ime = rs.getString("Ime");
            String prezime = rs.getString("Prezime");
            String naziv = rs.getString("Naziv");
            int csvet = rs.getInt("CSVET");

            System.out.println(ime + " " + prezime + ", " + naziv + ", " + csvet);
        }
    }

    private static DataSource createDataSource() {
        SQLServerDataSource ds = new SQLServerDataSource();
        ds.setServerName("localhost");
        //ds.setPortNumber(1433);
        ds.setDatabaseName("JavaAdv");
        ds.setUser("sa");
        ds.setPassword("SQL");
        ds.setEncrypt(false);
        return ds;
    }
}