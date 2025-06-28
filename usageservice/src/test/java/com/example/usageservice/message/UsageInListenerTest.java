package com.example.usageservice.message;

import com.example.usageservice.data.Producer;
import com.example.usageservice.data.HourlyStats;
import com.example.usageservice.service.UsageStorageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.mockito.Mockito.*;

class UsageInListenerTest {

    private UsageStorageService mockStorageService;
    private UsageOutPublisher mockUsageOutPublisher;
    private UsageInListener usageInListener;

    @BeforeEach
    void setUp() {
        mockStorageService = mock(UsageStorageService.class);
        mockUsageOutPublisher = mock(UsageOutPublisher.class);
        usageInListener = new UsageInListener(mockStorageService, mockUsageOutPublisher);
    }

    @Test
    void receiveMessage_validProducer_shouldProcessAndPublish() {
        Producer producer = new Producer("PRODUCER", "test", 10.0, LocalDateTime.now());

        HourlyStats mockStats = mock(HourlyStats.class);
        when(mockStorageService.processMessage(producer)).thenReturn(mockStats);

        usageInListener.receiveMessage(producer);

        verify(mockStorageService).processMessage(producer);
        verify(mockUsageOutPublisher).publish(mockStats);
    }

    @Test
    void receiveMessage_invalidProducer_shouldNotProcessOrPublish() {
        Producer invalidProducer = new Producer();  // Felder null

        usageInListener.receiveMessage(invalidProducer);

        verifyNoInteractions(mockStorageService);
        verifyNoInteractions(mockUsageOutPublisher);
    }
}
