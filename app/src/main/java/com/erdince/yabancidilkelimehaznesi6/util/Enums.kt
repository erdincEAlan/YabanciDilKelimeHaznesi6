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

enum class dbSources(val source: String) {
    Local("localDb"),
    Cloud("cloudDb")
}

enum class LocalDbType(val dbFor : String){
    User("userDataLocalDb"),
    Word("wordDataLocalDb")
}