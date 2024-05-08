package com.example.NonCiRestaCheGuardare.api

data class Movies(
    val id: Int,
    val title: String,
    val platform: List<String>
) {
    override fun toString(): String {
        return "[%07d] $title\n\tplatforms=$platform".format(id)
    }
}
