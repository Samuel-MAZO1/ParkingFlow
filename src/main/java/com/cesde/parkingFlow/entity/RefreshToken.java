package com.cesde.parkingFlow.entity;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

import org.hibernate.annotations.UuidGenerator;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "refresh_tokens")
public class RefreshToken {

	@Id
	@GeneratedValue
	@UuidGenerator
	private UUID id;
	
	@OneToOne
	@JoinColumn(name = "user_id", nullable = false)
	private User user;
	
	@Column(name = "token", nullable = false, unique = true)
	private String token;
	
	@Column(name = "expirationDate", nullable = false)
	private Instant expirationDate;
	
	@Column(name = "created_At", nullable = false, updatable = false)
	private LocalDateTime createdAt;

	@PrePersist
	  protected void onCreate() {
	    this.createdAt = LocalDateTime.now();
	  }
	
	public boolean isExpired() {
		return Instant.now().isAfter(this.expirationDate);
	}
}