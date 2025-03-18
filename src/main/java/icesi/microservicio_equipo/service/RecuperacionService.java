package icesi.microservicio_equipo.service;

import java.time.Duration;
import java.util.Arrays;
import java.util.Map;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import jakarta.annotation.PostConstruct;

@Service
public class RecuperacionService {

    @Autowired
    private KafkaConsumer<String, String> consumer;

    @Autowired
    private CheckpointService checkpointService;

    /**
     * Inicia el procesamiento: se suscribe a los topics, carga el último checkpoint y reanuda el consumo.
     */
    public void iniciarProcesamiento() {
        // Suscribir el consumidor a los topics de interés. Por ejemplo, "equipo-topic" o los que correspondan.
        consumer.subscribe(Arrays.asList("topic-a", "topic-b"));
        
        // Realizar un poll inicial para obtener las asignaciones de particiones
        consumer.poll(Duration.ofMillis(100));

        // Cargar el último offset procesado
        Map<TopicPartition, Long> ultimoOffsetProcesado = checkpointService.cargarUltimoOffset();
        // Si hay offsets guardados, reposicionar el consumidor en cada partición
        for (Map.Entry<TopicPartition, Long> entry : ultimoOffsetProcesado.entrySet()) {
            consumer.seek(entry.getKey(), entry.getValue());
        }

        // Bucle principal para consumir mensajes
        while (true) {
            ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));
            for (ConsumerRecord<String, String> record : records) {
                procesarRecord(record);
                checkpointService.guardarOffset(record.topic(), record.partition(), record.offset() + 1);
            }
            // Commit manual de offsets a Kafka:
            consumer.commitSync();
        }
        
    }

    private void procesarRecord(ConsumerRecord<String, String> record) {
        // Aquí implementa la lógica real de procesamiento del mensaje.
        System.out.println("Procesando mensaje: " + record.value());
    }

    // Arranca el procesamiento en un hilo separado para no bloquear el inicio de la aplicación.
    @PostConstruct
    public void iniciar() {
        new Thread(() -> iniciarProcesamiento()).start();
    }
}
