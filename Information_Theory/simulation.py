# This script simulates the Weak Law of Large Numbers.
# Author : Jayant Gupta
# Date : Jan 25, 2015
# Simulate the outcomes X1,X2,...Xn and  compute the following quantity for  n varying from 1 till 10000
# |1/n \sum_{i=1}^{n}(X_i)-E[X]| - WEAK LAW OF LARGE NUMBERS (WLLN)

from random import randint
import matplotlib.pyplot as plt

P_X_0 = 0.5
P_X_1 = 0.5
N = 10000

E_X = 1*P_X_0 + 0*P_X_1

print "The Expected value, E(X) is " + str(E_X)

# Generating samples.
samples=[]
for i in range(0,N):
	samples.append(float(randint(0,1)))

# Calculating the divergence 
sample_divergence = []
for i in range(1,len(samples) + 1):
	variance = 0.0
	for j in range(0,i):
		variance += samples[j]
	sample_divergence.append(abs(variance/i - E_X))

x_axis = range(1,N+1)

# Generating the plot
if(len(sample_divergence) == len(x_axis)):
	plt.plot(x_axis, sample_divergence)
	plt.title('Weak Law of Large Numbers')
	plt.xlabel('Number of Samples (N)')
	plt.ylabel(r'Epsilon ($\epsilon$)')
	plt.grid(True)
	plt.show()
