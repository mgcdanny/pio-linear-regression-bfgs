package org.template.vanilla

import org.apache.spark.mllib.optimization.{LBFGS, LeastSquaresGradient, SquaredL2Updater}
import org.apache.spark.mllib.regression.LinearRegressionModel
import org.apache.spark.mllib.linalg.Vectors
import org.apache.spark.mllib.util.MLUtils

import org.apache.predictionio.controller.P2LAlgorithm
import org.apache.predictionio.controller.Params

import org.apache.spark.SparkContext
import org.apache.spark.SparkContext._
import org.apache.spark.rdd.RDD

import grizzled.slf4j.Logger


case class AlgorithmParams(
//Whether the model should train with an intercept
    val numCorrections : Int,
    val convergenceTol : Double,
    val maxNumIterations : Int,
    val regParam : Double
) extends Params


class Algorithm(val ap: AlgorithmParams)
  // extends PAlgorithm if Model contains RDD[]
  extends P2LAlgorithm[PreparedData, LinearRegressionModel, Query, PredictedResult] {

  @transient lazy val logger = Logger[this.type]

  def train(sc: SparkContext, data: PreparedData): LinearRegressionModel = {

    // Building the model
    val numFeatures = data.labeledPoints.take(1)(0).features.size
    // Run training algorithm to build the model
    val initialWeightsWithIntercept = Vectors.dense(new Array[Double](numFeatures + 1))

    val biasData = data.labeledPoints.map(x => (x.label, MLUtils.appendBias(x.features)))

    val (weightsWithIntercept, loss) = LBFGS.runLBFGS(
      biasData,
      new LeastSquaresGradient(),
      new SquaredL2Updater(),
      ap.numCorrections,
      ap.convergenceTol,
      ap.maxNumIterations,
      ap.regParam,
      initialWeightsWithIntercept)


    val model = new LinearRegressionModel(
      Vectors.dense(weightsWithIntercept.toArray.slice(0, weightsWithIntercept.size - 1)),
      weightsWithIntercept(weightsWithIntercept.size - 1))


    println("**************************************************************")
    // Evaluate model on training examples and compute training error
    val valuesAndPreds = data.labeledPoints.map { point =>
      val prediction = model.predict(point.features)
      (point.label, prediction)
    }
    val MSE = valuesAndPreds.map{ case(v, p) => math.pow((v - p), 2) }.mean()
    println("training Mean Squared Error = " + MSE)
    println("**************************************************************")

    model

  }

  def predict(model: LinearRegressionModel, query: Query): PredictedResult = {
    val result = model.predict(Vectors.dense(query.x1, query.x2))
    new PredictedResult(result)
  }
}

