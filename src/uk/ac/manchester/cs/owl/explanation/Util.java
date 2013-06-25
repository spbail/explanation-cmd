package uk.ac.manchester.cs.owl.explanation;

import com.clarkparsia.pellet.owlapiv3.PelletReasonerFactory;
import org.semanticweb.owl.explanation.api.Explanation;
import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;
import org.semanticweb.owlapi.util.SimpleRenderer;
import org.semanticweb.owlapi.util.SimpleShortFormProvider;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 * Created by
 * User: Samantha Bail
 * Date: 25/06/2013
 * Time: 12:35
 * The University of Manchester
 */


public class Util {

    /**
     * @param time the time in minutes
     * @return time converted into nanoseconds
     */
    public static long minutesToNanoSeconds(long time) {
        return (long) (time * 60 * Math.pow(10, 9));
    }


    /**
     * @param reasonerName
     * @return
     * @throws Exception
     */
    public static OWLReasonerFactory getReasonerFactory(String reasonerName) throws Exception {
        ReasonerType r = ReasonerType.get(reasonerName);
        //System.out.println("Loaded reasoner: " + reasonerName);

        return getReasonerFactory(r);
    }

    /**
     * @param r
     * @return
     * @throws Exception
     */
    public static OWLReasonerFactory getReasonerFactory(ReasonerType r) throws Exception {
        switch (r) {
            case PELLET:
                return new PelletReasonerFactory();
//            case HERMIT:
//                return new Reasoner.ReasonerFactory();
//            case FACTPP:
//                return new FaCTPlusPlusReasonerFactory();
//            case JFACT:
//                return new JFactFactory();
            default:
                throw new Exception("Invalid reasoner name " + r.name());
        }

    }

    public enum ReasonerType {
        PELLET,
        FACTPP,
        JFACT,
        HERMIT;

        public static ReasonerType get(String s) {
            return ReasonerType.valueOf(s.toUpperCase());
        }
    }


    /**
     * prints an explanation
     * @param e the explanation to print
     */
    public static void print(Explanation<OWLAxiom> e) {
//        System.out.print("Entailment: ");
//        print(e.getEntailment());
        for (OWLAxiom ax : e.getAxioms()) {
            if (!ax.getAxiomType().equals(AxiomType.DECLARATION)) {
                System.out.print("    ");
                print(ax);
            }
        }
        System.out.println("");

    }

    /**
     * @param a the OWL axiom to be printed
     */
    public static void print(OWLAxiom a) {
        SimpleRenderer r = new SimpleRenderer();
        r.setShortFormProvider(new SimpleShortFormProvider());
        System.out.println(r.render(a.getAxiomWithoutAnnotations()));
    }

    /**
     * @param a the OWL axiom to be printed
     */
    public static String render(OWLAxiom a) {
        SimpleRenderer r = new SimpleRenderer();
        r.setShortFormProvider(new SimpleShortFormProvider());
        return r.render(a.getAxiomWithoutAnnotations());
    }


    /**
     * @param number any integer
     * @param length how many positions do we want to pad the string to?
     * @return number padded with zeros to make up 4 digits
     */
    public static String pad(int number, int length) {
        String s = Integer.toString(number);
        return ZEROS[length - s.length()] + s;
    }

    private static String[] ZEROS = {"", "0", "00", "000", "0000", "00000", "000000", "0000000",};


    /**
     * Fetch the entire contents of a text file, and return it in a String.
     * This style of implementation does not throw Exceptions to the caller.
     * @param aFile is a file which already exists and can be read.
     * @return the content of the file
     */
    public static String getFileContents(File aFile) {
        StringBuilder contents = new StringBuilder();

        try {
            //use buffering, reading one line at a time
            //FileReader always assumes default encoding is OK!
            BufferedReader input = new BufferedReader(new FileReader(aFile));
            try {
                String line = null; //not declared within while loop
                 /*
                 * readLine is a bit quirky :
                 * it returns the content of a line MINUS the newline.
                 * it returns null only for the END of the stream.
                 * it returns an empty String if two newlines appear in a row.
                 */
                while ((line = input.readLine()) != null) {
                    contents.append(line);
                    contents.append(System.getProperty("line.separator"));
                }
            } finally {
                input.close();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return contents.toString();
    }


}
