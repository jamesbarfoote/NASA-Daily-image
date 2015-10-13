package nz.ac.vuw.ecs.nasadailyimage;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;


/**
 * The class implements the SAXParser handler to
 *      1. Handle each of the incoming events from the XML
 *      2. Instantiate and return an Image object by obtained
 *         content (the latest daily image)
 * <p/>
 * @author Aaron Chen
 * @version 1.0
 * @since 2015-08-31
 */
public class IotdHandler extends DefaultHandler {
    private boolean isTitle = false;
    private boolean isDescription = false;
    private boolean isItem = false;
    private boolean isDate = false;

    private Image image = null;
    private String currentValue = "";

    public IotdHandler() {
        image = new Image();

    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {

        if (localName.equals("enclosure") && image.getUrl() == null) {
            image.setUrl(attributes.getValue(0));
        }

        if (localName.startsWith("item")) {
            isItem = true;
        }
        if (isItem) {
            isTitle = localName.equals("title");

            isDescription = localName.equals("description");

            isDate = localName.equals("pubDate");
        }
    }

    @Override
    public void characters(char ch[], int start, int length) {
        currentValue = new String(ch, start, length);
        if (isTitle && image.getTitle() == null) {
            image.setTitle(currentValue);
        }
        if (isDescription && image.getDescription() == null) {
            image.setDescription(currentValue);
        }
        if (isDate && image.getDate() == null) {
            image.setDate(currentValue);
        }
    }

    public Image getImage() {
        return image;
    }

}
