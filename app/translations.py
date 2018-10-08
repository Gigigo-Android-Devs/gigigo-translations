import requests
import xml.etree.cElementTree as ET

r = requests.get('http://translations-q.woah.com/translations/app/index.json')
translations  = requests.get(r.json()['default'])

resources = ET.Element("resources")

for key in translations.json().keys():
    ET.SubElement(resources, "string", name = key).text = key

tree = ET.ElementTree(resources)
tree.write("src/main/res/values/translations.xml")