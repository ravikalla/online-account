package com.synechron.onlineacc.service.UserServiceImpl;

import static com.synechron.onlineacc.util.AppConstants.EXTERNAL_AUDIT_URI_DEPOSIT;
import static com.synechron.onlineacc.util.AppConstants.EXTERNAL_AUDIT_URI_WITHDRAW;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.Date;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.synechron.onlineacc.dao.PrimaryAccountDao;
import com.synechron.onlineacc.dao.SavingsAccountDao;
import com.synechron.onlineacc.domain.PrimaryAccount;
import com.synechron.onlineacc.domain.PrimaryTransaction;
import com.synechron.onlineacc.domain.SavingsAccount;
import com.synechron.onlineacc.domain.SavingsTransaction;
import com.synechron.onlineacc.domain.User;
import com.synechron.onlineacc.service.AccountService;
import com.synechron.onlineacc.service.TransactionService;
import com.synechron.onlineacc.service.UserService;
@Service
public class AccountServiceImpl implements AccountService {
	private static final Logger L = LogManager.getLogger(AccountServiceImpl.class);

	private static int nextAccountNumber = 11223145;

    @Autowired
    private PrimaryAccountDao primaryAccountDao;

    @Autowired
    private SavingsAccountDao savingsAccountDao;

    @Autowired
    private UserService userService;
    
    @Autowired
    private TransactionService transactionService;

    @Value("${external.audit.url}")
    private String externalAuditURL;

    public PrimaryAccount createPrimaryAccount() {
    		int intAccNum = 0;
    		boolean blnAccountExists = true;
    		List<PrimaryAccount> lstFindByAccountNumber;
    		while (blnAccountExists) {
    			intAccNum = accountGen();
        		lstFindByAccountNumber = primaryAccountDao.findByAccountNumber(intAccNum);
    			if (CollectionUtils.isEmpty(lstFindByAccountNumber))
    				blnAccountExists = false;
    		}

        PrimaryAccount primaryAccount = new PrimaryAccount();
        primaryAccount.setAccountBalance(new BigDecimal("0.0"));
        primaryAccount.setAccountNumber(intAccNum);

        primaryAccountDao.save(primaryAccount);

        L.debug("63 : Primary account number = {}", primaryAccount.getAccountNumber());
        lstFindByAccountNumber = primaryAccountDao.findByAccountNumber(primaryAccount.getAccountNumber());
        if (!CollectionUtils.isEmpty(lstFindByAccountNumber))
        		return lstFindByAccountNumber.get(0);
        else
        		return null;
    }

    public SavingsAccount createSavingsAccount() {
		int intAccNum = 0;
		boolean blnAccountExists = true;
		List<SavingsAccount> lstFindByAccountNumber;
		while (blnAccountExists) {
			intAccNum = accountGen();
    			lstFindByAccountNumber = savingsAccountDao.findByAccountNumber(intAccNum);
			if (CollectionUtils.isEmpty(lstFindByAccountNumber))
				blnAccountExists = false;
		}

        SavingsAccount savingsAccount = new SavingsAccount();
        savingsAccount.setAccountBalance(new BigDecimal("0.0"));
        savingsAccount.setAccountNumber(intAccNum);

        savingsAccountDao.save(savingsAccount);

        L.debug("88 : Savings account number = {}", savingsAccount.getAccountNumber());
        lstFindByAccountNumber = savingsAccountDao.findByAccountNumber(savingsAccount.getAccountNumber());
        if (!CollectionUtils.isEmpty(lstFindByAccountNumber))
        		return lstFindByAccountNumber.get(0);
        else
        		return null;
    }
    
    public void deposit(String accountType, double amount, Principal principal) {
    	L.debug("Start : AccountServiceImpl.deposit(...) : accountType = {}, amount = {}", accountType, amount);
        User user = userService.findByUsername(principal.getName());

        if (accountType.equalsIgnoreCase("Primary")) {
            PrimaryAccount primaryAccount = user.getPrimaryAccount();
            primaryAccount.setAccountBalance(primaryAccount.getAccountBalance().add(new BigDecimal(amount)));
            primaryAccountDao.save(primaryAccount);

            Date date = new Date();

            PrimaryTransaction primaryTransaction = new PrimaryTransaction(date, "Deposit to Primary Account", "Account", "Finished", amount, primaryAccount.getAccountBalance(), primaryAccount);
            transactionService.savePrimaryDepositTransaction(primaryTransaction);

            callBofaDeposit(amount);
            
        } else if (accountType.equalsIgnoreCase("Savings")) {
            SavingsAccount savingsAccount = user.getSavingsAccount();
            savingsAccount.setAccountBalance(savingsAccount.getAccountBalance().add(new BigDecimal(amount)));
            savingsAccountDao.save(savingsAccount);

            Date date = new Date();
            SavingsTransaction savingsTransaction = new SavingsTransaction(date, "Deposit to savings Account", "Account", "Finished", amount, savingsAccount.getAccountBalance(), savingsAccount);
            transactionService.saveSavingsDepositTransaction(savingsTransaction);

            callBofaDeposit(amount);
        }
        L.debug("End : AccountServiceImpl.deposit(...) : accountType = {}, amount = {}", accountType, amount);
    }

    public void withdraw(String accountType, double amount, Principal principal) {
    	L.debug("Start : AccountServiceImpl.withdraw(...) : accountType = {}, amount = {}", accountType, amount);
        User user = userService.findByUsername(principal.getName());

        if (accountType.equalsIgnoreCase("Primary")) {
            PrimaryAccount primaryAccount = user.getPrimaryAccount();
            primaryAccount.setAccountBalance(primaryAccount.getAccountBalance().subtract(new BigDecimal(amount)));
            primaryAccountDao.save(primaryAccount);

            Date date = new Date();

            PrimaryTransaction primaryTransaction = new PrimaryTransaction(date, "Withdraw from Primary Account", "Account", "Finished", amount, primaryAccount.getAccountBalance(), primaryAccount);
            transactionService.savePrimaryWithdrawTransaction(primaryTransaction);
            callBofaWithdraw(amount);
        } else if (accountType.equalsIgnoreCase("Savings")) {
            SavingsAccount savingsAccount = user.getSavingsAccount();
            savingsAccount.setAccountBalance(savingsAccount.getAccountBalance().subtract(new BigDecimal(amount)));
            savingsAccountDao.save(savingsAccount);

            Date date = new Date();
            SavingsTransaction savingsTransaction = new SavingsTransaction(date, "Withdraw from savings Account", "Account", "Finished", amount, savingsAccount.getAccountBalance(), savingsAccount);
            transactionService.saveSavingsWithdrawTransaction(savingsTransaction);
            callBofaWithdraw(amount);
        }
        L.debug("End : AccountServiceImpl.withdraw(...) : accountType = {}, amount = {}", accountType, amount);
    }

    private int accountGen() {
        return ++nextAccountNumber;
    }

	private void callBofaDeposit(double amount) {
		L.debug("Start : AccountServiceImpl.callBofaDeposit() : amount = {} : externalAuditURL = {}", amount, externalAuditURL);
		try {
			RestTemplate rest = new RestTemplate();
			String strResponse = rest.getForObject(externalAuditURL + EXTERNAL_AUDIT_URI_DEPOSIT + "/" + amount, String.class);
			L.info("Bofa online deposit response: " + strResponse);
		} catch (RestClientException e) {
			L.error("Rest call to Bofa-online deposit service failed : externalAuditURL = {}\nRestClientException e = {}", externalAuditURL, e);
			throw e;
		}
		L.debug("End : AccountServiceImpl.callBofaDeposit() : amount = {} : externalAuditURL = {}", amount, externalAuditURL);
	}

	private void callBofaWithdraw(double amount) {
		L.debug("Start : AccountServiceImpl.callBofaWithdraw(), amount = {}", amount);
		try {
			RestTemplate rest = new RestTemplate();
			String str = rest.getForObject(externalAuditURL + EXTERNAL_AUDIT_URI_WITHDRAW + "/" + amount, String.class);
			L.info("Bofa online withdraw response: " + str);
		} catch (RestClientException e) {
			L.error("Rest call to Bofa-online withdraw service failed : RestClientException e = {}", e);
			throw e;
		}
		L.debug("End : AccountServiceImpl.callBofaWithdraw() : amount = {}", amount);
	}
}
