package io.joshatron.holiday.core.exception;

public enum ListExceptionReason {
    ITEM_NOT_FOUND,
    ITEM_ALREADY_ADDED,
    ALREADY_CLAIMED,
    CANT_CLAIM_OWN,
    CLAIMING_STARTED,
    USER_NOT_AUTHORIZED,
    ITEM_NOT_CLAIMED;
}
