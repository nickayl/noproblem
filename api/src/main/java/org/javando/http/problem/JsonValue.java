package org.javando.http.problem;

import java.util.Date;

public interface JsonValue extends JsonValueKt {

    static JsonString of(String string) {
        return Companion.getProvider().newValue(string);
    }

    static JsonDate of(Date value) {
        return Companion.getProvider().newDateValue(value);
    }

    static JsonDate ofDate(String string) {
        return Companion.getProvider().newDateValue(string);
    }

    static JsonInt of(int mInt) {
        return Companion.getProvider().newValue(mInt);
    }

    static JsonFloat of(float mFloat) {
        return Companion.getProvider().newValue(mFloat);
    }

    static JsonDouble of(double mDouble) {
        return Companion.getProvider().newValue(mDouble);
    }

    static JsonBoolean of(boolean mBoolean) {
        return Companion.getProvider().newValue(mBoolean);
    }

    static JsonValue of(Object mObject) {
        return Companion.getProvider().newValue(mObject);
    }
}

