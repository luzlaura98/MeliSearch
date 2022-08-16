package com.luz.melisearch.domain.repo

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.luz.melisearch.data.constants.AppConstants.FIRST_PAGE
import com.luz.melisearch.data.constants.AppConstants.INITIAL_OFFSET
import com.luz.melisearch.data.constants.AppConstants.ITEMS_PER_PAGE
import com.luz.melisearch.data.services.MeliAPI
import com.luz.melisearch.domain.entities.ItemMeli
import com.luz.melisearch.utils.PagingUtils
import okio.IOException
import retrofit2.HttpException

/**
 * Created by Luz on 16/8/2022.
 */
class ItemsPagingSource(
    private val keyword : String,
    private val service: MeliAPI
) : PagingSource<Int, ItemMeli>() {

    private val pagingUtils = PagingUtils()

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ItemMeli> {
        return try {
            val nextPage: Int = params.key ?: pagingUtils.firstPage
            val response = service.search(
                keyword = keyword,
                offset = pagingUtils.calculateOffset(nextPage)
            )
            val lastPage = pagingUtils.calculateLastPage(response.paging?.totalItems?:0)
            return LoadResult.Page(
                data = response.results,
                prevKey = pagingUtils.calculatePrevPage(nextPage),
                nextKey = pagingUtils.calculateNextPage(nextPage, lastPage)
            )
        } catch (e: IOException) {
            LoadResult.Error(e)
        } catch (e: HttpException) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, ItemMeli>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }
}