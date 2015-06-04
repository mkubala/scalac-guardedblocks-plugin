package basic

import scala.util.Random

object IfElseFail {

  {
    if (Random.nextBoolean()) {
      "true"
    } else {
      "false"
    }

    "oops"
  }

}
