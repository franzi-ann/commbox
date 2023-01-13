package de.fomwebtech.email;


/*Diese Klasse legt die Metadaten für ein File an. Die Metadaten umfassen den Namen der Datei, die ID des Dokumententyps, die E-Mail-Adresse
 * des Kunden, den Betreff, den Vorschau-Inhalt, das Standard-Löschdatum in Millisekunden seit dem epoch (01.01.1970) und das Dokumentendatum
 * in Millisekunden seit dem epoch.  */

public class FileMetaData {
	private final String filename;
	private final int documentTypeId;
	private String customerEmail = null;
	private String subject = null;
	private String previewContent = null;
	private long defaultDeletionEpochMS = -1;
	private long documentDateEpochMS = -1;

	public FileMetaData(String filename, int documentTypeId) {
		this.documentTypeId=documentTypeId;
		this.filename=filename;
		// TODO Auto-generated constructor stub
	}

	public String getFilename() {
		return filename;
	}

	public long getDocumentTypeId() {
		return documentTypeId;
	}


	public String getCustomerEmail() {
		return customerEmail;
	}

	public void setCustomerEmail(String customerEmail) {
		this.customerEmail = customerEmail;
	}

	
	public long getDefaultDeletionEpochMS() {
		return defaultDeletionEpochMS;
	}

	public void setDefaultDeletionEpochMS(long defaultDeletionEpochMS) {
		this.defaultDeletionEpochMS = defaultDeletionEpochMS;
	}

/*Diese Methode prüft, ob die E-Mail-Adresse des Kunden nicht null ist */
	
	public boolean isValidConstraint() {
		return  customerEmail != null ;
	}


	public long getDocumentDateEpochMS() {
		return documentDateEpochMS;
	}

	public void setDocumentDateEpochMS(long documentDateEpochMS) {
		this.documentDateEpochMS = documentDateEpochMS;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getPreviewContent() {
		return previewContent;
	}

	public void setPreviewContent(String previewContent) {
		this.previewContent = previewContent;
	}
}
