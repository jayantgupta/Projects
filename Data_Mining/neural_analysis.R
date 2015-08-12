#!/usr/bin/env Rscript
	
# Author : Jayant Gupta
# Date : February 24, 2015
# Code for Binary Neural Network Classifier.

library(sampling)
library(nnet)
library(caret)

trainData = data.frame(read.csv('relevant_data/b_train_data.csv'))
trainData = subset(trainData, select=-c(url,suffix,H_3, sd_4))
testData = data.frame(read.csv('relevant_data/b_test_data.csv'))
#testData = subset(testData, select=-c(url, suffix, Class, ID_unit, Prob, Stratum))
testData = subset(testData, select=-c(url, suffix, Class, H_3, ID_unit, Prob,Stratum,sd_4))
print(nrow(trainData))
print(ncol(trainData))
print(nrow(testData))
print(ncol(testData))
colnames(trainData)
colnames(testData)
h_n=list()
nn_a=list()
t_e = list()
for ( i in 1:10){
	print(i)
	t1 = proc.time()
	h_n = c(h_n, i)
#model<-nnet(Binary_Class~n + H_. + X.dots + mu_1 + mu_2 + url_length + sd_1 + sd_2 + sd_3, trainData, size=i, decay=0.05)
	model<-nnet(Binary_Class~., trainData, size=i)
	predictions=predict(model, testData[,-18], type="class")
	str(predictions)
	print("??")
	str(testData$Binary_Class)
	results=confusionMatrix(predictions, testData$Binary_Class)
	nn_a = c(nn_a, results[3][[1]][1][[1]])
	t2 = proc.time()
	te = t2-t1
	t_e = c(t_e, te['elapsed'][[1]])
}	
plot(h_n, nn_a, type="n", xlab='# of hidden layers', ylab='Accuracy', main='Accuracy v/s Hidden Nodes')
lines(h_n, nn_a) 
plot(h_n, t_e, type="n", xlab='# of hidden layers', ylab='Time Taken (in seconds)', main='Time Taken v/s Hidden Nodes')
lines(h_n, t_e) 
