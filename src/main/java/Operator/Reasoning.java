package Operator;

import org.semanticweb.HermiT.ReasonerFactory;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.reasoner.*;

import java.io.File;

public class Reasoning {

    public static void runReasoner() {
        // init
        OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
        File university = new File("resources/CoreDMOntology.owl");
        File ontFile = new File("resources/results/ExtraModForTimeSeriesClassificationTask.owl");
        OWLOntology localUni;

        try {
            // load the (local) OWL ontology
            localUni = manager.loadOntologyFromOntologyDocument(ontFile);
            System.out.println("Loaded ontology: " + localUni.getOntologyID());
            IRI location = manager.getOntologyDocumentIRI(localUni);
            System.out.println("\tfrom: " + location);

            long time = System.currentTimeMillis();

            // get and configure a reasoner (HermiT)
            OWLReasonerFactory reasonerFactory = new ReasonerFactory();
            ConsoleProgressMonitor progressMonitor = new ConsoleProgressMonitor();
            OWLReasonerConfiguration config = new SimpleConfiguration(progressMonitor);

            // create the reasoner instance, classify and compute inferences
            OWLReasoner reasoner = reasonerFactory.createReasoner(localUni, config);
            // perform all the inferences now, to avoid subsequent ad-hoc
            // reasoner calls
            reasoner.precomputeInferences(InferenceType.values());
            System.out.println(System.currentTimeMillis()-time);
        } catch (OWLOntologyCreationException e) {
            e.printStackTrace();
        }

    }

        public static void main(String[] args) {
            runReasoner();
    }
}
