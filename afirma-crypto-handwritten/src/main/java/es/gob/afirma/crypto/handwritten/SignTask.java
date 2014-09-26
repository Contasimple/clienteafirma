package es.gob.afirma.crypto.handwritten;

import java.io.ByteArrayInputStream;
import java.net.URI;
import java.net.URL;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import es.gob.afirma.core.misc.Base64;
import es.gob.afirma.signers.tsp.pkcs7.TsaParams;
import es.gob.afirma.signers.tsp.pkcs7.TsaRequestExtension;

/** Tarea completa de firma manuscrita.
 * @author Astrid Idoate. */
@XmlRootElement(namespace = "es.gob.afirma.crypto.handwritten")
public final class SignTask {

	@XmlElement(name = "tsaParams")
	private TsaParams tsaParams;

	@XmlElement(name = "retrieveUrl")
	private URL retrieveUrl;

	@XmlElement(name = "saveUrl")
	private URL saveUrl;

	@XmlElement(name = "saveUrlPostParam")
	private String saveUrlPostParam;

	@XmlElement(name = "cert")
	private String base64Cert;

	@XmlElementWrapper(name = "bioSigns")
	@XmlElement(name = "bioSign")
	private List<SingleBioSignData> bioSigns = new ArrayList<SingleBioSignData>(0);

	@XmlElement(name = "completeWithCriptoSign")
	private boolean completeWithCriptoSign;

	@XmlElement(name = "completeCriptoSignExtraParams")
	private Map<String, String> completeCriptoSignExtraParams;

	/** Construye una tarea de firma vac&iacute;a. */
	public SignTask() {
		// Vacio para la serializacion JAXB
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder("Tarea de firmas biometricas:\n"); //$NON-NLS-1$
		if (this.tsaParams != null) {
			sb.append("  Parametros de sello de tiempo: "); //$NON-NLS-1$
			sb.append(this.tsaParams.toString());
			sb.append('\n');
		}

		sb.append("  URL de descarga de PDF: "); //$NON-NLS-1$
		sb.append(this.retrieveUrl);
		sb.append('\n');

		sb.append("  URL de guardado de PDF: "); //$NON-NLS-1$
		sb.append(this.saveUrl);
		sb.append('\n');

		if (this.saveUrlPostParam != null) {
			sb.append("  Parametro para el POST de la URL de guardado de PDF: "); //$NON-NLS-1$
			sb.append(this.saveUrlPostParam);
			sb.append('\n');
		}

		sb.append("  Certificado de cifrado: "); //$NON-NLS-1$
		sb.append(this.base64Cert == null ? "No" : "Si"); //$NON-NLS-1$ //$NON-NLS-2$
		sb.append('\n');

		sb.append("  Firmas biometricas a realizar:\n"); //$NON-NLS-1$

		for (final SingleBioSignData sbsd : this.bioSigns) {
			sb.append("    "); //$NON-NLS-1$
			sb.append(sbsd.toString());
			sb.append('\n');
		}

		sb.append("  Completar el proceso con firma criptografica: "); //$NON-NLS-1$
		sb.append(this.completeWithCriptoSign ? "Si" : "No"); //$NON-NLS-1$ //$NON-NLS-2$
		sb.append('\n');

		if (this.completeWithCriptoSign) {
			sb.append("  Parametros de la firma criptografica: "); //$NON-NLS-1$
			sb.append(this.completeCriptoSignExtraParams);
		}

		return sb.toString();
	}

	/** Construye una tarea de firma.
	 * @param tsa Datos para el sellado de tiempo.
	 * @param retrieveUrlPdf URL para recuperar el PDF.
	 * @param saveUrlPdf URL para guardar el PDF (POST de un servicio Web).
	 * @param saveUrlPostParam Nombre del par&aacute;metro del POST del servicio Web
	 *                         de guarado del PDF donde hay que pasar este.
	 * @param certificate Certificado X.509 para el cifrado de la firma.
	 * @param bioSignsList Lista de firmas biom&eacute;tricas a hacer.
	 * @param complete <code>true</code> si el proceso debe finalizar con una firma
	 *                 criptogr&aacute;fica del operador, <code>false</code> en caso
	 *                 contrario.
	 * @param completeSignExtraParams par&aacute;metros adicionales de la firma final
	 *                                con certificado. */
	public SignTask(final TsaParams tsa,
					final URL retrieveUrlPdf,
					final URL saveUrlPdf,
					final String saveUrlPostParam,
					final String certificate,
					final List<SingleBioSignData> bioSignsList,
					final boolean complete,
					final Map<String, String> completeSignExtraParams) {

		this.tsaParams = tsa;
		this.retrieveUrl = retrieveUrlPdf;
		this.saveUrl = saveUrlPdf;
		this.saveUrlPostParam = saveUrlPostParam;
		this.base64Cert = certificate;
		this.bioSigns = bioSignsList;
		this.completeWithCriptoSign = complete;
		this.completeCriptoSignExtraParams = completeSignExtraParams != null ?
			completeSignExtraParams :
				new ConcurrentHashMap<String, String>();

	}

	/** Obtiene los datos para el sellado de tiempo.
	 * @return Datos para el sellado de tiempo. */
	public TsaParams getTsaParams() {
		return this.tsaParams;
	}

	/** Obtiene la URL para recuperar el PDF.
	 * @return URL para recuperar el PDF. */
	public URL getRetrieveUrl() {
		return this.retrieveUrl;
	}

	/** Obtiene la URL para guardar el PDF (POST de un servicio Web).
	 * @return URL para guardar el PDF (POST de un servicio Web). */
	public URL getSaveUrl() {
		return this.saveUrl;
	}

	/** Obtiene el nombre del par&aacute;metro del POST del servicio Web de guarado del PDF donde hay que pasar este.
	 * @return nombre del par&aacute;metro del POST del servicio Web de guarado del PDF donde hay que pasar este. */
	public String getSaveUrlPostParam() {
		return this.saveUrlPostParam;
	}

	/** Obtiene el certificado X.509 (en Base64) para el cifrado de la firma.
	 * @return certificado X.509 (en Base64) para el cifrado de la firma. */
	public String getCert() {
		return this.base64Cert;
	}

	/** Obtiene la lista de firmas biom&eacute;tricas a hacer.
	 * @return lista de firmas biom&eacute;tricas a hacer. */
	public List<SingleBioSignData> getBioSigns() {
		return this.bioSigns;
	}

	/** Obtiene <code>true</code> si el proceso debe finalizar con una firma criptogr&aacute;fica del operador,
	 * y en caso contrario se obtiene <code>false</code>.
	 * @return boolean indicando si el proceso debe finalizar con una firma criptogr&aacute;fica del operador. */
	public boolean isCompleteWithCriptoSign() {
		return this.completeWithCriptoSign;
	}

	/** Obtiene los par&aacute;metros adicionales de la firma final con certificado.
	 * @return Par&aacute;metros adicionales de la firma final con certificado. */
	public Map<String, String> getCompleteCriptoSignExtraParams() {
		return this.completeCriptoSignExtraParams;
	}

	/** Obtiene una tarea de firmas biom&eacute;tricas a partir de su XML de definici&oacute;n.
	 * @param xml XML de definici&oacute;n de la tarea de firmas biom&eacute;tricas.
	 *            Este XML puede prporcionarse como texto o codificado en Base64.
	 * @return Objeto de tarea de firmas biom&eacute;tricas.
	 * @throws JAXBException Si falla la deserializaci&oacute;n desde el XML. */
	public static SignTask getInstance(final String xml) throws JAXBException {
		if (xml == null) {
			throw new IllegalArgumentException("El XML de entrada no puede ser nulo"); //$NON-NLS-1$
		}
		byte[] rawXml;
		try {
			rawXml = Base64.decode(xml);
		}
		catch(final Exception e) {
			Logger.getLogger("es.gob.afirma").info("Los datos de entrada no estaban en Base64: " + e); //$NON-NLS-1$ //$NON-NLS-2$
		}
		rawXml = xml.getBytes();
		final Unmarshaller um = JAXBContext.newInstance(SignTask.class).createUnmarshaller();
		return (SignTask) um.unmarshal(new ByteArrayInputStream(rawXml));
	}

	public static void main(final String[] args) throws Exception {

		final TsaParams tsaParams = new TsaParams(
			true,
			"4.3.2.1", //$NON-NLS-1$
			new URI("http://kaka.ka"), //$NON-NLS-1$
			"user", //$NON-NLS-1$
			"password", //$NON-NLS-1$
			new TsaRequestExtension[] { new TsaRequestExtension("1.2.3.4", false, new byte[] { (byte) 0xff, (byte) 0xfa }) }, //$NON-NLS-1$
			"SHA-512", //$NON-NLS-1$
			new byte[] { 0x00, 0x01, 0x02, 0x03 },
			"p12password" //$NON-NLS-1$
		);

		final SingleBioSignData sbd = new SingleBioSignData(
			new SignerInfoBean("Astrid", "Idoate", "Gil", "12345678Z"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			"<html><body><h1>HOLA</h1></body></html>", //$NON-NLS-1$
			null,
			new Rectangle(10, 10, 100, 100),
			new Rectangle(50, 30, 200, 75)
		);
		final List<SingleBioSignData> signs = new ArrayList<SingleBioSignData>(1);
		signs.add(sbd);

		final Map<String, String> p = new ConcurrentHashMap<String, String>();
		p.put("clave", "valor"); //$NON-NLS-1$ //$NON-NLS-2$

		final SignTask st = new SignTask(
			tsaParams,
			new URL("http://www.google.com/"), //$NON-NLS-1$
			new URL("http://www.ibm.es"), //$NON-NLS-1$
			"data", //$NON-NLS-1$
			Base64.encode(
				((X509Certificate) CertificateFactory.getInstance("X.509").generateCertificate( //$NON-NLS-1$
					TestBioSigner.class.getResourceAsStream("/democert.cer") //$NON-NLS-1$
				)).getEncoded()
			),
			signs,
			true,
			p
		);

		final Marshaller m = JAXBContext.newInstance(SignTask.class).createMarshaller();
	    m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
	    m.marshal(st, System.out);

	    System.out.println();
	    System.out.println(st.toString());

	}

}