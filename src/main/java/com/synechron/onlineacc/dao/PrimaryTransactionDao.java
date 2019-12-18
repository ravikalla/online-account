package com.synechron.onlineacc.dao;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.synechron.onlineacc.domain.PrimaryTransaction;

public interface PrimaryTransactionDao extends CrudRepository<PrimaryTransaction, Long> {

    List<PrimaryTransaction> findAll();
}
