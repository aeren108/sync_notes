package editor;

import com.mysql.jdbc.StringUtils;
import javafx.scene.layout.StackPane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import org.w3c.dom.Document;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.StringWriter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Editor extends StackPane {
    final WebView browser = new WebView();
    final WebEngine webEngine = browser.getEngine();

    public Editor() {
        webEngine.setJavaScriptEnabled(true);
        webEngine.load(getClass().getResource("/style/panel.html").toExternalForm());
        webEngine.setUserStyleSheetLocation(getClass().getResource("/style/panelstyle.css").toString());
        setPrefHeight(5000);
        getChildren().add(browser);
    }

    public void setStyleFile(String url) {
        webEngine.setUserStyleSheetLocation(url);
    }

    public String getHTML() {
        String html;
        try {
            Document doc = webEngine.getDocument();
            DOMSource domSource = new DOMSource(doc);
            StringWriter writer = new StringWriter();
            StreamResult result = new StreamResult(writer);
            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer transformer = tf.newTransformer();
            transformer.transform(domSource, result);
            html =  writer.toString();

            Pattern p = Pattern.compile("<BODY>([^<]+)</BODY>");
            Matcher matcher = p.matcher(html);

            if (matcher.find())
                System.out.println(matcher.group(1));
            else
                System.out.println("Bulmadım");

            return html;
        } catch (TransformerException e) {
            e.printStackTrace();
        }

        return null;
    }

    public void setHTML(String html) {
        webEngine.loadContent(html);
    }
}
