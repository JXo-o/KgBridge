package edu.bjtu.kgbridge.service;

import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.rdf.model.*;
import org.apache.jena.util.iterator.ExtendedIterator;
import guru.nidi.graphviz.attribute.Label;
import guru.nidi.graphviz.engine.Format;
import guru.nidi.graphviz.engine.Graphviz;
import guru.nidi.graphviz.model.MutableGraph;
import guru.nidi.graphviz.model.MutableNode;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static guru.nidi.graphviz.model.Factory.*;

/**
 * ClassName: OWLGraphService
 * Package: edu.bjtu.kgbridge.service
 * Description:
 *
 * @author JX
 * @version 1.0
 * @date 2024/5/19 17:07
 */
@Service
public class OWLGraphService {

    public byte[] generateGraph(Path owlFilePath) throws IOException {
        // Load OWL model
        OntModel model = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM);
        model.read(Files.newInputStream(owlFilePath), null, "RDF/XML");

        // Create a Graphviz graph
        MutableGraph graph = mutGraph("OWL Graph").setDirected(true);

        // Iterate over classes
        ExtendedIterator<OntClass> classes = model.listClasses();
        while (classes.hasNext()) {
            OntClass cls = classes.next();
            String className = cls.getLocalName();
            if (className != null) {
                MutableNode classNode = mutNode(className);
                graph.add(classNode);

                // Iterate over properties
                ExtendedIterator<Property> properties = cls.listProperties().mapWith(Statement::getPredicate);
                while (properties.hasNext()) {
                    Property property = properties.next();
                    String propertyName = property.getLocalName();
                    if (propertyName != null) {
                        ExtendedIterator<Resource> targets = cls.listPropertyValues(property).mapWith(RDFNode::asResource);
                        while (targets.hasNext()) {
                            Resource target = targets.next();
                            String targetName = target.getLocalName();
                            if (targetName != null) {
                                MutableNode targetNode = mutNode(targetName);
                                graph.add(targetNode);
                                classNode.addLink(to(targetNode).with(Label.of(propertyName)));
                            }
                        }
                    }
                }
            }
        }

        // Render the graph to a byte array
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        Graphviz.fromGraph(graph).render(Format.PNG).toOutputStream(byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }
}