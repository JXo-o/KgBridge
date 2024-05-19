package edu.bjtu.kgbridge.service;

import org.apache.jena.rdf.model.*;
import org.apache.jena.reasoner.Reasoner;
import org.apache.jena.reasoner.rulesys.GenericRuleReasoner;
import org.apache.jena.reasoner.rulesys.Rule;
import org.apache.jena.util.PrintUtil;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

/**
 * ClassName: JenaService
 * Package: edu.bjtu.kgbridge.service
 * Description:
 *
 * @author JX
 * @version 1.0
 * @date 2024/5/18 23:30
 */
@Service
public class JenaService {

    private static final Path ONTOLOGY_PATH = Path.of(
            System.getProperty("user.dir"),
            "python-service",
            "ontology_files",
            "bridge.owl"
    );

    private static final Path RULES_PATH = Path.of(
            System.getProperty("user.dir"),
            "python-service",
            "input_data",
            "jena_output"
    );

    private static final Path OUTPUT_PATH = Path.of(
            System.getProperty("user.dir"),
            "python-service",
            "ontology_files",
            "infer_result.owl"
    );

    public String performInference() throws IOException {
        if (!Files.exists(ONTOLOGY_PATH)) {
            throw new IOException("Ontology file not found: " + ONTOLOGY_PATH);
        }

        if (!Files.exists(RULES_PATH)) {
            throw new IOException("Rules file not found: " + RULES_PATH);
        }

        Model model = ModelFactory.createDefaultModel();
        model.read(Files.newInputStream(ONTOLOGY_PATH), null, "RDF/XML");

        String prefixUri = model.getNsPrefixURI("");
        if (prefixUri == null) {
            throw new IllegalStateException("No default prefix found in the ontology.");
        }

        PrintUtil.registerPrefix("", prefixUri);

        Reasoner reasoner = new GenericRuleReasoner(loadRules());
        InfModel infModel = ModelFactory.createInfModel(reasoner, model);

        StringBuilder results = new StringBuilder();
        infModel.listStatements().forEachRemaining(statement -> {
            if (statement.getPredicate().getLocalName().equals("推理结果")) {
                results.append(statement.getSubject().getLocalName())
                        .append(" -> ")
                        .append(statement.getObject().toString())
                        .append("\n");
            }
        });

        try (OutputStream out = Files.newOutputStream(OUTPUT_PATH)) {
            infModel.write(out, "RDF/XML");
            System.out.println("推理结果保存至-->" + OUTPUT_PATH);
        }

        return results.toString();
    }

    /**
     * 从文件中读取Jena规则,加载
     *
     * @return 加载后的Jena规则
     * @throws IOException 异常
     */
    private List<Rule> loadRules() throws IOException {
        String rulesContent = getRules();
        return Rule.parseRules(rulesContent);
    }

    /**
     * 读取Jena规则文件并返回内容
     *
     * @return Jena规则文件内容
     * @throws IOException 异常
     */
    public String getRules() throws IOException {
        if (!Files.exists(RULES_PATH)) {
            throw new IOException("Rules file not found: " + RULES_PATH);
        }

        try (var lines = Files.lines(RULES_PATH)) {
            return lines.collect(Collectors.joining("\n"));
        }
    }

}