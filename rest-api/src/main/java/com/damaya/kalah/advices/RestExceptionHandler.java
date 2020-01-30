package com.damaya.kalah.advices;

import com.damaya.kalah.core.entities.exceptions.GameAlreadyFinishedException;
import com.damaya.kalah.core.entities.exceptions.GameNotFoundException;
import com.damaya.kalah.core.entities.exceptions.InvalidMoveException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@ControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

    private static final Logger LOCAL_LOGGER = LoggerFactory.getLogger(RestExceptionHandler.class);

    @ExceptionHandler({GameNotFoundException.class})
    public void handleGameNotFoundException(GameNotFoundException e, HttpServletResponse response) throws IOException {
        LOCAL_LOGGER.debug(e.getMessage());
        response.sendError(HttpStatus.NOT_FOUND.value());
    }

    @ExceptionHandler({InvalidMoveException.class})
    public void handleInvalidMoveException(InvalidMoveException e, HttpServletResponse response) throws IOException {
        LOCAL_LOGGER.debug(e.getMessage());
        response.sendError(HttpStatus.BAD_REQUEST.value());
    }

    @ExceptionHandler({GameAlreadyFinishedException.class})
    public void handleInvalidMoveException(GameAlreadyFinishedException e, HttpServletResponse response) throws IOException {
        LOCAL_LOGGER.debug(e.getMessage());
        response.sendError(HttpStatus.CONFLICT.value());
    }
}
