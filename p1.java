import java.util.Scanner;

public class p1 {
    final Scanner sc = new Scanner(System.in);

    public static void main(String argv[]) {
        System.out.println(":: PROGRAM START");

        if (argv.length < 1) {
            System.out.println("Need database properties filename");
        } else {
            BankingSystem.init(argv[0]);
            BankingSystem.testConnection();
            System.out.println();
            p1 cmd = new p1();
            cmd.cmdLine();
        }

        System.out.println(":: PROGRAM END");
    }

    public void cmdLine() {
        boolean flag = true;

        String inputStr = "";

        do {
            System.out.println("Screen # 1 (Title - Welcome to the Self Services Banking System! - Main Menu)\n" +
                    "1. New Customer\n" +
                    "2. Customer Login\n" +
                    "3. Exit");

            switch (promptInt(1, 3)) {
                case 1:
                    System.out.println("Enter Name, Gender, Age, and Pin with space as delimiter");
                    String[] strArray = sc.nextLine().split(" ");
                    String res = BankingSystem.newCustomer(strArray[0], strArray[1], strArray[2], strArray[3]);
                    System.out.println("Customer Id = " + res);
                    break;
                case 2:
                    System.out.println("Enter Customer ID and Pin with space as delimiter");
                    strArray = sc.nextLine().split(" ");
                    if (strArray[0].equals("0") && strArray[1].equals("0")) {
                        screen4();
                    } else if (BankingSystem.login(strArray[0], strArray[1]))
                        screen3(strArray[0]);
                    else
                        System.out.println("No Such customer Exists!");
                    break;
                case 3:
                    flag = false;
                    break;
                default:
                    break;
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
                    "6. Account Summary\n" +
                    "7. Exit");
            switch (promptInt(1, 7)) {
                case 1:
                    System.out.println("Enter customer ID, account type, and balance (Initial deposit) with space as delimiter");
                    String[] strArray = sc.nextLine().split(" ");
                    String res = BankingSystem.openAccount(strArray[0], strArray[1], strArray[2]);
                    System.out.println("Account number = " + res);
                    break;
                case 2:
                    System.out.println("Enter Account # (It will change the status attribute to " +
                            "‘I’ and empty the balance for that account.)");
                    strArray = sc.nextLine().split(" ");
                    if (BankingSystem.getAccounts(cusID).contains(Integer.valueOf(strArray[0])))
                        BankingSystem.closeAccount(strArray[0]);
                    else
                        System.out.println("Cannot close this account!");
                    break;
                case 3:
                    System.out.println("Enter Account # and deposit amount with space as delimiter");
                    strArray = sc.nextLine().split(" ");
                    BankingSystem.deposit(strArray[0], strArray[1]);
                    break;
                case 4:
                    System.out.println("Enter Account # and withdraw amount with space as delimiter");
                    strArray = sc.nextLine().split(" ");
                    System.out.println("Check " + BankingSystem.getAccounts(cusID).contains(Integer.valueOf(strArray[0])));
                    for (Integer x : BankingSystem.getAccounts(cusID)) {
                        System.out.println(x);
                    }
                    if (BankingSystem.getAccounts(cusID).contains(Integer.valueOf(strArray[0])))
                        BankingSystem.withdraw(strArray[0], strArray[1]);
                    else
                        System.out.println("Cannot withdraw from this account!");
                    break;
                case 5:
                    System.out.println("First enter the source account #, then the destination account # and then transfer amount with space as delimiter");
                    strArray = sc.nextLine().split(" ");

                    if (BankingSystem.getAccounts(cusID).contains(Integer.valueOf(strArray[0])))
                        BankingSystem.transfer(strArray[0], strArray[1], strArray[0]);
                    else
                        System.out.println("Cannot transfer from the source account!");
                    break;
                case 6:
                    BankingSystem.accountSummary(cusID);
                    break;
                case 7:
                    flag = false;
                    break;
                default:
                    System.out.println("Invalid Option");
                    break;
            }

        } while (flag);
    }

    private void screen4() {
        boolean flag = true;
        do {
            System.out.println("Screen # 4 (Title – Administrator Main Menu)\n" +
                    "1. Account Summary for a Customer\n" +
                    "2. Report A :: Customer Information with Total Balance in Decreasing Order\n" +
                    "3. Report B :: Find the Average Total Balance Between Age Groups\n" +
                    "4. Exit");
            switch (promptInt(1, 4)) {
                case 1:
                    System.out.println("Enter the customer ID to view their Account Summary");
                    promptInt();
                    break;
                case 2:
                    BankingSystem.reportA();
                    break;
                case 3:
                    System.out.println("Enter a min & max age to compute and display Average Balance with space as delimiter");
                    String[] strArray = sc.nextLine().split(" ");
                    BankingSystem.reportB(strArray[0], strArray[1]);
                    break;
                case 4:
                    flag = false;
                    break;
                default:
                    break;
            }
        } while (flag);

    }
}
