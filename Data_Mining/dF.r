#!/usr/bin/env Rscript

library(randomForest)
library(caret)

#red_train=read.csv('m_train_reduced_data.csv')
red_train=read.csv('relevant_data/m_train_data.csv')
red_train=subset(red_train, select=-c(url, suffix, H_3))

#red_test=read.csv('final_test_features.csv')
red_test=read.csv('relevant_data/m_test_data.csv')
red_test= subset(red_test, select=-c(url, suffix, H_3))

print(length(summary(red_train$Class)))
print(length(summary(red_test$Class)))

#nt = c(10,20,30,40,50,60,70,80,90,100,110,120,130,140,150,160,170,180,190,200)
nt2 = c(10,20,30,40,50,60,70,80,90,100)
accuracy=list()
for(i in nt2){
	print(i)
	model=randomForest(Class~., data=red_train, ntree=i, mtry=5, importance=TRUE)
	results=predict(model, red_test, type="class")
	cM=confusionMatrix(results, red_test$Clas)
	accuracy=c(accuracy, cM[3][[1]][1][[1]])
#	file_name=paste0(i,'_df_result.csv')
#	write.csv(results, file_name)
}
print(nt2)
print (accuracy)
plot(nt2, accuracy, type="n", xlab='# of trees', ylab='Accuracy', main='Accuracy v/s #trees')
lines(nt2, accuracy)
