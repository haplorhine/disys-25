package com.example.usageservice;

import com.example.usageservice.data.Producer;
import com.example.usageservice.message.UsageInListener;
import com.example.usageservice.message.UsageOutPublisher;
import com.example.usageservice.repository.UsageRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.mockito.Mockito.*;

class UsageInListenerTest {

    private UsageRepository mockRepository;
    private UsageOutPublisher mockUsageOutPublisher;
    private RabbitTemplate mockRabbitTemplate;
    private UsageInListener usageInListener;

    // wird vor jedem einzelnen test automatisch ausgeführt
    // initialisiert mocks und testobjekt
    @BeforeEach
    void setUp() {
        // mocks = platzhalter für abhängigkeiten
        mockRepository = mock(UsageRepository.class);
        mockUsageOutPublisher = mock(UsageOutPublisher.class);
        mockRabbitTemplate = mock(RabbitTemplate.class);

        // erstelle das objekt mit den mocks
        usageInListener = new UsageInListener(mockRabbitTemplate, mockRepository, mockUsageOutPublisher);
    }

    // prüft: PRODUCER-nachricht wird gespeichert und weitergegeben
    @Test
    void receiveMessage_shouldSaveAndPublish() {
        // testnachricht mit aktuellem zeitstempel
        LocalDateTime now = LocalDateTime.now();
        Producer producer = new Producer("PRODUCER", "COMMUNITY", 1.0, now);

        // simuliere: kein eintrag vorhanden
        when(mockRepository.findById(UsageInListener.truncateToHour(now)))
                .thenReturn(Optional.empty());

        // methode ausführen
        usageInListener.receiveMessage(producer);

        // prüfe, ob speicherung und veröffentlichung passiert sind
        verify(mockRepository).saveAndFlush(any());
        verify(mockUsageOutPublisher).publish(any());
    }
}
