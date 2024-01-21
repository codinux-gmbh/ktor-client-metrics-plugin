package net.codinux.web.jokes.model

data class Joke(
    val category: String,
    val type: String,
    val joke: String? = null,
    val setup: String? = null,
    val delivery: String? = null,
//    val flags: Set<Flag>,
    val id: Int,
    val safe: Boolean,
    val lang: String
)