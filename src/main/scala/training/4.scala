package training

import scalaz.State

/** Refactored helper methods, introduced type alias, and introduced for-comprehension. */
object FourthExample {
  type StateCache[A] = State[Cache, A]
  trait SocialService {
    def followerStats(u: String): StateCache[FollowerStats]
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

  object FakeSocialService extends SocialService {
    def followerStats(u: String) = for {
      ofs <- checkCache(u)
      fs <- ofs match {
        case Some(fs) => State.state[Cache, FollowerStats](fs)
        case None => retrieve(u)
      }
    } yield fs

    private def checkCache(u: String): StateCache[Option[FollowerStats]] = State { c =>
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

    private def retrieve(u: String): StateCache[FollowerStats] = for {
      fs <- State.state(callWebService(u))
      tfs = Timestamped(fs, System.currentTimeMillis)
      _ <- State.modify[Cache] { _.update(u, tfs) }
    } yield fs

    private def callWebService(u: String): FollowerStats =
      FollowerStats(u, 0, 0)
  }

}
