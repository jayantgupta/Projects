#!/usr/bin/env Rscript

# Load caret library
library(caret)

#load uniform.csv file
data<-read.csv("uniform.csv")

# create a cross validation object
train_control <- trainControl(method="cv", number=10)

# create a subset of the data to be trained
data<-subset(data, select=-c(H_3, H_.))

# Create the model
model<-train(DGA.Family~., data=data[,3:18], trControl=train_control, method="nb")

print(model)

# Predict using the model
predictions <- predict(model, data[,3:17])

print(predictions)
