package com.cascaio.backend.v1.control;

import org.hibernate.exception.ConstraintViolationException;

import javax.ejb.EJBException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 * User: jpkrohling
 * Date: 7/10/13
 * Time: 5:41 PM
 */
@Provider
public class DuplicateRecordExceptionMapper implements ExceptionMapper<EJBException> {

	@Override
	public Response toResponse(EJBException exception) {

		if (containsCause(exception, ConstraintViolationException.class)) {
			return Response.status(Response.Status.CONFLICT).build();
		}

		return Response.status(Response.Status.GONE).build();
	}

	private boolean containsCause(Exception e, Class<? extends Throwable> causeClazz) {
		Throwable cause = e.getCause();
		while (cause != null) {
			if (cause.getClass() == causeClazz) {
				return true;
			}
			cause = cause.getCause();
		}
		return false;
	}

}
