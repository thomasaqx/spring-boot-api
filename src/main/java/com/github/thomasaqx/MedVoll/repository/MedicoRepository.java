package com.github.thomasaqx.MedVoll.repository;

import com.github.thomasaqx.MedVoll.domain.medico.Medico;

import org.springframework.data.jpa.repository.JpaRepository;

public interface MedicoRepository extends JpaRepository <Medico, Long> {


}
