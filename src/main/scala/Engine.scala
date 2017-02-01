package org.template.vanilla

import org.apache.predictionio.controller.IEngineFactory
import org.apache.predictionio.controller.Engine

class Query(
  val x1 : Double,
  val x2 : Double,
  val y : Double
) extends Serializable

class PredictedResult(
  val yHat: Double
) extends Serializable


object VanillaEngine extends IEngineFactory {
  def apply() = {
    new Engine(
      classOf[DataSource],
      classOf[Preparator],
      Map("algo" -> classOf[Algorithm]),
      classOf[Serving])
  }
}
