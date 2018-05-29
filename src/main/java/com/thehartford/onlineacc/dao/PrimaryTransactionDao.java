package com.thehartford.onlineacc.dao;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.thehartford.onlineacc.domain.PrimaryTransaction;

public interface PrimaryTransactionDao extends CrudRepository<PrimaryTransaction, Long> {

    List<PrimaryTransaction> findAll();
}
