package com.luz.melisearch.utils

import org.junit.Test

import org.junit.Assert.*
import org.junit.Before
import java.lang.IllegalArgumentException

/**
 * Created by Luz on 16/8/2022.
 */
class PagingUtilsTest{

    lateinit var pagingUtils: PagingUtils

    companion object{
        private const val FIRST_PAGE = 0
        private const val INITIAL_OFFSET = 1
    }

    @Before
    fun onBefore(){
        pagingUtils = PagingUtils(
            firstPage = 0,
            initialOffset = 1,
            itemsPerPage = 10
        )
        // page 0 [ offset 1 ... offset 10]
        // page 1 [ offset 11 ... offset 20 ]

        // if total items = 24
        // last page (2) [ offset 21 ... offset 24]
        // if total items = 30
        // last page (2) [ offset 21 ... offset 30]
    }

    @Test
    fun `calculate offset of first page`(){
        assertEquals(INITIAL_OFFSET,pagingUtils.calculateOffset(FIRST_PAGE))
    }

    @Test
    fun `calculate offset of other page`(){
        // Third page
        assertEquals(21,pagingUtils.calculateOffset(FIRST_PAGE + 2))
    }

    @Test
    fun `calculate previous page if current page is the first one`(){
        assertEquals(null,pagingUtils.calculatePrevPage(FIRST_PAGE))
    }

    @Test
    fun `calculate previous page if current page is the second one`(){
        assertEquals(FIRST_PAGE,pagingUtils.calculatePrevPage(FIRST_PAGE + 1))
    }

    @Test
    fun `calculate previous page if current page is other one`(){
        // Third page
        assertEquals(FIRST_PAGE + 1,pagingUtils.calculatePrevPage(FIRST_PAGE + 2))
    }

    @Test(expected = IllegalArgumentException::class)
    fun `calculate previous page if current page is lower than first page`() {
        pagingUtils.calculatePrevPage(FIRST_PAGE - 1)
    }

    @Test
    fun `calculate next page if current page is the first one and there are more pages`(){
        assertEquals(
            FIRST_PAGE + 1,
            pagingUtils.calculateNextPage(FIRST_PAGE, 5)
        )
    }

    @Test
    fun `calculate next page if current page is the first one and there are not more pages`(){
        assertEquals(
            null,
            pagingUtils.calculateNextPage(FIRST_PAGE, FIRST_PAGE)
        )
    }

    @Test
    fun `calculate next page if current page is the first one and last page is zero`(){
        assertEquals(
            null,
            pagingUtils.calculateNextPage(FIRST_PAGE, 0)
        )
    }

    @Test
    fun `calculate next page if current page is the first one and last page is null`(){
        assertEquals(
            null,
            pagingUtils.calculateNextPage(FIRST_PAGE, null)
        )
    }

    @Test
    fun `calculate last page if total items is zero`(){
        assertEquals(
            null,
            pagingUtils.calculateLastPage(0)
        )
    }

    @Test
    fun `calculate last page if total items is grater than zero, round to up`(){
        assertEquals(
            2,
            pagingUtils.calculateLastPage(24)
        )
    }

    @Test
    fun `calculate last page if total items is grater than zero`(){
        assertEquals(
            2,
            pagingUtils.calculateLastPage(29)
        )
    }

    @Test
    fun `calculate last page if total items are multiple of items per page`(){
        assertEquals(
            2,
            pagingUtils.calculateLastPage(30)
        )
    }
}