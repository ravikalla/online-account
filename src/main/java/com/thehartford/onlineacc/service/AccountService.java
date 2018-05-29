package com.thehartford.onlineacc.service;

import java.security.Principal;

import com.thehartford.onlineacc.domain.PrimaryAccount;
import com.thehartford.onlineacc.domain.SavingsAccount;

public interface AccountService {
	
    PrimaryAccount createPrimaryAccount();
    
    SavingsAccount createSavingsAccount();
    
    void deposit(String accountType, double amount, Principal principal);
    
    void withdraw(String accountType, double amount, Principal principal);
    
}
