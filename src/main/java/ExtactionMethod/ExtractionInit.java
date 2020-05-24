package ExtactionMethod;

public class ExtractionInit {
    private String InitOntologyFile = "resources\\CoreDMOntology.owl";
//    private String ExtractedOntologyName = "ExtraMod.owl";
//    private String ExtractedOntologyDir = "resources\\results";
    private String ExtractedOntologyIRI = "http://www.semanticweb.org/ExtraMod.owl";

    public String getInitOntologyFile(){
        return InitOntologyFile;
    }
    public void setInitOntologyFile(String InitOntologyFile) {
        this.InitOntologyFile = InitOntologyFile;
    }

//    public String getExtractedOntologyName(){
//        return ExtractedOntologyName;
//    }
//    public void setExtractedOntologyName(String ExtractedOntologyName) {
//        this.ExtractedOntologyName = ExtractedOntologyName;
//    }
//
//    public String getExtractedOntologyDir(){
//        return ExtractedOntologyDir;
//    }
//    public void setExtractedOntologyDir(String ExtractedOntologyDir) {
//        this.ExtractedOntologyDir = ExtractedOntologyDir;
//    }
    public String getExtractedOntologyIRI(){
        return ExtractedOntologyIRI;
    }
    public void setExtractedOntologyIRI(String ExtractedOntologyIRI) {
        this.ExtractedOntologyIRI = ExtractedOntologyIRI;
    }

}
