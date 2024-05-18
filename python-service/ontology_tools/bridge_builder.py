from utility_scripts.util import MyUtil
from ontology_tools.ontology_builder import Ontology
import datetime
import os


class BridgeBuilder:

    def __init__(self, input_path, output_path, output_format="xml"):

        self.template_path = os.path.join("input_data", "bridge_template")
        self.label_path = os.path.join("input_data", "ner_label")
        self.input_path = input_path
        self.output_path = output_path
        self.output_format = output_format

    def _get_prefix(self):

        return MyUtil.parse_ontology(self.template_path, "CLASS")[-1]

    def _parse_label(self):

        content = MyUtil.read_file(self.label_path)
        components = []
        properties = []
        parent = self._get_prefix()

        for line in content:
            line = line.strip()
            if line:
                parts = line.split()
                if len(parts) >= 2:
                    component_name = parts[0]
                    property_name = parts[1].split(' ')[0]
                    components.append(f"{parent}#{component_name}")
                    properties.append(f"{component_name}#{property_name}")

        return components, properties

    def _create_input(self):

        components, properties = self._parse_label()
        content_dict = {
            "CLASS": components,
            "PROPERTIES": properties
        }

        with open(os.path.join("input_data", "bridge_template"), 'r', encoding='utf-8') as file:
            lines = file.readlines()

        new_lines = []
        sections_found = {key: False for key in content_dict.keys()}
        new_lines.append("@@@" + datetime.datetime.now().strftime("%Y-%m-%d %H:%M:%S"))

        for line in lines:
            for key in content_dict.keys():
                keyword = "###" + key
                if line.strip() == keyword:
                    sections_found[key] = True
                elif sections_found[key] and line.strip().startswith("###"):
                    new_lines.extend([item + "\n" for item in content_dict[key]])
                    sections_found[key] = False
            new_lines.append(line)

        for key, is_section_found in sections_found.items():
            if is_section_found:
                new_lines.extend([item + "\n" for item in content_dict[key]])

        formatted_lines = [f"\n{line}\n" if line.strip().startswith("###") else line for line in new_lines]

        with open(self.input_path, 'w', encoding='utf-8') as file:
            file.writelines(formatted_lines)

    def build_logic(self):

        self._create_input()

        ontology_builder = Ontology(self.input_path)
        ontology_builder.build_ontology()
        Ontology.serialise(ontology_builder.get_kg(), self.output_path, self.output_format)

        print("桥梁本体已存至" + str(self.output_path))
