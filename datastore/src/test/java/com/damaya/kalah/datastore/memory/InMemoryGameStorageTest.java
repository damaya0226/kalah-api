package com.damaya.kalah.datastore.memory;

import com.damaya.kalah.core.entities.domain.Game;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test of class {@link InMemoryGameStorage}
 * Created by Diego Amaya on 29/01/2020.
 */
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {InMemoryGameStorageTest.Config.class})
public class InMemoryGameStorageTest {

    @Autowired
    private InMemoryGameStorage storage;

    @Configuration
    public static class Config {
        @Bean
        public InMemoryGameStorage storage() {
            return new InMemoryGameStorage();
        }
    }

    @Test
    public void testSave() {
        Game game = Game.builder().id(UUID.randomUUID().toString()).build();
        assertThat(storage.save(game)).isEqualTo(game);
    }

    @Test
    public void testFindByIdWhenGameIsFound() {
        Game game = Game.builder().id(UUID.randomUUID().toString()).build();
        storage.save(game);
        assertThat(storage.findById(game.getId()))
                .isNotNull().isPresent().get().isNotNull().isEqualTo(game);
    }

    @Test
    public void testFindByIdWhenGameIsNotFound() {
        Game game = Game.builder().id(UUID.randomUUID().toString()).build();
        assertThat(storage.findById(game.getId()))
                .isNotNull().isNotPresent();
    }

}