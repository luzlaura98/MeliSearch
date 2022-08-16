package com.luz.melisearch.utils

import com.luz.melisearch.data.constants.AppConstants.FIRST_PAGE
import com.luz.melisearch.data.constants.AppConstants.INITIAL_OFFSET
import com.luz.melisearch.data.constants.AppConstants.ITEMS_PER_PAGE
import kotlin.math.ceil

/**
 * Created by Luz on 16/8/2022.
 * Useful class to calculate page to request through offset and limit.
 */
class PagingUtils(
    val firstPage : Int = FIRST_PAGE,
    private val initialOffset: Int = INITIAL_OFFSET,
    private val itemsPerPage: Int = ITEMS_PER_PAGE
) {
    /**
     * @param page reference to calculate the offset
     * For example, if itemsPerPage = 3 and initialOffset = 0
     * Offset of first page should be 0 and the next one should be 3.
     * @return offset
     * */
    fun calculateOffset(page: Int): Int {
        return (page - firstPage) * itemsPerPage + initialOffset
    }

    /**
     * @throws IllegalArgumentException if @param currentPage is lower than firstPage
     * @return previous page. If @param currentPage is the first page, there is not previous page
     * available.
     * */
    fun calculatePrevPage(currentPage : Int) : Int? {
        if (currentPage < firstPage)
            throw IllegalArgumentException("Invalid current page, " +
                    "current page can't be lower than first page.")
        return if (currentPage == firstPage) null else currentPage - 1
    }

    /**
     * @return nextPage. If current page is the last page, then the next page should be null.
     * */
    fun calculateNextPage(currentPage: Int, lastPage: Int): Int? {
        return if (currentPage + 1 <= lastPage) currentPage + 1 else null
    }

    /**
     * For example, total items = 10, items per page = 5. Then, there are 2 pages: page 1 and page 2.
     *
     * If the pagination starts with page = 1, the pages are 1, 2.
     *
     * If the pagination starts with page = 0, the pages are 0, 1.
     * @param totalItems quantity of items available
     * @return last page
     * */
    fun calculateLastPage(totalItems: Int): Int {
        val roundUp = ceil(totalItems / itemsPerPage.toDouble())
        return roundUp.toInt() - 1 + firstPage
    }
}