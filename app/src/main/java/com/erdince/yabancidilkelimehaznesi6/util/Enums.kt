package com.erdince.yabancidilkelimehaznesi6.util

import android.os.Bundle
import androidx.core.os.bundleOf

enum class WordType(val value: String) {
    PreparedWord("preparedWord"),
    CustomWord("customWord")
}

enum class Keys(val key: String) {
    PreviousWordKey("lastWordId"),
    WordIdKey("wordId"),
    WordTypeKey("wordSource")
}

enum class BundleSets(val keyOfBundle: String, val bundlePair: Bundle) {
    NavigationLoopBreaker("navigationLoopBreaker", bundleOf(Pair("navigationLoopBreaker", true)))
}