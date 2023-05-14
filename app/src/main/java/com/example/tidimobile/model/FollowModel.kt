package com.example.tidimobile.model

class FollowModelGet(
    val followers: ArrayList<FollowersData>? = null
) {
    data class FollowersData(
        val _id: String? = null,
        val idFollow: IDFollowData? = null,
    ) {
        data class IDFollowData(
            val _id: String? = null,
            val firstName: String? = null,
            val lastName: String? = null,
            val avatar: String? = null,
            val createdAt: String? = null

        )
    }
}

class FollowingModelGet(
    val followers: ArrayList<FollowersData>? = null
) {
    data class FollowersData(
        val _id: String? = null,
        val idUser: IDFollowData? = null,
    ) {
        data class IDFollowData(
            val _id: String? = null,
            val firstName: String? = null,
            val lastName: String? = null,
            val avatar: String? = null,
            val createdAt: String? = null

        )
    }
}

class FollowModelAdd(
    val idFollow: String? = null
)

class FollowModelCheck(
    val result: Boolean? = null
)