package basic

import scala.util.Random

object NestedFail {

  {
    if (Random.nextBoolean()) {
      "anything"
      "true"
    } else {
      "false"
    }
  }

}
