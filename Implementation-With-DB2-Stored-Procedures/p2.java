import java.io.FileInputStream;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Types;
import java.util.Properties;
import java.util.Scanner;

public class p2 {
    // Connection properties
    private static String driver;
    private static String url;
    private static String username;
    private static String password;
    // JDBC Objects
    private static Connection con;
    private static CallableStatement cs;
    final Scanner sc = new Scanner(System.in);

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

    public static void main(String argv[]) {
        System.out.println(":: PROGRAM START");

        if (argv.length < 1) {
            System.out.println("Need database properties filename");
        } else {
            init(argv[0]);
            testConnection();
            System.out.println();
            p2 cmd = new p2();
            cmd.cmdLine();
        }

        System.out.println(":: PROGRAM END");
    }

    public void cmdLine() {
        boolean flag = true;
        do {
            System.out.println("Screen # 1 (Title - Welcome to the Self Services Banking System! - Main Menu)\n" +
                    "1. New Customer\n" +
                    "2. Customer Login\n" +
                    "3. Exit");
            try {
                switch (promptInt(1, 3)) {
                    case 1:
                        System.out.println("Enter Name, Gender, Age, and Pin with space as delimiter");
                        String[] strArray = sc.nextLine().split(" ");
                        cs = con.prepareCall("call P2.CUST_CRT(?,?,?,?,?,?,?)");
                        //cs.registerOutParameter(1, Types.INTEGER);
                        cs.setString(1, strArray[0]);
                        cs.setString(2, strArray[1]);
                        cs.setString(3, strArray[2]);
                        cs.setString(4, strArray[3]);
                        cs.registerOutParameter(5, Types.INTEGER);
                        cs.registerOutParameter(6, Types.INTEGER);
                        cs.registerOutParameter(7, Types.VARCHAR);
                        cs.execute();
                        if (cs.getInt(6) == 0)
                            System.out.println("Customer Id = " + cs.getInt(5));
                        else throw new Exception("Error: " + cs.getString(7));
                        break;
                    case 2:
                        System.out.println("Enter Customer ID and Pin with space as delimiter");
                        strArray = sc.nextLine().split(" ");
                        cs = con.prepareCall("call P2.CUST_LOGIN(?,?,?,?,?)");
                        cs.setString(1, strArray[0]);
                        cs.setString(2, strArray[1]);
                        cs.registerOutParameter(3, Types.INTEGER);
                        cs.registerOutParameter(4, Types.INTEGER);
                        cs.registerOutParameter(5, Types.VARCHAR);
                        cs.execute();
                        if (cs.getInt(3) == 1)
                            screen3(strArray[0]);
                        else if (cs.getInt(3) != 0) {
                            throw new Exception("Error: " + cs.getString(7));
                        } else
                            System.out.println("No Such customer Exists!");
                        break;
                    case 3:
                        flag = false;
                        break;
                    default:
                        break;
                }
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }

        } while (flag);
    }

    private Integer promptInt() {
        return promptInt(Integer.MIN_VALUE, Integer.MAX_VALUE);
    }

    private Integer promptInt(int min, int max) {
        int inputInt = 0;
        try {
            System.out.print(">>");
            inputInt = Integer.parseInt(sc.nextLine());
            if (inputInt < min || inputInt > max) throw new Exception();
        } catch (Exception e) {
            System.out.println("Invalid option!");
        }
        return inputInt;
    }

    private void screen3(String cusID) {
        boolean flag = true;
        do {
            System.out.println("Screen # 3 (Title – Customer Main Menu)\n" +
                    "1. Open Account\n" +
                    "2. Close Account\n" +
                    "3. Deposit\n" +
                    "4. Withdraw\n" +
                    "5. Transfer\n" +
                    "6. Exit");
            try {
                switch (promptInt(1, 7)) {
                    case 1:
                        System.out.println("Enter customer ID, account type, and balance (Initial deposit) with space as delimiter");
                        String[] strArray = sc.nextLine().split(" ");
                        cs = con.prepareCall("call P2.ACCT_OPN(?,?,?,?,?,?)");
                        cs.setString(1, strArray[0]);
                        cs.setString(2, strArray[2]);
                        cs.setString(3, strArray[1]);
                        cs.registerOutParameter(4, Types.INTEGER);
                        cs.registerOutParameter(5, Types.INTEGER);
                        cs.registerOutParameter(6, Types.VARCHAR);
                        cs.execute();
                        if (cs.getInt(5) == 0) {
                            System.out.println("Account number = " + cs.getInt(4));
                        } else
                            throw new Exception("Error: " + cs.getString(6));
                        break;
                    case 2:
                        System.out.println("Enter Account # (It will change the status attribute to " +
                                "‘I’ and empty the balance for that account.)");
                        strArray = sc.nextLine().split(" ");
                        cs = con.prepareCall("call P2.ACCT_CLS(?,?,?)");
                        cs.setString(1, strArray[0]);
                        cs.registerOutParameter(2, Types.INTEGER);
                        cs.registerOutParameter(3, Types.VARCHAR);
                        cs.execute();
                        if (cs.getInt(2) == 0) {
                            System.out.println("Account Closed!");
                        } else
                            throw new Exception("Error: " + cs.getString(3));
                        break;
                    case 3:
                        System.out.println("Enter Account # and deposit amount with space as delimiter");
                        strArray = sc.nextLine().split(" ");
                        cs = con.prepareCall("call P2.ACCT_DEP(?,?,?,?)");
                        cs.setString(1, strArray[0]);
                        cs.setString(2, strArray[1]);
                        cs.registerOutParameter(3, Types.INTEGER);
                        cs.registerOutParameter(4, Types.VARCHAR);
                        cs.execute();
                        if (cs.getInt(3) == 0) {
                            System.out.println("Deposit Successful!");
                        } else
                            throw new Exception("Error: " + cs.getString(4));
                        break;
                    case 4:
                        System.out.println("Enter Account # and withdraw amount with space as delimiter");
                        strArray = sc.nextLine().split(" ");
                        cs = con.prepareCall("call P2.ACCT_WTH(?,?,?,?)");
                        cs.setString(1, strArray[0]);
                        cs.setString(2, strArray[1]);
                        cs.registerOutParameter(3, Types.INTEGER);
                        cs.registerOutParameter(4, Types.VARCHAR);
                        cs.execute();
                        if (cs.getInt(3) == 0) {
                            System.out.println("Withdraw Successful!");
                        } else
                            throw new Exception("Error: " + cs.getString(4));
                        break;
                    case 5:
                        System.out.println("First enter the source account #, then the destination account # and then transfer amount with space as delimiter");
                        strArray = sc.nextLine().split(" ");

                        cs = con.prepareCall("call P2.ACCT_TRX(?,?,?,?,?)");
                        cs.setString(1, strArray[0]);
                        cs.setString(2, strArray[1]);
                        cs.setString(3, strArray[2]);
                        cs.registerOutParameter(4, Types.INTEGER);
                        cs.registerOutParameter(5, Types.VARCHAR);
                        cs.execute();
                        if (cs.getInt(4) == 0) {
                            System.out.println("Transfer Successful!");
                        } else
                            throw new Exception("Error: " + cs.getString(5));
//                    else
//                        System.out.println("Cannot transfer from the source account!");
                        break;
                    case 7:
                        flag = false;
                        break;
                    default:
                        System.out.println("Invalid Option");
                        break;
                }
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }

        } while (flag);
    }


}
