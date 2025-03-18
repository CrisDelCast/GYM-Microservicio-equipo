package icesi.microservicio_equipo.service;

import java.util.HashMap;
import java.util.Map;
import org.apache.kafka.common.TopicPartition;
import org.springframework.stereotype.Service;

@Service
public class CheckpointService {

    // En un entorno real, reemplaza este Map con una solución persistente (DB, archivo, etc.)
    private final Map<TopicPartition, Long> checkpointMap = new HashMap<>();

    public void guardarOffset(String topic, int partition, long offset) {
        TopicPartition tp = new TopicPartition(topic, partition);
        checkpointMap.put(tp, offset);
        // Aquí se podría agregar lógica para persistir el offset en una base de datos
        System.out.println("Checkpoint guardado: " + topic + " - Partición: " + partition + " - Offset: " + offset);
    }

    public Map<TopicPartition, Long> cargarUltimoOffset() {
        // En un entorno real, se debería recuperar el offset desde el almacenamiento persistente
        return new HashMap<>(checkpointMap);
    }
}
