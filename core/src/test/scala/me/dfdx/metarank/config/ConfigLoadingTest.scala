package me.dfdx.metarank.config

import better.files.Resource
import me.dfdx.metarank.config.Config.{ConfigSyntaxError, YamlDecodingError}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class ConfigLoadingTest extends AnyFlatSpec with Matchers {
  it should "load valid config" in {
    val yaml   = Resource.my.getAsString("/config/config.valid.yml")
    val result = Config.load(yaml)
    result.isRight shouldBe true
    result.map(_.featurespace.name) shouldBe Right("demo")
  }

  it should "fail on config with no keyspaces" in {
    val yaml   = Resource.my.getAsString("/config/config.invalid.nokeyspaces.yml")
    val result = Config.load(yaml)
    result should matchPattern { case Left(ConfigSyntaxError(_, _)) =>
    }
  }

  it should "fail on config with broken yaml" in {
    val yaml   = Resource.my.getAsString("/config/config.invalid.invalidyaml.yml")
    val result = Config.load(yaml)
    result should matchPattern { case Left(YamlDecodingError(_, _)) =>
    }
  }
}
