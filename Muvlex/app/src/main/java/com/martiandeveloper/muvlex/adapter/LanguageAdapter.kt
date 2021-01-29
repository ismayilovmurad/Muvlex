package com.martiandeveloper.muvlex.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.martiandeveloper.muvlex.R
import com.martiandeveloper.muvlex.databinding.RecyclerviewLanguageItemBinding
import com.martiandeveloper.muvlex.databinding.RecyclerviewLanguageItemCheckedBinding
import com.martiandeveloper.muvlex.model.Language
import java.util.*
import kotlin.collections.ArrayList

class LanguageAdapter(
    private val languageList: ArrayList<Language>,
    private val itemClickListener: ItemClickListener
) : RecyclerView.Adapter<RecyclerView.ViewHolder>(), Filterable {

    private var languageListFilter = ArrayList<Language>()

    init {
        languageListFilter = languageList
    }

    class LanguageViewHolder(private val binding: RecyclerviewLanguageItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(language: Language, itemClickListener: ItemClickListener) {

            binding.let {

                with(language) {
                    it.primaryLanguage = primaryLanguage
                    it.secondaryLanguage = secondaryLanguage
                }

                it.executePendingBindings()

            }

            itemView.setOnClickListener {
                itemClickListener.onItemClick(language)
            }

        }

    }

    class LanguageViewHolderChecked(private val binding: RecyclerviewLanguageItemCheckedBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(language: Language) {

            binding.let {
                it.primaryLanguage = language.primaryLanguage
                it.executePendingBindings()
            }

        }

    }

    override fun getItemViewType(position: Int): Int {
        return if (languageListFilter[position].checked) 1 else 0
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder {
        return if (viewType == 0) LanguageViewHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(
                    parent.context
                ), R.layout.recyclerview_language_item, parent, false
            )
        ) else LanguageViewHolderChecked(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.recyclerview_language_item_checked,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder.itemViewType == 0) (holder as LanguageViewHolder).bind(
            languageListFilter[position],
            itemClickListener
        ) else (holder as LanguageViewHolderChecked).bind(languageListFilter[position])
    }

    override fun getItemCount(): Int {
        return languageListFilter.count()
    }

    interface ItemClickListener {
        fun onItemClick(language: Language)
    }

    override fun getFilter(): Filter {

        return object : Filter() {

            override fun performFiltering(constraint: CharSequence?): FilterResults {

                val charSearch = constraint.toString()

                languageListFilter = if (charSearch.isEmpty()) languageList else {

                    val resultList = ArrayList<Language>()

                    for (i in languageList) {

                        with(charSearch) {

                            if (i.primaryLanguage.toLowerCase(Locale.ROOT)
                                    .contains(toLowerCase(Locale.ROOT)) || i.secondaryLanguage
                                    .toLowerCase(
                                        Locale.ROOT
                                    ).contains(toLowerCase(Locale.ROOT))
                            ) resultList.add(i)

                        }

                    }

                    resultList

                }

                val filterResults = FilterResults()
                filterResults.values = languageListFilter

                return filterResults

            }

            @Suppress("UNCHECKED_CAST")
            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                languageListFilter = results?.values as ArrayList<Language>
                notifyDataSetChanged()
            }

        }

    }

}
