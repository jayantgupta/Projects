#!/usr/bin/python
# Author : Jayant Gupta
# Date : February, 23 2015
# This script uses the language distribution of english and french languages.
# Then use the relative entropy to identify the language of the document.
import operator
from math import log
import sys

# Printing the dictionary.
def print_dict(input_dict):
	sorted_dict=sorted(input_dict.items(), key=operator.itemgetter(0))
	for item in sorted_dict:
		print item

# Initializing the distribution of different languages.
def init():
	fp = open('en_1.txt','r')
	en_dist = dict()
	for line in fp:
		line=line.strip().split()
		en_dist[line[0].lower()] = float(line[1][:-1])
	
	fp = open('fr_1.txt','r')
	fr_dist = dict()
	for line in fp:
		line=line.strip().split()
		fr_dist[line[0]] = float(line[1].replace('%',''))

	fr_dist = normalize(fr_dist)
#	print_dict(fr_dist)

	fr_keys = fr_dist.keys()
	for key in fr_keys:
		if key not in en_dist:
			en_dist[key] = 0.

	en_dist = normalize(en_dist)
#	print_dict(en_dist)
	return en_dist.keys(), en_dist, fr_dist

# Normalizing the dictionary.
def normalize(dict_1):
	value_sum = sum(dict_1.values())
	for key in dict_1:
		dict_1[key] = dict_1[key]/value_sum
	return dict_1

# Generating the distribution of the input document.
def doc_distribution(input_doc, dict_keys):
	doc_dist = dict()
	for key in dict_keys:
		doc_dist[key]=0.
	fp = open(input_doc, 'r')
	data = fp.read()
	for c in data:
		c = c.lower()
		if c in doc_dist:
			doc_dist[c] += 1
	doc_dist = normalize(doc_dist)
#	print_dict(doc_dist)
	return doc_dist

# Calculate the relative entropy between the language distribution and
# the document distribution.
# p = language distribution
# q = doc_distribution
def rel_entropy(p, q):
	dict_keys = p.keys()
	rel_H = 0.
	for key in dict_keys:
		p_x = p[key]
		q_x = q[key]
		if(p_x==0. or q_x==0.):
			rel_H += p_x
		else:
		 	rel_H += p_x*log(p_x/q_x , 2)
	return rel_H

def predict_language(en_H, fr_H):
	print "Relative Entropy w.r.t English : " + str(en_H)
	print "Relative Entropy w.r.t French : " + str(fr_H)
	if(en_H < fr_H):
		print "Language of the document: English"
	elif(en_H > fr_H):
	 	print "Language of the document: French"
	else:
		print "There is a tie, cannot determine the language of the document"

# "Usage: python language_recognition <input_file_path>"
if __name__ == '__main__':
	dict_keys, en_dist, fr_dist = init()
	doc_dist = doc_distribution(sys.argv[1], dict_keys)
	en_H = rel_entropy(en_dist, doc_dist)
	fr_H = rel_entropy(fr_dist, doc_dist)
	predict_language(en_H, fr_H)
