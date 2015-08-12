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
model<-train(Class~., data=data[,2:18], trControl=train_control, method="nb")

# Predict using the model
predictions <- predict(model, data[,2:18])

print( predictions)
