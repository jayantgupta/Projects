# !/usr/bin/python

# Author : Jayant Gupta
# Date : March 7, 2015

if __name__ == '__main__':
	cM=dict()
	correct=0.
	incorrect=0.
#	fp = open('dt_result.csv','r')
	fp = open('Results/df_actual.csv','r')
	actual = fp.read()
	actual = actual.split('\n')
	actual.pop()
	for j in range(0,9,10):

		fp = open('classes.txt','r')
		results = fp.read()
		results = results.split('\n')
		results.pop()
		L=len(actual)
		print ('here')

		if(len(actual) == len(results)):
			for i in range(1,L):
#r=results[i].replace('"','').split(',')[1]
				r=results[i].split(',')[1]
				a=actual[i].replace('"','').split(',')[1]
#			print r + ' ' + a
				if(r==a):
					correct+=1.
				else:
			 		incorrect+=1.
				if r not in cM:
				 	cM[r]=dict()
				if a not in cM[r]:
					cM[r][a]=0
				cM[r][a] += 1

		print correct
		print incorrect
		print str(j) + ',' + str((correct*100)/(incorrect+correct))
#	print cM
