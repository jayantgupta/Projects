#!/usr/bin/env Rscript

# Script to compute the baselines
# Author : Jayant Gupta
# Date : March 3, 2015
# Majority Class
# Sampling based on prior probabilities

library(caret)

#Majority Class baseline for Multi-Class Classification
trainData=read.csv('relevant_data/m_train_data.csv')
testData=read.csv('final_test_features.csv')
base1=nullModel(trainData,trainData$Class)
results=predict(base1, newdata=testData, type="class")
write.csv(results, 'maj_result.csv')
#cM=confusionMatrix(results, testData$Class)
#print(cM$overall)

#Probability based sampling for multi-class Classification
class_table=table(trainData$Class)
pd=data.frame(class_table/sum(class_table))
prob=c(pd$Freq)
vals=pd$Var1

results=sample(vals, nrow(testData), prob=prob, replace=T)
write.csv(results, 'prob_results.csv')
#cM=confusionMatrix(results, testData$Class)
#print(cM$overall)

#Majority Class baseline for Binary Classification
trainData=read.csv('relevant_data/b_train_data.csv')
#testData=read.csv('b_test_data.csv')
testData=read.csv('final_test_features.csv')
base2=nullModel(trainData,trainData$Binary_Class)
results=predict(base2, newdata=testData, type="class")
cM=confusionMatrix(results, testData$Binary_Class)
print(cM$overall)

#Probability based sampling for Binary Classification
results=sample(c("D",'ND'), nrow(testData), prob=c(0.5, 0.5), replace=T)
cM=confusionMatrix(results, testData$Binary_Class)
print(cM$overall)
