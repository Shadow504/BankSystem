package bank;

import com.mysql.cj.jdbc.MysqlConnectionPoolDataSource;

public class BankDataConnection {
    public static MysqlConnectionPoolDataSource ds = new MysqlConnectionPoolDataSource();

    //We should not add rowsets here as rowsets also act as result-sets, and result-sets differ for each querries / classes
    static {
        ds.setUser("root");
        ds.setPassword("1234");
        ds.setDatabaseName("bank");
        ds.setPort(3306);
    }

    private BankDataConnection() {}
}