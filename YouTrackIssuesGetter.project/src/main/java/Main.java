import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import org.apache.commons.lang3.StringUtils;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class Main {

    public static void main(String[] args){
        int max = 5000, after = 0;

        String restUri = "https://youtrack.jetbrains.net/rest/issue/byproject/KT?filter=Bug+%23Submitted&after=" + after + "&max=" + max;
        Client client = Client.create();
        WebResource webResource = client.resource(restUri);
        ClientResponse response = webResource.accept("application/xml").get(ClientResponse.class);

        String output = response.getEntity(String.class);
        BufferedWriter bw;
        try {
            bw = new BufferedWriter
                    (new OutputStreamWriter(new FileOutputStream("C:\\Users\\saf-s\\Desktop\\Work_and_projects\\YouTrackIssuesGetter.project\\BugIssues.xml")
                            , StandardCharsets.UTF_8));
            bw.write(output);
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(Issues.class);
            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();

            File XMLfile = new File("C:\\Users\\saf-s\\Desktop\\Work_and_projects\\YouTrackIssuesGetter.project\\BugIssues.xml");

            Issues issues = (Issues) jaxbUnmarshaller.unmarshal(XMLfile);
            Issues bugIssues = new Issues();
            Field tmpField;
            List<Field> fieldList;
            Issue tmpIssue;
            List<Issue> issueList = new ArrayList<>();
            for (Issue issue : issues.getIssueList()){
                tmpField = new Field();
                fieldList = new ArrayList<>();
                tmpIssue = new Issue();
                String kotlinDesc = "";
                for (Field field : issue.getFieldList()){

                    String desc = field.getValue();
                    if (desc.contains("```kotlin")){
                        kotlinDesc = StringUtils.substringBetween(desc, "```kotlin", "```");
                        tmpField.setName(field.getName());
                        tmpField.setValue(kotlinDesc);
                    }
                }
                if ("".equals(kotlinDesc)) continue;
                fieldList.add(tmpField);
                tmpIssue.setId(issue.getId());
                tmpIssue.setEntityId(issue.getEntityId());
                tmpIssue.setFieldList(fieldList);
                issueList.add(tmpIssue);
            }
            bugIssues.setIssueList(issueList);

            Marshaller jaxbmarshaller = jaxbContext.createMarshaller();
            jaxbmarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            File finalFile = new File("C:\\Users\\saf-s\\Desktop\\Work_and_projects\\YouTrackIssuesGetter.project\\Issues.xml");

            jaxbmarshaller.marshal(bugIssues, finalFile);
        } catch (JAXBException e) {
            e.printStackTrace();
        }



        //WebTarget webTarget = client.target("https://youtrack.jetbrains.net/rest/issue/byproject/KT");
            //WebTarget issueWebTarget = webTarget.path("?filter=Bug+%23Open");
            //Invocation.Builder invocationBuilder = issueWebTarget.request(MediaType.APPLICATION_XML);
            //Issues response = invocationBuilder.get(Issues.class);
            /*try {
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
        }*/
    }

    /*private static void sendGet(URL mainAddress, String fileName){
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
    }*/
}


// Создаём класс Issue
// Создаём 2 объекта класса: ID и field
// ID - int, field - class
// Class field: name и value
// name - string, value - string
// Найти библиотеку для парсинга XML
