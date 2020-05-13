package io.joshatron.holiday.core.wishlist.exception;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@AllArgsConstructor
@Getter
@EqualsAndHashCode
public class WishlistException extends RuntimeException {
    private WishlistExceptionReason reason;
}
