package uk.ac.manchester.cs.owl.explanation;

import org.semanticweb.owl.explanation.api.Explanation;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;
import org.semanticweb.owlapi.model.OWLAxiom;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by
 * User: Samantha Bail
 * Date: 25/06/2013
 * Time: 12:34
 * The University of Manchester
 */


public class ExplanationGeneratorUI {

    public static void main(String[] args) throws Exception {
        if (args.length > 1) {

            Map<String, String> params = setParams(args);

            printParams(params);

            OWLReasonerFactory rf = Util.getReasonerFactory(params.get("-r"));
            int timeout = Integer.parseInt(params.get("-t"));
            int limit = Integer.parseInt(params.get("-l"));
            File ontFile = new File(params.get("-o"));
            OWLAxiom entailment = getEntailment(params.get("-e"));

            System.out.println("\n>> Computing explanations for " + Util.render(entailment));


            System.out.println("");


            ExplanationGeneratorEngine engine = new ExplanationGeneratorEngine(timeout, limit, rf);
            Set<Explanation<OWLAxiom>> explanations = engine.computeExplanations(ontFile, entailment);


            printExplanations(explanations, params.get("-d"));

            System.exit(0);


        } else {
            printUsage();
            System.exit(-1);
        }


    }

    private static void printUsage() {
        System.out.println("Usage: ");
        System.out.println("    -o ontology file");
        System.out.println("    -e entailment file");
        System.out.println("    -t timeout in seconds");
        System.out.println("    -l maximum number of justifications");
        System.out.println("    -d output directory");

    }

    private static void printParams(Map<String, String> params) {
        System.out.println(">> Running with parameters");
        System.out.println("    -o " + params.get("-o"));
        System.out.println("    -e " + params.get("-e"));
        System.out.println("    -t " + params.get("-t") + " seconds");
        System.out.println("    -l " + params.get("-l"));
        System.out.println("    -d " + params.get("-d"));
    }

    private static void printExplanations(Set<Explanation<OWLAxiom>> explanations, String outDir) {
        if (outDir.equals("")) {
            printExplanationsToScreen(explanations);
        } else {
            saveExplanationsToFile(explanations, new File(outDir));
        }

    }

    /**
     * @param explanations
     * @param outDir
     */
    private static void saveExplanationsToFile(Set<Explanation<OWLAxiom>> explanations, File outDir) {
        int counter = 1;
        if (explanations.size() > 0) {
            outDir.mkdirs();
            for (Explanation<OWLAxiom> j : explanations) {
                File outFile = new File(outDir, "ex." + Util.pad(counter++, 4) + ".owl.xml");
                try {
                    Explanation.store(j, new FileOutputStream(outFile));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        System.out.println("\n\n>> " + explanations.size() + " explanations saved to " + outDir.getAbsolutePath());
    }

    private static void printExplanationsToScreen(Set<Explanation<OWLAxiom>> explanations) {
        OWLAxiom entailment = explanations.iterator().next().getEntailment();
        System.out.println("\n>> Entailment");
        Util.print(entailment);
        System.out.println("\n>> Explanations");
        for (Explanation<OWLAxiom> ex : explanations) {
            Util.print(ex);
        }

    }

    private static OWLAxiom getEntailment(String entailment) {
        File ef = new File(entailment);
        if (ef.isFile()) {
            String s = Util.getFileContents(ef);
            OWLXMLInlineAxiomParser p = new OWLXMLInlineAxiomParser();
            return p.parseAtomicSubsumptionAxiom(s);
        }
        return null;

    }

    private static Map<String, String> setParams(String[] args) {
        Map<String, String> params = new HashMap<String, String>();

        params.put("-l", "10000");
        params.put("-t", "0");
        params.put("-r", "pellet");

        int i = 0;
        int length = args.length;

        while (i < length - 1) {
            String flag = args[i];
            String val = args[i + 1];
            params.put(flag, val);
            i = i + 2;
        }

        return params;

    }

}
