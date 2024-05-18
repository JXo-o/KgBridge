from rdflib import Graph, Namespace
import os


class ConvertToRule:

    @staticmethod
    def convert(kg_path):
        kg = Graph()
        kg.parse(kg_path, format="xml")

        # 通过前缀获取命名空间
        ns = Namespace(kg.store.namespace(""))
        components = ConvertToRule.get_instances(kg, ns.Component)

        rules = []
        mapping = ConvertToRule.read_mapping_from_file()

        for component in components:
            rule = []
            flag = ['a']
            component_name = ConvertToRule.get_name(component)
            rule.append("[" + component_name + "规则:")
            rule.append(f"(?{ConvertToRule.increment_char(flag)} rdf:type :{component_name})")
            properties = ConvertToRule.get_has_property(kg, component)

            for m_property in properties:
                p_flag = ConvertToRule.increment_char(flag)
                rule.append(f"(?a :{ConvertToRule.get_name(m_property)} ?{p_flag})")
                # print(f"(?a :{ConvertToRule.get_name(m_property)} ?{p_flag})")
                num_constraints = ConvertToRule.get_meets_num(kg, m_property)

                for num_constraint in num_constraints:
                    num_constraint_name = ConvertToRule.get_name(num_constraint)
                    num_operator = mapping.get(num_constraint_name)
                    threshold = ConvertToRule.get_threshold(kg, num_constraint)[0]
                    rule.append(f"{num_operator}(?{p_flag} {threshold})")
                    # print(f"{num_operator}(?{p_flag} {threshold})")

            rule.append("->(?a :推理结果 :合格)]")
            rules.append(''.join(rule))
            # print(''.join(rule))

        ConvertToRule.save_rules(rules)

    @staticmethod
    def get_instances(kg, key):
        instances_query = f"""
            SELECT ?instance WHERE {{
                ?instance rdf:type/rdfs:subClassOf* <{key}>
            }}
        """
        instances = kg.query(instances_query)
        return [instance_row["instance"] for instance_row in instances]

    @staticmethod
    def get_property(kg, key, m_property):
        instances_query = f"""
            SELECT ?instance WHERE {{
                <{key}> :{m_property} ?instance
            }}
        """
        instances = kg.query(instances_query)
        return [instance_row["instance"] for instance_row in instances]

    @staticmethod
    def get_has_property(kg, key):
        return ConvertToRule.get_property(kg, key, "hasProperty")

    @staticmethod
    def get_meets_num(kg, key):
        return ConvertToRule.get_property(kg, key, "meetsNumericConstraint")

    @staticmethod
    def get_threshold(kg, key):
        return ConvertToRule.get_property(kg, key, "threshold")

    @staticmethod
    def write_mapping_to_file(mapping, filename):
        with open(filename, 'w', encoding='utf-8') as file:
            for key, value in mapping.items():
                file.write(f"{key}:{value}\n")

    @staticmethod
    def read_mapping_from_file(filename=os.path.join("input_data", "jena_mapping")):
        mapping = {}
        with open(filename, 'r', encoding='utf-8') as file:
            lines = file.readlines()
            for line in lines:
                key, value = line.strip().split(':')
                mapping[key] = value
        return mapping

    @staticmethod
    def save_rules(rules, output_path=os.path.join("input_data", "jena_output")):
        with open(output_path, 'w', encoding='utf-8') as file:
            for rule in rules:
                file.write(str(rule) + '\n')
        print("转换后的Jena规则已存至" + str(output_path))

    @staticmethod
    def increment_char(ch_list):
        old_char = ch_list[0]
        ascii_value = ord(ch_list[0])
        new_ascii_value = ascii_value + 1
        new_char = chr(new_ascii_value)
        ch_list[0] = new_char
        return old_char

    @staticmethod
    def get_name(key):
        return str(key).split("#")[-1]
