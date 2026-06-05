package com.cesde.parkingFlow.repository;

import com.cesde.parkingFlow.entity.Vehiculo;
import com.cesde.parkingFlow.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface VehiculoRepository extends JpaRepository<Vehiculo, Long> {
    boolean existsByPlaca(String placa);
    long countByUser(User user);
    
    // Recupera solo los vehículos que pertenecen al usuario autenticado
    List<Vehiculo> findByUser(User user);
    
    // Busca un vehículo específico asegurando la propiedad del mismo
    Optional<Vehiculo> findByIdAndUser(Long id, User user);
}