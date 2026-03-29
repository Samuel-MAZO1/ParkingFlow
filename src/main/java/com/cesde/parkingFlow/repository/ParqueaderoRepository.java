package com.cesde.parkingFlow.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.cesde.parkingFlow.entity.Parqueadero;

@Repository
public interface ParqueaderoRepository extends JpaRepository<Parqueadero, Long> {
	
}