package javax.rfc7807.api;

public interface JsonValue extends JsonValueKt {

    static JsonValue of(String string) {
        return Providers.getSelected().newValue(string);
    }

    static JsonValue of(int mInt) {
        return Providers.getSelected().newValue(mInt);
    }

    static JsonValue of(float mFloat) {
        return Providers.getSelected().newValue(mFloat);
    }

    static JsonValue of(double mDouble) {
        return Providers.getSelected().newValue(mDouble);
    }

    static JsonValue of(boolean mBoolean) {
        return Providers.getSelected().newValue(mBoolean);
    }

    static JsonValue of(Object mObject) {
        return Providers.getSelected().newValue(mObject);
    }
}

