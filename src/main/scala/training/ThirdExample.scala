package training

import scalaz.State
import scalaz.Monoid

object ThirdExample {
  trait SocialService {
    def followerStats(u: String): State[Cache, FollowerStats]
  }

  case class FollowerStats(
    username: String,
    numFollowers: Int,
    numFollowing: Int)

  case class Timestamped[A](value: A, timestamp: Long)

  case class Cache(
    stats: Map[String, Timestamped[FollowerStats]],
    hits: Int,
    misses: Int) {

    def get(username: String): Option[Timestamped[FollowerStats]] =
      stats.get(username)

    def update(u: String, s: Timestamped[FollowerStats]): Cache =
      Cache(stats + (u -> s), hits, misses)
  }

  implicit val CacheMonoid = new Monoid[Cache] {
    override def zero = Cache(Map.empty, 0, 0)
    override def append(a: Cache, b: => Cache) =
      Cache(a.stats ++ b.stats, a.hits + b.hits, a.misses + b.misses)
  }


  object FakeSocialService extends SocialService {
    def followerStats(u: String) = {
      State(checkCache(u)) flatMap { ofs =>
        ofs match {
          case Some(fs) => State.state(fs)
          case None => State(retrieve(u))
        }
      }
    }

    private def checkCache(u: String)(c: Cache): (Cache, Option[FollowerStats]) = {
      c.get(u) match {
        case Some(Timestamped(fs, ts))
          if !stale(ts) =>
          (c.copy(hits = c.hits + 1), Some(fs))
        case other =>
          (c.copy(misses = c.misses + 1), None)
      }
    }

    private def stale(ts: Long): Boolean = {
      System.currentTimeMillis - ts > (5 * 60 * 1000L)
    }

    private def retrieve(u: String)(c: Cache): (Cache, FollowerStats) = {
      val fs = callWebService(u)
      val tfs = Timestamped(fs, System.currentTimeMillis)
      (c.update(u, tfs), fs)
    }

    private def callWebService(u: String): FollowerStats =
      FollowerStats(u, 0, 0)
  }

}
