#!/usr/bin

from math import log

f=open('tom_sawyer.txt', 'r')
data=f.read()
data = data.strip()
L = len(data)
freq=dict()

#*********** UNIGRAM ENTROPY ************
chars=range(97,123)
for char in chars:
	freq[char]=0
for c in data:
	c=ord(c.lower())
	if(c>=97 and c<=122):
		freq[c] += 1

H_X = 0
total = sum(freq.values())

for value in freq.values():
	p_x = float(value)/total
	if p_x > 0:
		H_X += p_x*log(1./p_x, 2)
print "Unigram Entropy: " + str(H_X)

#*********** BIGRAM ENTROPY ************

freq=dict()
for i in range(0, L, 2):
	bigram=data[i:i+2]
	if bigram in freq:
		freq[bigram] += 1
	else:
		freq[bigram] = 1

H_X = 0
total = sum(freq.values())

for value in freq.values():
	H_X += (float(value)/total) * log(float(total)/value, 2)

print "Bigram Entropy: " + str(H_X)

#*********** TRIGRAM ENTROPY *************

freq=dict()
for i in range(0, L, 3):
	trigram=data[i:i+3]
	if trigram in freq:
		freq[trigram] += 1
	else:
		freq[trigram] = 1

H_X = 0
total = sum(freq.values())

for value in freq.values():
	H_X += (float(value)/total) * log(float(total)/value, 2)

print "Trigram Entropy: " + str(H_X)
