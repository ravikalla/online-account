package com.thehartford.onlineacc.dao;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.thehartford.onlineacc.domain.SavingsTransaction;

public interface SavingsTransactionDao extends CrudRepository<SavingsTransaction, Long> {

    List<SavingsTransaction> findAll();
}

