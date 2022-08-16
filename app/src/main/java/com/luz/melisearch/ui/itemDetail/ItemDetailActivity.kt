package com.luz.melisearch.ui.itemDetail

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.core.view.isVisible
import androidx.viewpager2.widget.ViewPager2
import com.luz.melisearch.R
import com.luz.melisearch.databinding.ActivityItemDetailBinding
import com.luz.melisearch.domain.entities.ItemMeli
import com.luz.melisearch.domain.entities.ItemMeliDetail
import com.luz.melisearch.domain.repo.ItemsRepository
import com.luz.melisearch.ui.adapters.PicturesAdapter
import com.luz.melisearch.ui.base.BaseActivity
import com.luz.melisearch.utils.PlaceholderManager
import com.luz.melisearch.utils.Resource
import com.luz.melisearch.utils.toPriceFormat
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

/**
 * Created by Luz on 15/8/2022.
 */
@AndroidEntryPoint
class ItemDetailActivity : BaseActivity() {

    companion object {
        private const val EXTRA_ITEM_MELI = "item"
        fun intentFor(context: Context, itemMeli: ItemMeli): Intent {
            return Intent(context, ItemDetailActivity::class.java)
                .putExtra(EXTRA_ITEM_MELI, itemMeli)
        }
    }

    private lateinit var binding: ActivityItemDetailBinding

    private val placeholderManager by lazy {
        PlaceholderManager(
            binding.placeholderContainer,
            binding.itemDetailContainer.root
        )
    }

    @Inject
    lateinit var repository: ItemsRepository
    private val viewModel: ItemDetailViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityItemDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar!!.title = getString(R.string.item_detail_title)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        val item = intent.getParcelableExtra<ItemMeli>(EXTRA_ITEM_MELI)
        if (item == null) {
            placeholderManager.showPlaceholderMessage(null, null)
            return
        }

        viewModel.itemLiveData.observe(this) { resource ->
            when (resource) {
                is Resource.Loading -> placeholderManager.showLoading(true)
                is Resource.Success -> {
                    placeholderManager.showLoading(false)
                    showItemDetail(resource.data!!)
                }
                is Resource.Error -> placeholderManager.showPlaceholderMessage(resource.message) {
                    viewModel.getItemDetail(item.id)
                }
            }
        }
        viewModel.getItemDetail(item.id)
    }

    private fun showItemDetail(data: ItemMeliDetail) {
        with(binding.itemDetailContainer) {

            val soldText = resources.getQuantityString(
                R.plurals.soldCount, data.soldQuantity ?: 0,
                data.soldQuantity ?: 0
            )
            tvSold.text = soldText

            tvTitle.text = data.title

            if (data.subtitle.isNullOrEmpty())
                tvSubtitle.isVisible = false
            else {
                tvSubtitle.text = data.subtitle
                tvSubtitle.isVisible = true
            }

            if (data.permalink != null) {
                ivShare.setOnClickListener { onClickShare(data.permalink) }
            } else
                ivShare.isVisible = false

            val urlPics = data.pictures.mapNotNull { it.url }
            initViewPager(urlPics)

            tvPrice.text = data.price.toPriceFormat()
        }
    }

    private fun initViewPager(urlPics: List<String>) {
        if (urlPics.isEmpty()) {
            binding.itemDetailContainer.viewPager.isVisible = false
            return
        }
        if (urlPics.size > 1) {
            binding.itemDetailContainer.tvIndicatorPage.text = "1/${urlPics.size}"
            val onPageChangeCallback = object :
                ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    binding.itemDetailContainer.tvIndicatorPage.text =
                        "${position + 1}/${urlPics.size}"
                }
            }
            binding.itemDetailContainer.viewPager.registerOnPageChangeCallback(onPageChangeCallback)
            binding.itemDetailContainer.tvIndicatorPage.isVisible = true
        } else {
            binding.itemDetailContainer.tvIndicatorPage.isVisible = false
        }
        binding.itemDetailContainer.viewPager.apply {
            adapter = PicturesAdapter(urlPics)
            isVisible = true
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }

    private fun onClickShare(permalink: String) {
        val sendIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, permalink)
            type = "text/plain"
        }
        val shareIntent = Intent.createChooser(sendIntent, getString(R.string.text_share_item))
        startActivity(shareIntent)
    }

}