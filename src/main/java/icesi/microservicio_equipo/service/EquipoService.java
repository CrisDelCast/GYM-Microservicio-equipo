package icesi.microservicio_equipo.service;

import icesi.microservicio_equipo.model.EquipoId;
import icesi.microservicio_equipo.model.Estado;
import icesi.microservicio_equipo.model.Equipo;
import icesi.microservicio_equipo.repository.EquipoRepository;
import jakarta.transaction.Transactional;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EquipoService {

    @Autowired
    private EquipoRepository equipoRepository;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    /**
     * Obtiene un equipo por su ID.
     */
    public Equipo obtenerEquipo(EquipoId id) {
        return equipoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Equipo no encontrado"));
    }

    /**
     * Cambia la cantidad de un equipo y envía una notificación a RabbitMQ.
     */
    public void cambiarCantidad(EquipoId id, String nuevaCantidad) {
        Equipo equipo = obtenerEquipo(id);
        equipo.actualizarCantidad(nuevaCantidad);
        equipoRepository.save(equipo);

        // Notificar a RabbitMQ sobre el cambio de cantidad
        String mensaje = "📦 Cantidad de equipo actualizada: ID " + id + " - Nueva cantidad: " + nuevaCantidad;
        rabbitTemplate.convertAndSend("equipos.intercambio", "equipos.cantidad", mensaje);
    }

    /**
     * Cambia el estado de un equipo y notifica a RabbitMQ.
     */
    @Transactional
    public void cambiarEstado(EquipoId id, Estado nuevoEstado) {
        Equipo equipo = obtenerEquipo(id);
        equipo.actualizarEstado(nuevoEstado);
        equipoRepository.save(equipo);

        // Notificar a RabbitMQ sobre el cambio de estado
        String mensaje = "🔧 Estado del equipo cambiado: ID " + id + " - Nuevo estado: " + nuevoEstado;
        rabbitTemplate.convertAndSend("equipos.intercambio", "equipos.estado", mensaje);
    }
}
