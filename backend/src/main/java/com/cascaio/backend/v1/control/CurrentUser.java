package com.cascaio.backend.v1.control;

import javax.inject.Qualifier;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * User: jpkrohling
 * Date: 6/22/13
 * Time: 12:24 PM
 */
@Qualifier
@Retention(RUNTIME)
@Target({FIELD, METHOD, PARAMETER})
public @interface CurrentUser {
}
