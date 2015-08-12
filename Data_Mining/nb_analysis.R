#!/usr/bin/env Rscript

# To be run in the directory where the dataset is present
# Author : Jayant Gupta
# Start Date : February 10, 2015

# load the library
library(caret)
# load the iris dataset
	data(iris)
# define training control
	train_control <- trainControl(method="cv", number=10)
# train the model 
	model <- train(Species~., data=iris, trControl=train_control, method="nb")
# make predictions
predictions <- predict(model, iris[,1:4])

# summarize results
confusionMatrix(predictions, iris$Species)
