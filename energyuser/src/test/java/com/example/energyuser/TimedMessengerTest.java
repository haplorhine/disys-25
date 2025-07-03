package com.example.energyuser;

import com.example.energyuser.data.Producer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.time.LocalDateTime;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TimedMessengerTest {

    private RabbitTemplate rabbitTemplate;
    private TimedMessenger timedMessenger;

    // wird vor jedem einzelnen test automatisch ausgeführt
    // initialisiert mocks und testobjekt
    @BeforeEach
    void setUp() {
        // mock = ein platzhalter-objekt, das sich wie ein echtes objekt verhält
        // führt aber keine echte logik aus – nur zum testen gedacht
        rabbitTemplate = mock(RabbitTemplate.class);
        timedMessenger = new TimedMessenger(rabbitTemplate);
    }

    // prüfe, ob eine USER-nachricht korrekt an die queue geschickt wird
    // @Test markiert eine methode als test – sie wird automatisch vom testframework ausgeführt
    @Test
    void sendConsumer_shouldSendProducerMessageToQueue() {
        timedMessenger.sendConsumer();

        // ArgumentCaptor fängt das übergebene objekt ab, damit wir es genau untersuchen können
        // damit prüfen wir, ob typ, kwh, zeitstempel usw. richtig gesetzt wurden
        ArgumentCaptor<Producer> captor = ArgumentCaptor.forClass(Producer.class);
        verify(rabbitTemplate, times(1)).convertAndSend(eq("usage_in"), captor.capture());

        Producer msg = captor.getValue();
        assertNotNull(msg);
        assertEquals("USER", msg.getType());
        assertEquals("COMMUNITY", msg.getAssociation());
        assertNotNull(msg.getDatetime());
        assertTrue(msg.getKwh() > 0);
    }
}
