package com.openmobilehub.android.storage.core

@Suppress("MagicNumber")
enum class ThumbnailSize(val width: Int) {
    VERY_SMALL(16),
    SMALL(32),
    MEDIUM(64),
    LARGE(128),
    VERY_LARGE(256)
}
