#!/usr/bin/env Rscript

# February 26 ,2015
# Jayant Gupta
# This script samples the data to produce different dataset
# based upon the user requirements.

library(sampling)
library(caret)

######################## Creating binary DataSet ############################

data=read.csv('updated_features.csv')
b_size=c(237036, 237036)
b_strata=strata(data, stratanames="Binary_Class", size=b_size, method="srswor")
b_data=getdata(data, b_strata)

b_train=createDataPartition(b_data$Binary_Class, p=4/5, list=FALSE)

b_train_data=b_data[b_train,]
b_train_data=subset(b_train_data, select=-c(Class, ID_unit, Prob, Stratum))
write.csv(b_train_data, file="b_train_data.csv", row.names=FALSE)

b_test_data=b_data[-b_train,]
b_test_data=subset(b_test_data, select=-c(Class, ID_unit, Prob, Stratum))
write.csv(b_test_data, file="b_test_data.csv", row.names=FALSE)

######################## Creating Multi-Class DataSet #########################

dga = subset(data, Binary_Class=="D")
non_dga = subset(data, Binary_Class=="ND")
nd_strata=strata(non_dga, stratanames="Class", size=c(90000))
nd_data=getdata(non_dga, nd_strata)
nd_data=subset(nd_data, select=-c(Binary_Class, ID_unit, Prob, Stratum))
dga = subset(dga, select=-c(Binary_Class))

m_data = rbind(dga,nd_data)
m_train=createDataPartition(m_data$Class, p=4/5, list=FALSE)

m_train_data=m_data[m_train,]
write.csv(m_train_data, file="m_train_data.csv", row.names=FALSE)

m_test_data=m_data[-m_train,]
write.csv(m_test_data, file="m_test_data.csv", row.names=FALSE)
	
#Uniform sampling based on the class with lowest number of training samples.
#sampled_strata<-strata(input_data, stratanames="DGA.Family", size=rep(5,30), method="srswor")

#Extract complete rows based on the url ID from the strata
#sampled_data<-getdata(input_data, sampled_strata)

# Retain the necessary and important columns here
#train_data<-subset(sampled_data, select = -c())
