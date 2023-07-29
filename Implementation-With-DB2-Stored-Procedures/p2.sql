--
-- db2 -td"@" -f p2.sql
--
CONNECT TO CS157A@
--
--
DROP PROCEDURE P2.CUST_CRT@
DROP PROCEDURE P2.CUST_LOGIN@
DROP PROCEDURE P2.ACCT_OPN@
DROP PROCEDURE P2.ACCT_CLS@
DROP PROCEDURE P2.ACCT_DEP@
DROP PROCEDURE P2.ACCT_WTH@
DROP PROCEDURE P2.ACCT_TRX@
DROP PROCEDURE P2.ADD_INTEREST@
--
--
CREATE PROCEDURE P2.CUST_CRT
(IN p_name CHAR(15), IN p_gender CHAR(1), IN p_age INTEGER, IN p_pin INTEGER, OUT id INTEGER, OUT sql_code INTEGER, OUT err_msg CHAR(100))
LANGUAGE SQL
  BEGIN
    IF p_gender != 'M' AND p_gender != 'F' THEN
      SET sql_code = -100;
      SET err_msg = 'Invalid gender';
    ELSEIF p_age <= 0 THEN
      SET sql_code = -99;
      SET err_msg = 'Invalid age';
    ELSEIF p_pin < 0 THEN
      SET sql_code = -98;
      SET err_msg = 'Invalid pin';
    ELSE
        SET p_pin = p2.encrypt(p_pin);
        INSERT INTO p2.customer(name, gender, age, pin)
            VALUES(p_name,p_gender,p_age,p_pin);
        SET id = (SELECT id from p2.customer
            where name like p_name and
                  gender = p_gender and
                  age = p_age and
                  pin = p_pin);
        SET sql_code = 0;
    END IF;
END@
--
CREATE PROCEDURE P2.CUST_LOGIN
(IN p_cusId INTEGER, IN p_pin INTEGER, OUT Valid INTEGER, OUT sql_code INTEGER, OUT err_msg CHAR(100))
LANGUAGE SQL
 BEGIN

    declare stored_pin INTEGER;
    SET stored_pin = (SELECT pin from p2.customer
                                 where id = p_cusID);
    IF p_cusID <= 0 THEN
          SET sql_code = -90;
          SET err_msg = 'Invalid customer id';
          SET Valid = 0;
    ELSEIF p_pin <= 0 THEN
         SET sql_code = -89;
         SET err_msg = 'Invalid pin';
         SET Valid = 0;

    ELSEIF p2.decrypt(stored_pin) = p_pin and EXISTS(SELECT * from p2.customer
                         where id = p_cusID ) THEN
         SET Valid = 1;
    ELSE
         SET sql_code = -88;
             SET err_msg = 'Invalid id or pin';
             SET Valid = 0;

    END IF;
 END@

CREATE PROCEDURE P2.ACCT_OPN
(IN p_cusID INTEGER, IN p_balance INTEGER, IN p_type CHAR, OUT id INTEGER, OUT sql_code INTEGER, OUT err_msg CHAR(100))
LANGUAGE SQL
 BEGIN
 IF p_cusID <= 0 THEN
       SET sql_code = -80;
       SET err_msg = 'Invalid customer id';
     ELSEIF p_balance <= 0 THEN
       SET sql_code = -79;
       SET err_msg = 'Invalid balance';
     ELSEIF p_type != 'C' and p_type != 'S'THEN
       SET sql_code = -78;
       SET err_msg = 'Invalid account type';
     ELSEIF  EXISTS(SELECT * from p2.customer
                              where id = p_cusID) THEN
       INSERT INTO p2.account(id, balance, type, status)
                   VALUES(p_cusID, p_balance, p_type, 'A');
       SET id = (SELECT number from p2.account
                   where id = p_cusID and type = p_type);
               SET sql_code = 0;
     ELSE
        SET sql_code = -77;
        SET err_msg = 'Invalid id';
      END IF;
 END@

CREATE PROCEDURE P2.ACCT_CLS
(IN p_accId INTEGER, OUT sql_code INTEGER, OUT err_msg CHAR(100))
LANGUAGE SQL
 BEGIN
    IF p_accId <= 0 THEN
        SET sql_code = -70;
        SET err_msg = 'Invalid account number';
    ELSEIF NOT EXISTS(SELECT * from p2.account
                       where number = p_accId ) THEN
        SET sql_code = -69;
                SET err_msg = 'Invalid account';
    ELSEIF  EXISTS(SELECT * from p2.account
                                     where number = p_accId and status = 'I') THEN
                      SET sql_code = -68;
                      SET err_msg = 'Invalid account';
    ELSE
        UPDATE p2.account SET status = 'I', balance = 0 where number = p_accId;
     END IF;
 END@

CREATE PROCEDURE P2.ACCT_DEP
(IN p_accId INTEGER, IN p_amount INTEGER, OUT sql_code INTEGER, OUT err_msg CHAR(100))
LANGUAGE SQL
 BEGIN
    IF p_accId <= 0 THEN
            SET sql_code = -60;
            SET err_msg = 'Invalid account number';
     ELSEIF p_amount <= 0 THEN
           SET sql_code = -59;
           SET err_msg = 'Invalid amount';
     ELSEIF NOT EXISTS(SELECT * from p2.account
                            where number = p_accId) THEN
             SET sql_code = -58;
             SET err_msg = 'Invalid account';
     ELSEIF  EXISTS(SELECT * from p2.account
                                 where number = p_accId and status = 'I') THEN
                  SET sql_code = -58;
                  SET err_msg = 'Invalid account';
     ELSE
    UPDATE p2.account set balance = balance + p_amount WHERE number = p_accId;
    END IF;
 END@

CREATE PROCEDURE P2.ACCT_WTH
(IN p_accId INTEGER, IN p_amount INTEGER, OUT sql_code INTEGER, OUT err_msg CHAR(100))
LANGUAGE SQL
 BEGIN
      IF p_accId <= 0 THEN
             SET sql_code = -50;
             SET err_msg = 'Invalid account number';
      ELSEIF p_amount <= 0 THEN
            SET sql_code = -49;
            SET err_msg = 'Invalid amount';
      ELSEIF NOT EXISTS(SELECT * from p2.account
                                  where number = p_accId) THEN
            SET sql_code = -48;
            SET err_msg = 'Invalid account';
     ELSEIF  EXISTS(SELECT * from p2.account
                                      where number = p_accId and status = 'I') THEN
                       SET sql_code = -47;
                       SET err_msg = 'Invalid account';
     ELSEIF  p_amount > (SELECT balance from p2.account
                    where number = p_accId and status = 'A') THEN
          SET sql_code = -46;
         SET err_msg = 'Not enough funds';
              ELSE
    UPDATE p2.account set balance = balance - p_amount WHERE number = p_accId;
    END IF;
 END@

CREATE PROCEDURE P2.ACCT_TRX
(IN p_srcAcc INTEGER, IN p_desAcc INTEGER, IN p_amount INTEGER, OUT sql_code INTEGER, OUT err_msg CHAR(100))
LANGUAGE SQL
 BEGIN
      IF p_srcAcc <= 0 THEN
             SET sql_code = -40;
             SET err_msg = 'Invalid account number';
      ELSEIF p_desAcc <= 0 THEN
              SET sql_code = -39;
              SET err_msg = 'Invalid account number';
      ELSEIF p_amount <= 0 THEN
            SET sql_code = -38;
            SET err_msg = 'Invalid amount';
      ELSEIF NOT EXISTS(SELECT * from p2.account
                          where number = p_srcAcc) THEN
                  SET sql_code = -37;
                  SET err_msg = 'Invalid source account';
      ELSEIF NOT EXISTS(SELECT * from p2.account
                             where number = p_desAcc) THEN
                   SET sql_code = -36;
                   SET err_msg = 'Invalid destination account';
      ELSEIF  EXISTS(SELECT * from p2.account
                      where number = p_srcAcc and status = 'I' or
                      number = p_desAcc and status = 'I') THEN
                        SET sql_code = -37;
                        SET err_msg = 'Invalid account';
      ELSE
--    UPDATE p2.account set balance = balance - p_amount WHERE number = p_srcAcc;
--    UPDATE p2.account set balance = balance + p_amount WHERE number = p_desAcc;
       CALL P2.ACCT_WTH(p_srcAcc, p_amount, sql_code,err_msg);
       CALL P2.ACCT_DEP(p_desAcc, p_amount, sql_code,err_msg);
    END IF;
 END@

CREATE PROCEDURE P2.ADD_INTEREST
(IN Savings_Rate FLOAT, IN Checking_Rate FLOAT,  OUT sql_code INTEGER, OUT err_msg CHAR(100))
LANGUAGE SQL
 BEGIN
     IF Savings_Rate <= 0 and Savings_Rate > 1 THEN
                 SET sql_code = -30;
                 SET err_msg = 'Invalid Savings_Rate';
     ELSEIF Checking_Rate <= 0 and Checking_Rate > 1 THEN
                  SET sql_code = -29;
                  SET err_msg = 'Invalid Checking_Rate';
     ELSE
         UPDATE p2.account set balance = balance + balance*Checking_Rate
            WHERE type = 'C' and status = 'A';
         UPDATE p2.account set balance = balance + balance*Savings_Rate
             WHERE type = 'S' and status = 'A';
     END IF;
 END@

TERMINATE@
--
--
