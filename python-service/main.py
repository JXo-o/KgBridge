from utility_scripts.util import MyUtil
from ontology_tools.data_insert import DataInsert
from ontology_tools.standard_builder import StandardOntology
from ontology_tools.convert_to_rule import ConvertToRule
from ontology_tools.bridge_builder import BridgeBuilder
from ifc_tools.kg_completer import KnowledgeGraphCompleter
import os
import argparse
import sys


def main(ifc_file_path):
    # # 命名实体识别部分，示例
    # ner_util = NERUtil()
    # ner_util.train_model()
    # label_list = MyUtil.correct_labels(
    #     MyUtil.print_label(
    #         ner_util,
    #         os.path.join("input_data", "ner_label")
    #     )
    # )

    label_list = MyUtil.read_file(os.path.join("input_data", "ner_label"))
    label_list = [
        label.split() for label in label_list
    ]

    # 标准规范本体生成，示例
    StandardOntology(
        os.path.join("input_data", "standard_ontology"),
        os.path.join("ontology_files", "standard_ontology.ontology_files")
    ).build_logic()

    # 桥梁本体生成，示例
    BridgeBuilder(
        os.path.join("input_data", "bridge_ontology"),
        os.path.join("ontology_files", "bridge_ontology.ontology_files")
    ).build_logic()

    # 知识图谱数据插入，示例
    data_insert = DataInsert()
    for element in label_list:
        data_insert.insert_data(element)
    data_insert.save_file()

    # 本体与数据融合部分，示例
    # kg = Ontology.merge_kg(
    #     os.path.join("ontology_files", "standard_ontology.ontology_files"),
    #     os.path.join("ontology_files", "standard_data.ontology_files"),
    #     namespace=Namespace(
    #         MyUtil.parse_ontology(
    #             os.path.join("input_data", "standard_ontology"),
    #             "NAMESPACE"
    #         )[0]
    #     )
    # )
    # Ontology.serialise(kg, os.path.join("ontology_files", "standard_final.ontology_files"), "xml")

    # 解析IFC，在桥梁图谱中添加数据
    KnowledgeGraphCompleter(
        ifc_file_path,
        os.path.join("ontology_files", "bridge_ontology.ontology_files")
    ).data_insert().save_file(
        os.path.join("ontology_files", "bridge.ontology_files"),
        "xml"
    )

    # 知识图谱转Jena规则
    ConvertToRule.convert(
        os.path.join("ontology_files", "standard.ontology_files")
    )


if __name__ == "__main__":
    parser = argparse.ArgumentParser(description="Process IFC file path")
    parser.add_argument("ifc_file_path", type=str, nargs='?', help="Path to the IFC file")
    args = parser.parse_args()

    if not args.ifc_file_path:
        print("Error: Please provide the path to the IFC file.")
        parser.print_help()
        sys.exit(1)

    main(args.ifc_file_path)
