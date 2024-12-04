import xml.etree.ElementTree as ET
import json
import re

svg_file = './mapcreator/Risk_board.svg'
tree = ET.parse(svg_file)
root = tree.getroot()

namespace = {'svg': 'http://www.w3.org/2000/svg'}

layer_id = 'layer4'
layer = root.find(f".//svg:g[@id='{layer_id}']", namespace)

if layer is not None:
    objects_data = {}

    for path in layer.findall('.//svg:path', namespace):
        obj_id = path.get('id')
        d_attr = path.get('d')
        
        objects_data[obj_id] = {
            'path': d_attr
        }
    json_file = './mapcreator/paises.json'
    with open(json_file, 'w') as f:
        json.dump(objects_data, f, indent=4)

layer_id = 'layer3'
layer = root.find(f".//svg:g[@id='{layer_id}']", namespace)
if layer is not None:
    objects_data = {}

    for path in layer.findall('.//svg:path', namespace):
        obj_id = path.get('id')
        d_attr = path.get('d')
        
        objects_data[obj_id] = {
            'path': d_attr
        }
    json_file = './mapcreator/caminos.json'
    with open(json_file, 'w') as f:
        json.dump(objects_data, f, indent=4)
