import java.io.FileInputStream;
import java.sql.*;
import java.util.ArrayList;
import java.util.Properties;

/**
 * Manage connection to database and perform SQL statements.
 */
public class BankingSystem {
    // Connection properties
    private static String driver;
    private static String url;
    private static String username;
    private static String password;

    // JDBC Objects
    private static Connection con;
    private static Statement stmt;
    private static ResultSet rs;
    private static PreparedStatement prepdstmt;

    /**
     * Initialize database connection given properties file.
     *
     * @param filename name of properties file
     */
    public static void init(String filename) {
        try {
            Properties props = new Properties();                        // Create a new Properties object
            FileInputStream input = new FileInputStream(filename);    // Create a new FileInputStream object using our filename parameter
            props.load(input);                                        // Load the file contents into the Properties object
            driver = props.getProperty("jdbc.driver");                // Load the driver
            url = props.getProperty("jdbc.url");                        // Load the url
            username = props.getProperty("jdbc.username");            // Load the username
            password = props.getProperty("jdbc.password");            // Load the password
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Test database connection.
     */
    public static void testConnection() {
        System.out.println(":: TEST - CONNECTING TO DATABASE");
        try {
            Class.forName(driver);
            con = DriverManager.getConnection(url, username, password);
            //con.close();
            System.out.println(":: TEST - SUCCESSFULLY CONNECTED TO DATABASE");
        } catch (Exception e) {
            System.out.println(":: TEST - FAILED CONNECTED TO DATABASE");
            e.printStackTrace();
        }
    }

    /**
     * Create a new customer.
     *
     * @param name   customer name
     * @param gender customer gender
     * @param age    customer age
     * @param pin    customer pin
     */
    public static String newCustomer(String name, String gender, String age, String pin) {
        System.out.println(":: CREATE NEW CUSTOMER - RUNNING");
        int rows = 0;
        try {
            String sql = "INSERT INTO p1.customer(name, gender, age, pin) VALUES(?,?,?,?)";
            prepdstmt = con.prepareStatement(sql);
            prepdstmt.setString(1, name);
            prepdstmt.setString(2, gender);
            prepdstmt.setString(3, age);
            prepdstmt.setString(4, pin);
            rows = prepdstmt.executeUpdate();
        } catch (Exception e) {
            if (e.getClass().toString().equals("class com.ibm.db2.jcc.am.SqlDataException"))
                System.out.println("CREATE NEW CUSTOMER - ERROR - INVALID PIN");
            else
                System.out.println(e);
        }
        if (rows > 0) {
            System.out.println(":: CREATE NEW CUSTOMER - SUCCESS");
            return Integer.toString(getIdOrNumber("customer"));

        }
        return null;
    }

    public static boolean login(String cusID, String pin) {
        try {
            String sql = "SELECT id FROM p1.customer WHERE id = " + cusID + " and pin = " + pin;
            prepdstmt = con.prepareStatement(sql);
            ResultSet resObj = prepdstmt.executeQuery();
            return resObj.next();
        } catch (Exception e) {
            System.out.println(e);
        }
        return false;
    }

    private static int getIdOrNumber(String tableName) {
        try {
            String sql = "SELECT IDENTITY_VAL_LOCAL() AS id FROM p1." + tableName + ";";
            prepdstmt = con.prepareStatement(sql);
            ResultSet resObj = prepdstmt.executeQuery();
            resObj.next();
            return resObj.getInt(1);
        } catch (Exception e) {
            System.out.println(e);
        }
        return -1;
    }

    /**
     * Open a new account.
     *
     * @param id     customer id
     * @param type   type of account
     * @param amount initial deposit amount
     */
    public static String openAccount(String id, String type, String amount) {
        System.out.println(":: OPEN ACCOUNT - RUNNING");
        int rows = 0;
        try {
            String sql = "INSERT INTO p1.account(id, balance, type, status) VALUES(?,?,?,'A')";
            prepdstmt = con.prepareStatement(sql);
            prepdstmt.setString(1, id);
            prepdstmt.setString(3, type);
            prepdstmt.setString(2, amount);
            rows = prepdstmt.executeUpdate();
        } catch (Exception e) {
            System.out.println(e);
        }
        if (rows > 0) {
            System.out.println(":: OPEN ACCOUNT - SUCCESS");
            return Integer.toString(getIdOrNumber("account"));
        }
        return null;
    }

    /**
     * Close an account.
     *
     * @param accNum account number
     */
    public static void closeAccount(String accNum) {
        System.out.println(":: CLOSE ACCOUNT - RUNNING");
        /* insert your code here */
        int rows = 0;
        try {
            String sql = "UPDATE p1.account SET status = 'I' where number = " + accNum;
            prepdstmt = con.prepareStatement(sql);
            rows = prepdstmt.executeUpdate();
        } catch (Exception e) {
            System.out.println(e);
        }
        if (rows > 0) {
            System.out.println(":: CLOSE ACCOUNT - SUCCESS");
        }

    }

    /**
     * Deposit into an account.
     *
     * @param accNum account number
     * @param amount deposit amount
     */
    public static void deposit(String accNum, String amount) {
        System.out.println(":: DEPOSIT - RUNNING");
        try {
            if (Integer.valueOf(amount) <= 0)
                System.out.println(":: DEPOSIT - ERROR - INVALID AMOUNT");
            else {
                String sql = "UPDATE p1.account set balance = balance + " + amount + " WHERE number =" + accNum;
                prepdstmt = con.prepareStatement(sql);
                prepdstmt.executeUpdate();
            }
        } catch (Exception e) {
            if (e.getClass().toString().equals("class com.ibm.db2.jcc.am.SqlIntegrityConstraintViolationException")) {
                System.out.println(":: DEPOSIT - ERROR - NOT ENOUGH FUNDS");
            } else
                System.out.println(":: DEPOSIT - ERROR - INVALID AMOUNT");
        }
        System.out.println(":: DEPOSIT - SUCCESS");

    }

    /**
     * Withdraw from an account.
     *
     * @param accNum account number
     * @param amount withdraw amount
     */
    public static void withdraw(String accNum, String amount) {
        System.out.println(":: WITHDRAW - RUNNING");
        try {
            if (Integer.valueOf(amount) <= 0)
                System.out.println(":: WITHDRAW - INVALID AMOUNT");
            else {
                String sql = "UPDATE p1.account set balance = balance - " + amount + " WHERE number =" + accNum;
                prepdstmt = con.prepareStatement(sql);
                prepdstmt.executeUpdate();
            }
        } catch (Exception e) {
            if (e.getClass().toString().equals("class com.ibm.db2.jcc.am.SqlIntegrityConstraintViolationException")) {
                System.out.println(":: WITHDRAW - ERROR - NOT ENOUGH FUNDS");
            } else
                System.out.println(":: WITHDRAW - ERROR - INVALID AMOUNT");
        }
        System.out.println(":: WITHDRAW - SUCCESS");
    }

    /**
     * Transfer amount from source account to destination account.
     *
     * @param srcAccNum  source account number
     * @param destAccNum destination account number
     * @param amount     transfer amount
     */
    public static void transfer(String srcAccNum, String destAccNum, String amount) {
        System.out.println(":: TRANSFER - RUNNING");
        try {
            if (Integer.valueOf(amount) <= 0)
                System.out.println(":: TRANSFER - ERROR - INVALID AMOUNT");
            else {
                String sql = "UPDATE p1.account set balance = balance - "
                        + amount + " WHERE number = " + srcAccNum;
                prepdstmt = con.prepareStatement(sql);
                prepdstmt.executeUpdate();
                sql = "UPDATE p1.account set balance = balance + "
                        + amount + " WHERE number =" + destAccNum;
                prepdstmt = con.prepareStatement(sql);
                prepdstmt.executeUpdate();
            }
        } catch (Exception e) {
            if (e.getClass().toString().equals("class com.ibm.db2.jcc.am.SqlIntegrityConstraintViolationException")) {
                System.out.println(":: TRANSFER - ERROR -NOT ENOUGH FUNDS");
            } else
                System.out.println(":: TRANSFER - ERROR -INVALID AMOUNT");
        }
        System.out.println(":: TRANSFER - SUCCESS");
    }

    /**
     * Display account summary.
     *
     * @param cusID customer ID
     */
    public static void accountSummary(String cusID) {
        System.out.println(":: ACCOUNT SUMMARY - RUNNING");
        try {
            Statement stmt = con.createStatement();                                              //Create a statement
            String query = "SELECT number, balance FROM p1.account where id = " + cusID + " and status = 'A'";         //The query to run
            ResultSet rs = stmt.executeQuery(query);
            System.out.printf("| %-10s | %-10s |%n", "NUMBER", "BALANCE");
            System.out.printf("--------------------------------%n");
            int total = 0;
            while (rs.next()) {
                total += Integer.valueOf(rs.getString("balance"));
                System.out.printf("| %-10s | %-10s |%n", rs.getString("number"), rs.getString("balance"));
            }
            System.out.printf("--------------------------------%n");
            System.out.printf("TOTAL % 20d %n", total);


        } catch (Exception e) {

            System.out.println(e);

        }
        System.out.println(":: ACCOUNT SUMMARY - SUCCESS");
    }

    public static ArrayList<Integer> getAccounts(String cusID) {
        try {
            Statement stmt = con.createStatement();                                              //Create a statement
            String query = "SELECT number FROM p1.account WHERE id = " + cusID;
            ResultSet rs = stmt.executeQuery(query);
            ArrayList<Integer> accounts = new ArrayList<>();
            while (rs.next()) {
                accounts.add(Integer.valueOf(rs.getString("number")));
            }
            return accounts;
        } catch (Exception e) {
            System.out.println(e);
        }
        return null;
    }

    public static void printResultSet(ResultSet resultSet) throws SQLException {
        ResultSetMetaData rsmd = resultSet.getMetaData();
        int columnsNumber = rsmd.getColumnCount();
        while (resultSet.next()) {
            for (int i = 1; i <= columnsNumber; i++) {
                if (i > 1) System.out.print(",  ");
                String columnValue = resultSet.getString(i);
                System.out.print(columnValue + " " + rsmd.getColumnName(i));
            }
            System.out.println("");
        }
    }

    /**
     * Display Report A - Customer Information with Total Balance in Decreasing Order.
     */
    public static void reportA() {
        System.out.println(":: REPORT A - RUNNING");
        try {
            String sql = "SELECT p1.customer.ID, Name, Gender, Age, SUM(balance) AS Total_Balance " +
                    "FROM p1.customer, p1.account " +
                    "WHERE p1.customer.id = p1.account.id and status = 'A' " +
                    "GROUP BY p1.customer.ID, name, gender, Age " +
                    "ORDER BY Total_Balance DESC";
            prepdstmt = con.prepareStatement(sql);
            ResultSet resultSet = prepdstmt.executeQuery();
            printResultSet(resultSet);
        } catch (Exception e) {
            System.out.println(e);
        }
        System.out.println(":: REPORT A - SUCCESS");
    }

    /**
     * Display Report B - Customer Information with Total Balance in Decreasing Order.
     *
     * @param min minimum age
     * @param max maximum age
     */
    public static void reportB(String min, String max) {
        System.out.println(":: REPORT B - RUNNING");
        try {
            String sql = "SELECT AVG(Total_Balance) AS Average " +
                    "FROM( " +
                    "    SELECT SUM(balance) AS Total_Balance, Age " +
                    "    FROM p1.customer, p1.account " +
                    "    WHERE p1.customer.id = p1.account.id and status = 'A' " +
                    "        and Age >= " + min + " and Age <= " + max +
                    "    GROUP BY Age " +
                    ")";
            prepdstmt = con.prepareStatement(sql);
            ResultSet resultSet = prepdstmt.executeQuery();
            printResultSet(resultSet);
        } catch (Exception e) {
            System.out.println(e);
        }
        System.out.println(":: REPORT B - SUCCESS");
    }
}
