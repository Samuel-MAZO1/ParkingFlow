package com.cesde.parkingFlow.service;

import com.cesde.parkingFlow.dto.SuscripcionRequestDTO;
import com.cesde.parkingFlow.dto.SuscripcionRenovarRequestDTO;
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
        User user = getUserByEmail(userEmail);
        Vehiculo vehiculo = vehiculoRepository.findById(request.getVehiculoId())
                .orElseThrow(() -> new NotFound("El vehículo especificado no existe"));

        if (!vehiculo.getUser().getId().equals(user.getId())) {
            throw new RegistroInvalido("Operación denegada: Solo puede adquirir suscripciones para sus propios vehículos");
        }

        PlanAbonado plan = planAbonadoRepository.findById(request.getPlanId())
                .orElseThrow(() -> new NotFound("El plan de abonado seleccionado no existe"));

        if (plan.getActivo() != null && !plan.getActivo()) {
            throw new RegistroInvalido("El plan seleccionado está desactivado por la administración");
        }

        if (!vehiculo.getTipoVehiculo().equals(plan.getTipoVehiculo())) {
            throw new RegistroInvalido("El tipo de vehículo no corresponde con la categoría del plan");
        }

        if (suscripcionRepository.existsByVehiculoAndEstado(vehiculo, EstadoSuscripcion.ACTIVA)) {
            throw new RegistroInvalido("El vehículo con placa " + vehiculo.getPlaca() + " ya posee una suscripción ACTIVA en curso");
        }

        LocalDate fechaInicio = request.getFechaInicio() != null ? request.getFechaInicio() : LocalDate.now();
        LocalDate fechaFin = fechaInicio.plusDays(30);

        Suscripcion suscripcion = Suscripcion.builder()
                .vehiculo(vehiculo)
                .planAbonado(plan)
                .fechaInicio(fechaInicio)
                .fechaFin(fechaFin)
                .estado(EstadoSuscripcion.ACTIVA)
                .entradasUsadas(0)
                .referenciaPago(request.getReferenciaPago())
                .build();

        return convertToResponseDTO(suscripcionRepository.save(suscripcion));
    }

    // --- NUEVO MÉTODO PARA LA HISTORIA DE USUARIO 10 ---
    
    @Transactional
    public SuscripcionResponseDTO renovarSuscripcion(Long idSuscripcionAnterior, SuscripcionRenovarRequestDTO request, String userEmail) {
        // 1. Validar la existencia de la suscripción previa que se desea renovar
        Suscripcion suscripcionAnterior = suscripcionRepository.findById(idSuscripcionAnterior)
                .orElseThrow(() -> new NotFound("La suscripción anterior especificada no existe"));

        // Filtro de Seguridad: Validar que la suscripción pertenezca al abonado autenticado
        if (!suscripcionAnterior.getVehiculo().getUser().getEmail().equals(userEmail)) {
            throw new RegistroInvalido("Operación denegada: No puede renovar una suscripción que no pertenece a sus vehículos");
        }

        // 2. Criterio de aceptación: Validar la ventana de tiempo permitida
        LocalDate hoy = LocalDate.now();
        LocalDate fechaFinAnterior = suscripcionAnterior.getFechaFin();

        // Límite inferior: Recién vencidas (máximo 7 días atrás)
        LocalDate limiteInferior = hoy.minusDays(7);
        // Límite superior: Que venzan en los próximos 5 días
        LocalDate limiteSuperior = hoy.plusDays(5);

        if (fechaFinAnterior.isBefore(limiteInferior) || fechaFinAnterior.isAfter(limiteSuperior)) {
            throw new RegistroInvalido("No es posible renovar. Solo se permiten suscripciones que venzan en los próximos 5 días o recién vencidas (máximo 7 días de retraso)");
        }

        // 3. Obtener y validar el nuevo plan solicitado (puede ser igual o diferente al anterior)
        PlanAbonado nuevoPlan = planAbonadoRepository.findById(request.getPlanId())
                .orElseThrow(() -> new NotFound("El plan seleccionado para la renovación no existe"));

        if (nuevoPlan.getActivo() != null && !nuevoPlan.getActivo()) {
            throw new RegistroInvalido("El plan seleccionado está desactivado por la administración");
        }

        // Validar compatibilidad de tipo de vehículo con el nuevo plan
        Vehiculo vehiculo = suscripcionAnterior.getVehiculo();
        if (!vehiculo.getTipoVehiculo().equals(nuevoPlan.getTipoVehiculo())) {
            throw new RegistroInvalido("El tipo de vehículo (" + vehiculo.getTipoVehiculo() + 
                    ") no es compatible con el nuevo plan seleccionado (" + nuevoPlan.getTipoVehiculo() + ")");
        }

        // 4. Criterio de aceptación: Nueva fecha_inicio = fecha_fin anterior + 1 día
        LocalDate nuevaFechaInicio = fechaFinAnterior.plusDays(1);
        
        // Calcular fecha_fin (30 días de cobertura estándar del sistema)
        LocalDate nuevaFechaFin = nuevaFechaInicio.plusDays(30);

        // Opcional: Si la suscripción anterior seguía en estado ACTIVA, podemos dejar que termine su ciclo natural.
        // Si estaba marcada con otro estado anterior y ya venció cronológicamente, nos aseguramos de actualizar su estado real.
        if (fechaFinAnterior.isBefore(hoy) && suscripcionAnterior.getEstado() == EstadoSuscripcion.ACTIVA) {
            suscripcionAnterior.setEstado(EstadoSuscripcion.VENCIDA);
            suscripcionRepository.save(suscripcionAnterior);
        }

        // 5. Construir y registrar la nueva suscripción renovada
        Suscripcion nuevaSuscripcion = Suscripcion.builder()
                .vehiculo(vehiculo)
                .planAbonado(nuevoPlan)
                .fechaInicio(nuevaFechaInicio)
                .fechaFin(nuevaFechaFin)
                .estado(EstadoSuscripcion.ACTIVA) // Inicia activa o pre-aprobada consecutivamente
                .entradasUsadas(0)
                .referenciaPago(request.getReferenciaPago())
                .build();

        return convertToResponseDTO(suscripcionRepository.save(nuevaSuscripcion));
    }

    private User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFound("Usuario no encontrado"));
    }

    private SuscripcionResponseDTO convertToResponseDTO(Suscripcion suscripcion) {
        return SuscripcionResponseDTO.builder()
                .id(suscripcion.getId())
                .vehiculoId(suscripcion.getVehiculo().getId())
                .placa(suscripcion.getVehiculo().getPlaca())
                .planId(suscripcion.getPlanAbonado().getId())
                .nombrePlan(suscripcion.getPlanAbonado().getNombre())
                .fechaInicio(suscripcion.getFechaInicio())
                .fechaFin(suscripcion.getFechaFin())
                .estado(suscripcion.getEstado())
                .entradasUsadas(suscripcion.getEntradasUsadas())
                .referenciaPago(suscripcion.getReferenciaPago())
                .build();
    }
}