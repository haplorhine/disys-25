package com.example.scheduled;

import com.example.scheduled.data.Producer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TimedMessengerTest {

    private RabbitTemplate rabbitTemplate;
    private TimedMessenger timedMessenger;

    // wird vor jedem einzelnen test automatisch ausgeführt
    // initialisiert mocks und testobjekt
    @BeforeEach
    void setUp() {
        // mock = ein platzhalter für rabbitTemplate, der keine echte verbindung aufbaut
        rabbitTemplate = mock(RabbitTemplate.class);

        // testet echte klasse ohne überschreiben – sunshineFactor bleibt wie er ist
        timedMessenger = new TimedMessenger(rabbitTemplate);
    }

    // prüfe, ob eine PRODUCER-nachricht an die queue geschickt wird
    @Test
    void sendProducer_shouldSendMessage() {
        timedMessenger.sendProducer();

        // kontrolliere, ob eine nachricht an "usage_in" gesendet wurde
        ArgumentCaptor<Producer> captor = ArgumentCaptor.forClass(Producer.class);
        verify(rabbitTemplate, times(1)).convertAndSend(eq("usage_in"), captor.capture());

        Producer msg = captor.getValue();
        assertNotNull(msg);
        assertEquals("PRODUCER", msg.getType());
        assertEquals("COMMUNITY", msg.getAssociation());
        assertNotNull(msg.getDatetime());
        assertTrue(msg.getKwh() > 0); // genauer test der kwh nicht nötig
    }
}
