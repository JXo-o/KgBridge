import json
import spacy
from spacy.training.example import Example


class NERTrainer:
    def __init__(self, train_data_path, model_output_path):
        self.train_data_path = train_data_path
        self.model_output_path = model_output_path
        self.doc = None

    def load_train_data(self):
        with open(self.train_data_path, 'r', encoding='utf-8') as file:
            data = json.load(file)
        train_data = []
        for item in data:
            text = item['text']
            entities = [(entity['start'], entity['end'], entity['ner_label']) for entity in item['entities']]
            train_data.append((text, {'entities': entities}))
        return train_data

    def train_model(self, iterations=100):
        train_data = self.load_train_data()
        nlp = spacy.blank("zh")
        if "ner_tools" not in nlp.pipe_names:
            ner = nlp.add_pipe("ner_tools", last=True)

        for label in ["ENTITY", "PROPERTY", "OPERATOR", "VALUE"]:
            ner.add_label(label)
        nlp.begin_training()

        for itn in range(iterations):
            losses = {}
            for text, annotations in train_data:
                example = Example.from_dict(nlp.make_doc(text), annotations)
                nlp.update([example], losses=losses)
            print("Iteration:", itn + 1, " Loss:", losses)
        nlp.to_disk(self.model_output_path)
