package org.common.audit.columns;

import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;

import org.common.domain.ContextThreadLocal;
import org.common.utils.TimestampUtil;

/**
 * JPA listener to set audit column details whenever an entity is persisted.
 *
 * The listener interface for receiving auditColumns events. The class that is
 * interested in processing a auditColumns event implements this interface, and
 * the object created with that class is registered with a component using the
 * component's <code>addAuditColumnsListener<code> method. When
 * the auditColumns event occurs, that object's appropriate
 * method is invoked.
 * @see AuditColumnsEvent
 */
public class AuditColumnsListener {

	@PrePersist
	void onCreate(final Object entity) {

		if(entity instanceof AuditableColumns) {

			final AuditableColumns auditableColumns = (AuditableColumns) entity;
			AuditColumns auditColumns = auditableColumns.getAuditColumns();

			if(auditColumns == null) {
				auditColumns = new AuditColumns();
				auditableColumns.setAuditColumns(auditColumns);
			}

			if(null == auditColumns.getCreatedBy()) {
				auditColumns.setCreatedBy(ContextThreadLocal.get().getTransactionRequestedByUsername());
			}

			if(null == auditColumns.getCreatedDate()) {
				auditColumns.setCreatedDate(TimestampUtil.getCurentTimestamp());
			}
		}
	}

	@PreUpdate
	void onPersist(final Object entity) {

		if(entity instanceof AuditableColumns) {

			final AuditableColumns auditableColumns = (AuditableColumns) entity;

			AuditColumns auditColumns = auditableColumns.getAuditColumns();
			if(auditColumns == null) {
				auditColumns = new AuditColumns();
				auditableColumns.setAuditColumns(auditColumns);
			}

			auditColumns.setModifiedBy(ContextThreadLocal.get().getTransactionRequestedByUsername());
			auditColumns.setModifiedDate(TimestampUtil.getCurentTimestamp());
		}
	}
}