package training

import scalaz.State
import scalaz.syntax.std.option._

/** Use state combinators. */
object FifthExample {
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

    def recordHit: Cache = copy(hits = this.hits + 1)
    def recordMiss: Cache = copy(misses = this.misses + 1)
  }

  object FakeSocialService extends SocialService {
    def followerStats(u: String) = for {
      ofs <- checkCache(u)
      fs <- ofs.fold(retrieve(u))(State.state[Cache, FollowerStats])
    } yield fs

    private def checkCache(u: String): StateCache[Option[FollowerStats]] = for {
      ofs <- State.gets { c: Cache => c.get(u).collect { case Timestamped(fs, ts) if !stale(ts) => fs } }
      _ <- State.modify { c: Cache => ofs ? c.recordHit | c.recordMiss }
    } yield ofs

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

