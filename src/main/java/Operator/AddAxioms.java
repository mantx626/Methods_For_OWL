package Operator;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.io.StringDocumentTarget;
import org.semanticweb.owlapi.model.*;
import org.semarglproject.vocab.OWL;

import java.io.*;
import java.util.*;


public class AddAxioms {
    public static OWLOntologyManager man = OWLManager.createOWLOntologyManager();
    public static List<String> ReadTextByLine(String fileDir) throws FileNotFoundException {
        // 文件所在位置
        File file = new File(fileDir);
        FileInputStream fis = new FileInputStream(file);
        InputStreamReader isr = null;
        try {
            isr = new InputStreamReader(fis,"UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        BufferedReader bufferedReader = new BufferedReader(isr);
        StringBuilder stringBuilder =new StringBuilder();
        List<String> strings =new ArrayList<>();
        String str=null;
        while (true) {
            try {
                if (!((str=bufferedReader.readLine())!=null)) break;
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (str.trim().length()>2) {
                strings.add(str);
            }
        }
        return strings;
    }
    public static OWLOntology addAxiomsList_1(String InitOntologyFile, List<String> subjectList, String ObjectProperty, String object) throws OWLOntologyCreationException, OWLOntologyStorageException {
        File InitOntology = new File(InitOntologyFile);
        OWLOntology ont = man.loadOntologyFromOntologyDocument(InitOntology);
        OWLDataFactory df = man.getOWLDataFactory();
        String base = ont.getOntologyID().getOntologyIRI().get().toString();
        Set<OWLAxiom> axioms = new HashSet<OWLAxiom>();
        for (String subjectStr : subjectList){
            OWLObjectProperty ObjProperty = df.getOWLObjectProperty(IRI.create(base+ "#" +ObjectProperty));
            OWLClass Obj = df.getOWLClass(IRI.create(base+ "#" + object));
            OWLClassExpression ObjPropertyObj = df.getOWLObjectSomeValuesFrom(ObjProperty, Obj);
            OWLClass sub = df.getOWLClass(IRI.create(base + "#" + subjectStr));
            OWLSubClassOfAxiom ax = df.getOWLSubClassOfAxiom(sub, ObjPropertyObj);
            axioms.add(ax);
        }
        man.addAxioms(ont, axioms);
        // System.out.println("RDF/XML: ");
        //man.saveOntology(ont, new StringDocumentTarget());
        return ont;
    }
    public static OWLOntology addAxioms1_List(String InitOntologyFile, String subject, String ObjectProperty, List<String> objectList) throws OWLOntologyCreationException, OWLOntologyStorageException {
        File InitOntology = new File(InitOntologyFile);
        OWLOntology ont = man.loadOntologyFromOntologyDocument(InitOntology);
        OWLDataFactory df = man.getOWLDataFactory();
        String base = ont.getOntologyID().getOntologyIRI().get().toString();
        Set<OWLAxiom> axioms = new HashSet<OWLAxiom>();
        for (String objectStr : objectList){
            OWLObjectProperty ObjProperty = df.getOWLObjectProperty(IRI.create(base+ "#" +ObjectProperty));
            OWLClass obj = df.getOWLClass(IRI.create(base+ "#" + objectStr));
            OWLClassExpression ObjPropertyObj = df.getOWLObjectSomeValuesFrom(ObjProperty, obj);
            OWLClass subj = df.getOWLClass(IRI.create(base + "#" + subject));
            OWLSubClassOfAxiom ax = df.getOWLSubClassOfAxiom(subj, ObjPropertyObj);
            axioms.add(ax);
        }
        man.addAxioms(ont, axioms);
        // System.out.println("RDF/XML: ");
        //man.saveOntology(ont, new StringDocumentTarget());
        return ont;
    }
    public static void addAxiom1_1(String InitOntologyFile, String subject, String ObjectProperty, String object) throws OWLOntologyCreationException, OWLOntologyStorageException {
        File InitOntology = new File(InitOntologyFile);
        OWLOntology ont = man.loadOntologyFromOntologyDocument(InitOntology);
        OWLDataFactory df = man.getOWLDataFactory();
        String base = ont.getOntologyID().getOntologyIRI().get().toString();
        OWLObjectProperty ObjProperty = df.getOWLObjectProperty(IRI.create(base+ "#" +ObjectProperty));
        OWLClass obj = df.getOWLClass(IRI.create(base+ "#" + object));
        OWLClassExpression ObjPropertyObj = df.getOWLObjectSomeValuesFrom(ObjProperty, obj);
        OWLClass subj = df.getOWLClass(IRI.create(base + "#" + subject));
        OWLSubClassOfAxiom ax = df.getOWLSubClassOfAxiom(subj, ObjPropertyObj);
        man.applyChange(new AddAxiom(ont, ax));
        String filename= "CoreDMOntology.owl";
        String directory=
                "C:\\Users\\MTX\\Documents\\GitHub\\Methods_For_OWL\\resources";
        File file=new File(directory,filename);
        man.saveOntology(ont, IRI.create(file.toURI()));
        //return ont;
    }
    //Saving method
    public static void SaveMergedOntology (String MergedOntologyName, String MergedOntologyDir, OWLOntology MergedOntology) throws OWLOntologyStorageException {
        String filename= MergedOntologyName;
        String directory= MergedOntologyDir;
        File file=new File(directory,filename);
        man.saveOntology(MergedOntology, IRI.create(file.toURI()));
    }
    public static void main(String[] args) throws OWLOntologyCreationException, FileNotFoundException, OWLOntologyStorageException {
        String InitOntologyFile =
                "C:\\Users\\MTX\\Documents\\GitHub\\Methods_For_OWL\\resources\\CoreDMOntology.owl";

        String textFile =
                "C:\\Users\\MTX\\Documents\\GitHub\\Methods_For_OWL\\resources\\StringList.txt";
        List StrList = ReadTextByLine(textFile);
        String subject = "ClusteringModel";
//        String object = "Noisy_values";
        String ObjectProperty = "isOutputOf";
//        OWLOntology NewOntList_1 = addAxiomsList_1(InitOntologyFile, StrList, ObjectProperty, object);
//
        String NewOntologyName = "NewOnt" + subject + ObjectProperty + "List.owl";
        String NewOntologyDir =  "C:\\Users\\MTX\\Documents\\GitHub\\Methods_For_OWL\\resources\\results";
        OWLOntology NewOnt1_List = addAxioms1_List(InitOntologyFile, subject,ObjectProperty,StrList);
        SaveMergedOntology(NewOntologyName, NewOntologyDir, NewOnt1_List);

        //Add an axiom from the text.file
//        String addAxiomText =
//                "C:\\Users\\MTX\\Documents\\GitHub\\Methods_For_OWL\\resources\\addAxiom.txt";
//        List addAxiomStr = ReadTextByLine(addAxiomText);
//        addAxiom1_1(InitOntologyFile, (String) addAxiomStr.get(0),(String) addAxiomStr.get(1),(String) addAxiomStr.get(2));

    }
}
