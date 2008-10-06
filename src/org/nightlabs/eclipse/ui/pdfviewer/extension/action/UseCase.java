package org.nightlabs.eclipse.ui.pdfviewer.extension.action;

public class UseCase {
	private String useCaseId;
	public UseCase(String useCaseId) {
		if (useCaseId == null)
			throw new IllegalArgumentException("useCaseID must not be null!");

		this.useCaseId = useCaseId;
	}

	public String getUseCaseId() {
		return useCaseId;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((useCaseId == null) ? 0 : useCaseId.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;

		final UseCase other = (UseCase) obj;

		if (useCaseId == null)
			return other.useCaseId == null;

		return useCaseId.equals(other.useCaseId);
	}
}
