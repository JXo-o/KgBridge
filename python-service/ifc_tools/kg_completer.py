from ontology_tools.convert_to_rule import ConvertToRule
from ontology_tools.ontology_builder import Ontology
from utility_scripts.util import MyUtil
from ifc_tools.ifc_parser import IFCParser
from rdflib import Literal
from rdflib.namespace import RDF
import os


class KnowledgeGraphCompleter:

    def __init__(self, ifc_path, kg_path, kg_format="xml"):
        self.mapping = ConvertToRule.read_mapping_from_file()
        self.ontology = Ontology()
        self.ontology.parse_ontology(
            kg_path,
            kg_format,
            MyUtil.parse_ontology(os.path.join("input_data", "bridge_ontology"), "NAMESPACE")[0]
        )
        self.label_content = MyUtil.read_file(os.path.join("input_data", "ner_label"))
        self.ifc_parser = IFCParser(ifc_path)
        self._init()

    def _init(self):
        for line in self.label_content:
            parts = line.strip().split()
            ifc_name = self.mapping.get(parts[0])
            if len(parts) >= 2 and ifc_name:
                self.ontology.add_triple(parts[0], RDF.type, "Component")
                self.ontology.add_triple(ifc_name, RDF.type, "Component")
                self.ontology.add_triple(parts[0], "equalsTo", ifc_name)

    def data_insert(self):
        # ConvertToRule.get_instances(self.ontology_tools.get_kg(), self.ontology_tools.get_ns().Component)
        for line in self.label_content:
            parts = line.strip().split()
            zh_name = parts[0]
            ifc_name = self.mapping.get(zh_name).split("#")
            self.ifc_parser.load_elements(zh_name, ifc_name)
            key = self.mapping.get(parts[1])
            if key and (mapped_value := self.mapping.get(key)):
                key = f"property:{mapped_value}"
            r_list = self.ifc_parser.get_dimensions(key)

            for res in r_list:
                key, value = res.strip().split("#")
                self.ontology.add_triple(key, RDF.type, zh_name)
                print(Literal(value))
                self.ontology.add_triple(key, parts[1], Literal(value))

        return self.ontology
