import xml.etree.ElementTree as ET
import os

file = os.getcwd() + '\\data\\Output\\output.xml'
tree = ET.parse(file)
root = tree.getroot()

rule23Array = []
rule232Array = []
rule23Count = 0
rule232Count = 0

for child in root:
    rule23 = 0
    rule232 = 0
    for grandChild in child:
        if grandChild.tag == 'Rule23':
            rule23 = rule23 + float(grandChild.get('EnergyConsumed'))
            rule23Count = rule23Count + 1
        if grandChild.tag == 'Rule232':
            rule232 = rule232 + float(grandChild.get('EnergyConsumed'))
            rule232Count = rule232Count + 1

    rule23Array.append(rule23)
    rule232Array.append(rule232)

print("Values for rule 23:")
for value in rule23Array:
    print(value)

print("Values for rule 232:")
for value in rule232Array:
    print(value)
