/*
 *
 * CDDL HEADER START
 *
 * The contents of this file are subject to the terms of the
 * Common Development and Distribution License, Version 1.0 only
 * (the "License"). You may not use this file except in compliance
 * with the License.
 *
 * You can obtain a copy of the license at license/ESCIDOC.LICENSE
 * or http://www.escidoc.de/license.
 * See the License for the specific language governing permissions
 * and limitations under the License.
 *
 * When distributing Covered Code, include this CDDL HEADER in each
 * file and include the License file at license/ESCIDOC.LICENSE.
 * If applicable, add the following below this CDDL HEADER, with the
 * fields enclosed by brackets "[]" replaced with your own identifying
 * information: Portions Copyright [yyyy] [name of copyright owner]
 *
 * CDDL HEADER END
 */
/*
 * Copyright 2006-2007 Fachinformationszentrum Karlsruhe Gesellschaft
 * für wissenschaftlich-technische Information mbH and Max-Planck-
 * Gesellschaft zur Förderung der Wissenschaft e.V.
 * All rights reserved. Use is subject to license terms.
 */
package de.mpg.imeji.presentation.beans;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.event.AjaxBehaviorEvent;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import de.mpg.imeji.logic.storage.util.MediaUtils;
import de.mpg.imeji.presentation.util.BeanHelper;
import de.mpg.imeji.presentation.util.PropertyReader;

/**
 * JavaBean managing the imeji configuration which is made directly by the
 * administrator from the web (i.e. not in the property file)
 * 
 * @author saquet (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 */
@ManagedBean(name = "Configuration")
@ApplicationScoped
public class ConfigurationBean {
	/**
	 * Enumeration of available configuration
	 * 
	 * @author saquet (initial creation)
	 * @author $Author$ (last modification)
	 * @version $Revision$ $LastChangedDate$
	 */
	private enum CONFIGURATION {
		SNIPPET, CSS_DEFAULT, CSS_ALT, MAX_FILE_SIZE, FILE_TYPES, STARTPAGE_HTML, DATA_VIEWER_FORMATS, DATA_VIEWER_URL;
	}

	private Properties config;
	private File configFile;
	private FileTypes fileTypes;
	private String lang = "en";
	private final static Logger logger = Logger
			.getLogger(ConfigurationBean.class);
	// A list of predefined file types, which is set when imeji is initialized
	private final static String predefinedFileTypes = "[Image@en,Bilder@de=jpg,jpeg,tiff,tiff,jp2,pbm,gif,png,psd][Video@en,Video@de=wmv,swf,rm,mp4,mpg,m4v,avi,mov.asf,flv,srt,vob][Audio@en,Ton@de=aif,iff,m3u,m4a,mid,mpa,mp3,ra,wav,wma][Document@en,Dokument@de=doc,docx,odt,pages,rtf,tex,rtf,bib,csv,ppt,pps,pptx,key,xls,xlr,xlsx,gsheet,nb,numbers,ods,indd,pdf,dtx]";
	
	private String dataViewerUrl;
	
	private String dataViewerFormatListString;
	
	/**
	 * Constructor, create the file if not existing
	 * 
	 * @throws URISyntaxException
	 * @throws IOException
	 */
	public ConfigurationBean() throws IOException, URISyntaxException {
		
		configFile = new File(PropertyReader.getProperty("imeji.tdb.path") + "/conf.xml");
		if (!configFile.exists())
			configFile.createNewFile();
		readConfig();
	}

	/**
	 * Load the imeji configuration from a {@link File}
	 * 
	 * @param f
	 * @throws IOException
	 */
	private String readConfig() {
		try {
			config = new Properties();
			FileInputStream in = new FileInputStream(configFile);
			config.loadFromXML(in);
		} catch (Exception e) {
			logger.info("conf.xml can not be read, probably emtpy");
		}
		Object ft = config.get(CONFIGURATION.FILE_TYPES.name());
		if (ft == null) {
			this.fileTypes = new FileTypes(predefinedFileTypes);
			saveConfig();
		} else
			this.fileTypes = new FileTypes((String) ft);
		this.dataViewerUrl = (String) config.get(CONFIGURATION.DATA_VIEWER_URL.name());
		this.dataViewerFormatListString = (String) config.get(CONFIGURATION.DATA_VIEWER_FORMATS.name());
		return "";
	}

	/**
	 * Save the configuration in the config file
	 */
	public void saveConfig() {
		System.out.println("saveconfig");
		try {
			setProperty(CONFIGURATION.FILE_TYPES.name(), fileTypes.toString());
			setProperty(CONFIGURATION.DATA_VIEWER_URL.name(), dataViewerUrl);
			setProperty(CONFIGURATION.DATA_VIEWER_FORMATS.name(), dataViewerFormatListString);
			config.storeToXML(new FileOutputStream(configFile),"imeji configuration File");
			BeanHelper.removeBeanFromMap(this.getClass());
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	public void getDataViewerFormatslistener(AjaxBehaviorEvent event) throws ClientProtocolException, IOException, JSONException{
		
		String connURL = dataViewerUrl + "/api/explain/formats";
		DefaultHttpClient httpclient = new DefaultHttpClient();
		HttpGet httpget = new HttpGet(connURL);
		HttpResponse resp = httpclient.execute(httpget);
		if(200 == resp.getStatusLine().getStatusCode())
		{
			HttpEntity entity = resp.getEntity();
	        if (entity != null) {
	            String retSrc = EntityUtils.toString(entity); 
	            JSONArray array = new JSONArray(retSrc);
	            String str = "";
	            int i=0;
	            while(i < array.length())
	            {
	            	str += array.get(i)+", ";
	            	i++;
	            }
	            setDataViewerFormatListString(str);
	            
	         }
		}

	}

	/**
	 * Set the value of a configuration property, and save it on disk
	 * 
	 * @param name
	 * @param value
	 */
	private void setProperty(String name, String value) {
		config.setProperty(name, value);
	}

	/**
	 * Set the Snippet in the configuration
	 * 
	 * @param str
	 */
	public void setSnippet(String str) {
		setProperty(CONFIGURATION.SNIPPET.name(), str);
	}

	/**
	 * Read the snippet from the configuration
	 * 
	 * @return
	 */
	public String getSnippet() {
		return (String) config.get(CONFIGURATION.SNIPPET.name());
	}

	public boolean isImageMagickInstalled() throws IOException,
			URISyntaxException {
		return MediaUtils.verifyImageMagickInstallation();
	}

	/**
	 * Set the url of the default CSS
	 * 
	 * @param url
	 */
	public void setDefaultCss(String url) {
		setProperty(CONFIGURATION.CSS_DEFAULT.name(), url);
	}

	/**
	 * Return the url of the default CSS
	 * 
	 * @return
	 */
	public String getDefaultCss() {
		return (String) config.get(CONFIGURATION.CSS_DEFAULT.name());
	}

	/**
	 * Set the url of the default CSS
	 * 
	 * @param url
	 */
	public void setAlternativeCss(String url) {
		setProperty(CONFIGURATION.CSS_ALT.name(), url);
	}

	/**
	 * Return the url of the default CSS
	 * 
	 * @return
	 */
	public String getAlternativeCss() {
		return (String) config.get(CONFIGURATION.CSS_ALT.name());
	}

	/**
	 * Set the url of the default CSS
	 * 
	 * @param url
	 */
	public void setUploadMaxFileSize(String size) {
		try {
			Integer.parseInt(size);
		} catch (Exception e) {
			setProperty(CONFIGURATION.MAX_FILE_SIZE.name(), "");
		}
		setProperty(CONFIGURATION.MAX_FILE_SIZE.name(), size);
	}

	/**
	 * Return the url of the default CSS
	 * 
	 * @return
	 */
	public String getUploadMaxFileSize() {
		String size = (String) config.get(CONFIGURATION.MAX_FILE_SIZE.name());
		if (size == null || size.equals(""))
			return "0";
		return size;
	}

	/**
	 * Get the type of Files
	 * 
	 * @return
	 */
	public FileTypes getFileTypes() {
		return this.fileTypes;
	}

	/**
	 * Set the type of Files
	 * 
	 * @param types
	 */
	public void setFileTypes(FileTypes types) {
		this.fileTypes = types;
	}

	/**
	 * Set the Property according to the selected lang
	 * 
	 * @param html
	 */
	public void setStartPageHTML(String html) {
		setProperty(CONFIGURATION.STARTPAGE_HTML.name() + "_" + lang, html);
	}

	/**
	 * Get the value according to the selected lang
	 * 
	 * @return
	 */
	public String getStartPageHTML() {
		return getStartPageHTML(lang);
	}

	/**
	 * Get the html snippet for a specified lang
	 * 
	 * @param lang
	 * @return
	 */
	public String getStartPageHTML(String lang) {
		String html = (String) config.get(CONFIGURATION.STARTPAGE_HTML.name()
				+ "_" + lang);
		return html != null ? html : "";
	}

	/**
	 * @return the lang
	 */
	public String getLang() {
		return lang;
	}

	/**
	 * @param lang
	 *            the lang to set
	 */
	public void setLang(String lang) {
		this.lang = lang;
	}

	/**
	 * @return the list of all formats supported by the data viewer service
	 */
	public String getDataViewerFormatListString() {
		 
		return dataViewerFormatListString;
	}

	/**
	 * @param str
	 * 
	 */
	public void setDataViewerFormatListString(String str) {
		this.dataViewerFormatListString = str;
		
	}

	/**
	 * true if the format is supported by the data viewer service
	 * 
	 * @param format
	 * @return
	 */
	public boolean isDataViewerSupportedFormats(String format) {
		return getDataViewerFormatListString().contains(format);
	}

	/**
	 * @return the url of the data viewer service
	 */
	public String getDataViewerUrl() {
		return dataViewerUrl;
	}

	/**
	 * @param str
	 * 
	 */
	public void setDataViewerUrl(String str) {
		dataViewerUrl = str;
		
	}
}
