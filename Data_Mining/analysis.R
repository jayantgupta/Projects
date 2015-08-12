#!/usr/bin/env Rscript

# To be Run in the directory where the dataset is present.
# Author : Jayant Gupta
# Start Date : February 9, 2015

library(rpart)
library(rpart.plot)
library(rattle)

cur_dir <- getwd()
print(paste("The current Working directory is ", cur_dir))
input_data<-read.csv("uniform.csv")
length(input_data)
colnames(input_data)

# Creating the function here.
decision_function<-DGA.Family ~ H_2
decision_control<-rpart.control(cp=0, xval=10)

# Building the tree here.
tree<-rpart(decision_function, data=input_data, control = decision_function)

# PLOT II
fancyRpartPlot(tree)

# PLOT I
prp(tree)

# Cross Validation of decision trees.
# for the first model the error is
# 26.67%

# X-val relative error ~ 86.5%

# Pruning
# Run on large dataset
# Change Functions

# dev.copy2pdf(file = <file name>)
