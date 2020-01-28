package com.damaya.kalah.core.interfaces;

import com.damaya.kalah.core.entities.Game;
import org.springframework.validation.annotation.Validated;

@Validated
public interface GameService {

    Game create();
}
