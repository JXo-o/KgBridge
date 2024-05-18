from rdflib.namespace import OWL
from ontology_tools.ontology_builder import Ontology
import os


class StandardOntology:

    def __init__(self, input_path, output_path, output_format="xml"):

        self.input_path = input_path
        self.output_path = output_path
        self.output_format = output_format

    def build_logic(self):

        ontology_builder = Ontology(self.input_path)
        ontology_builder.build_ontology()

        # ontology_builder.add_triple("Component", OWL.disjointWith, "Property")
        # ontology_builder.add_triple("Component", OWL.disjointWith, "Constraint")
        # ontology_builder.add_triple("Property", OWL.disjointWith, "Constraint")
        # ontology_builder.add_triple("NumericalConstraint", OWL.disjointWith, "SpatialConstraint")
        ontology_builder.add_triple("hasProperty", OWL.inverseOf, "btoComponent")

        Ontology.serialise(ontology_builder.get_kg(), self.output_path, self.output_format)

        print("标准规范本体已存至" + str(self.output_path))
