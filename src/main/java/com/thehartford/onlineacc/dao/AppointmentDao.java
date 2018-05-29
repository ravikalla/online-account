package com.thehartford.onlineacc.dao;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.thehartford.onlineacc.domain.Appointment;

public interface AppointmentDao extends CrudRepository<Appointment, Long> {

    List<Appointment> findAll();
}
