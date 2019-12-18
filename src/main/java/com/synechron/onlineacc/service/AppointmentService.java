package com.synechron.onlineacc.service;

import java.util.List;

import com.synechron.onlineacc.domain.Appointment;

public interface AppointmentService {
    
	Appointment createAppointment(Appointment appointment);

    List<Appointment> findAll();

    Appointment findAppointment(Long id);

    void confirmAppointment(Long id);
}
