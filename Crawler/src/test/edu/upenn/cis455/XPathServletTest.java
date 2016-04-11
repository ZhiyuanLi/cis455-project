package test.edu.upenn.cis455;

import java.io.BufferedReader;
import java.io.FileReader;
import org.w3c.dom.Document;

import edu.upenn.cis455.servlet.XPathServlet;
import junit.framework.TestCase;

public class XPathServletTest extends TestCase {

	XPathServlet servlet;

	/**
	 * create a servlet class
	 */
	protected void setUp() throws Exception {
		super.setUp();
		servlet = new XPathServlet();
	}

	/**
	 * test if a path refer to a local file
	 */
	public void testCheckLocal() {
		String path1 = "./src/test1.xml";
		String path2 = "./src/test2.html";
		assertFalse(servlet.checkLocal("http://localhost:8080/" + path1));
		assertFalse(servlet.checkLocal("http://localhost:8080/" + path2));
	}

	/**
	 * given path, read html file and convert content into Document type
	 */
	public void testGenerateHTMLDom() {
		String localPath = "test2.html";
		StringBuilder sb = new StringBuilder();
		FileReader fr;
		try {
			fr = new FileReader(localPath);
			BufferedReader br = new BufferedReader(fr);
			String nextLine = "";
			while ((nextLine = br.readLine()) != null) {
				sb.append(nextLine);
			}
			br.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		Document doc = servlet.generateHTMLDom(sb.toString());
		assertTrue(doc != null);
		assertEquals(doc.getDocumentElement().getNodeName(), "html");
		assertEquals(doc.getChildNodes().getLength(), 2);
		assertEquals(doc.getChildNodes().item(0).getNodeType(), 8);
		assertEquals(doc.getChildNodes().item(1).getNodeType(), 1);
	}

}
