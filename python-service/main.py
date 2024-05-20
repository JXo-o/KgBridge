from ner_tools.ner_util import NERUtil
from utility_scripts.util import MyUtil
from ontology_tools.data_insert import DataInsert
from ontology_tools.standard_builder import StandardOntology
from ontology_tools.convert_to_rule import ConvertToRule
from ontology_tools.bridge_builder import BridgeBuilder
from ifc_tools.kg_completer import KnowledgeGraphCompleter
import os
import argparse
import sys

current_directory = os.path.join(os.getcwd(), "python-service")


def ner():
    # 命名实体识别部分，示例
    ner_util = NERUtil()
    ner_util.train_model()
    label_list = MyUtil.print_label(
        ner_util,
        os.path.join(current_directory, "input_data", "ner_input")
    )
    with open(os.path.join(current_directory, "input_data", "ner_label"), 'w', encoding='utf-8') as file:
        for labels in label_list:
            line = ' '.join(map(str, labels))
            file.write(line + '\n')


def standard():
    # 标准规范本体生成，示例
    StandardOntology(
        os.path.join(current_directory, "input_data", "standard_ontology"),
        os.path.join(current_directory, "ontology_files", "standard_ontology.owl")
    ).build_logic()


def standard_data():
    # 标准规范图谱数据插入，示例
    label_list = MyUtil.read_file(
        os.path.join(current_directory, "input_data", "ner_label")
    )
    label_list = [
        label.split() for label in label_list
    ]

    data_insert = DataInsert()
    for element in label_list:
        data_insert.insert_data(element)
    data_insert.save_file()


def bridge():
    # 桥梁本体生成，示例
    BridgeBuilder(
        os.path.join(current_directory, "input_data", "bridge_ontology"),
        os.path.join(current_directory, "ontology_files", "bridge_ontology.owl")
    ).build_logic()


def rule():
    # 知识图谱转Jena规则
    ConvertToRule.convert(
        os.path.join(current_directory, "ontology_files", "standard.owl")
    )


def ifc(ifc_file_path):
    # 解析IFC，在桥梁图谱中添加数据
    KnowledgeGraphCompleter(
        ifc_file_path,
        os.path.join(current_directory, "ontology_files", "bridge_ontology.owl")
    ).data_insert().save_file(
        os.path.join(current_directory, "ontology_files", "bridge.owl"),
        "xml"
    )


if __name__ == "__main__":
    parser = argparse.ArgumentParser(description="Process method name or IFC file path")
    parser.add_argument("input", type=str)
    args = parser.parse_args()

    if not args.input:
        print("Error: No method name or file path provided.")
        parser.print_help()
        sys.exit(1)

    if args.input in ["ner", "standard", "standard_data", "bridge", "rule"]:
        globals()[args.input]()
    elif os.path.isfile(args.input):
        ifc(args.input)
    else:
        print("Error: Invalid method name or file path.")
        parser.print_help()
        sys.exit(1)
