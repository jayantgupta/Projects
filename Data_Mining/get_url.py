#!/usr/bin/python

# Script to extract url and class from the final csv file
# Used to generate our own features.

# Author : Jayant Gupta
# Date : March 6, 2015

fp = open('Hidden_Alexa_DGA_PaperFeatures_no_mcr.csv','r')
data = fp.read()
rows = data.split('\n')

rows.pop() # remove last element, as it is an empty string
	
out=""
for row in rows:
 tokens=row.split(',')
 out+=tokens[2]+','+tokens[1]+'\n'

out=out.replace('"', '')
fp = open('final_test_t.txt','w')
rows = out.split('\n')
for row in rows:
	fp.write(row + '\n')
	fp.flush()

fp.close()
