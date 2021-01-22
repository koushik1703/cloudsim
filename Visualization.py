import xml.etree.ElementTree as ET
import os

file = os.getcwd() + '\\data\\Output\\output.xml'
tree = ET.parse(file)
root = tree.getroot()

classOneArray = []
classTwoArray = []
classThreeArray = []
classFourArray = []

for child in root:
    classOne = 0
    classTwo = 0
    classThree = 0
    classFour = 0
    classOneCount = 0
    classTwoCount = 0
    classThreeCount = 0
    classFourCount = 0
    for grandChild in child:
        if grandChild.tag == 'Rule0':
            classOne = classOne + float(grandChild.get('EnergyConsumed'))
            classOneCount = classOneCount + 1
        if grandChild.tag == 'Rule32':
            classOne = classOne + float(grandChild.get('EnergyConsumed'))
            classOneCount = classOneCount + 1
        if grandChild.tag == 'Rule160':
            classOne = classOne + float(grandChild.get('EnergyConsumed'))
            classOneCount = classOneCount + 1
        if grandChild.tag == 'Rule232':
            classOne = classOne + float(grandChild.get('EnergyConsumed'))
            classOneCount = classOneCount + 1

        if grandChild.tag == 'Rule4':
            classTwo = classTwo + float(grandChild.get('EnergyConsumed'))
            classTwoCount = classTwoCount + 1
        if grandChild.tag == 'Rule108':
            classTwo = classTwo + float(grandChild.get('EnergyConsumed'))
            classTwoCount = classTwoCount + 1
        if grandChild.tag == 'Rule218':
            classTwo = classTwo + float(grandChild.get('EnergyConsumed'))
            classTwoCount = classTwoCount + 1
        if grandChild.tag == 'Rule250':
            classTwo = classTwo + float(grandChild.get('EnergyConsumed'))
            classTwoCount = classTwoCount + 1

        if grandChild.tag == 'Rule22':
            classThree = classThree + float(grandChild.get('EnergyConsumed'))
            classThreeCount = classThreeCount + 1
        if grandChild.tag == 'Rule30':
            classThree = classThree + float(grandChild.get('EnergyConsumed'))
            classThreeCount = classThreeCount + 1
        if grandChild.tag == 'Rule126':
            classThree = classThree + float(grandChild.get('EnergyConsumed'))
            classThreeCount = classThreeCount + 1
        if grandChild.tag == 'Rule150':
            classThree = classThree + float(grandChild.get('EnergyConsumed'))
            classThreeCount = classThreeCount + 1
        if grandChild.tag == 'Rule182':
            classThree = classThree + float(grandChild.get('EnergyConsumed'))
            classThreeCount = classThreeCount + 1

        if grandChild.tag == 'Rule110':
            classFour = classFour + float(grandChild.get('EnergyConsumed'))
            classFourCount = classFourCount + 1


    classOne = classOne / classOneCount
    classTwo = classTwo / classTwoCount
    classThree = classThree / classThreeCount
    classFour = classFour / classFourCount

    classOneArray.append(classOne)
    classTwoArray.append(classTwo)
    classThreeArray.append(classThree)
    classFourArray.append(classFour)

print("Values for class one:")
for value in classOneArray:
    print(value)

print("Values for class two:")
for value in classTwoArray:
    print(value)

print("Values for class three:")
for value in classThreeArray:
    print(value)

print("Values for class four:")
for value in classFourArray:
    print(value)
