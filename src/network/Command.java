package network;

public enum Command {

    PING(false),
    LOGIN(true),
    REGISTER(true),
    BYE(false),
    ADD_ADDRESS(true),
    GET_ADDRESS(false),
    UPDATE_ADDRESS(true),
    REMOVE_ADDRESS(true),
    GET_ALL_ADDRESSES(false),
    ADD_ARTIST(true),
    GET_ARTIST(false),
    UPDATE_ARTIST(true),
    REMOVE_ARTIST(true),
    GET_ALL_ARTISTS(false),
    ADD_ART_PRODUCT(true),
    GET_ART_PRODUCT(false),
    UPDATE_ART_PRODUCT(true),
    REMOVE_ART_PRODUCT(true),
    GET_ALL_ART_PRODUCTS(false),
    ADD_DIGITAL_PRODUCT(true),
    GET_DIGITAL_PRODUCT(false),
    UPDATE_DIGITAL_PRODUCT(true),
    REMOVE_DIGITAL_PRODUCT(true),
    GET_ALL_DIGITAL_PRODUCTS(false),
    ADD_PHYSICAL_PRODUCT(true),
    GET_PHYSICAL_PRODUCT(false),
    UPDATE_PHYSICAL_PRODUCT(true),
    REMOVE_PHYSICAL_PRODUCT(true),
    GET_ALL_PHYSICAL_PRODUCTS(false),
    ADD_DIMENSION(true),
    GET_DIMENSION(false),
    UPDATE_DIMENSION(true),
    REMOVE_DIMENSION(true),
    GET_ALL_DIMENSIONS(false),
    ADD_SUBSCRIBER(true),
    GET_SUBSCRIBER(false),
    UPDATE_SUBSCRIBER(true),
    REMOVE_SUBSCRIBER(true),
    GET_ALL_SUBSCRIBERS(false),
    ADD_SUBSCRIPTION(true),
    GET_SUBSCRIPTION(false),
    UPDATE_SUBSCRIPTION(true),
    REMOVE_SUBSCRIPTION(true),
    GET_ALL_SUBSCRIPTIONS(false),
    GET_USER(false),
    UPDATE_USER(true),
    REMOVE_USER(true),
    GET_ALL_USERS(false),
    ADD_PURCHASE(true);

    private final boolean requiresLock;

    Command(boolean requiresLock) {
        this.requiresLock = requiresLock;
    }

    public boolean requiresLock() {
        return requiresLock;
    }
}
