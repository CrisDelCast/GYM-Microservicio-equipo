package icesi.microservicio_equipo.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfiguracion {

    @Bean
    public TopicExchange intercambioEquipos() {
        return new TopicExchange("equipos.intercambio");
    }

    @Bean
    public Queue colaActualizacionCantidad() {
        return new Queue("equipos.cantidad", true);
    }

    @Bean
    public Queue colaCambioEstado() {
        return new Queue("equipos.estado", true);
    }

    @Bean
    public Binding enlaceCantidad(Queue colaActualizacionCantidad, TopicExchange intercambioEquipos) {
        return BindingBuilder.bind(colaActualizacionCantidad).to(intercambioEquipos).with("equipos.cantidad");
    }

    @Bean
    public Binding enlaceEstado(Queue colaCambioEstado, TopicExchange intercambioEquipos) {
        return BindingBuilder.bind(colaCambioEstado).to(intercambioEquipos).with("equipos.estado");
    }
}
