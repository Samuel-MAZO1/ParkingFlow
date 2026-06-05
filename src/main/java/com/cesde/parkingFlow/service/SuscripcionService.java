package com.cesde.parkingFlow.service;

import com.cesde.parkingFlow.dto.SuscripcionRequestDTO;
import com.cesde.parkingFlow.dto.response.SuscripcionResponseDTO;
import com.cesde.parkingFlow.entity.PlanAbonado;
import com.cesde.parkingFlow.entity.User;
import com.cesde.parkingFlow.entity.Vehiculo;
import com.cesde.parkingFlow.entity.Suscripcion;
import com.cesde.parkingFlow.enums.EstadoSuscripcion;
import com.cesde.parkingFlow.exception.custom.NotFound;
import com.cesde.parkingFlow.exception.custom.RegistroInvalido;
import com.cesde.parkingFlow.repository.PlanAbonadoRepository;
import com.cesde.parkingFlow.repository.UserRepository;
import com.cesde.parkingFlow.repository.VehiculoRepository;
import com.cesde.parkingFlow.repository.SuscripcionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class SuscripcionService {

    private final SuscripcionRepository suscripcionRepository;
    private final VehiculoRepository vehiculoRepository;
    private final PlanAbonadoRepository planAbonadoRepository;
    private final UserRepository userRepository;

    @Transactional
    public SuscripcionResponseDTO adquirirSuscripcion(SuscripcionRequestDTO request, String userEmail) {
        // 1. Obtener usuario autenticado
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new NotFound("Usuario abonado no encontrado"));

        // 2. Validar existencia del vehículo
        Vehiculo vehiculo = vehiculoRepository.findById(request.getVehiculoId())
                .orElseThrow(() -> new NotFound("El vehículo especificado no existe"));

        // Filtro de Seguridad: El vehículo debe estar registrado bajo la cuenta del abonado logueado
        if (!vehiculo.getUser().getId().equals(user.getId())) {
            throw new RegistroInvalido("Operación denegada: Solo puede adquirir suscripciones para sus propios vehículos");
        }

        // 3. Validar existencia y disponibilidad del plan de suscripción
        PlanAbonado plan = planAbonadoRepository.findById(request.getPlanId())
                .orElseThrow(() -> new NotFound("El plan de abonado seleccionado no existe"));

        if (plan.getActivo() != null && !plan.getActivo()) {
            throw new RegistroInvalido("El plan seleccionado está desactivado por la administración");
        }

        // Validación de Consistencia: No vincular planes de MOTO a CARROS o viceversa
        if (!vehiculo.getTipoVehiculo().equals(plan.getTipoVehiculo())) {
            throw new RegistroInvalido("El tipo de vehículo (" + vehiculo.getTipoVehiculo() + 
                    ") no corresponde con la categoría del plan (" + plan.getTipoVehiculo() + ")");
        }

        // 4. Criterio de aceptación: Un vehículo solo puede tener una suscripción activa
        if (suscripcionRepository.existsByVehiculoAndEstado(vehiculo, EstadoSuscripcion.ACTIVA)) {
            throw new RegistroInvalido("El vehículo con placa " + vehiculo.getPlaca() + " ya posee una suscripción ACTIVA en curso");
        }

        // 5. Criterio de aceptación: Fecha inicio inmediato (hoy) o fecha futura
        LocalDate fechaInicio = request.getFechaInicio();
        if (fechaInicio == null) {
            fechaInicio = LocalDate.now();
        }

        // 6. Criterio de aceptación: Calcular fecha_fin (Exactamente 30 días)
        LocalDate fechaFin = fechaInicio.plusDays(30);

        // 7. Construcción del registro transaccional
        Suscripcion suscripcion = Suscripcion.builder()
                .vehiculo(vehiculo)
                .planAbonado(plan)
                .fechaInicio(fechaInicio)
                .fechaFin(fechaFin)
                .estado(EstadoSuscripcion.ACTIVA)
                .entradasUsadas(0)
                .referenciaPago(request.getReferenciaPago())
                .build();

        Suscripcion guardada = suscripcionRepository.save(suscripcion);

        // 8. Transformación y retorno del DTO de respuesta
        return SuscripcionResponseDTO.builder()
                .id(guardada.getId())
                .vehiculoId(guardada.getVehiculo().getId())
                .placa(guardada.getVehiculo().getPlaca())
                .planId(guardada.getPlanAbonado().getId())
                .nombrePlan(guardada.getPlanAbonado().getNombre())
                .fechaInicio(guardada.getFechaInicio())
                .fechaFin(guardada.getFechaFin())
                .estado(guardada.getEstado())
                .entradasUsadas(guardada.getEntradasUsadas())
                .referenciaPago(guardada.getReferenciaPago())
                .build();
    }
}