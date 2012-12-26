package vn.edu.hcmut.cse.trafficdirection.main;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import vn.edu.hcmut.cse.trafficdirection.node.NodeGPS;

import android.content.Context;
import android.util.Log;

public class GPXReader {
	private String xmlfile = null;
	private InputStream xmlInputStream = null;
	public GPXHandler handler;
	public String IOError = "";
	public String SAXError = "";
	private String file;
	public Context context;

	public GPXReader(String file) {
		// instantiate our handler
		this.file = file;
		handler = new GPXHandler();
		handler.startDocument();

	}

	public void readXMLfromResource(Context _context) {
		context = _context;
	}

	public String getXMLString() {
		if (xmlInputStream != null) {
			return xmlfile;
		}
		return null;
	}

	public boolean parseStructure() throws ParserConfigurationException {
		try {
			// create the factory
			SAXParserFactory factory = SAXParserFactory.newInstance();
			// create a parser
			SAXParser parser = factory.newSAXParser();

			// create the reader (scanner)
			XMLReader xmlreader = parser.getXMLReader();

			// assign our handler
			xmlreader.setContentHandler(handler);

			File f = new File(file);
			Log.d("F", file);
			FileInputStream fis = new FileInputStream(f);
			xmlInputStream = fis;

			xmlreader.parse(new InputSource(xmlInputStream));
			handler.isLoaded = true;

		} catch (SAXException e) {
			SAXError = e.getMessage();
			return false;
		} catch (IOException e) {
			IOError = e.getMessage();
			return false;
		}

		return true;
	}

	class GPXHandler extends DefaultHandler {
		public boolean isLoaded;
		static final int FLAG_NONE = 0;
		static final int FLAG_NAME = 1;
		static final int FLAG_ELE = 2;
		static final int FLAG_TIME = 3;
		static final int FLAG_SPEED = 4;

		public ArrayList<NodeGPS> data;
		public String GPX_Name;
		public String GPX_Time;

		private NodeGPS node;

		private int flag = FLAG_NONE;
		private boolean isTrKPT = false;

		public GPXHandler() {
		}

		public void startDocument() {
			data = new ArrayList<NodeGPS>();
		}

		public void startElement(String uri, String name, String qName,
				Attributes atts) throws SAXException {
			if (qName.equals("name")) {
				flag = FLAG_NAME;
				return;
			} else if (qName.equals("trkpt")) {
				isTrKPT = true;
				node = new NodeGPS(atts.getValue("lat"), atts.getValue("lon"));
				return;
			} else if (qName.equals("ele")) {
				flag = FLAG_ELE;
				return;
			} else if (qName.equals("time")) {
				flag = FLAG_TIME;
				return;
			} else if (qName.equals("gpx10:speed")) {
				flag = FLAG_SPEED;
				return;
			}
		}

		public void endElement(String uri, String name, String qName) {
			if (qName.equals("trkpt")) {
				isTrKPT = false;
				data.add(node);
			}

			flag = FLAG_NONE;
		}

		public void characters(char ch[], int start, int length) {
			switch (flag) {
			case FLAG_NAME:
				GPX_Name = new String(ch, start, length);
				break;
			case FLAG_ELE:
				node.setEle(new String(ch, start, length));
				break;
			case FLAG_TIME:
				if (isTrKPT)
					node.setTime(new String(ch, start, length));
				else
					GPX_Time = new String(ch, start, length);
				break;
			case FLAG_SPEED:
				node.setSpeed(new String(ch, start, length));
				break;
			default:
				break;
			}
		}
	}
}
