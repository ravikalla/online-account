package com.thehartford.onlineacc.dao;

import com.thehartford.onlineacc.domain.SavingsAccount;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

public interface SavingsAccountDao extends CrudRepository<SavingsAccount, Long> {

    List<SavingsAccount> findByAccountNumber (int accountNumber);
}
