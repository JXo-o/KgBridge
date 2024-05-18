import ifcopenshell
import ifcopenshell.geom
import itertools


class IFCParser:
    def __init__(self, ifc_path):
        self.ifc_file = ifcopenshell.open(ifc_path)
        self.settings = ifcopenshell.geom.settings()
        self.element_type_zh = None
        self.element_type = None
        self.element_name = None
        self.elements = None

    def load_elements(self, element_type_zh, element_types):
        self.element_type_zh = element_type_zh
        self.element_type = element_types[0]
        self.elements = self.ifc_file.by_type(self.element_type)

        if len(element_types) == 2:
            self.element_name = element_types[1]
            self.elements = [
                element for element in self.elements if getattr(element, 'Name', None) == self.element_name
            ]
        elif len(element_types) != 1:
            raise ValueError("element_types should be a list of length 1 or 2")

    def _calculate_height(self, element):
        return self._calculate_dimension(element, axis='Z')

    def _calculate_length(self, element):
        return self._calculate_dimension(element, axis='X')

    def _calculate_width(self, element):
        return self._calculate_dimension(element, axis='Y')

    def _calculate_dimension(self, element, axis):
        try:
            shape = ifcopenshell.geom.create_shape(self.settings, element)
            vertices_raw = shape.geometry.verts

            # 将顶点坐标分为三元组
            vertices = list(itertools.zip_longest(*[iter(vertices_raw)] * 3))

            # 根据指定轴获取坐标
            if axis == 'X':
                coordinates = [v[0] for v in vertices]
            elif axis == 'Y':
                coordinates = [v[1] for v in vertices]
            elif axis == 'Z':
                coordinates = [v[2] for v in vertices]
            else:
                raise ValueError(f"Unsupported axis: {axis}")

            # 计算最高点和最低点的差值，即为元素在该轴上的长度
            dimension_value = max(coordinates) - min(coordinates)
            return dimension_value
        except RuntimeError as e:
            print(f"Error processing element ID: {element.GlobalId}, Error: {e}")
            return None

    def _calculate_diameter(self, element):
        try:
            shape = ifcopenshell.geom.create_shape(self.settings, element)
            vertices_raw = shape.geometry.verts

            # 将顶点坐标分为三元组
            vertices = list(itertools.zip_longest(*[iter(vertices_raw)] * 3))

            # 假设圆柱体位于XY平面上，计算XY平面上的最大距离
            xy_coordinates = [(v[0], v[1]) for v in vertices]
            max_distance = 0
            for i, (x1, y1) in enumerate(xy_coordinates):
                for x2, y2 in xy_coordinates[i + 1:]:
                    distance = ((x2 - x1) ** 2 + (y2 - y1) ** 2) ** 0.5
                    if distance > max_distance:
                        max_distance = distance

            return max_distance
        except RuntimeError as e:
            print(f"Error processing element ID: {element.GlobalId}, Error: {e}")
            return None

    def get_dimensions(self, dimension_key):
        results = []

        for element in self.elements:
            if dimension_key == "height":
                dimension_value = self._calculate_height(element)
            elif dimension_key == "length":
                dimension_value = self._calculate_length(element)
            elif dimension_key == "width":
                dimension_value = self._calculate_width(element)
            elif dimension_key == "diameter":
                dimension_value = self._calculate_diameter(element)
            elif dimension_key.startswith("property:"):
                property_name = dimension_key.split(":", 1)[1]
                dimension_value = self._calculate_property_value(element, property_name)
                print(dimension_value)
            else:
                raise ValueError(f"Unsupported dimension key: {dimension_key}")

            if dimension_value is not None:
                if dimension_key.startswith("property:"):
                    results.append(f"{self.element_type_zh}-{element.GlobalId}#{dimension_value}")
                else:
                    results.append(f"{self.element_type_zh}-{element.GlobalId}#{dimension_value:.2f}")
            else:
                print(
                    f"{self.element_type} ID: {element.GlobalId}, {dimension_key.capitalize()}: Not available"
                )

        return results

    @staticmethod
    def _calculate_property_value(element, property_name):
        try:
            for is_defined_by in element.IsDefinedBy:
                related_properties = is_defined_by.RelatingPropertyDefinition
                for prop in related_properties.HasProperties:
                    if prop.Name == property_name:
                        return prop.NominalValue.wrappedValue
            return None
        except AttributeError:
            print(f"Error processing element ID: {element.GlobalId}, Property '{property_name}' not found.")
            return None
