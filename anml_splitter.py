#!/usr/bin/python
#This splitter assumes that the network contains the automata sequentially.
# Moreover, the splitter assumes that the first state of each automaton is
# the start state and that in the case of multiple starts states, there is
# a distance of one transition between each start state ad the remainder of the automaton.

import xml.etree.ElementTree as ET
import sys
import copy

tree = ET.parse(sys.argv[1])
tree_copy = copy.deepcopy(tree)
root = tree.getroot()
items = list(root)
network = items[0]
root_copy = tree_copy.getroot()
items_copy = list(root_copy)
network_copy = items_copy[0]
output_dir = sys.argv[2]

#cleaning the root copy
for country in network_copy.findall('state-transition-element'):
    network_copy.remove(country)
for country in network_copy.findall('or'):
    network_copy.remove(country)
i = 0

tree_to_save = copy.deepcopy(tree_copy)
root_to_save = tree_to_save.getroot()
items_to_save = list(root_to_save)
network_to_save = items_to_save[0]

#get each state of the automaton
for state in network:
    #if state is one of the initial states
    if state.get('start') == 'all-input':
        #if it is not the first initial state of the overall network
        found = False
        if i > 0:
            for state_next in state:
                comp_state_next_1 = state_next.get('element')
                comp_state_id = state.get('id')
                for state_s in network_to_save:
                    if comp_state_next_1 == state_s.get('id'):
                        found = True
                    for state_s_next in state_s:
                        comp_state_2 = state_s_next.get('element')
                        if comp_state_2 is not None:
                            if comp_state_2 == comp_state_id:
                                found = True
                                break
                    if found:
                        break        
            
                if found:
                    break
            if found == False:
                tree_to_save.write(sys.argv[2] + str(i - 1)+'.anml')
                tree_to_save = copy.deepcopy(tree_copy)
                root_to_save = tree_to_save.getroot()
                items_to_save = list(root_to_save)
                network_to_save = items_to_save[0]
        if found == False:
            i = i + 1
    state_copy = copy.deepcopy(state)
    network_to_save.append(state_copy)
tree_to_save.write(sys.argv[2] + str(i - 1)+'.anml')
automata_file = open(sys.argv[2] + sys.argv[3], 'w')
automata_file.write(str(i))
automata_file.close()
print("Total automaton found: " + str(i))