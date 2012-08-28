package training

import scalaz._
import Scalaz._

object StateTExample {

  val getAndIncrement: State[Int, Int] =
    State { s => (s + 1, s) }

  object BeforeStateT {
    println(getAndIncrement.replicateM(10).evalZero)
    println(getAndIncrement.replicateM(100000).evalZero)
  }

  object AfterStateT {
    import Free.Trampoline
    println(getAndIncrement.lift[Trampoline].replicateM(100000).evalZero.run)
  }

}
