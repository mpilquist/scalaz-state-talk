package training

import scalaz._
import Scalaz._

object StateTExample {

  val getAndIncrement: State[Int, Int] =
    State { s => (s + 1, s) }

  // Note: Somewhere between Scalaz 7-m3 and 7-final, evalZero can no longer find Monoid[Int] implicitly (diverging implicit expansion...).

  object BeforeStateT {
    println(getAndIncrement.replicateM(10).evalZero(implicitly, Monoid[Int]))
    println(getAndIncrement.replicateM(100000).evalZero(implicitly, Monoid[Int]))
  }

  object AfterStateT {
    import Free.Trampoline
    println(getAndIncrement.lift[Trampoline].replicateM(100000).evalZero(implicitly, Monoid[Int]).run)
  }

}
