package com.sqgf.web.utils;

import java.io.IOException;
import java.util.Iterator;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

public class FindOldDepend {

	public static void main(String[] args) throws DocumentException, ClientProtocolException, IOException {

		SAXReader reader = new SAXReader();
		Document document = reader.read("D:/mws/zhuanche/trunk/zhuanche_project/pom.xml");

		Element root = document.getRootElement();

		Element dependencies = root.element("dependencies");

		System.out.println(dependencies.elements().size());

		for (Iterator i = dependencies.elementIterator(); i.hasNext();) {
			Element dependency = (Element) i.next();

			String groupId = dependency.elementText("groupId");
			String artifactId = dependency.elementText("artifactId");

			CloseableHttpClient client = HttpClients.createDefault();
			HttpGet get = new HttpGet("https://mvnrepository.com/artifact/" + groupId + "/" + artifactId + "/");

			HttpResponse response = client.execute(get);
			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {

				String html = EntityUtils.toString(response.getEntity());
				if (html.indexOf("This artifact was moved to:") >= 0) {
					System.out.println(groupId + ":" + artifactId);
				}

			} else {
				System.out.println(groupId + ":" + artifactId + "error");
			}

		}

	}

}
