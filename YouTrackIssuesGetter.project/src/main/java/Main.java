import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

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

import static com.google.common.net.HttpHeaders.USER_AGENT;

class IdLexicComparator implements Comparator<String>{

    public int compare(String a, String b){

        return a.compareTo(b);
    }
}
class IdLengthComparator implements Comparator<String>{

    public int compare(String a, String b){

        if(a.length()> b.length())
            return 1;
        else if(a.length()< b.length())
            return -1;
        else
            return 0;
    }
}

public class Main {
    public static void main(String[] args){

        try {

            URL mainAddress = new URL("https://youtrack.jetbrains.net/rest/issue/byproject/KT?filter=");
            sendGet(mainAddress);

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setValidating(true);
            factory.setNamespaceAware(false);

            DocumentBuilder docBuilder = factory.newDocumentBuilder();
            DefaultHandler dh = new DefaultHandler();
            docBuilder.setErrorHandler(dh);

            Document doc = docBuilder.parse("BugIssues.xml");

            PrintWriter pw = new PrintWriter("BugIssues.txt", "UTF-8");

            pw.println("List of issues:");
            pw.println();

            Node root = doc.getDocumentElement();
            NodeList issues = root.getChildNodes();
            int rootLength = issues.getLength();
            Map<String, String> ids = new HashMap<>();
            for (int i=0; i<rootLength; i++) {
                Node issue = issues.item(i);
                NamedNodeMap atr=issue.getAttributes();
                ids.put(atr.item(1).toString(), atr.item(0).toString());
            }
            
            Comparator<String> idComp = new IdLengthComparator().thenComparing(new IdLexicComparator());
            Map<String, String> sortedIds = new TreeMap<>(idComp);
            sortedIds.putAll(ids);
            for (Map.Entry<String, String> elem : sortedIds.entrySet()) {
                pw.println(elem.getKey() + " " + elem.getValue());
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

    private static void sendGet(URL mainAddress){
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            System.out.println("Enter \'filter\' parameter");
            String filter = br.readLine();
            System.out.println("Enter \'max\' parameter");
            String max = br.readLine();
            br.close();

            URL address = new URL(mainAddress + URLEncoder.encode(filter, "UTF-8") + "&max=" + max);
            HttpURLConnection conn = (HttpURLConnection) address.openConnection();
            conn.setRequestMethod("GET");

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(conn.getInputStream()));
            String inputLine;
            StringBuilder result = new StringBuilder();

            while ((inputLine = in.readLine()) != null) {
                result.append(inputLine);
            }
            in.close();
            PrintWriter pw = new PrintWriter("BugIssues.xml", "UTF-8");
            pw.print(result.toString());
            pw.close();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
