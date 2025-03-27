package com.jwoglom.pumpx2.pump.messages.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface HistoryLogProps {
    int opCode();
    int size() default 26;
    boolean usedByAndroid() default false;
    boolean referencedInAndroid() default false;
    boolean usedByTidepool() default false;
    boolean usedByTconnectsync() default false;
    String displayName() default "";
}
