package com.synechron.onlineacc.dao;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.synechron.onlineacc.domain.Appointment;

public interface AppointmentDao extends CrudRepository<Appointment, Long> {

    List<Appointment> findAll();
}
