package com.hussam.lhc;

import com.hussam.lhc.database.DatabaseManager;
import com.hussam.lhc.model.ParticleEvent;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@TestConfiguration
@Profile("test")
public class UnifiedTestConfig {

    @Bean
    @Primary
    public DatabaseManager testDatabaseManager() {
        return new InMemoryTestDatabase();
    }

    public static class InMemoryTestDatabase extends DatabaseManager {
        private final List<ParticleEvent> inMemoryDb = new ArrayList<>();

        public InMemoryTestDatabase() {
            for (int i = 0; i < 20; i++) {
                ParticleEvent event = new ParticleEvent(
                    java.util.UUID.randomUUID(),
                    Instant.now().minusSeconds(i * 1000),
                    50.0 + i * 10.0,
                    com.hussam.lhc.model.ParticleType.ELECTRON,
                    i % 2 == 0
                );
                inMemoryDb.add(event);
            }
        }

        @Override
        public void insertBatch(List<ParticleEvent> events) {
            inMemoryDb.addAll(events);
        }

        @Override
        public List<ParticleEvent> queryHighEnergyEvents(int limit, double minEnergy) {
            List<ParticleEvent> filtered = new ArrayList<>();
            for (ParticleEvent event : inMemoryDb) {
                if (event.getEnergyGev() >= minEnergy) {
                    filtered.add(event);
                    if (filtered.size() >= limit) {
                        break;
                    }
                }
            }
            return filtered;
        }

        @Override
        public long countHighEnergyEvents(double minEnergy) {
            long count = 0;
            for (ParticleEvent event : inMemoryDb) {
                if (event.getEnergyGev() >= minEnergy) {
                    count++;
                }
            }
            return count;
        }

        @Override
        public DatabaseStatistics getStatistics() {
            if (inMemoryDb.isEmpty()) {
                return new DatabaseStatistics(0, 0.0, 0.0, 0.0, 0);
            }

            double totalEnergy = 0;
            double maxEnergy = Double.MIN_VALUE;
            double minEnergy = Double.MAX_VALUE;
            long highEnergyCount = 0;

            for (ParticleEvent event : inMemoryDb) {
                totalEnergy += event.getEnergyGev();
                maxEnergy = Math.max(maxEnergy, event.getEnergyGev());
                minEnergy = Math.min(minEnergy, event.getEnergyGev());
                if (event.getEnergyGev() >= 50.0) {
                    highEnergyCount++;
                }
            }

            return new DatabaseStatistics(
                inMemoryDb.size(),
                totalEnergy / inMemoryDb.size(),
                maxEnergy,
                minEnergy,
                highEnergyCount
            );
        }

        @Override
        public void shutdown() {
            inMemoryDb.clear();
        }

        @Override
        public int getQueueDepth() {
            return 0;
        }
    }
}
