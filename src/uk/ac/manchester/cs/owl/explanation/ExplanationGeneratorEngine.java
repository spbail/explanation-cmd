package uk.ac.manchester.cs.owl.explanation;

import org.semanticweb.owl.explanation.api.Explanation;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import org.semanticweb.owl.explanation.api.ExplanationGeneratorFactory;
import org.semanticweb.owl.explanation.api.ExplanationGeneratorInterruptedException;
import org.semanticweb.owl.explanation.api.ExplanationManager;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;

/**
 * Created by
 * User: Samantha Bail
 * Date: 25/06/2013
 * Time: 12:38
 * The University of Manchester
 */


public class ExplanationGeneratorEngine {

    private long ENTAILMENT_TIMEOUT_MINS = 0;
    private long EXPLANATION_TIMEOUT_MINS = 0;
    private int EXPLANATION_LIMIT = 0;
    private OWLReasonerFactory rf;

    public ExplanationGeneratorEngine(int timeout, int limit, OWLReasonerFactory rf) {
        this.EXPLANATION_LIMIT = limit;
        this.ENTAILMENT_TIMEOUT_MINS = timeout;
        this.EXPLANATION_TIMEOUT_MINS = timeout / 2;
        this.rf = rf;
    }

    public Set<Explanation<OWLAxiom>> computeExplanations(File ontFile, OWLAxiom entailment) {
        Set<Explanation<OWLAxiom>> explanations = new HashSet<Explanation<OWLAxiom>>();
        OWLOntologyManager manager = OWLManager.createOWLOntologyManager();

        try {
            OWLOntology ontology = manager.loadOntologyFromOntologyDocument(ontFile);
            explanations = computeExplanations(ontology, entailment);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return explanations;
    }


    /**
     * @param ontology
     * @param entailment
     * @return
     */
    public Set<Explanation<OWLAxiom>> computeExplanations(OWLOntology ontology, OWLAxiom entailment) {
        ExplanationGeneratorFactory<OWLAxiom> genFac = ExplanationManager.createExplanationGeneratorFactory(rf);
        TimeoutProgressMonitor<OWLAxiom> mon = new TimeoutProgressMonitor<OWLAxiom>(EXPLANATION_TIMEOUT_MINS, ENTAILMENT_TIMEOUT_MINS);
        org.semanticweb.owl.explanation.api.ExplanationGenerator<OWLAxiom> exGen = genFac.createExplanationGenerator(ontology, mon);
        Set<Explanation<OWLAxiom>> explanations = new HashSet<Explanation<OWLAxiom>>();

        try {
            explanations = exGen.getExplanations(entailment, EXPLANATION_LIMIT);
        } catch (ExplanationGeneratorInterruptedException e) {
            // we're already printing an error in the progress monitor
        }

        return explanations;
    }

}
