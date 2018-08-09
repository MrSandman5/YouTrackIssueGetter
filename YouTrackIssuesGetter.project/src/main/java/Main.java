import org.w3c.dom.*;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.net.ssl.HttpsURLConnection;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.*;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class Main {
    public static void main(String[] args){

        try {
            URL mainAddress = new URL("https://youtrack.jetbrains.net/rest/issue/byproject/KT?filter=Bug+%23Open&max=100");
            sendGet(mainAddress, "BugIssues");

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setValidating(true);
            factory.setNamespaceAware(false);

            DocumentBuilder docBuilder = factory.newDocumentBuilder();
            DefaultHandler dh = new DefaultHandler();
            docBuilder.setErrorHandler(dh);
            Document doc = docBuilder.parse("BugIssues.xml");
            doc.getDocumentElement().normalize();

            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            System.out.println("Enter Kotlin code: ");
            String kotlinCode = br.readLine();
            PrintWriter pw = new PrintWriter("BugIssues.txt", "UTF-8");

            NodeList issues = doc.getElementsByTagName("issue");
            int rootLength = issues.getLength();
            for (int i=0; i<rootLength; i++) {
                Node issue = issues.item(i);
                if (issue.getNodeType() == Node.ELEMENT_NODE) {
                    Element eElement = (Element) issue;
                    NodeList fields = eElement.getElementsByTagName("field");
                    int fieldsCount = fields.getLength();
                    for (int j=0; j<fieldsCount; j++){
                        Node field = fields.item(j);
                        if (field.getNodeType() == Node.ELEMENT_NODE) {
                            Element elem = (Element) field;
                            String code = elem.getElementsByTagName("value").item(0).getTextContent();
                            if (elem.getAttribute("xsi:type").equals("SingleField")
                                    && elem.getAttribute("name").equals("description")
                                    && code.contains(kotlinCode)) {
                                pw.println("Current element : " + issue.getNodeName());
                                pw.println("id : " + eElement.getAttribute("id"));
                                pw.println("entityId : " + eElement.getAttribute("entityId"));
                                pw.println("code : " + code + "\n");
                            }
                        }
                    }
                }
            }
            pw.close();

        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void sendGet(URL mainAddress, String fileName){
        try {
            HttpsURLConnection conn = (HttpsURLConnection) mainAddress.openConnection();
            conn.setRequestMethod("GET");

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(conn.getInputStream()));
            String inputLine;
            StringBuilder result = new StringBuilder();

            while ((inputLine = in.readLine()) != null) {
                result.append(inputLine + " \n");
            }
            in.close();
            PrintWriter pw = new PrintWriter(fileName + ".xml", "UTF-8");
            pw.print(result.toString());
            pw.close();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
