package com.cesde.parkingFlow.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


import com.cesde.parkingFlow.entity.RefreshToken;
import com.cesde.parkingFlow.entity.User;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, UUID> {
	
	Optional<RefreshToken> findByToken(String token);
	
	@Modifying
	@Query("DELETE FROM RefreshToken rt WHERE rt.user = :user ")
	void deleteByUsuario (@Param("user") User user);
	
	@Modifying
	@Query("DELETE FROM RefreshToken rt WHERE rt.expirationDate < CURRENT_TIMESTAMP")
	void deleteByexpirationDate();

}