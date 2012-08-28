package training

import scalaz.Monoid
import scalaz.syntax.traverse._
import scalaz.std.list._


/** Shows type class integration with Monoid, Applicative, and Traverse. */
object TypeclassIntegrationExample {

  import FifthExample._

  implicit val CacheMonoid = new Monoid[Cache] {
    override def zero = Cache(Map.empty, 0, 0)
    override def append(a: Cache, b: => Cache) =
      Cache(a.stats ++ b.stats, a.hits + b.hits, a.misses + b.misses)
  }

  val s = FakeSocialService
  val listOfState: List[StateCache[FollowerStats]] = List(s.followerStats("u1"), s.followerStats("u2"), s.followerStats("u1"))
  val stateOfList: StateCache[List[FollowerStats]] = listOfState.sequence[StateCache, FollowerStats]
  stateOfList.run(CacheMonoid.zero) //runZero
}
