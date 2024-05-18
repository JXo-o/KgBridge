import os


class MyUtil:
    @staticmethod
    def read_file(file_path):
        lines = []
        with open(file_path, 'r', encoding='utf-8') as file:
            for line in file:
                lines.append(line.strip())
        return lines

    @staticmethod
    def parse_sentence(ner_util, sentence):
        ner_util.process_sentence(sentence)
        r_list = [ner_util.get_entities(entity_type) for entity_type in ["ENTITY", "PROPERTY", "OPERATOR", "VALUE"]]
        return r_list

    @staticmethod
    def print_label(ner_util, file_path=os.path.join("input_data", "ner_input")):
        sentences = MyUtil.read_file(file_path)
        sentences = [
            sentence.split() if MyUtil.check_list_elements(sentences) else MyUtil.parse_sentence(ner_util, sentence) for
            sentence in sentences
        ]
        print("标签如下：（依次为构件、属性、约束关系、临界值）")
        print(sentences)
        return sentences

    @staticmethod
    def contains_nil(r_list):
        return any("nil" in sublist for sublist in r_list)

    @staticmethod
    def check_list_elements(lst):
        for item in lst:
            parts = item.split(' ')
            if len(parts) != 4:
                return False
        return True

    @staticmethod
    def correct_labels(label_list):
        while True:
            print("请输入要修改的标签，以三元组形式输入: 行 列 值，或输入 q 退出:")
            user_input = input().strip()

            if user_input.lower() == 'q':
                print("修改成功后的标签如下:")
                print(label_list)
                break

            try:
                row, col, new_value = map(str, user_input.split())
                row = int(row)
                col = int(col)

                if 0 <= row < len(label_list) and 0 <= col < len(label_list[row]):
                    label_list[row][col] = new_value
                    print("修改成功！")
                else:
                    print("输入的行或列超出范围，请重新输入。")
            except ValueError:
                print("无效的输入格式，请输入三个值，用空格分隔。")

        with open(os.path.join("input_data", "ner_label"), 'w', encoding='utf-8') as file:
            for labels in label_list:
                line = ' '.join(map(str, labels))
                file.write(line + '\n')

        return label_list

    @staticmethod
    def parse_ontology(file_path, key):
        results = []
        start = False
        with open(file_path, 'r', encoding='utf-8') as file:
            for line in file:
                if line.startswith("###" + key):
                    start = True
                    continue
                if start:
                    if line.startswith("###"):
                        break
                    if len(line.strip()):
                        results.append(line.strip())
        return results

