# This code implements Huffman encoding.
# Author : Jayant Gupta
# Date : Feb 21, 2015

import operator
import pickle

class Tree(object):
	def __init__(self):
		self.left = None
		self.right = None
		self.freq = None
		self.data = None

def print_tree(root):
	if(root == None):
		return
	print_tree(root.left)
	print str(root.freq) + ":" + str(root.data)
	print_tree(root.right)

def create_tree_1(t_1, t_2):
	root = Tree()
	root.freq = t_1[1] + t_2[1]
	root.data='*'
	root.left=Tree()
	root.left.freq=t_1[1]
	root.left.data=t_1[0]
	root.right=Tree()
	root.right.freq=t_2[1]
	root.right.data=t_2[0]
	return root

def create_tree_2(t_1, t_2):
	root=Tree()
	root.freq = t_1[1] + t_2[1]
	root.data = '*'
	root.left = t_1[0]
	root.right = t_2[0]
	return root

# t_1=(tree,freq), t_2=(data, freq)
def create_tree_3(t_1, t_2):
	root = Tree()
	root.freq = t_1[1] + t_2[1]
	root.data = '*'
	if (t_1[1] > t_2[1]):
		root.left = Tree()
		root.left.freq = t_2[1]
		root.left.data = t_2[0]
		root.right = t_1[0]
	else:
		root.right = Tree()
		root.right.freq = t_2[1]
		root.right.data = t_2[0]
		root.left = t_1[0]
	return root

def edit_list(fl,root):
	new_val = fl[0][1] + fl[1][1]
	fl.pop(0)
	fl.pop(0)
	fl.insert(0,(root, new_val))
	return

def generate_tree(fl):
	while(len(fl) > 1):
		fl=sorted(fl, key=operator.itemgetter(1))
		if(type(fl[0][0]) is not Tree and type(fl[1][0]) is not Tree):
			root = create_tree_1(fl[0], fl[1])
			edit_list(fl, root)

		elif(type(fl[0][0]) is Tree and type(fl[1][0]) is Tree):
			root = create_tree_2(fl[0], fl[1])
			edit_list(fl, root)

		elif(type(fl[0][0]) is Tree and type(fl[1][0]) is not Tree):
			root = create_tree_3(fl[0], fl[1])
			edit_list(fl, root)

		elif(type(fl[0][0]) is not Tree and type(fl[1][0]) is Tree):
			root = create_tree_3(fl[1], fl[0])
			edit_list(fl, root)
	return fl[0][0]

code = dict()
def generate_code(root, code_string):
	if(root is None):
		return
	if(root.data is not '*'):
		code[root.data]=code_string
	generate_code(root.left, code_string + '0')
	generate_code(root.right, code_string + '1')


def generate_frequency():
	f=open('tom_sawyer.txt','r')
	data=f.read()
	L=len(data)
	freq=dict()

	for c in data:
		c=ord(c)
		if c not in freq:
			freq[c] = 1
		else:
			freq[c] += 1
	fl=list(freq.items())
	return fl,data

def encode_and_write_file(data):
	new_data=''
	compressed_data=''
	for c in data:
		c=ord(c)
		new_data+=code[c]
	L = len(new_data)
	M = L/8
	R = L%8
	for i in range(0,M):
		compressed_data += chr(int(new_data[i*8:(i+1)*8],2))
	if(R > 0):
		end_buffer = new_data[M*8:]
		compressed_data += chr(int(end_buffer,2))

	f=open('out.txt','w')
	f.write(compressed_data)
	f.flush()
	f.close()

# Takes input of the type dict()	
def write_code(code):
	code_tree=list(code.items())
	with open('code.txt','wb') as f:
		pickle.dump(code_tree, f)
#	f.write(str(code_tree))
#	f.flush()
#	f.close()

if __name__ == '__main__':
	fl, data = generate_frequency()
	if(len(fl) > 1):
		root = generate_tree(fl)
		generate_code(root, '')
		encode_and_write_file(data)
		write_code(code)
	else:
		print ("Zero Entropy")
