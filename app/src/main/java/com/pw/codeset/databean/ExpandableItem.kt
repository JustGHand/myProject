package com.pw.codeset.databean
// 使用密封类定义列表项类型
sealed class ExpandableItem<T> {
    data class Group<T>(
        val title: String,
        val subItems: List<T>,
        var isExpanded: Boolean = false
    ) : ExpandableItem<T>()

    data class Child<T>(
        val data: T
    ) : ExpandableItem<T>()
}