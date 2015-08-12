#!/usr/bin/python

# This code compresses a file using lempel-ziv compression algorithm.
# Author : Jayant Gupta
# Date : Februaru 22, 2015

from math import log
from math import floor
import operator

def write_file(new_data):
	compressed_data=''
	L = len(new_data)
	M = L/8 
	R = L%8 
	for i in range(0,M):
		compressed_data += chr(int(new_data[i*8:(i+1)*8],2))
	if(R > 0): 
		end_buffer = new_data[M*8:]
		compressed_data += chr(int(end_buffer,2))

	f=open('lz_out.txt','w')
	f.write(compressed_data)
	f.flush()
	f.close()

def encode(input_file):
	fp = open(input_file, 'r')
	data = fp.read()
	encoded_data = ''
	pattern = ''
	code = dict()
	for i in range(0, 128):
		code[chr(i)] = bin(i)[2:].zfill(7)

	count = 128
	for c in data:
		if c is '#':
			break
		if ord(c)>127:
			continue
		pattern += c
		code_buffer = int(floor(log(count,2))) + 1
		if pattern is not '' and pattern not in code:
			if count == 127:
				print ord(pattern[:-1])
			encoded_data += code[pattern[:-1]].zfill(code_buffer)
			count += 1
			code_buffer = int(floor(log(count,2))) + 1
			code[pattern]=bin(count)[2:].zfill(code_buffer)
			pattern = pattern[-1]

	if pattern is not '':
		encoded_data += code[pattern].zfill(code_buffer)
	write_file(encoded_data)

def read_file(coded_file):
	fp = open(coded_file, 'r')
	data = fp.read()
	coded_data=''
	last_char = data[-1]
	data=data[:-1]
	for c in data:
		c = ord(c)
		c = bin(c)[2:].zfill(8)
		coded_data += c
	c = ord(last_char)
	c = bin(c)[2:]
	coded_data += c
	return coded_data

def decode(encoded_file):
	encoded_data = read_file(encoded_file)
	code = dict()
	for i in range(0, 128):
		code[i] = chr(i)
	count = 129
	flag = 0
	i = 0
	pattern=''
	L = len(encoded_data)
	oc = ''
	while(i < L):
		cb = int(floor(log(count,2)) + 1)
		c = encoded_data[i:i+cb]
		i+=cb
		index = int(c,2)
		if(index not in code):
			index -=1
		oc += code[index]
		pattern += code[index][0]
		if not flag:
			flag = 1
		elif flag:
			code[count] = pattern
			pattern=code[index]
			count+=1
	fp = open('lz_decoded_file.txt','w')
	fp.write(oc)
	fp.flush()
	fp.close()

if __name__ == '__main__':
	encode('tom_sawyer.txt')
	decode('lz_out.txt')
