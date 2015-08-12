#!/usr/bin/python

# A generic code to decode the file, would require the encoding scheme and the encoded file.

import pickle
def read_code(code_file):
	with open(code_file, 'rb') as f:
		my_list = pickle.load(f)
	print my_list
	code = dict()
	for item in my_list:
		code[item[1]] = item[0]
	return code

def decode_file(coded_file, code, decoded_file):
	fp = open(coded_file,'r')
	data = fp.read()
	coded_data=''
	decoded_data=''
	last_char=data[-1]
	data = data[:-1]
	for c in data:
		c = ord(c)
		c = bin(c)[2:].zfill(8)
		coded_data += c
	c = ord(last_char)
	c = bin(c)[2:]
	coded_data += c
	current_code=''
	for c in coded_data:
		current_code += c
		if(current_code in code):
			decoded_data += chr(code[current_code])
			current_code = ''
	
	fp = open(decoded_file, 'w')
	fp.write(decoded_data)
	fp.flush()
	fp.close()

if __name__ == '__main__':
	code=read_code('code.txt')
	decode_file('out.txt', code, 'decoded_file.txt')
