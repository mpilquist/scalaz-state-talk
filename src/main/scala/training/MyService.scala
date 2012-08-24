package training

object FirstExample {
  trait SocialService {
    /**
     * Retrieves the following statistics
     * for the specified user.
     */
    def followerStats(username: String): FollowerStats
  }

  case class FollowerStats(
    username: String,
    numFollowers: Int,
    numFollowing: Int)

  case class Cache(
    stats: Map[String, FollowerStats],
    hits: Int,
    misses: Int) {

    def get(username: String): Option[FollowerStats] =
      stats.get(username)

    def update(u: String, s: FollowerStats): Cache =
      Cache(stats + (u -> s), hits, misses)
  }

  object FakeSocialService extends SocialService {
    def followerStats(username: String) = {
      FollowerStats(username, 0, 0)
    }
  }

}
