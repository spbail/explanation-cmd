package uk.ac.manchester.cs.owl.explanation;

import org.apache.tools.ant.filters.StringInputStream;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.InputStream;

/**
 * Created by
 * User: Samantha Bail
 * Date: 15/02/2013
 * Time: 13:33
 * The University of Manchester
 */


public class OWLXMLInlineAxiomParser extends DefaultHandler {

    private org.semanticweb.owlapi.model.OWLClass subClass;
    private org.semanticweb.owlapi.model.OWLClass superClass;
    private OWLDataFactory df = OWLManager.getOWLDataFactory();

    public OWLSubClassOfAxiom parseAtomicSubsumptionAxiom(String s) {
        SAXParserFactory f = SAXParserFactory.newInstance();
        try {
            SAXParser parser = f.newSAXParser();
            InputStream in = new StringInputStream(s);
            parser.parse(in, this);
            if (subClass != null && superClass != null) {
                return df.getOWLSubClassOfAxiom(subClass, superClass);
            }

        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) {
        if (qName.equals("Class")) {
            IRI iri = IRI.create(attributes.getValue(0));
            if (subClass == null) {
                subClass = df.getOWLClass(iri);
            } else {
                superClass = df.getOWLClass(iri);
            }
        }
    }

}
