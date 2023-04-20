package com.example.tidimobile.model

data class BlogModelBasic(
    val blogs: ArrayList<BlogBasicObject>? = null
) {
    data class BlogBasicObject(
        val _id: String? = null,
        val idUser: UserBlogObject? = null,
        val title: String? = null,
        val status: Boolean? = null,
        val description: String? = null,
        val createdAt: String? = null,
    ) {
        data class UserBlogObject(
            val _id: String? = null,
            val firstName: String? = null,
            val lastName: String? = null
        )
    }
}

data class BlogModel(
    val blogs: List<BlogObject>? = null
) {
    data class BlogObject(
        val _id: String? = null,
        val idUser: String? = null,
        val title: String? = null,
        val status: Boolean? = null,
        val createdAt: String? = null,
        val content: ContentObject? = null,
        val description: String? = null
    )
}

data class BlogModelDetail(
    val blog: BlogObject? = null
) {
    data class BlogObject(
        val tag: ArrayList<String>? = null,
        val _id: String? = null,
        val idUser: UserModel? = null,
        val title: String? = null,
        val content: ContentObject? = null,
        val status: Boolean? = null,
        val createdAt: String? = null,
    ) {
        data class UserModel(
            val _id: String? = null,
            val firstName: String? = null,
            val lastName: String? = null,
            val avatar: String? = null
        )
    }
}

data class BlogNewModel(
    val idUser: String?=null,
    val title: String?=null,
    val content: ContentObject?=null,
    val description: String?=null,
    val status: Boolean?= null
)

//data class BlogSearchModel(
//    val blogs: ArrayList<Title>?= null,
//){
//    data class Title(val id: String?= null,val title: String?=null)
//}
data class ContentObject(
    val time: String? = null,
    val blocks: java.util.ArrayList<BlockObject>? = null
) {
    data class BlockObject(
        val id: String? = null,
        val type: String? = null,
        val data: DataObject? = null,
        val tunes: TunesObject? = null,
    ) {
        data class DataObject(
            val text: String? = null,
            val level: Int? = null,
            val url: String? = null,
            val caption: String? = null,
            val withBorder: Boolean? = null,
            val withBackground: Boolean? = null,
            val stretched: Boolean? = null
        )

        data class TunesObject(
            val anyTuneName: AnyTuneNameObject? = null
        ) {
            data class AnyTuneNameObject(
                val alignment: String? = null
            )
        }
    }
}