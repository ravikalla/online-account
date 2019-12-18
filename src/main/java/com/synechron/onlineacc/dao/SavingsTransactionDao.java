package com.synechron.onlineacc.dao;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.synechron.onlineacc.domain.SavingsTransaction;

public interface SavingsTransactionDao extends CrudRepository<SavingsTransaction, Long> {

    List<SavingsTransaction> findAll();
}

