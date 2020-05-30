package io.joshatron.holiday.core.exception;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@AllArgsConstructor
@Getter
@EqualsAndHashCode
public class ListException extends RuntimeException {
    private ListExceptionReason reason;
}
