package org.nightlabs.base.ui.errorreport.mantis;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPOutputStream;

import javax.imageio.ImageIO;

import org.apache.commons.httpclient.HostConfiguration;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpState;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.multipart.ByteArrayPartSource;
import org.apache.commons.httpclient.methods.multipart.FilePart;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.httpclient.methods.multipart.StringPart;
import org.apache.log4j.Logger;
import org.eclipse.core.net.proxy.IProxyData;
import org.eclipse.core.net.proxy.IProxyService;
import org.eclipse.swt.graphics.ImageData;
import org.nightlabs.base.ui.exceptionhandler.errorreport.CauseEffectThrowablePair;
import org.nightlabs.base.ui.exceptionhandler.errorreport.ErrorReport;
import org.nightlabs.base.ui.exceptionhandler.errorreport.IErrorReportSender;
import org.nightlabs.base.ui.util.ImageUtil;
import org.nightlabs.util.IOUtil;
import org.osgi.util.tracker.ServiceTracker;

/**
 * Base mantis error report sender. Tested and working with Mantis 1.0.8 and 1.0.7.
 * @author Niklas Schiffler <nick@nightlabs.de>
 * @author Marc Klinger - marc[at]nightlabs[dot]de
 */
public abstract class AbstractMantisErrorReportSender implements IErrorReportSender
{
	private static final Logger logger = Logger.getLogger(AbstractMantisErrorReportSender.class);

	/**
	 * Get the base mantis URL for all operations.
	 * @return teh base mantis URL.
	 */
	protected abstract String getMantisBaseUrl();

	/**
	 * Get the userName.
	 * @return the userName
	 */
	protected abstract String getUserName();

	/**
	 * Get the password.
	 * @return the password
	 */
	protected abstract String getPassword();

	/**
	 * Get the projectId.
	 * @return the projectId
	 */
	public abstract int getProjectId();

	/* (non-Javadoc)
	 * @see org.nightlabs.base.ui.exceptionhandler.errorreport.IErrorReportSender#sendErrorReport(org.nightlabs.base.ui.exceptionhandler.errorreport.ErrorReport)
	 */
	@Override
	public void sendErrorReport(final ErrorReport errorReport) throws IOException
	{
		ServiceTracker tracker = new ServiceTracker(
				Activator.getDefault().getBundle().getBundleContext(), IProxyService.class.getName(), null
		);
		tracker.open();
		try {
			final IProxyService proxyService = (IProxyService) tracker.getService();

			HttpClient client = new HttpClient() {
				@Override
				public int executeMethod(HostConfiguration hostconfig, HttpMethod method, HttpState state) throws IOException, HttpException {
					String host = method.getURI().getHost();
					String protocol = method.getURI().getScheme();
					IProxyData proxyData = null;
					if ("http".equalsIgnoreCase(protocol))
						proxyData = proxyService.getProxyDataForHost(host, IProxyData.HTTP_PROXY_TYPE);
					else if ("https".equalsIgnoreCase(protocol))
						proxyData = proxyService.getProxyDataForHost(host, IProxyData.HTTPS_PROXY_TYPE);

					if (proxyData != null && (proxyData.getHost() == null || proxyData.getHost().isEmpty())) {
						if (proxyData == null)
							logger.debug("sendErrorReport.HttpClient.executeMethod: Got empty proxyData for host=\"" + host + "\" and protocol=\"" + protocol + "\"! Discarding it.");

						proxyData = null;
					}

					if (logger.isDebugEnabled()) {
						if (proxyData == null)
							logger.debug("sendErrorReport.HttpClient.executeMethod: Will NOT use proxy for host=\"" + host + "\" and protocol=\"" + protocol + "\"!");
						else
							logger.debug("sendErrorReport.HttpClient.executeMethod: Will use proxy for host=\"" + host + "\" and protocol=\"" + protocol + "\": proxyData.host=\"" + proxyData.getHost() + "\" proxyData.port=\"" + proxyData.getPort() + "\" proxyData.userId=\"" + proxyData.getUserId() + "\"");
					}

					if (proxyData != null) {
						if (hostconfig == null)
							hostconfig = getHostConfiguration();

						hostconfig.setProxy(proxyData.getHost(), proxyData.getPort());
						if (proxyData.getUserId() != null && !proxyData.getUserId().isEmpty()) {
							if (state == null)
								state = getState();

							state.setProxyCredentials(
									new AuthScope(proxyData.getHost(), proxyData.getPort()),
									new UsernamePasswordCredentials(proxyData.getUserId(), proxyData.getPassword())
							);
						}
					}

					return super.executeMethod(hostconfig, method, state);
				}

				@Override
				public int executeMethod(HostConfiguration hostConfiguration, HttpMethod method) throws IOException, HttpException {
					// This method probably delegates to the first overloaded executeMethod(...) version. Hence we don't need to do anything here.
					// I did not test it, because this method is never called, but I assume that it works this way. Marco.
					return super.executeMethod(hostConfiguration, method);
				}

				@Override
				public int executeMethod(HttpMethod method) throws IOException, HttpException {
					// This method delegates to the first overloaded executeMethod(...) version. Hence we don't need to do anything here.
					// I tested it 2009-11-11. Marco.
					return super.executeMethod(method);
				}
			};

//			client.getHostConfiguration().setProxy("myproxyhost", 8080);
//		  client.getState().setProxyCredentials(
//		  		new AuthScope(host, port, realm),
//		  		new UsernamePasswordCredentials("my-proxy-username", "my-proxy-password"));


			doLogin(client);
			doSelectProject(client);
			PostMethod m = doPostIssue(client, errorReport);
			if(errorReport.getIsSendScreenShot()) {
				String response = m.getResponseBodyAsString();
				Integer issueId = extractIssueId(response);
				if(issueId != null) {
					if (errorReport.getErrorScreenshot() != null) // the screenshot might be null!
						doAttachScreenshot(client, issueId, errorReport.getErrorScreenshot());
				}
			}
		} finally {
			tracker.close();
		}
	}

	/**
	 * @return The issue id or <code>null</code> if no issue id could be found or there are multiple issue ids in the response
	 */
	protected Integer extractIssueId(String response)
	{
		try {
			Integer issueId = null;
			Pattern p = Pattern.compile("\\\"view\\.php\\?id=(\\d+)\\\"");
			Matcher matcher = p.matcher(response);
			while(matcher.find()) {
				if(issueId == null)
					issueId = Integer.parseInt(matcher.group(1));
				else
					return null;
			}
			return issueId;
		} catch(Throwable e) {
			return null;
		}
	}

	protected PostMethod doAttachScreenshot(HttpClient client, int issueId, ImageData errorScreenshot) throws IOException
	{
		if (client == null)
			throw new IllegalArgumentException("client must not null!");

		if (errorScreenshot == null)
			throw new IllegalArgumentException("errorScreenshot must not null!");

		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		ImageIO.write(
				ImageUtil.convertToAWT(errorScreenshot),
				"JPG", //$NON-NLS-1$
				bout);

		PostMethod m = new PostMethod(getMantisBaseUrl()+"/bug_file_add.php");
		Part[] parts = new Part[] {
				new StringPart("bug_id", String.valueOf(issueId)),
				//				new StringPart("max_file_size", "5000000"),
				new FilePart("file", new ByteArrayPartSource("screenshot.jpg", bout.toByteArray()), "image/jpeg", "UTF-8"),
		};
		m.setRequestEntity(new MultipartRequestEntity(parts, m.getParams()));
		client.executeMethod(m);
		return m;
	}

	protected PostMethod doPostIssue(HttpClient client, ErrorReport errorReport) throws IOException
	{
		StringBuilder summary = new StringBuilder();
		summary.append(errorReport.getFirstThrowable().getClass().getName());
		String exMessage = errorReport.getFirstThrowable().getMessage();
		if(exMessage != null && !exMessage.isEmpty()) {
			summary.append(": ");
			summary.append(exMessage);
		}
		List<CauseEffectThrowablePair> throwablePairList = errorReport.getThrowablePairList();
		StringBuilder description = new StringBuilder();
		StringBuilder strackTraces = new StringBuilder();
		String userComment = errorReport.getUserComment();
		if(userComment != null && !userComment.isEmpty()) {
			description.append(userComment);
			description.append("\n\n");
		}
		for (CauseEffectThrowablePair causeEffectThrowablePair : throwablePairList) {
			description.append(causeEffectThrowablePair.getEffectThrowable().getClass().getName());
			String message = causeEffectThrowablePair.getEffectThrowable().getMessage();
			if(message != null && !message.isEmpty()) {
				description.append(": ");
				description.append(message);
			}
			description.append("\n");
			strackTraces.append(ErrorReport.getExceptionStackTraceAsString(causeEffectThrowablePair.getEffectThrowable()));
			strackTraces.append("\n\n");
		}
		if(description.length() == 0)
			description.append("no information available");

		ByteArrayOutputStream bout;
		try {
			bout = new ByteArrayOutputStream();
			GZIPOutputStream gzout = new GZIPOutputStream(bout);
			errorReport.writeXml(gzout, IOUtil.CHARSET_NAME_UTF_8);
			gzout.close();
		} catch(IOException e) {
			bout = null;
		}

		// post issue
		PostMethod m = new PostMethod(getMantisBaseUrl()+"/bug_report.php");
		List<Part> parts = new ArrayList<Part>(8);
		parts.add(new StringPart("project_id", String.valueOf(getProjectId())));
		parts.add(new StringPart("severity","60")); // 60 = major
		parts.add(new StringPart("priority","30")); // 30 = normal
		// Since we submit to the "Automated submissions" project which is *private*,
		// it is not necessary to make the issue itself private, too. This only causes
		// additional work when moving the issue into another project and often is
		// forgotten completely (leaving it "private" and thus nobody can see it even after
		// moving it). Therefore, I now submit them with view_state "public".
		// => https://www.jfire.org/modules/bugs/view.php?id=1194
		// Marco. 2009-07-20.
//		parts.add(new StringPart("view_state","50")); // 10 = public (default) / 50 = private
		parts.add(new StringPart("reproducibility", "100")); // 100 = N/A
		parts.add(new StringPart("summary", summary.toString()));
		parts.add(new StringPart("description", description.toString()));
		parts.add(new StringPart("additional_info", strackTraces.toString()));
		if(bout != null)
			parts.add(new FilePart("file", new ByteArrayPartSource("errorreport.xml.gz", bout.toByteArray()), "application/x-gzip", "UTF-8"));
		m.setRequestEntity(new MultipartRequestEntity(parts.toArray(new Part[parts.size()]), m.getParams()));
		client.executeMethod(m);
		return m;
	}

	protected PostMethod doSelectProject(HttpClient client) throws IOException
	{
		//select mantis project
		PostMethod m = new PostMethod(getMantisBaseUrl()+"/set_project.php");
		m.setParameter("ref", "bug_report_page.php");
		m.setParameter("project_id", String.valueOf(getProjectId()));
		client.executeMethod(m);
		return m;
	}

	protected PostMethod doLogin(HttpClient client) throws IOException
	{
		// mantis login
		PostMethod m = new PostMethod(getMantisBaseUrl()+"/login.php");
		m.addParameter("username", getUserName());
		m.addParameter("password", getPassword());
		client.executeMethod(m);
		return m;
	}
}
