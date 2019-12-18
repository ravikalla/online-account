package com.synechron.onlineacc.service;

import java.security.Principal;

import com.synechron.onlineacc.domain.PrimaryAccount;
import com.synechron.onlineacc.domain.SavingsAccount;

public interface AccountService {
	
    PrimaryAccount createPrimaryAccount();
    
    SavingsAccount createSavingsAccount();
    
    void deposit(String accountType, double amount, Principal principal);
    
    void withdraw(String accountType, double amount, Principal principal);
    
}
