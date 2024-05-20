from rdflib import Graph, Namespace, URIRef, Literal
from rdflib.namespace import FOAF, OWL, RDF, RDFS, XSD
from utility_scripts.util import MyUtil


class Ontology:

    def __init__(self, input_path=None):

        self.g = Graph()
        self.ns = None
        self.input_path = input_path

    def _create_class(self, c_name, parent=OWL.Thing):

        c_name = self._wrap_str(c_name)
        parent = self._wrap_str(parent)
        self.g.add((c_name, RDF.type, OWL.Class))
        self.g.add((c_name, RDFS.subClassOf, parent))

    def _create_property(self, key, key_type, key_domain, key_range, parent=OWL.topObjectProperty):

        key = self._wrap_str(key)
        key_type = self._wrap_str(key_type)
        key_domain = self._wrap_str(key_domain)
        key_range = self._wrap_str(key_range)
        parent = self._wrap_str(parent)

        self.g.add((key, RDF.type, key_type))
        self.g.add((key, RDFS.domain, key_domain))
        self.g.add((key, RDFS.range, key_range))
        self.g.add((key, RDFS.subPropertyOf, parent))

    def _namespace_bind(self):

        self.g.bind("ontology_files", OWL)
        self.g.bind("xsd", XSD)
        self.g.bind("rdf", RDF)
        self.g.bind("rdfs", RDFS)
        self.g.bind("foaf", FOAF)
        self.g.bind("", self.ns)

    def _wrap_str(self, key):

        if isinstance(key, Literal):
            return key

        if not key.startswith("http://"):
            key = URIRef(self.ns + key)
        return key

    def add_triple(self, subject, predicate, obj):

        subject = self._wrap_str(subject)
        predicate = self._wrap_str(predicate)
        obj = self._wrap_str(obj)

        self.g.add((subject, predicate, obj))

    def add_data(self, subject, predicate, obj):

        subject = self._wrap_str(subject)
        predicate = self._wrap_str(predicate)

        self.g.add((subject, predicate, obj))

    def set_namespace(self, namespace):

        self.ns = Namespace(namespace)
        self._namespace_bind()

    def parse_ontology(self, rpath, rformat, namespace):

        self.g.parse(source=rpath, format=rformat)
        self.ns = Namespace(namespace)

    def build_ontology(self):

        ns = MyUtil.parse_ontology(self.input_path, "NAMESPACE")
        clazz = MyUtil.parse_ontology(self.input_path, "CLASS")
        properties = MyUtil.parse_ontology(self.input_path, "PROPERTIES")
        self.set_namespace(ns[0])
        # print(Namespace(self.g.store.namespace("")))

        for c in clazz:
            c_lst = c.split("#")[::-1]
            self._create_class(*c_lst)

        for p in properties:
            p_lst = p.split("#")
            if len(p_lst) == 2:
                self._create_property(p_lst[1], OWL.DatatypeProperty, p_lst[0], XSD.string, OWL.topDataProperty)
            else:
                self._create_property(p_lst[1], OWL.ObjectProperty, p_lst[0], p_lst[2])

    def get_ns(self):
        return self.ns

    def get_kg(self):
        return self.g

    def save_file(self, save_url, save_format):
        self.g.bind("", self.ns)
        self.g.serialize(destination=save_url, format=save_format)
        print("#####SUCCESSFUL#####")

    @staticmethod
    def merge_kg(*paths, namespace, rformat="xml"):

        merged_kg = Graph()

        for path in paths:
            merged_kg += Graph().parse(path, rformat)

        merged_kg.bind("", namespace)
        return merged_kg

    @staticmethod
    def serialise(kg, save_url=None, save_format=None, is_print=False):

        res = kg.serialize(destination=save_url, format=save_format)
        if is_print:
            print(res)
        print("#####SUCCESSFUL#####")
