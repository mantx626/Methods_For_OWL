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
    static OWLOntologyManager manager = OWLManager.createOWLOntologyManager();

    public static void runHermitReasoner(String ontFileDir) {
        File ontFile = new File(ontFileDir);
        try {
            // load the (local) OWL ontology
            OWLOntology ont = manager.loadOntologyFromOntologyDocument(ontFile);
            System.out.println("Loaded ontology: " + ont.getOntologyID());
            IRI location = manager.getOntologyDocumentIRI(ont);
            System.out.println("\tfrom: " + location);

            long time = System.currentTimeMillis();

            // get and configure a reasoner (HermiT)
            OWLReasonerFactory reasonerFactory = new ReasonerFactory();
            ConsoleProgressMonitor progressMonitor = new ConsoleProgressMonitor();
            OWLReasonerConfiguration config = new SimpleConfiguration(progressMonitor);

            // create the reasoner instance, classify and compute inferences
            OWLReasoner reasoner = reasonerFactory.createReasoner(ont, config);
            // perform all the inferences now, to avoid subsequent ad-hoc
            // reasoner calls
            reasoner.precomputeInferences(InferenceType.values());
            System.out.println("has axioms: " + ont.getAxiomCount());
            System.out.println(System.currentTimeMillis()-time);
        } catch (OWLOntologyCreationException e) {
            e.printStackTrace();
        }

    }
    public static void runPelletReasoner(String ontFileDir) {

    }
        public static void main(String[] args) {
            // init
            String CoreDMOnt = "resources/CoreDMOntology.owl";
            String TSCOnt = "resources/results/ExtraModForTimeSeriesClassificationTask.owl";
            String ClassificationOnt = "C:\\Users\\MTX\\Documents\\GitHub\\Methods_For_OWL\\resources\\results\\ExtraMod_ClassificationModelingTask.owl";
            String ClusteringOntology = "C:\\Users\\MTX\\Documents\\GitHub\\Methods_For_OWL\\resources\\results\\ExtraModForClusteringTask.owl";
            runHermitReasoner(CoreDMOnt);
            runHermitReasoner(TSCOnt);
            runHermitReasoner(ClassificationOnt);
            runHermitReasoner(ClusteringOntology);
    }
}
