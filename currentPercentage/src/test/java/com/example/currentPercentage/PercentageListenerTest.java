package com.example.currentPercentage;

import com.example.currentPercentage.data.PercentageMessage;
import com.example.currentPercentage.entities.CurrentPercentage;
import com.example.currentPercentage.message.PercentageListener;
import com.example.currentPercentage.repository.CurrentPercentaceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.mockito.Mockito.*;

class PercentageListenerTest {

    private CurrentPercentaceRepository repository;
    private PercentageListener listener;

    // wird vor jedem einzelnen test automatisch ausgeführt
    // initialisiert mocks und testobjekt
    @BeforeEach
    void setUp() {
        // mock = datenbankplatzhalter für test
        repository = mock(CurrentPercentaceRepository.class);
        listener = new PercentageListener(repository);
    }

    // prüfe, ob eine nachricht korrekt verarbeitet und gespeichert wird
    @Test
    void receive_shouldSavePercentageData() {
        // baue eine testnachricht mit gültiger zeit
        LocalDateTime hour = LocalDateTime.of(2025, 7, 3, 10, 0);
        PercentageMessage msg = new PercentageMessage();
        msg.setHour(hour);
        msg.setGridPortion(0.6);
        msg.setCommunityDepleted(0.4);

        // simuliere: kein eintrag für diese stunde vorhanden
        when(repository.findById(hour)).thenReturn(Optional.empty());

        // führe methode aus
        listener.receive(msg);

        // prüfe, ob gespeichert wurde
        verify(repository, times(1)).save(any(CurrentPercentage.class));
    }
}
