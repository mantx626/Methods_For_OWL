package ExtactionMethod;

import org.junit.Test;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.formats.OWLXMLDocumentFormat;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.reasoner.BufferingMode;
import org.semanticweb.owlapi.reasoner.NodeSet;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.SimpleConfiguration;
import org.semanticweb.owlapi.reasoner.structural.StructuralReasoner;
import uk.ac.manchester.cs.owlapi.modularity.ModuleType;
import uk.ac.manchester.cs.owlapi.modularity.SyntacticLocalityModuleExtractor;

import java.awt.color.ICC_Profile;
import java.io.File;
import java.util.HashSet;
import java.util.Set;


public class SubOntologyExtraction {
    // Create our manager
    public static OWLOntologyManager man = OWLManager.createOWLOntologyManager();
    public static ExtractionInit Init(String InitOntologyFile, String ExtractedOntologyName, String ExtractedOntologyDir, String ExtractedOntologIRI) {
        ExtractionInit init = new ExtractionInit();
        init.setInitOntologyFile(InitOntologyFile);
        init.setExtractedOntologyName(ExtractedOntologyName);
        init.setExtractedOntologyDir(ExtractedOntologyDir);
        init.setExtractedOntologyIRI(ExtractedOntologIRI);
        return init;
    }
    public static OWLOntology Extract (String StartNodeName, String InitOntologyFile, String ExtractedOntologyIRI) throws OWLOntologyCreationException {

        OWLDataFactory dataFactory = OWLManager.getOWLDataFactory();
        File ExtraExample = new File(InitOntologyFile);
        OWLOntology Extra = man.loadOntologyFromOntologyDocument(ExtraExample);
        IRI location = man.getOntologyDocumentIRI(Extra);

        // We want to extract a module for the startnode. We therefore have to
        // generate a seed signature that contains "StartNodeName" and its
        // subclasses. We start by creating a signature that consists of
        // "StartNodeName".
        OWLClass StartNode = dataFactory.getOWLClass(IRI.create(Extra.getOntologyID().getOntologyIRI().get() + "#"+StartNodeName));
        Set<OWLEntity> sig = new HashSet<OWLEntity>();
        sig.add(StartNode);
        // We now add all subclasses (direct and indirect) of the chosen
        // classes. Ideally, it should be done using a DL reasoner, in order to
        // take inferred subclass relations into account. We are using the
        // structural reasoner of the OWL API for simplicity.
        Set<OWLEntity> seedSig = new HashSet<OWLEntity>();
        OWLReasoner reasoner = new StructuralReasoner(Extra, new SimpleConfiguration(), BufferingMode.NON_BUFFERING);
        for (OWLEntity ent : sig) {
            seedSig.add(ent);
            if (OWLClass.class.isAssignableFrom(ent.getClass())) {
                NodeSet<OWLClass> subClasses = reasoner.getSubClasses((OWLClass) ent, false);
                seedSig.addAll(subClasses.getFlattened());
            }
        }
        SyntacticLocalityModuleExtractor sme = new SyntacticLocalityModuleExtractor(man, Extra, ModuleType.BOT);
        IRI moduleIRI = IRI.create(ExtractedOntologyIRI);
        OWLOntology ExtractedMod = sme.extractAsOntology(seedSig, moduleIRI);
        return ExtractedMod;
    }
    //Saving method
    public static void SaveExtractedMod (String ExtractedOntologyName, String ExtractedOntologyDir, OWLOntology ExtractedMod) throws OWLOntologyStorageException {
        String filename= ExtractedOntologyName;
        String directory= ExtractedOntologyDir;
        File file=new File(directory,filename);
        man.saveOntology(ExtractedMod, IRI.create(file.toURI()));
    }


    public static void main(String[] args) throws OWLOntologyCreationException, OWLOntologyStorageException {
        ExtractionInit init = Init("resources\\CoreDMOntology.owl", "ExtraMod.owl","resources\\results", "http://www.semanticweb.org/ExtraMod.owl");
        OWLOntology ExtractedMod = Extract("ClassificationModelingTask", init.getInitOntologyFile(), init.getExtractedOntologyIRI());
        SaveExtractedMod(init.getExtractedOntologyName(),init.getExtractedOntologyDir(),ExtractedMod);

    }
}
