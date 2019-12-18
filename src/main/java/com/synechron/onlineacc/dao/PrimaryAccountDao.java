package com.synechron.onlineacc.dao;

import com.synechron.onlineacc.domain.PrimaryAccount;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

public interface PrimaryAccountDao extends CrudRepository<PrimaryAccount,Long> {

    List<PrimaryAccount> findByAccountNumber (int accountNumber);
}
