# This code extracts n-gram features and entropy features from the given data set.
#
# Project : Building a model to detect DGA based domains.
# Author : Jayant Gupta
# Date : January 28, 2015
#
# Update features : ".", suffix, url_length
#

import numpy as np
from math import log
import sys

# Creates the feature vector for each url
def feature_extraction(URLs, CL, output_file):
	# List of lists, containing the features for all the URLs.
	features=[]
	
	for count, url in enumerate(URLs):
		# print count
		
		# Number of unique characters in a URL
		url_feature=[len(''.join(set(url)))]
		
		# Generating n-gram features for a URL
		for n in [1,2,3,4]:
			url_feature += (n_gram(n,url))

		# Generating entropy features for the URL
		tokens = url.split(".")
		t_l = len(tokens)
		for n in [1,2,3]:
			if(n > t_l):
				url_feature.append("NA")
			else:
				nLD = '.'.join(tokens[t_l-n:t_l])
				url_feature += entropy(nLD)

		# Entropy of the complete URL, will contain duplicates with other entropy columns
		# if t_l > 3:
		url_feature += entropy(url) 

		# Adding the new features to the url feature set.
		url_feature.append(url.count('.'))
		url_feature.append(tokens[-1])
		url_feature.append(len(url))
		url_feature.append(CL[count])
		if(CL[count] == 'Non-DGA'):
			url_feature.append('ND')
		else:
			url_feature.append('D')

		features.append(url_feature)
		

	np.set_printoptions(suppress=True)
	header=['url', 'n', 'mu_1', 'me_1', 'sd_1', 'mu_2', 'me_2', 'sd_2', 'mu_3', 'me_3', 'sd_3', 'mu_4', 'me_4', 'sd_4', 'H_1', 'H_2', 'H_3', 'H_*', '#dots', 'suffix', 'url_length','Class','Binary_Class']
	f=open(output_file,'w')
	f.write(','.join(header) + '\n')
	f.flush()
#	print header[0:10]
#	print header[10:len(header)]
	for index, feature in enumerate(features):
		line = URLs[index] + ',' + ','.join(str(e) for e in feature) + '\n'
		f.write(line)
		f.flush()

#		print URLs[index] + " #features = " + str(len(feature))
#		print(np.around(feature, decimals=2))
	f.close()

# returns mean (mu), median (me) and standard-deviation (sd) of the variables.mp
def n_gram(n,url):
	table=dict()
	url_length = len(url)
	for index in range(url_length - n + 1):
		part_url = url[index:index + n]
		if(part_url in table):
			table[part_url] += 1
		else:
		 	table[part_url] = 1
	frequency = table.values()
	if(n == 1):
		if(len(frequency) == len(''.join(set(url)))):
			2==1+1
#			print "n_gram:Check Passed" + str(n)
			
		else:
		 	print "n_gram:Check Failed" + str(n)
			print len(frequency)
		 	print len(''.join(set(url)))

	mean = float(sum(frequency)) / len(frequency)
	
	distinct_chars = len(frequency)
	frequency.sort()
# print frequency
	if(distinct_chars % 2 == 0):
		mid_index = distinct_chars / 2
		median = (frequency[mid_index-1] + frequency[mid_index]) / 2
	else:
	 	median = frequency[distinct_chars / 2]

	sd_sum = 0
	for value in frequency:
		sd_sum += (value - mean)**2
	sd = float(sd_sum) / distinct_chars
	sd = sd**0.5
	return [mean, median, sd]

def entropy(url):
	table = dict()
	for index in range(0,len(url)):
		part_url = url[index:index+1]
		if(part_url not in table):
			table[part_url] = 1
		else:
		 	table[part_url] += 1
	n = len(url)
	p_table = []
	for value in table.values():
		p_table.append(float(value) / n)
	
	if(len(p_table) == len(''.join(set(url)))):
		2==1+1
#		print "entropy:Check Passed" 
	else:
	 	print "entropy:Check Failed"
	 	print len(frequency)
	 	print len(''.join(set(url)))

	entropy = 0
	for p in p_table:
		entropy += -1 * p * log(p,2)

	return [entropy]

def main():
	print('Usage : Python feature_extraction.py <input_file> <output_file>')
	input_file = sys.argv[1]
	output_file = sys.argv[2]
	URLs = []
	CL = []
	with open(input_file) as f:
		for line in f:
			line=line.strip()
			print line
			URLs.append(line.split(',')[0])
			CL.append(line.split(',')[1])
	
	# Removing the header element from the list.
	url=URLs.pop(0)
	cl=CL.pop(0)

	print "Size of the URL List = " + str(len(URLs))
	print "Size of the CL List = " + str(len(CL))
	
	feature_extraction(URLs, CL, output_file)

if __name__ == "__main__":
	main()
