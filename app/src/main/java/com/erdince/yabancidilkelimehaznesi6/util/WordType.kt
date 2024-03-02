package com.erdince.yabancidilkelimehaznesi6.util

enum class WordType(val value: String) {
    WordTypeKey("wordSource"),
    PreparedWord("preparedWords"),
    CustomWord("customWords")
}

enum class Keys(val key: String) {
    PreviousWordKey("lastWordId")
}