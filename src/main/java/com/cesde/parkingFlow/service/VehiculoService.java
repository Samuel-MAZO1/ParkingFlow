package com.cesde.parkingFlow.service;

import com.cesde.parkingFlow.dto.VehiculoRequestDTO;
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

import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class VehiculoService {

    private final VehiculoRepository vehiculoRepository;
    private final UserRepository userRepository;

    // RegEx formato Colombiano: 
    // Opción 1 (Carro): 3 letras seguidas de 3 números (ej. ABC123)
    // Opción 2 (Moto): 3 letras, 2 números y una letra al final (ej. ABC12A)
    private static final String RE_PLACA_COLOMBIA = "^[A-Z]{3}\\d{3}$|^[A-Z]{3}\\d{2}[A-Z]$";
    private static final Pattern PATTERN_PLACA = Pattern.compile(RE_PLACA_COLOMBIA);

    @Transactional
    public VehiculoResponseDTO registrarVehiculo(VehiculoRequestDTO request, String userEmail) {
        // 1. Obtener el usuario autenticado desde el email extraído del token
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new NotFound("Usuario no encontrado"));

        // 2. Normalizar placa a mayúsculas y quitar espacios en blanco laterales
        String placaFormateada = request.getPlaca().trim().toUpperCase();

        // 3. Validar formato de placa colombiana
        if (!PATTERN_PLACA.matcher(placaFormateada).matches()) {
            throw new RegistroInvalido("El formato de la placa no es válido para Colombia (Ej: AAA123 o AAA12A)");
        }

        // 4. Validar que la placa sea única en el sistema
        if (vehiculoRepository.existsByPlaca(placaFormateada)) {
            throw new RegistroInvalido("Esta placa ya se encuentra registrada");
        }

        // 5. Validar máximo 2 vehículos por abonado
        if (vehiculoRepository.countByUser(user) >= 2) {
            throw new RegistroInvalido("Límite excedido: Un abonado solo puede registrar un máximo de 2 vehículos");
        }

        // 6. Construir Entidad
        Vehiculo vehiculo = Vehiculo.builder()
                .placa(placaFormateada)
                .tipoVehiculo(request.getTipoVehiculo())
                .marca(request.getMarca())
                .modelo(request.getModelo())
                .color(request.getColor())
                .user(user)
                .build();

        Vehiculo guardado = vehiculoRepository.save(vehiculo);

        // 7. Retornar DTO de Respuesta
        return VehiculoResponseDTO.builder()
                .id(guardado.getId())
                .placa(guardado.getPlaca())
                .tipoVehiculo(guardado.getTipoVehiculo())
                .marca(guardado.getMarca())
                .modelo(guardado.getModelo())
                .color(guardado.getColor())
                .userId(user.getId())
                .build();
    }
}