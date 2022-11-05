package bank.client;

import javax.sql.rowset.CachedRowSet;
import javax.sql.rowset.RowSetProvider;

import java.sql.*;
import bank.BankDataConnection;
import  com.mysql.cj.jdbc.MysqlConnectionPoolDataSource;

public class ClientQueries {
    private ClientQueries() {}

    static MysqlConnectionPoolDataSource ds = BankDataConnection.ds;

    static Connection cn;
    static CachedRowSet crs;

    static String insertClientStatement = "INSERT INTO clients(name) VALUES (?)";

    static String checkClientName = "SELECT EXISTS(SELECT id FROM clients WHERE name = ?) AS exist";
    static String getClientId = "SELECT id FROM clients WHERE name = ?";

    static {

        try (CachedRowSet newCrs = RowSetProvider.newFactory().createCachedRowSet()) {

            cn = ds.getConnection();

            crs = newCrs;
        } catch (SQLException e) {

            e.printStackTrace();
        }
    }

    public static void insertClient(String name) throws SQLException {
        try (PreparedStatement ps = cn.prepareStatement(insertClientStatement)) {
            ps.setString(1, name);

            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean checkIfClientExist(String name) throws SQLException {
        crs.setCommand(checkClientName);
        crs.setString(1, name);

        crs.execute(cn);
        crs.first();

        return crs.getBoolean("exist");
    }

    public static int getClientIdWithName(String name) throws SQLException {
        crs.setCommand(getClientId);
        crs.setString(1, name);

        crs.execute(cn);
        crs.first();    

        return crs.getInt("id");
    }

    static String changeClientNameQuery = 
    "UPDATE client SET name = ? WHERE id = ?";

    public static void changeClientName(String newName, int clientId) {
        try (PreparedStatement ps = cn.prepareStatement(changeClientNameQuery)) {

            ps.setString(1, newName);
            ps.setInt(2, clientId);

            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}