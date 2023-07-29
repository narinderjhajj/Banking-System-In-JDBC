Description:
This implementation of the Banking System is based on the stored procedures related to the banking application logic in project

Project Specification:
Section A: Schema Definitions
The p2_create.sql file for project 3 contains the 2 create table.
Section B: User Defined Functions – Two UDFs are created in create.clp.
P2.encrypt (pin integer): Is used in the CUST_CRT (…) as we only store encrypted PW.
P2.decrypt (pin integer): Is used in CUST_LOGIN (…) to decrypt/verify the encrypted PW.
Section C: SQL/PL Stored Procedures:
1. CUST_CRT (Name, Gender, Age, Pin, ID, sqlcode, err_msg)
2. CUST_LOGIN (ID, Pin, Valid, sqlcode, err_msg) (Valid = 1 if match, 0 for failure)
3. ACCT_OPN (ID, Balance, Type, Number, sqlcode, err_msg)
4. ACCT_CLS (Number, sqlcode, err_msg)
5. ACCT_DEP (Number, Amt, sqlcode, err_msg)
6. ACCT_WTH (Number, Amt, sqlcode, err_msg)
7. ACCT_TRX (Src_Acct, Dest_Acct, Amt, sqlcode, err_msg)
8. ADD_INTEREST (Savings_Rate, Checking_Rate, sqlcode, err_msg)
Section D: User Interfaces - Required
1. Same command line interface described in P1. STP is called instead
of using JDBC queries from BankingSystem.
Section E: Additional Notes:
1. Stored procedures 1-7 logic will be similar to what is implemented in P1.
2. Implementation of ACCT_TRX() by calling ACCT_WTH() and then ACCT_DEP()l.
3. ADD_INTEREST() is a new procedure for all “active” accounts only.
4. The bold text above means output parameter.
5. Is able to handle error conditions gracefully with an user defined sqlcode and custom text
message.
6. Handles error condition gracefully.
7. There are separate Savings and Checking rates (type float – e.g. if 5% interest, then it should be
0.05).
8. File named p2.sql is used for stored procedure definitions.