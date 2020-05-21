package MergingMethod;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.util.OWLEntityRenamer;
import org.semanticweb.owlapi.util.OWLOntologyMerger;
import org.semarglproject.vocab.OWL;

import java.io.File;
import java.util.*;

public class MergingMethod {

    public static void main(String[] args) throws OWLOntologyCreationException, OWLOntologyStorageException {
        //init
        ArrayList<OWLOntology> ontologies = new ArrayList<>();
        Set<OWLAxiom> axioms = new HashSet<OWLAxiom>();
        Set<OWLImportsDeclaration> imports = new HashSet<OWLImportsDeclaration>();
        IRI MergedOntologyIRI = IRI.create("http://www.semanticweb.org/MergedOntology.owl");

        //load
        OWLOntologyManager man = OWLManager.createOWLOntologyManager();
        OWLDataFactory dataFactory = OWLManager.getOWLDataFactory();
        //load merged ontologies
        File ExtraExample = new File("resources\\CoreDMOntology.owl");
        OWLOntology Extra = man.loadOntologyFromOntologyDocument(ExtraExample);
        ontologies.add(Extra);

        File DataCharacteristics = new File("resources\\DataCharacteristics.owl");
        OWLOntology DataChar = man.loadOntologyFromOntologyDocument(DataCharacteristics);
        ontologies.add(DataChar);

        //Extract axioms and declaractions
        OWLOntologyManager manForMerging = OWLManager.createOWLOntologyManager();
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
//      //The info of the MergedOntology
//        System.out.println(MergedOntology);


        //Rename
//        MergedOntology.getSignature().forEach(System.out::println);
        final OWLEntityRenamer renamer = new OWLEntityRenamer(manForMerging, manForMerging.getOntologies());
        final Map<OWLEntity, IRI> entity2IRIMap = new HashMap<>();

        MergedOntology.getSignature().forEach(toRename -> {
            final IRI iri = toRename.getIRI();
            entity2IRIMap.put(toRename, IRI.create(iri.toString().replaceFirst("[^*]+(?=#|;)", MergedOntologyIRI.toString())));
        });
        MergedOntology.applyChanges(renamer.changeIRI(entity2IRIMap));


//        //The info of the MergedOntology after renaming
//        MergedOntology.getSignature().forEach(System.out::println);
//        System.out.println(MergedOntology);

        //Save
        String separator= File.separator;
        String filename="MergedOntology.owl";
        String directory="resources\\results";
        File file=new File(directory,filename);
        manForMerging.saveOntology(MergedOntology, IRI.create(file.toURI()));


    }

}
