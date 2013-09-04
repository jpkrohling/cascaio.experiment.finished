package com.cascaio.backend.v1.control;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * User: jpkrohling
 * Date: 6/22/13
 * Time: 11:16 AM
 */
@Retention(RUNTIME)
@Target({TYPE})
public @interface RequiresReferenceDataApplication {
}
