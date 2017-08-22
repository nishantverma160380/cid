package org.common.utils;

import org.common.constants.CommonConstants;
import org.common.domain.ContextInfo;
import org.common.domain.ContextThreadLocal;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;

/**
 * The Class HttpUtil contains methods for preparing the "context" header which could be
 * passed for each external service call.
 */
public class ContextInfoHttpUtil {

	/**
	 * Gets the HttpEntity with "context" header for GET call which do not have any entity to be passed as
	 * request body. Access context info object from "ContextThreadLocal".
	 *
	 * Usage:
	 *
	 * HttpEntity<?> entity = HttpUtil.getEntityWithHeaders();
	 * this.restTemplate.exchange(this.employeeServiceURL, HttpMethod.GET, entity, List.class).getBody();
	 *
	 * @return the HttpEntity with "context" header
	 */
	public static HttpEntity<?> getEntityWithHeaders() {
		final ContextInfo contextInfo = ContextThreadLocal.get();
		final Object entity = null;
		return getEntity(contextInfo, entity);
	}

	/**
	 * Gets the HttpEntity with "context" header for GET call which do not have any entity to be passed as
	 * request body.
	 *
	 * Usage:
	 *
	 * HttpEntity<?> entity = HttpUtil.getEntityWithHeaders(contextInfo);
	 * this.restTemplate.exchange(this.employeeServiceURL, HttpMethod.GET, entity, List.class).getBody();
	 *
	 * @param contextInfo the context info which needs to be included in header
	 * @return the HttpEntity with "context" header.
	 */
	public static HttpEntity<?> getEntityWithHeaders(final ContextInfo contextInfo) {
		final Object entity = null;
		return getEntity(contextInfo, entity);
	}

	/**
	 * Gets the HttpEntity with "context" header for POST call. Access context info object from "ContextThreadLocal".
	 *
	 * Usage:
	 *
	 * HttpEntity<?> entity = HttpUtil.getEntityWithHeaders(new Object());
	 * this.restTemplate.exchange(this.employeeServiceURL, HttpMethod.POST, entity, List.class).getBody();
	 *
	 * @param entityToSubmitInRequest the entity to submit in request body
	 * @return the HttpEntity with "context" header
	 */
	public static HttpEntity<?> getEntityWithHeaders(final Object entityToSubmitInRequest) {
		final ContextInfo contextInfo = ContextThreadLocal.get();
		return getEntity(contextInfo, entityToSubmitInRequest);
	}

	/**
	 * Gets the HttpEntity with "context" header for POST call.
	 *
	 * Usage:
	 *
	 * HttpEntity<?> entity = HttpUtil.getEntityWithHeaders(contextInfo, new Object());
	 * this.restTemplate.exchange(this.employeeServiceURL, HttpMethod.POST, entity, List.class).getBody();
	 *
	 * @param contextInfo the context info which needs to be included in header
	 * @param entityToSubmitInRequest the entity to submit in request body
	 * @return the HttpEntity with "context" header
	 */
	public static HttpEntity<?> getEntityWithHeaders(final ContextInfo contextInfo, final Object entityToSubmitInRequest) {
		return getEntity(contextInfo, entityToSubmitInRequest);
	}

	/**
	 * Gets the HttpEntity with "context" header for POST call. Accepts the operation name as
	 * input parameter for constructing the context header.
	 *
	 * Usage:
	 *
	 * HttpEntity<?> entity = HttpUtil.getEntityWithHeaders("create user", new Object());
	 * this.restTemplate.exchange(this.employeeServiceURL, HttpMethod.POST, entity, List.class).getBody();
	 *
	 * @param operation the operation name
	 * @param entityToSubmitInRequest the entity to submit in request
	 * @return the HttpEntity with "context" header
	 */
	public static HttpEntity<?> getEntityWithHeaders(final String operation, final Object entityToSubmitInRequest) {
		final ContextInfo contextInfo = ContextThreadLocal.get();
		contextInfo.setOperation(operation);
		return getEntity(contextInfo, entityToSubmitInRequest);
	}

	/**
	 * Gets the entity with headers.
	 *
	 * Usage:
	 *
	 * HttpEntity<?> entity = HttpUtil.getEntityWithHeaders("user", "create user", new Object());
	 * this.restTemplate.exchange(this.employeeServiceURL, HttpMethod.POST, entity, List.class).getBody();
	 *
	 * @param moduleName the module name
	 * @param operation the operation name
	 * @param entityToSubmitInRequest the entity to submit in request
	 * @return the HttpEntity with "context" header
	 */
	public static HttpEntity<?> getEntityWithHeaders(final String moduleName, final String operation, final Object entityToSubmitInRequest) {
		final ContextInfo contextInfo = ContextThreadLocal.get();
		contextInfo.setModuleName(moduleName);
		contextInfo.setOperation(operation);

		return getEntity(contextInfo, entityToSubmitInRequest);
	}

	/**
	 * The helper method to construct the Http Entity object with context header
	 *
	 * @param entityToSubmitInRequest the entity to submit in request body
	 * @param contextInfo the context info which needs to be included in header
	 * @return the HttpEntity with "context" header
	 */
	private static HttpEntity<?> getEntity(final ContextInfo contextInfo, final Object entityToSubmitInRequest) {
		final HttpHeaders headers = new HttpHeaders();
		headers.add(CommonConstants.CONTEXT_INFORMATION_REQUEST_PARAMETER, contextInfo.toString());

		HttpEntity<?> httpEntity = null;

		if(entityToSubmitInRequest == null) {
			httpEntity = new HttpEntity<String>(headers);
		} else {
			httpEntity = new HttpEntity<>(entityToSubmitInRequest, headers);
		}
		return httpEntity;
	}
}