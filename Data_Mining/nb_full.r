# Load the libraries:
library("klaR")
library("caret")

# Read the data
data<-read.csv(“fulldataset”)

# x has all other labels, y has class label.
x=data[,-18]
y=data$Class

# Train the model using Naïve bayes
model = train(x,y,'nb',trControl=trainControl(method='cv',number=10))

# predicting the model:
predict(model$finalModel,x)
predict(model$finalModel,x)$class

# Build the confusion matrix:
table(predict(model$finalModel,x)$class,y)
