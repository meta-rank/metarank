package me.dfdx.metarank.feature

import me.dfdx.metarank.config.Config.{FeatureConfig, FeedbackConfig, FeedbackTypeConfig}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class FeatureRegistryConfigTest extends AnyFlatSpec with Matchers {
  it should "load tumbling window from config" in {
    val conf = FeedbackConfig(
      List(
        FeedbackTypeConfig("pageview", 1, List(FeatureConfig(TumblingWindowCountingFeature.name, List(1, 2, 4), 10)))
      )
    )
    val registry = FeatureRegistry.fromConfig(conf)
    registry.feedback shouldBe Map(
      "pageview" -> List(TumblingWindowCountingFeature(List(1, 2, 4), 10, "pageview"))
    )
  }

}
