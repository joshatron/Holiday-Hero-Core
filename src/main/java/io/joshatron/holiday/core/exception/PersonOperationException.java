package io.joshatron.holiday.core.exception;


import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class PersonOperationException extends RuntimeException {
    private PersonOperationExceptionReason reason;
}
