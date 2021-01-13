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
    private val itemClickListener: ItemClickListener,
) : RecyclerView.Adapter<RecyclerView.ViewHolder>(), Filterable {

    private var languageFilterList = ArrayList<Language>()

    init {
        languageFilterList = languageList
    }

    class LanguageViewHolder0(private val binding: RecyclerviewLanguageItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(language: Language, itemClickListener: ItemClickListener) {

            binding.let {
                it.primaryLanguage = language.primaryLanguage
                it.secondaryLanguage = language.secondaryLanguage
                it.executePendingBindings()
            }

            itemView.setOnClickListener {
                itemClickListener.onItemClick(language)
            }

        }

    }

    class LanguageViewHolder1(private val binding: RecyclerviewLanguageItemCheckedBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(language: Language) {

            binding.let {
                it.primaryLanguage = language.primaryLanguage
                it.executePendingBindings()
            }

        }

    }

    override fun getItemViewType(position: Int): Int {

        return if (languageFilterList[position].isChecked) {
            1
        } else {
            0
        }

    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder {

        val binding0: RecyclerviewLanguageItemBinding = DataBindingUtil
            .inflate(
                LayoutInflater.from(parent.context),
                R.layout.recyclerview_language_item,
                parent,
                false
            )

        val binding1: RecyclerviewLanguageItemCheckedBinding = DataBindingUtil
            .inflate(
                LayoutInflater.from(parent.context),
                R.layout.recyclerview_language_item_checked,
                parent,
                false
            )

        return when (viewType) {
            0 -> LanguageViewHolder0(binding0)
            else -> LanguageViewHolder1(binding1)
        }

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        when (holder.itemViewType) {

            0 -> {
                (holder as LanguageViewHolder0).bind(
                    languageFilterList[position], itemClickListener
                )
            }

            1 -> {
                (holder as LanguageViewHolder1).bind(
                    languageFilterList[position]
                )
            }

        }

    }

    override fun getItemCount(): Int {
        return languageFilterList.count()
    }

    interface ItemClickListener {
        fun onItemClick(language: Language)
    }

    override fun getFilter(): Filter {

        return object : Filter() {

            override fun performFiltering(constraint: CharSequence?): FilterResults {

                val charSearch = constraint.toString()

                languageFilterList = if (charSearch.isEmpty()) {
                    languageList
                } else {

                    val resultList = ArrayList<Language>()

                    for (i in languageList) {

                        if (i.primaryLanguage.toLowerCase(Locale.ROOT)
                                .contains(charSearch.toLowerCase(Locale.ROOT)) || i.secondaryLanguage
                                .toLowerCase(
                                    Locale.ROOT
                                ).contains(charSearch.toLowerCase(Locale.ROOT))
                        ) {
                            resultList.add(i)
                        }

                    }

                    resultList

                }

                val filterResults = FilterResults()
                filterResults.values = languageFilterList

                return filterResults

            }

            @Suppress("UNCHECKED_CAST")
            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                languageFilterList = results?.values as ArrayList<Language>
                notifyDataSetChanged()
            }

        }

    }

}
