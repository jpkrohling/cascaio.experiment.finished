package com.cascaio.backend.v1.control;

import org.hibernate.exception.ConstraintViolationException;

import javax.ejb.EJBException;
import javax.persistence.NoResultException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 * User: jpkrohling
 * Date: 7/10/13
 * Time: 5:41 PM
 */
@Provider
public class GeneralExceptionMapper implements ExceptionMapper<EJBException> {

	@Override
	public Response toResponse(EJBException exception) {

		if (containsCause(exception, ConstraintViolationException.class)) {
			return Response.status(Response.Status.CONFLICT).build();
		}

		if (containsCause(exception, NoResultException.class)) {
			return Response.status(Response.Status.NOT_FOUND).build();
		}

		if (containsCause(exception, IllegalArgumentException.class)) {
			return Response.status(Response.Status.BAD_REQUEST).build();
		}

		return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
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
