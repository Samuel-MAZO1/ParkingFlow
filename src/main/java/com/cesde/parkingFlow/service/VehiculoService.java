package com.cesde.parkingFlow.service;

import com.cesde.parkingFlow.dto.VehiculoRequestDTO;
import com.cesde.parkingFlow.dto.VehiculoUpdateDTO;
import com.cesde.parkingFlow.dto.response.VehiculoResponseDTO;
import com.cesde.parkingFlow.entity.User;
import com.cesde.parkingFlow.entity.Vehiculo;
import com.cesde.parkingFlow.exception.custom.NotFound;
import com.cesde.parkingFlow.exception.custom.RegistroInvalido;
import com.cesde.parkingFlow.repository.UserRepository;
import com.cesde.parkingFlow.repository.VehiculoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VehiculoService {

    private final VehiculoRepository vehiculoRepository;
    private final UserRepository userRepository;

    private static final String RE_PLACA_COLOMBIA = "^[A-Z]{3}\\d{3}$|^[A-Z]{3}\\d{2}[A-Z]$";
    private static final Pattern PATTERN_PLACA = Pattern.compile(RE_PLACA_COLOMBIA);

    @Transactional
    public VehiculoResponseDTO registrarVehiculo(VehiculoRequestDTO request, String userEmail) {
        User user = getUserByEmail(userEmail);
        String placaFormateada = request.getPlaca().trim().toUpperCase();

        if (!PATTERN_PLACA.matcher(placaFormateada).matches()) {
            throw new RegistroInvalido("El formato de la placa no es válido para Colombia (Ej: AAA123 o AAA12A)");
        }
        if (vehiculoRepository.existsByPlaca(placaFormateada)) {
            throw new RegistroInvalido("Esta placa ya se encuentra registrada");
        }
        if (vehiculoRepository.countByUser(user) >= 2) {
            throw new RegistroInvalido("Límite excedido: Un abonado solo puede registrar un máximo de 2 vehículos");
        }

        Vehiculo vehiculo = Vehiculo.builder()
                .placa(placaFormateada)
                .tipoVehiculo(request.getTipoVehiculo())
                .marca(request.getMarca())
                .modelo(request.getModelo())
                .color(request.getColor())
                .user(user)
                .build();

        return convertToResponseDTO(vehiculoRepository.save(vehiculo));
    }

    // --- NUEVOS MÉTODOS DE LA HISTORIA DE USUARIO 8 ---

    @Transactional(readOnly = true)
    public List<VehiculoResponseDTO> listarMisVehiculos(String userEmail) {
        User user = getUserByEmail(userEmail);
        return vehiculoRepository.findByUser(user).stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public VehiculoResponseDTO actualizarVehiculo(Long id, VehiculoUpdateDTO request, String userEmail) {
        User user = getUserByEmail(userEmail);
        
        // Al buscar por ID y Usuario mitigamos que intente editar un vehículo ajeno
        Vehiculo vehiculo = vehiculoRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new NotFound("Vehículo no encontrado o no pertenece a su cuenta de abonado"));

        // Se modifican todos los campos solicitados excepto la placa
        vehiculo.setTipoVehiculo(request.getTipoVehiculo());
        vehiculo.setMarca(request.getMarca());
        vehiculo.setModelo(request.getModelo());
        vehiculo.setColor(request.getColor());

        return convertToResponseDTO(vehiculoRepository.save(vehiculo));
    }

    @Transactional
    public void eliminarVehiculo(Long id, String userEmail) {
        User user = getUserByEmail(userEmail);
        
        Vehiculo vehiculo = vehiculoRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new NotFound("Vehículo no encontrado o no pertenece a su cuenta de abonado"));

        // Criterio de aceptación: Solo si no está actualmente estacionado
        if (isVehiculoEstacionado(vehiculo.getPlaca())) {
            throw new RegistroInvalido("No es posible eliminar el vehículo porque se encuentra estacionado dentro del parqueadero");
        }

        vehiculoRepository.delete(vehiculo);
    }

    // Método auxiliar de validación de estado en parqueadero
    private boolean isVehiculoEstacionado(String placa) {
        // Interconexión futura US-013+: Aquí se inyectará el RegistroEstacionamientoRepository
        // Ejemplo lógico: return registroEstacionamientoRepository.existsByPlacaAndEstado(placa, EstadoRegistro.ACTIVO);
        return false; 
    }

    private User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFound("Usuario no encontrado"));
    }

    private VehiculoResponseDTO convertToResponseDTO(Vehiculo vehiculo) {
        return VehiculoResponseDTO.builder()
                .id(vehiculo.getId())
                .placa(vehiculo.getPlaca())
                .tipoVehiculo(vehiculo.getTipoVehiculo())
                .marca(vehiculo.getMarca())
                .modelo(vehiculo.getModelo())
                .color(vehiculo.getColor())
                .userId(vehiculo.getUser().getId())
                .build();
    }
}