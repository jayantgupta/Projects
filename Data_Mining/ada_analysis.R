#!/usr/bin/env Rscript

# Author : Jayant Gupta
# Date : March 1, 2015
# Code for Adaboost

library(ada)
library(rpart)

ada_train=read.csv("nn_train.csv")
ada_train=subset(ada_train, select=-c(X, suffix))

ada_test=read.csv("nn_test.csv")
ada_test=subset(ada_test, select=-c(X, suffix))

#ada_train=read.csv("b_train_data.csv")
#ada_train=subset(ada_train, select=-c(url, suffix))
#ada_test=read.csv("b_test_data.csv")
#ada_test=subset(ada_test, select=-c(url, suffix, Class, ID_unit, Prob, Stratum))

ada_train=read.csv("m_train_data.csv")
ada_train=subset(ada_train, select=-c(url, suffix))

ada_test=read.csv("m_test_data.csv")
ada_test=subset(ada_test, select=-c(url, suffix))

control=rpart.control(cp=-1, maxdepth=10, xval=0)
ada1=ada(Binary_Class~., data=ada_train, test.x=ada_test[,-20], test.y=ada_test[,20], type="gentle", control=control, iter=2)

accuracy=list()
iters=c(5, 10, 15, 20, 25, 30, 35, 40, 45, 50, 55, 60, 65, 70, 75, 80)
t_e = list()
#t0 = proc.time()
for ( i in iters){
	t0 = proc.time()
	ada1=ada(Binary_Class~., data=ada_train, test.x=ada_test[,-20], test.y=ada_test[,20], type="gentle", control=control, iter=i)
#ada1=update(ada1, ada_train[,-20], ada_train[,20], test.x=ada_test[,-20], test.y=ada_test[,20], n.iter=i, type="gentle", control=control)
	results = ada1$confusion
	acc = (results[[1]] + results[[4]]) / (results[[1]] + results[[2]] + results[[3]] + results[[4]])
	print(acc)
	accuracy=c(accuracy, acc*100)
	t=proc.time()
	te=t-t0
	t_e = c(t_e, te['elapsed'][[1]])
}
print(iters)
print(accuracy)
plot(iters, accuracy, type="n", xlab="# iterations", ylab="Accuracy", main="Accuracy v/s Number of iterations")
lines(iters, accuracy)
plot(iters, t_e, type="n", xlab="# iterations", ylab="Time Taken (in seconds)", main="Time Taken v/s Number of iterations")
lines(iters, t_e)
