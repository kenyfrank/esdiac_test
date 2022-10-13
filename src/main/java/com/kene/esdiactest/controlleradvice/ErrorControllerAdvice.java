package com.kene.esdiactest.controlleradvice;


import com.kene.esdiactest.config.ErrorResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ResponseStatusException;

@ControllerAdvice
public class ErrorControllerAdvice {

    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @ExceptionHandler(ChangeSetPersister.NotFoundException.class)
    public ResponseEntity<?> handle(ChangeSetPersister.NotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }


    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<?> handleUnauthorized(ChangeSetPersister.NotFoundException e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<?> handleBadRequest(BadRequestException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<?> handleResponseStatusException(ResponseStatusException e) {
        return ResponseEntity.status(e.getStatus()).body(e.getReason());
    }


    @ExceptionHandler(ErrorResponse.class)
    public ResponseEntity<?> handle(ErrorResponse e) {
        return ResponseEntity.status(e.getApiResponse().getCode()).body(e.getApiResponse());
    }

}
