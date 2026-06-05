package com.cesde.parkingFlow.service;

import com.cesde.parkingFlow.dto.PlanAbonadoRequestDTO;
import com.cesde.parkingFlow.dto.response.PlanAbonadoResponseDTO;
import com.cesde.parkingFlow.entity.PlanAbonado;
import com.cesde.parkingFlow.exception.custom.NotFound;
import com.cesde.parkingFlow.exception.custom.RegistroInvalido;
import com.cesde.parkingFlow.repository.PlanAbonadoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PlanAbonadoService {

    private final PlanAbonadoRepository planAbonadoRepository;

    @Transactional
    public PlanAbonadoResponseDTO crearPlan(PlanAbonadoRequestDTO request) {
        validarConsistenciaEntradas(request);

        PlanAbonado plan = PlanAbonado.builder()
                .nombre(request.getNombre())
                .tipoVehiculo(request.getTipoVehiculo())
                .precioMensual(request.getPrecioMensual())
                .entradasIlimitadas(request.getEntradasIlimitadas())
                .maxEntradas(request.getEntradasIlimitadas() ? null : request.getMaxEntradas())
                .horarioInicio(request.getHorarioInicio())
                .horarioFin(request.getHorarioFin())
                .activo(true)
                .build();

        return convertToResponseDTO(planAbonadoRepository.save(plan));
    }

    @Transactional(readOnly = true)
    public List<PlanAbonadoResponseDTO> listarTodos() {
        return planAbonadoRepository.findAll().stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<PlanAbonadoResponseDTO> listarActivos() {
        return planAbonadoRepository.findByActivoTrue().stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public PlanAbonadoResponseDTO actualizarPlan(Long id, PlanAbonadoRequestDTO request) {
        PlanAbonado plan = planAbonadoRepository.findById(id)
                .orElseThrow(() -> new NotFound("El plan de abonado solicitado no existe"));

        validarConsistenciaEntradas(request);

        plan.setNombre(request.getNombre());
        plan.setTipoVehiculo(request.getTipoVehiculo());
        plan.setPrecioMensual(request.getPrecioMensual());
        plan.setEntradasIlimitadas(request.getEntradasIlimitadas());
        plan.setMaxEntradas(request.getEntradasIlimitadas() ? null : request.getMaxEntradas());
        plan.setHorarioInicio(request.getHorarioInicio());
        plan.setHorarioFin(request.getHorarioFin());

        return convertToResponseDTO(planAbonadoRepository.save(plan));
    }

    @Transactional
    public void desactivarPlan(Long id) {
        PlanAbonado plan = planAbonadoRepository.findById(id)
                .orElseThrow(() -> new NotFound("El plan de abonado solicitado no existe"));
        
        plan.setActivo(false);
        planAbonadoRepository.save(plan);
    }

    private void validarConsistenciaEntradas(PlanAbonadoRequestDTO request) {
        if (!request.getEntradasIlimitadas() && (request.getMaxEntradas() == null || request.getMaxEntradas() <= 0)) {
            throw new RegistroInvalido("Si las entradas no son ilimitadas, debe definir un límite máximo de visitas válido (mayor a 0)");
        }
    }

    private PlanAbonadoResponseDTO convertToResponseDTO(PlanAbonado plan) {
        return PlanAbonadoResponseDTO.builder()
                .id(plan.getId())
                .nombre(plan.getNombre())
                .tipoVehiculo(plan.getTipoVehiculo())
                .precioMensual(plan.getPrecioMensual())
                .entradasIlimitadas(plan.getEntradasIlimitadas())
                .maxEntradas(plan.getMaxEntradas())
                .horarioInicio(plan.getHorarioInicio())
                .horarioFin(plan.getHorarioFin())
                .activo(plan.getActivo())
                .build();
    }
}