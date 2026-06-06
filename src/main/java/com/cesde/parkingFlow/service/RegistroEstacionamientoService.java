package com.cesde.parkingFlow.service;

import com.cesde.parkingFlow.dto.RegistroEntradaAbonadoRequestDTO;
import com.cesde.parkingFlow.dto.RegistroEntradaRequestDTO;
import com.cesde.parkingFlow.dto.RegistroSalidaRequestDTO;
import com.cesde.parkingFlow.dto.response.RecaudoResponseDTO;
import com.cesde.parkingFlow.dto.response.TicketResponseDTO;
import com.cesde.parkingFlow.entity.ConfiguracionCapacidad;
import com.cesde.parkingFlow.entity.PlanAbonado;
import com.cesde.parkingFlow.entity.RegistroEstacionamiento;
import com.cesde.parkingFlow.entity.Suscripcion;
import com.cesde.parkingFlow.entity.Tarifa;
import com.cesde.parkingFlow.entity.Vehiculo;
import com.cesde.parkingFlow.enums.EstadoRegistro;
import com.cesde.parkingFlow.enums.EstadoSuscripcion;
import com.cesde.parkingFlow.exception.custom.NotFound;
import com.cesde.parkingFlow.exception.custom.RegistroInvalido;
import com.cesde.parkingFlow.repository.ConfiguracionCapacidadRepository;
import com.cesde.parkingFlow.repository.RegistroEstacionamientoRepository;
import com.cesde.parkingFlow.repository.SuscripcionRepository;
import com.cesde.parkingFlow.repository.VehiculoRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.regex.Pattern;
import java.util.List;
import com.cesde.parkingFlow.repository.TarifaRepository;

@Service
@RequiredArgsConstructor
public class RegistroEstacionamientoService {

    private final RegistroEstacionamientoRepository registroEstacionamientoRepository;
    private final ConfiguracionCapacidadRepository configuracionCapacidadRepository;
    private final VehiculoRepository vehiculoRepository;
    private final SuscripcionRepository suscripcionRepository;
    private final TarifaRepository tarifaRepository;

    private static final String RE_PLACA_COLOMBIA = "^[A-Z]{3}\\d{3}$|^[A-Z]{3}\\d{2}[A-Z]$";
    private static final Pattern PATTERN_PLACA = Pattern.compile(RE_PLACA_COLOMBIA);

    @Transactional
    public TicketResponseDTO registrarEntradaOcasional(RegistroEntradaRequestDTO request) {
        String placaFormateada = request.getPlaca().trim().toUpperCase();

        // 1. Validar sintaxis de placa colombiana
        if (!PATTERN_PLACA.matcher(placaFormateada).matches()) {
            throw new RegistroInvalido("El formato de la placa no es válido para Colombia (Ej: AAA123 o AAA12A)");
        }

        // 2. Control de estado: El vehículo no puede entrar si ya se encuentra adentro (Estado: ACTIVO)
        if (registroEstacionamientoRepository.existsByPlacaAndEstado(placaFormateada, EstadoRegistro.ACTIVO)) {
            throw new RegistroInvalido("El vehículo con placa " + placaFormateada + " ya se encuentra actualmente dentro del parqueadero");
        }

        // 3. Criterio de aceptación: Validar que haya capacidad disponible para ese tipo
        ConfiguracionCapacidad capacidadConfig = configuracionCapacidadRepository.findByTipoVehiculo(request.getTipoVehiculo())
                .orElseThrow(() -> new NotFound("No se encontró la configuración de capacidad base para: " + request.getTipoVehiculo()));

        long vehiculosEstacionados = registroEstacionamientoRepository.countByTipoVehiculoAndEstado(request.getTipoVehiculo(), EstadoRegistro.ACTIVO);

        if (vehiculosEstacionados >= capacidadConfig.getCapacidadAsignada()) {
            throw new RegistroInvalido("Capacidad máxima alcanzada: No hay celdas disponibles para el tipo " + request.getTipoVehiculo());
        }

        // 4. Mapear y persistir el registro del vehículo ocasional
        RegistroEstacionamiento registro = RegistroEstacionamiento.builder()
                .placa(placaFormateada)
                .tipoVehiculo(request.getTipoVehiculo())
                .esAbonado(false) // Marcado explícitamente como falso por ser ocasional
                .suscripcionId(null)
                .estado(EstadoRegistro.ACTIVO)
                .build();

        RegistroEstacionamiento guardado = registroEstacionamientoRepository.save(registro);

        // 5. Criterio de aceptación: Generar ticket con número único estructurado
        return TicketResponseDTO.builder()
                .numeroTicket("TKT-" + String.format("%06d", guardado.getId()))
                .placa(guardado.getPlaca())
                .tipoVehiculo(guardado.getTipoVehiculo())
                .horaEntrada(guardado.getHoraEntrada())
                .estado(guardado.getEstado())
                .esAbonado(guardado.getEsAbonado())
                .build();
    }

    // --- NUEVO MÉTODO PARA LA HISTORIA DE USUARIO 14 ---

    @Transactional
    public TicketResponseDTO registrarEntradaAbonado(RegistroEntradaAbonadoRequestDTO request) {
        String placaFormateada = request.getPlaca().trim().toUpperCase();

        // 1. Validar sintaxis de la placa
        if (!PATTERN_PLACA.matcher(placaFormateada).matches()) {
            throw new RegistroInvalido("El formato de la placa no es válido para Colombia (Ej: AAA123 o AAA12A)");
        }

        // 2. Control de estado: No permitir re-ingreso si el vehículo ya está adentro
        if (registroEstacionamientoRepository.existsByPlacaAndEstado(placaFormateada, EstadoRegistro.ACTIVO)) {
            throw new RegistroInvalido("El vehículo con placa " + placaFormateada + " ya se encuentra adentro del parqueadero");
        }

        // 3. Criterio de aceptación: Buscar por placa en el maestro de vehículos
        Vehiculo vehiculo = vehiculoRepository.findByPlaca(placaFormateada)
                .orElseThrow(() -> new NotFound("El vehículo con placa " + placaFormateada + " no está registrado en el sistema como abonado"));

        // 4. Criterio de aceptación: Validar suscripción activa
        Suscripcion suscripcion = suscripcionRepository.findByVehiculoAndEstado(vehiculo, EstadoSuscripcion.ACTIVA)
                .orElseThrow(() -> new RegistroInvalido("El vehículo con placa " + placaFormateada + " no cuenta con una suscripción ACTIVA"));

        // Verificar validez de fechas vigentes en el tiempo
        LocalDate hoy = LocalDate.now();
        if (hoy.isBefore(suscripcion.getFechaInicio()) || hoy.isAfter(suscripcion.getFechaFin())) {
            throw new RegistroInvalido("La suscripción del vehículo se encuentra fuera del rango de fechas de vigencia contratadas");
        }

        // 5. Criterio de aceptación: Validar horario permitido del plan (Soporta rangos cruzados de medianoche)
        PlanAbonado plan = suscripcion.getPlanAbonado();
        LocalTime ahora = LocalTime.now();
        boolean horarioValido;

        if (plan.getHorarioInicio().isBefore(plan.getHorarioFin())) {
            // Horario estándar diurno (Ej: 06:00 AM a 06:00 PM)
            horarioValido = !ahora.isBefore(plan.getHorarioInicio()) && !ahora.isAfter(plan.getHorarioFin());
        } else {
            // Horario nocturno cruzado (Ej: 06:00 PM a 06:00 AM del día siguiente)
            horarioValido = !ahora.isBefore(plan.getHorarioInicio()) || !ahora.isAfter(plan.getHorarioFin());
        }

        if (!horarioValido) {
            throw new RegistroInvalido("Acceso denegada por horario: El plan '" + plan.getNombre() + 
                    "' solo permite ingresos en el rango " + plan.getHorarioInicio() + " - " + plan.getHorarioFin());
        }

        // 6. Criterio de aceptación: Validar entradas disponibles y descontar si aplica
        if (!plan.getEntradasIlimitadas()) {
            if (suscripcion.getEntradasUsadas() >= plan.getMaxEntradas()) {
                throw new RegistroInvalido("Acceso denegado: Se han agotado las entradas disponibles (" + 
                        suscripcion.getEntradasUsadas() + "/" + plan.getMaxEntradas() + ") para este plan mensual");
            }
            // Descontar entrada sumando al contador de uso
            suscripcion.setEntradasUsadas(suscripcion.getEntradasUsadas() + 1);
            suscripcionRepository.save(suscripcion);
        }

        // 7. Mapear y registrar el movimiento de parqueo (Estado: ACTIVO)
        RegistroEstacionamiento registro = RegistroEstacionamiento.builder()
                .placa(placaFormateada)
                .tipoVehiculo(vehiculo.getTipoVehiculo())
                .esAbonado(true) // Criterio: No generará cobros al salir por marcarse verdadero
                .suscripcionId(suscripcion.getId())
                .estado(EstadoRegistro.ACTIVO)
                .build();

        RegistroEstacionamiento guardado = registroEstacionamientoRepository.save(registro);

        return TicketResponseDTO.builder()
                .numeroTicket("TKT-" + String.format("%06d", guardado.getId()))
                .placa(guardado.getPlaca())
                .tipoVehiculo(guardado.getTipoVehiculo())
                .horaEntrada(guardado.getHoraEntrada())
                .estado(guardado.getEstado())
                .esAbonado(guardado.getEsAbonado())
                .build();
    }

    // --- NUEVO MÉTODO PARA LA HISTORIA DE USUARIO 15 ---

    @Transactional
    public RecaudoResponseDTO registrarSalidaYCobrar(RegistroSalidaRequestDTO request) {
        String parametro = request.getPlacaOTicket().trim().toUpperCase();
        RegistroEstacionamiento registro;

        // 1. Criterio de aceptación: Buscar por placa o número de ticket
        if (parametro.startsWith("TKT-")) {
            try {
                Long idTicket = Long.parseLong(parametro.substring(4));
                registro = registroEstacionamientoRepository.findByIdAndEstado(idTicket, EstadoRegistro.ACTIVO)
                        .orElseThrow(() -> new NotFound("No se encontró un servicio de parqueo ACTIVO con el ticket provisto"));
            } catch (NumberFormatException e) {
                throw new RegistroInvalido("El formato del número de ticket no es válido");
            }
        } else {
            registro = registroEstacionamientoRepository.findByPlacaAndEstado(parametro, EstadoRegistro.ACTIVO)
                    .orElseThrow(() -> new NotFound("No se encontró un servicio de parqueo ACTIVO para la placa: " + parametro));
        }

        // 2. Criterio de aceptación: Calcular tiempo de permanencia
        LocalDateTime horaSalida = LocalDateTime.now();
        long minutosPermanencia = ChronoUnit.MINUTES.between(registro.getHoraEntrada(), horaSalida);
        if (minutosPermanencia < 0) minutosPermanencia = 0; // Control por desfases de reloj del server

        double montoCobrado = 0.0;

        // 3. Criterio de aceptación: Si es abonado con suscripción válida: SIN COBRO
        if (!registro.getEsAbonado()) {
            // 4. Criterio de aceptación: Si es ocasional: calcular monto según tarifa vigente
            List<Tarifa> tarifasDisponibles = tarifaRepository.findByTipoVehiculoAndActivoTrue(registro.getTipoVehiculo());
            
            if (tarifasDisponibles.isEmpty()) {
                throw new NotFound("No se encuentra ninguna tarifa activa registrada en el sistema para el tipo: " + registro.getTipoVehiculo());
            }

            // Selección de tarifa por franja horaria actual
            LocalTime ahora = horaSalida.toLocalTime();
            Tarifa tarifaVigente = tarifasDisponibles.stream()
                    .filter(t -> t.getHoraInicio() == null || t.getHoraFin() == null ||
                            (t.getHoraInicio().isBefore(t.getHoraFin()) ? 
                             (!ahora.isBefore(t.getHoraInicio()) && !ahora.isAfter(t.getHoraFin())) : 
                             (!ahora.isBefore(t.getHoraInicio()) || !ahora.isAfter(t.getHoraFin()))))
                    .findFirst()
                    .orElse(tarifasDisponibles.get(0)); // Caída por defecto a la primera tarifa activa si no hay franjas

            // Regla de cobro comercial: Calcular horas completas (redondeo hacia arriba)
            double horasACobrar = Math.ceil(minutosPermanencia / 60.0);
            if (horasACobrar == 0) horasACobrar = 1; // Cobro mínimo de 1 hora al ingresar

            // 5. Criterio de aceptación: Aplicar tarifa_dia_completo si supera 8 horas
            if (horasACobrar > 8 && tarifaVigente.getValorDiaCompleto() != null) {
                montoCobrado = tarifaVigente.getValorDiaCompleto();
            } else {
                montoCobrado = horasACobrar * tarifaVigente.getValorHora();
            }
        }

        // 6. Criterio de aceptación: Registrar método de pago, monto y cambiar estado a FINALIZADO
        registro.setHoraSalida(horaSalida);
        registro.setMontoCobrado(montoCobrado);
        registro.setMetodoPago(request.getMetodoPago().toUpperCase());
        registro.setEstado(EstadoRegistro.FINALIZADO);

        registroEstacionamientoRepository.save(registro);

        // 7. Liberación de celda en tiempo real (Sincronización de Ocupación)
        ConfiguracionCapacidad capacidadConfig = configuracionCapacidadRepository.findByTipoVehiculo(registro.getTipoVehiculo())
                .orElseThrow(() -> new NotFound("Configuración de capacidad no encontrada"));
        
        if (capacidadConfig.getOcupacionActual() != null && capacidadConfig.getOcupacionActual() > 0) {
            capacidadConfig.setOcupacionActual(capacidadConfig.getOcupacionActual() - 1);
            configuracionCapacidadRepository.save(capacidadConfig);
        }

        // 8. Retornar DTO de liquidación de salida
        return RecaudoResponseDTO.builder()
                .numeroTicket("TKT-" + String.format("%06d", registro.getId()))
                .placa(registro.getPlaca())
                .tipoVehiculo(registro.getTipoVehiculo())
                .horaEntrada(registro.getHoraEntrada())
                .horaSalida(registro.getHoraSalida())
                .minutosPermanencia(minutosPermanencia)
                .esAbonado(registro.getEsAbonado())
                .montoCobrado(registro.getMontoCobrado())
                .metodoPago(registro.getMetodoPago())
                .estado(registro.getEstado())
                .build();
    }
}