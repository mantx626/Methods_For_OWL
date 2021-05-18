package Operator;
//import org.semanticweb.HermiT.Reasoner;
import java.io.*;
import java.util.Collections;
import java.util.Set;

import org.coode.owlapi.manchesterowlsyntax.ManchesterOWLSyntaxEditorParser;
import org.semanticweb.HermiT.ReasonerFactory;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.expression.OWLEntityChecker;
import org.semanticweb.owlapi.expression.ParserException;
import org.semanticweb.owlapi.expression.ShortFormEntityChecker;
import org.semanticweb.owlapi.io.StringDocumentSource;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.reasoner.*;
import org.semanticweb.owlapi.reasoner.structural.StructuralReasonerFactory;
import org.semanticweb.owlapi.util.BidirectionalShortFormProvider;
import org.semanticweb.owlapi.util.BidirectionalShortFormProviderAdapter;
import org.semanticweb.owlapi.util.ShortFormProvider;
import org.semanticweb.owlapi.util.SimpleShortFormProvider;

public class DLQuery {
    private static void doQueryLoop(DLQueryPrinter dlQueryPrinter)
            throws IOException {
        while (true) {
            // Prompt the user to enter a class expression
            System.out
                    .println("Please type a class expression in Manchester Syntax and press Enter (or press x to exit):");
            System.out.println("");
            String classExpression = readInput();
            // Check for exit condition
            long time = System.currentTimeMillis();
            if (classExpression.equalsIgnoreCase("x")) {
                break;
            }
            dlQueryPrinter.askQuery(classExpression.trim());
            System.out.println();
            System.out.println();
            System.out.println(System.currentTimeMillis()-time);
        }
    }

    private static String readInput() throws IOException {
        InputStream is = System.in;
        InputStreamReader reader;
        reader = new InputStreamReader(is, "UTF-8");
        BufferedReader br = new BufferedReader(reader);
        return br.readLine();
    }

    private static OWLReasoner createReasoner(final OWLOntology rootOntology) {
        // We need to create an instance of OWLReasoner. An OWLReasoner provides
        // the basic query functionality that we need, for example the ability
        // obtain the subclasses of a class etc. To do this we use a reasoner
        // factory.
        // Create a reasoner factory.
        OWLReasonerFactory reasonerFactory = new StructuralReasonerFactory();
        return reasonerFactory.createReasoner(rootOntology);
    }

    public static void main(String[] args) throws OWLOntologyCreationException {
//        String InitOntologyFile = "resources\\CoreDMOntology.owl";
//        OWLDataFactory dataFactory = OWLManager.getOWLDataFactory();
//        File InitFile = new File(InitOntologyFile);
//        OWLOntology InitOnt = man.loadOntologyFromOntologyDocument(InitFile);
//        //System.out.println(InitOnt);
        try {
            // Load an example ontology.
            OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
//            String InitOntologyFile = "resources\\CoreDMOntology.owl";
            String InitOntologyFile = "resources\\TSC.owl";
            OWLDataFactory dataFactory = OWLManager.getOWLDataFactory();
            File InitFile = new File(InitOntologyFile);
            OWLOntology ontology = manager.loadOntologyFromOntologyDocument(InitFile);
            System.out.println("Loaded ontology: " + ontology.getOntologyID());
            // We need a reasoner to do our query answering
//            OWLReasoner reasoner = createReasoner(ontology);
            // get and configure a reasoner (HermiT)
            OWLReasonerFactory reasonerFactory = new ReasonerFactory();
            ConsoleProgressMonitor progressMonitor = new ConsoleProgressMonitor();
            OWLReasonerConfiguration config = new SimpleConfiguration(progressMonitor);

            // create the reasoner instance, classify and compute inferences
            OWLReasoner reasoner = reasonerFactory.createReasoner(ontology, config);
            // Entities are named using IRIs. These are usually too long for use
            // in user interfaces. To solve this
            // problem, and so a query can be written using short class,
            // property, individual names we use a short form
            // provider. In this case, we'll just use a simple short form
            // provider that generates short froms from IRI
            // fragments.
            ShortFormProvider shortFormProvider = new SimpleShortFormProvider();
            // Create the DLQueryPrinter helper class. This will manage the
            // parsing of input and printing of results

            DLQueryPrinter dlQueryPrinter = new DLQueryPrinter(
                    new DLQueryEngine(reasoner, shortFormProvider),
                    shortFormProvider);
            // Enter the query loop. A user is expected to enter class
            // expression on the command line.
            doQueryLoop(dlQueryPrinter);

        } catch (OWLOntologyCreationException e) {
            System.out.println("Could not load ontology: " + e.getMessage());
        } catch (IOException ioEx) {
            System.out.println(ioEx.getMessage());
        }




    }
}
class DLQueryEngine {

    private final OWLReasoner reasoner;
    private final DLQueryParser parser;

    /**
     * Constructs a DLQueryEngine. This will answer "DL queries" using the
     * specified reasoner. A short form provider specifies how entities are
     * rendered.
     *
     * @param reasoner
     *        The reasoner to be used for answering the queries.
     * @param shortFormProvider
     *        A short form provider.
     */
    public DLQueryEngine(OWLReasoner reasoner,
                         ShortFormProvider shortFormProvider) {
        this.reasoner = reasoner;
        OWLOntology rootOntology = reasoner.getRootOntology();
        parser = new DLQueryParser(rootOntology, shortFormProvider);
    }

    /**
     * Gets the superclasses of a class expression parsed from a string.
     *
     * @param classExpressionString
     *        The string from which the class expression will be parsed.
     * @param direct
     *        Specifies whether direct superclasses should be returned or not.
     * @return The superclasses of the specified class expression If there was a
     *         problem parsing the class expression.
     */
    public Set<OWLClass> getSuperClasses(String classExpressionString,
                                         boolean direct) {
        if (classExpressionString.trim().length() == 0) {
            return Collections.emptySet();
        }
        OWLClassExpression classExpression = parser
                .parseClassExpression(classExpressionString);
        NodeSet<OWLClass> superClasses = reasoner.getSuperClasses(
                classExpression, direct);
        return superClasses.getFlattened();
    }

    /**
     * Gets the equivalent classes of a class expression parsed from a string.
     *
     * @param classExpressionString
     *        The string from which the class expression will be parsed.
     * @return The equivalent classes of the specified class expression If there
     *         was a problem parsing the class expression.
     */
    public Set<OWLClass> getEquivalentClasses(String classExpressionString) {
        if (classExpressionString.trim().length() == 0) {
            return Collections.emptySet();
        }
        OWLClassExpression classExpression = parser
                .parseClassExpression(classExpressionString);
        Node<OWLClass> equivalentClasses = reasoner
                .getEquivalentClasses(classExpression);
        Set<OWLClass> result;
        if (classExpression.isAnonymous()) {
            result = equivalentClasses.getEntities();
        } else {
            result = equivalentClasses.getEntitiesMinus(classExpression
                    .asOWLClass());
        }
        return result;
    }

    /**
     * Gets the subclasses of a class expression parsed from a string.
     *
     * @param classExpressionString
     *        The string from which the class expression will be parsed.
     * @param direct
     *        Specifies whether direct subclasses should be returned or not.
     * @return The subclasses of the specified class expression If there was a
     *         problem parsing the class expression.
     */
    public Set<OWLClass> getSubClasses(String classExpressionString,
                                       boolean direct) {
        if (classExpressionString.trim().length() == 0) {
            return Collections.emptySet();
        }
        OWLClassExpression classExpression = parser
                .parseClassExpression(classExpressionString);
        NodeSet<OWLClass> subClasses = reasoner.getSubClasses(classExpression,
                direct);
        return subClasses.getFlattened();
    }

    /**
     * Gets the instances of a class expression parsed from a string.
     *
     * @param classExpressionString
     *        The string from which the class expression will be parsed.
     * @param direct
     *        Specifies whether direct instances should be returned or not.
     * @return The instances of the specified class expression If there was a
     *         problem parsing the class expression.
     */
    public Set<OWLNamedIndividual> getInstances(String classExpressionString,
                                                boolean direct) {
        if (classExpressionString.trim().length() == 0) {
            return Collections.emptySet();
        }
        OWLClassExpression classExpression = parser
                .parseClassExpression(classExpressionString);
        NodeSet<OWLNamedIndividual> individuals = reasoner.getInstances(
                classExpression, direct);
        return individuals.getFlattened();
    }
}

class DLQueryParser {

    private final OWLOntology rootOntology;
    private final BidirectionalShortFormProvider bidiShortFormProvider;

    /**
     * Constructs a DLQueryParser using the specified ontology and short form
     * provider to map entity IRIs to short names.
     *
     * @param rootOntology
     *        The root ontology. This essentially provides the domain vocabulary
     *        for the query.
     * @param shortFormProvider
     *        A short form provider to be used for mapping back and forth
     *        between entities and their short names (renderings).
     */
    public DLQueryParser(OWLOntology rootOntology,
                         ShortFormProvider shortFormProvider) {
        this.rootOntology = rootOntology;
        OWLOntologyManager manager = rootOntology.getOWLOntologyManager();
        Set<OWLOntology> importsClosure = rootOntology.getImportsClosure();
        // Create a bidirectional short form provider to do the actual mapping.
        // It will generate names using the input
        // short form provider.
        bidiShortFormProvider = new BidirectionalShortFormProviderAdapter(
                manager, importsClosure, shortFormProvider);
    }

    /**
     * Parses a class expression string to obtain a class expression.
     *
     * @param classExpressionString
     *        The class expression string
     * @return The corresponding class expression if the class expression string
     *         is malformed or contains unknown entity names.
     */
    public OWLClassExpression
    parseClassExpression(String classExpressionString) {
        OWLDataFactory dataFactory = rootOntology.getOWLOntologyManager()
                .getOWLDataFactory();
        // Set up the real parser
        ManchesterOWLSyntaxEditorParser parser = new ManchesterOWLSyntaxEditorParser(
                dataFactory, classExpressionString);
        parser.setDefaultOntology(rootOntology);
        // Specify an entity checker that wil be used to check a class
        // expression contains the correct names.
        OWLEntityChecker entityChecker = new ShortFormEntityChecker(
                bidiShortFormProvider);
        parser.setOWLEntityChecker(entityChecker);
        // Do the actual parsing
        return parser.parseClassExpression();
    }
}

class DLQueryPrinter {

    private final DLQueryEngine dlQueryEngine;
    private final ShortFormProvider shortFormProvider;

    /**
     * @param engine
     *        the engine
     * @param shortFormProvider
     *        the short form provider
     */
    public DLQueryPrinter(DLQueryEngine engine,
                          ShortFormProvider shortFormProvider) {
        this.shortFormProvider = shortFormProvider;
        dlQueryEngine = engine;
    }

    /**
     * @param classExpression
     *        the class expression to use for interrogation
     */
    public void askQuery(String classExpression) {
        if (classExpression.length() == 0) {
            System.out.println("No class expression specified");
        } else {
            try {
                StringBuilder sb = new StringBuilder();
                sb.append("\n--------------------------------------------------------------------------------\n");
                sb.append("QUERY:   ");
                sb.append(classExpression);
                sb.append("\n");
                sb.append("--------------------------------------------------------------------------------\n\n");
                // Ask for the subclasses, superclasses etc. of the specified
                // class expression. Print out the results.
                Set<OWLClass> superClasses = dlQueryEngine.getSuperClasses(
                        classExpression, true);
                printEntities("SuperClasses", superClasses, sb);
                Set<OWLClass> equivalentClasses = dlQueryEngine
                        .getEquivalentClasses(classExpression);
                printEntities("EquivalentClasses", equivalentClasses, sb);
                Set<OWLClass> subClasses = dlQueryEngine.getSubClasses(
                        classExpression, true);
                printEntities("SubClasses", subClasses, sb);
                Set<OWLNamedIndividual> individuals = dlQueryEngine
                        .getInstances(classExpression, true);
                printEntities("Instances", individuals, sb);
                System.out.println(sb.toString());
            } catch (ParserException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    private void printEntities(String name, Set<? extends OWLEntity> entities,
                               StringBuilder sb) {
        sb.append(name);
        int length = 50 - name.length();
        for (int i = 0; i < length; i++) {
            sb.append(".");
        }
        sb.append("\n\n");
        if (!entities.isEmpty()) {
            for (OWLEntity entity : entities) {
                sb.append("\t");
                sb.append(shortFormProvider.getShortForm(entity));
                sb.append("\n");
            }
        } else {
            sb.append("\t[NONE]\n");
        }
        sb.append("\n");
    }
}
