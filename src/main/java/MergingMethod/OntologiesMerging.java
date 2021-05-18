package MergingMethod;

import ExtactionMethod.ExtractionInit;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.util.OWLEntityRenamer;

import java.io.File;
import java.util.*;

public class OntologiesMerging {
    // Create our manager

    public static OWLOntologyManager manForMerging = OWLManager.createOWLOntologyManager();
    //Merging method
    public static MergingInit Init(String MergingOntologyDir1, String MergingOntologyDir2) {
        MergingInit init = new MergingInit();
        init.setMergingOntologyDir1(MergingOntologyDir1);
        init.setMergingOntologyDir2(MergingOntologyDir2);
        //init.setMergedOntologyiri(MergedOntologyiri);

        return init;
    }
    public static OWLOntology OntologiesMerging (String MergingOntologyDir1, String MergingOntologyDir2, String MergedOntologyiri) throws OWLOntologyCreationException {
        //Init
        ArrayList<OWLOntology> ontologies = new ArrayList<>();
        Set<OWLAxiom> axioms = new HashSet<OWLAxiom>();
        Set<OWLImportsDeclaration> imports = new HashSet<OWLImportsDeclaration>();
        IRI MergedOntologyIRI = IRI.create(MergedOntologyiri);

        //load
        OWLDataFactory dataFactory = OWLManager.getOWLDataFactory();
        //load merged ontologies
        File ExtraExample = new File(MergingOntologyDir1);
        OWLOntology Extra = manForMerging.loadOntologyFromOntologyDocument(ExtraExample);
        ontologies.add(Extra);
        manForMerging.removeOntology(Extra);

        File DataCharacteristics = new File(MergingOntologyDir2);
        OWLOntology DataChar = manForMerging.loadOntologyFromOntologyDocument(DataCharacteristics);
        ontologies.add(DataChar);
        manForMerging.removeOntology(DataChar);

        //Extract axioms and declaractions
        for (OWLOntology ontology : ontologies) {
            axioms.addAll(ontology.getAxioms());
            imports.addAll(ontology.getImportsDeclarations());
            manForMerging.removeOntology(ontology);
        }
//        for (OWLAxiom ax : axioms) {
//            System.out.println(ax);
//            }
        //Create the MergedOntology
        OWLOntology MergedOntology = manForMerging.createOntology(MergedOntologyIRI);
        manForMerging.addAxioms(MergedOntology, axioms);

        //Rename
//        MergedOntology.getSignature().forEach(System.out::println);
        final OWLEntityRenamer renamer = new OWLEntityRenamer(manForMerging, manForMerging.getOntologies());
        final Map<OWLEntity, IRI> entity2IRIMap = new HashMap<>();

        MergedOntology.getSignature().forEach(toRename -> {
            final IRI iri = toRename.getIRI();
            entity2IRIMap.put(toRename, IRI.create(iri.toString().replaceFirst("[^*]+(?=#|;)", MergedOntologyIRI.toString())));
        });
        MergedOntology.applyChanges(renamer.changeIRI(entity2IRIMap));

        return MergedOntology;
    }
    //Saving method
    public static void SaveMergedOntology (String MergedOntologyName, String MergedOntologyDir, OWLOntology MergedOntology) throws OWLOntologyStorageException {
        String filename= MergedOntologyName;
        String directory= MergedOntologyDir;
        File file=new File(directory,filename);
        manForMerging.saveOntology(MergedOntology, IRI.create(file.toURI()));
    }
    public static void main(String[] args) throws OWLOntologyCreationException, OWLOntologyStorageException {
        String MergingOntologyDir1 = "resources\\CoreDMOntology.owl";
        String MergingOntologyDir2 = "resources\\TSC.owl";
        MergingInit Init = Init(MergingOntologyDir1, MergingOntologyDir2);

        String MergedOntologyiri = "http://www.semanticweb.org/MergedOntology.owl";
        OWLOntology MergedOntology = OntologiesMerging(Init.getMergingOntologyDir1(),Init.getMergingOntologyDir2(), MergedOntologyiri);

        String MergedOntologyName = "MergedOntology.owl";
        String MergedOntologyDir = "resources\\results";
        SaveMergedOntology(MergedOntologyName, MergedOntologyDir, MergedOntology);
    }
}
